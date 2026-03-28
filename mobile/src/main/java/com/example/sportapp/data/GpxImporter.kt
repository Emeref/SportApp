package com.example.sportapp.data

import android.location.Location
import android.util.Xml
import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutDefinition
import com.example.sportapp.data.model.WorkoutLap
import com.example.sportapp.data.model.WorkoutSensor
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GpxImporter @Inject constructor() {

    private val iso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val iso8601FormatWithMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    data class ImportResult(
        val workout: WorkoutEntity,
        val points: List<WorkoutPointEntity>,
        val laps: List<WorkoutLap>,
        val warnings: List<String> = emptyList()
    )

    fun importGpx(inputStream: InputStream, definition: WorkoutDefinition): ImportResult {
        val points = parseGpx(inputStream)
        if (points.isEmpty()) throw Exception("Plik GPX nie zawiera punktów trasy.")

        val warnings = validateDefinition(points, definition)
        
        val firstPoint = points.first()
        val lastPoint = points.last()
        val startTime = firstPoint.timestamp
        val durationSeconds = (lastPoint.timestamp - firstPoint.timestamp) / 1000

        var totalDistanceGps = 0.0
        var totalAscent = 0.0
        var totalDescent = 0.0
        val bpms = mutableListOf<Int>()
        val cadences = mutableListOf<Double>()
        var maxSpeed = 0.0
        var maxAltitude = -Double.MAX_VALUE
        var minAltitude = Double.MAX_VALUE

        val workoutPoints = mutableListOf<WorkoutPointEntity>()
        
        for (i in points.indices) {
            val p = points[i]
            if (i > 0) {
                val prev = points[i - 1]
                val results = FloatArray(1)
                Location.distanceBetween(prev.lat, prev.lon, p.lat, p.lon, results)
                totalDistanceGps += results[0]
                
                val dist = results[0]
                val timeDiff = (p.timestamp - prev.timestamp) / 1000.0
                if (timeDiff > 0) {
                    val speed = (dist / timeDiff) * 3.6 // km/h
                    if (speed > maxSpeed) maxSpeed = speed
                }

                p.ele?.let { ele ->
                    prev.ele?.let { prevEle ->
                        val diff = ele - prevEle
                        if (diff > 0) totalAscent += diff else totalDescent += Math.abs(diff)
                    }
                }
            }
            
            p.ele?.let {
                if (it > maxAltitude) maxAltitude = it
                if (it < minAltitude) minAltitude = it
            }
            p.hr?.let { bpms.add(it) }
            p.cadence?.let { cadences.add(it.toDouble()) }

            val elapsedSeconds = (p.timestamp - startTime) / 1000
            val h = elapsedSeconds / 3600
            val m = (elapsedSeconds % 3600) / 60
            val s = elapsedSeconds % 60
            val timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)

            workoutPoints.add(WorkoutPointEntity(
                workoutId = 0,
                time = timeFormatted,
                latitude = p.lat,
                longitude = p.lon,
                bpm = p.hr,
                steps = null,
                stepsMin = p.cadence?.toDouble(),
                distanceSteps = null,
                distanceGps = totalDistanceGps.toInt(),
                speedGps = if (i > 0) {
                    val timeDiff = (p.timestamp - points[i-1].timestamp) / 1000.0
                    if (timeDiff > 0) {
                        val dist = FloatArray(1)
                        Location.distanceBetween(points[i-1].lat, points[i-1].lon, p.lat, p.lon, dist)
                        (dist[0] / timeDiff) * 3.6
                    } else 0.0
                } else 0.0,
                speedSteps = null,
                altitude = p.ele,
                totalAscent = totalAscent,
                totalDescent = totalDescent,
                calorieMin = null,
                calorieSum = null
            ))
        }

        val avgBpm = if (bpms.isNotEmpty()) bpms.average() else null
        val maxBpm = if (bpms.isNotEmpty()) bpms.maxOrNull() else null
        val avgCadence = if (cadences.isNotEmpty()) cadences.average() else null
        val maxCadenceValue = if (cadences.isNotEmpty()) cadences.maxOrNull() else null
        
        val calories = estimateCalories(definition, durationSeconds, avgBpm)

        val workout = WorkoutEntity(
            activityName = definition.name,
            startTime = startTime,
            durationFormatted = formatDuration(durationSeconds),
            durationSeconds = durationSeconds,
            steps = null,
            distanceSteps = null,
            distanceGps = totalDistanceGps,
            avgSpeedSteps = null,
            avgSpeedGps = if (durationSeconds > 0) (totalDistanceGps / durationSeconds) * 3.6 else 0.0,
            totalAscent = totalAscent,
            totalDescent = totalDescent,
            avgBpm = avgBpm,
            maxBpm = maxBpm,
            totalCalories = calories,
            maxCalorieMin = null,
            maxSpeed = maxSpeed,
            maxAltitude = if (maxAltitude == -Double.MAX_VALUE) null else maxAltitude,
            minAltitude = if (minAltitude == Double.MAX_VALUE) null else minAltitude,
            avgCadence = avgCadence,
            maxCadence = maxCadenceValue,
            avgPace = if (totalDistanceGps > 0) (durationSeconds / 60.0) / (totalDistanceGps / 1000.0) else null
        )

        val laps = generateLaps(workoutPoints, definition.autoLapDistance ?: 1000.0)

        return ImportResult(workout, workoutPoints, laps, warnings)
    }

    private fun parseGpx(inputStream: InputStream): List<RawPoint> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
        parser.setInput(inputStream, null)

        val points = mutableListOf<RawPoint>()
        var eventType = parser.eventType
        var currentPoint: RawPoint? = null
        var text: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val name = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (name == "trkpt") {
                        val lat = parser.getAttributeValue(null, "lat").toDouble()
                        val lon = parser.getAttributeValue(null, "lon").toDouble()
                        currentPoint = RawPoint(lat, lon, 0, null, null, null)
                    }
                }
                XmlPullParser.TEXT -> {
                    text = parser.text
                }
                XmlPullParser.END_TAG -> {
                    currentPoint?.let { cp ->
                        when (name) {
                            "ele" -> currentPoint = cp.copy(ele = text?.toDoubleOrNull())
                            "time" -> {
                                val date = try {
                                    iso8601Format.parse(text ?: "")
                                } catch (e: Exception) {
                                    try {
                                        iso8601FormatWithMillis.parse(text ?: "")
                                    } catch (e2: Exception) {
                                        null
                                    }
                                }
                                currentPoint = cp.copy(timestamp = date?.time ?: 0)
                            }
                            "hr", "gpxtpx:hr" -> currentPoint = cp.copy(hr = text?.toIntOrNull())
                            "cadence", "gpxtpx:cadence" -> currentPoint = cp.copy(cadence = text?.toIntOrNull())
                            "trkpt" -> {
                                points.add(cp)
                                currentPoint = null
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return points
    }

    private fun validateDefinition(points: List<RawPoint>, definition: WorkoutDefinition): List<String> {
        val warnings = mutableListOf<String>()
        val hasHr = points.any { it.hr != null }
        val hasEle = points.any { it.ele != null }
        val hasCadence = points.any { it.cadence != null }

        val supportedSensors = definition.sensors.filter { it.isRecording }.map { it.sensorId }
        
        if (hasHr && !supportedSensors.contains(WorkoutSensor.HEART_RATE.id)) {
            warnings.add("Plik zawiera dane tętna, ale wybrana aktywność ich nie obsługuje.")
        }
        if (hasEle && !supportedSensors.contains(WorkoutSensor.ALTITUDE.id)) {
            warnings.add("Plik zawiera dane wysokości, ale wybrana aktywność ich nie obsługuje.")
        }
        if (hasCadence && !supportedSensors.contains(WorkoutSensor.STEPS_PER_MINUTE.id)) {
            warnings.add("Plik zawiera dane kadencji, ale wybrana aktywność ich nie obsługuje.")
        }
        
        return warnings
    }

    private fun estimateCalories(definition: WorkoutDefinition, durationSeconds: Long, avgBpm: Double?): Double {
        val durationMinutes = durationSeconds / 60.0
        val baseMet = when (definition.baseType.uppercase()) {
            "RUNNING", "BIEGANIE" -> 10.0
            "CYCLING", "ROWER" -> 8.0
            "WALKING", "SPACER" -> 4.0
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
                
                val lapDurationSec = lapPoints.size.toLong() 
                
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
        return if (h > 0) String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
        else String.format(Locale.US, "%02d:%02d", m, s)
    }

    data class RawPoint(
        val lat: Double,
        val lon: Double,
        val timestamp: Long,
        val ele: Double?,
        val hr: Int?,
        val cadence: Int?
    )
}
