package com.example.sportapp.data

import com.example.sportapp.MobileTexts
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.BaseType
import com.example.sportapp.presentation.settings.HealthData
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.data.model.WorkoutSensor
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GpxImporter @Inject constructor() {

    data class ImportResult(
        val workout: WorkoutEntity,
        val points: List<WorkoutPointEntity>,
        val laps: List<WorkoutLap>,
        val warnings: List<String>
    )

    fun importGpx(
        inputStream: InputStream,
        definition: WorkoutDefinition,
        healthData: HealthData,
        texts: MobileTexts
    ): ImportResult {
        val rawPoints = parseGpx(inputStream)
        if (rawPoints.isEmpty()) throw Exception(texts.GPX_NO_POINTS)

        val startTime = rawPoints.first().timestamp
        val endTime = rawPoints.last().timestamp
        val durationSeconds = (endTime - startTime) / 1000

        val workoutPoints = mutableListOf<WorkoutPointEntity>()
        var totalDistance = 0.0
        var totalAscent = 0.0
        var totalDescent = 0.0
        
        var hasHr = false
        var hasEle = false
        var hasCadence = false

        for (i in rawPoints.indices) {
            val p = rawPoints[i]
            var speedGpsVal = 0.0
            if (i > 0) {
                val prev = rawPoints[i - 1]
                val dist = calculateDistance(prev.lat, prev.lon, p.lat, p.lon)
                totalDistance += dist
                
                val eleDiff = (p.ele ?: 0.0) - (prev.ele ?: 0.0)
                if (eleDiff > 0) totalAscent += eleDiff else totalDescent += Math.abs(eleDiff)

                val timeDiff = (p.timestamp - prev.timestamp) / 1000.0
                if (timeDiff > 0) speedGpsVal = (dist / timeDiff) * 3.6
            }
            
            if (p.hr != null) hasHr = true
            if (p.ele != null) hasEle = true
            if (p.cadence != null) hasCadence = true

            val seconds = (p.timestamp - startTime) / 1000
            val h = seconds / 3600
            val m = (seconds % 3600) / 60
            val s = seconds % 60
            val timeStr = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)

            workoutPoints.add(WorkoutPointEntity(
                workoutId = 0,
                time = timeStr,
                latitude = p.lat,
                longitude = p.lon,
                bpm = p.hr,
                steps = null,
                stepsMin = p.cadence?.toDouble(),
                distanceSteps = null,
                distanceGps = totalDistance.toInt(),
                speedGps = speedGpsVal,
                speedSteps = null,
                altitude = p.ele,
                totalAscent = totalAscent,
                totalDescent = totalDescent,
                calorieMin = null,
                calorieSum = null,
                pressure = null
            ))
        }

        val avgBpm = workoutPoints.mapNotNull { it.bpm }.average().takeIf { !it.isNaN() }
        val maxBpm = workoutPoints.mapNotNull { it.bpm }.maxOrNull()
        val calories = estimateCalories(definition, durationSeconds, avgBpm)

        val workout = WorkoutEntity(
            activityName = definition.name,
            startTime = startTime,
            durationSeconds = durationSeconds,
            durationFormatted = formatDuration(durationSeconds),
            distanceGps = totalDistance,
            totalCalories = calories,
            avgBpm = avgBpm,
            maxBpm = maxBpm,
            totalAscent = totalAscent,
            totalDescent = totalDescent,
            avgSpeedGps = if (durationSeconds > 0) (totalDistance / durationSeconds) * 3.6 else 0.0,
            maxSpeed = workoutPoints.mapNotNull { it.speedGps }.maxOrNull(),
            maxAltitude = workoutPoints.mapNotNull { it.altitude }.maxOrNull(),
            minAltitude = workoutPoints.mapNotNull { it.altitude }.minOrNull(),
            autoLapDistance = definition.autoLapDistance
        )

        val laps = generateLaps(workoutPoints, definition.autoLapDistance ?: 1000.0)
        val warnings = getCompatibilityWarnings(definition, hasHr, hasEle, hasCadence, texts)

        return ImportResult(workout, workoutPoints, laps, warnings)
    }

    private fun parseGpx(inputStream: InputStream): List<RawPoint> {
        val points = mutableListOf<RawPoint>()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(inputStream, "UTF-8")

        var eventType = parser.eventType
        var currentLat: Double? = null
        var currentLon: Double? = null
        var currentEle: Double? = null
        var currentTime: Long? = null
        var currentHr: Int? = null
        var currentCadence: Int? = null
        var text: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (tagName == "trkpt") {
                        currentLat = parser.getAttributeValue(null, "lat")?.toDoubleOrNull()
                        currentLon = parser.getAttributeValue(null, "lon")?.toDoubleOrNull()
                        currentEle = null
                        currentTime = null
                        currentHr = null
                        currentCadence = null
                    }
                }
                XmlPullParser.TEXT -> {
                    text = parser.text
                }
                XmlPullParser.END_TAG -> {
                    when (tagName) {
                        "ele" -> currentEle = text?.toDoubleOrNull()
                        "time" -> currentTime = text?.let { 
                            try { Instant.parse(it).toEpochMilli() } 
                            catch (e: Exception) { 
                                try { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(it)?.time }
                                catch (e2: Exception) { null }
                            }
                        }
                        "hr", "gpxtpx:hr" -> currentHr = text?.toIntOrNull()
                        "cad", "gpxtpx:cad" -> currentCadence = text?.toIntOrNull()
                        "trkpt" -> {
                            if (currentLat != null && currentLon != null && currentTime != null) {
                                points.add(RawPoint(currentLat, currentLon, currentTime, currentEle, currentHr, currentCadence))
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return points
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    private fun getCompatibilityWarnings(definition: WorkoutDefinition, hasHr: Boolean, hasEle: Boolean, hasCadence: Boolean, texts: MobileTexts): List<String> {
        val warnings = mutableListOf<String>()
        val supportedSensors = definition.sensors.filter { it.isRecording }.map { it.sensorId }
        
        if (hasHr && !supportedSensors.contains(WorkoutSensor.HEART_RATE.id)) {
            warnings.add(texts.GPX_WARN_HR)
        }
        if (hasEle && !supportedSensors.contains(WorkoutSensor.ALTITUDE.id)) {
            warnings.add(texts.GPX_WARN_ELE)
        }
        if (hasCadence && !supportedSensors.contains(WorkoutSensor.STEPS_PER_MINUTE.id)) {
            warnings.add(texts.GPX_WARN_CADENCE)
        }
        
        return warnings
    }

    private fun estimateCalories(definition: WorkoutDefinition, durationSeconds: Long, avgBpm: Double?): Double {
        val durationMinutes = durationSeconds / 60.0
        val baseMet = when (definition.baseType) {
            BaseType.RUNNING, BaseType.TREADMILL_RUNNING -> 10.0
            BaseType.CYCLING, BaseType.MOUNTAIN_BIKING, BaseType.ROAD_BIKING -> 8.0
            BaseType.WALKING, BaseType.SPEED_WALKING -> 4.0
            BaseType.HIKING -> 6.0
            BaseType.SWIMMING_POOL, BaseType.SWIMMING_OPEN_WATER -> 8.0
            BaseType.STRENGTH_TRAINING, BaseType.CALISTHENICS -> 5.0
            BaseType.HIIT -> 12.0
            BaseType.YOGA, BaseType.PILATES -> 3.0
            BaseType.FOOTBALL, BaseType.BASKETBALL -> 8.0
            else -> 6.0
        }
        
        return if (avgBpm != null && avgBpm > 0) {
            val hrFactor = (avgBpm / 140.0).coerceIn(0.5, 2.0)
            durationMinutes * baseMet * hrFactor
        } else {
            durationMinutes * baseMet
        }
    }

    private fun generateLaps(points: List<WorkoutPointEntity>, lapDistanceMeters: Double): List<WorkoutLap> {
        val laps = mutableListOf<WorkoutLap>()
        var currentLapStartIdx = 0
        var lastLapDistance = 0.0
        var lapNumber = 1

        for (i in points.indices) {
            val currentDistance = points[i].distanceGps?.toDouble() ?: 0.0
            if (currentDistance - lastLapDistance >= lapDistanceMeters || i == points.size - 1) {
                val lapPoints = points.subList(currentLapStartIdx, i + 1)
                if (lapPoints.isEmpty()) continue

                val lapDist = currentDistance - lastLapDistance
                val firstP = lapPoints.first()
                val lastP = lapPoints.last()
                
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
                val lapDurationSec = try {
                    val start = sdf.parse(firstP.time)?.time ?: 0L
                    val end = sdf.parse(lastP.time)?.time ?: 0L
                    (end - start) / 1000
                } catch (e: Exception) {
                    lapPoints.size.toLong()
                }
                
                val avgSpeed = if (lapDurationSec > 0) (lapDist / lapDurationSec) * 3.6 else 0.0
                val avgHr = lapPoints.mapNotNull { it.bpm }.average().toInt().takeIf { it > 0 } ?: 0
                
                laps.add(WorkoutLap(
                    workoutId = 0,
                    lapNumber = lapNumber++,
                    durationMillis = lapDurationSec * 1000,
                    distanceMeters = lapDist,
                    avgPaceSecondsPerKm = if (lapDist > 10) (lapDurationSec / (lapDist / 1000.0)).toInt() else 0,
                    avgSpeed = avgSpeed,
                    maxSpeed = lapPoints.mapNotNull { it.speedGps }.maxOrNull() ?: 0.0,
                    avgHeartRate = avgHr,
                    maxHeartRate = lapPoints.mapNotNull { it.bpm }.maxOrNull() ?: 0,
                    totalAscent = (lastP.totalAscent ?: 0.0) - (firstP.totalAscent ?: 0.0),
                    totalDescent = (lastP.totalDescent ?: 0.0) - (firstP.totalDescent ?: 0.0),
                    startLocationIndex = currentLapStartIdx,
                    endLocationIndex = i
                ))
                
                lastLapDistance = currentDistance
                currentLapStartIdx = i
            }
        }
        return laps
    }

    private fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, s)
        else String.format(Locale.US, "%02d:%02d", m, s)
    }

    private data class RawPoint(
        val lat: Double,
        val lon: Double,
        val timestamp: Long,
        val ele: Double?,
        val hr: Int?,
        val cadence: Int?
    )
}
