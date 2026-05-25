package com.example.sportapp.data.export

import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.BaseType
import com.example.sportapp.data.model.WorkoutLap
import com.garmin.fit.*
import java.util.*

class FitExporter : ActivityExporter {

    override fun getExtension(): String = "fit"
    override fun getMimeType(): String = "application/vnd.ant.fit"

    override fun generateExport(
        workout: WorkoutEntity,
        points: List<WorkoutPointEntity>,
        laps: List<WorkoutLap>
    ): ByteArray {
        val tempFile = java.io.File.createTempFile("fit_export", ".fit")
        val encoder = FileEncoder(tempFile, Fit.ProtocolVersion.V2_0)

        // 1. File ID
        val fileIdMesg = FileIdMesg().apply {
            setType(com.garmin.fit.File.ACTIVITY)
            setManufacturer(Manufacturer.DEVELOPMENT)
            setProduct(1)
            setSerialNumber(12345L)
            setTimeCreated(DateTime(Date(workout.startTime)))
        }
        encoder.write(fileIdMesg)

        // 2. User Profile
        val userProfileMesg = UserProfileMesg().apply {
            setGender(Gender.MALE)
            setAge(30)
        }
        encoder.write(userProfileMesg)

        // 3. Events - Start
        val startEvent = EventMesg().apply {
            setTimestamp(DateTime(Date(workout.startTime)))
            setEvent(Event.TIMER)
            setEventType(EventType.START)
            setEventGroup(0.toShort())
        }
        encoder.write(startEvent)

        // 4. Records
        points.forEachIndexed { index, point ->
            val recordMesg = RecordMesg().apply {
                val pointTime = workout.startTime + (index * 1000L)
                setTimestamp(DateTime(Date(pointTime)))
                
                if (point.latitude != null && point.longitude != null) {
                    setPositionLat(semicircles(point.latitude))
                    setPositionLong(semicircles(point.longitude))
                }
                
                point.altitude?.let { setAltitude(it.toFloat()) }
                point.bpm?.let { setHeartRate(it.toInt().toShort()) }
                point.speedGps?.let { setSpeed(it.toFloat() / 3.6f) }
                point.distanceGps?.let { setDistance(it.toFloat()) }
                point.stepsMin?.let { setCadence(it.toInt().toShort()) }
            }
            encoder.write(recordMesg)
        }

        // 5. Laps
        if (laps.isNotEmpty()) {
            laps.forEach { lap ->
                val lapMesg = LapMesg().apply {
                    setStartTime(DateTime(Date(workout.startTime + (lap.startLocationIndex * 1000L))))
                    setTimestamp(DateTime(Date(workout.startTime + (lap.endLocationIndex * 1000L))))
                    setTotalElapsedTime(lap.durationMillis.toFloat() / 1000f)
                    setTotalTimerTime(lap.durationMillis.toFloat() / 1000f)
                    setTotalDistance(lap.distanceMeters.toFloat())
                    setAvgSpeed(lap.avgSpeed.toFloat() / 3.6f)
                    setMaxSpeed(lap.maxSpeed.toFloat() / 3.6f)
                    setAvgHeartRate(lap.avgHeartRate.toInt().toShort())
                    setMaxHeartRate(lap.maxHeartRate.toInt().toShort())
                    setTotalAscent(lap.totalAscent.toInt())
                    setTotalDescent(lap.totalDescent.toInt())
                }
                encoder.write(lapMesg)
            }
        } else {
            val lapMesg = LapMesg().apply {
                setStartTime(DateTime(Date(workout.startTime)))
                setTimestamp(DateTime(Date(workout.startTime + workout.durationSeconds * 1000L)))
                setTotalElapsedTime(workout.durationSeconds.toFloat())
                setTotalTimerTime(workout.durationSeconds.toFloat())
                workout.distanceGps?.let { setTotalDistance(it.toFloat()) }
                workout.avgSpeedGps?.let { setAvgSpeed(it.toFloat() / 3.6f) }
                workout.maxSpeed?.let { setMaxSpeed(it.toFloat() / 3.6f) }
                workout.avgBpm?.let { setAvgHeartRate(it.toInt().toShort()) }
                workout.maxBpm?.let { setMaxHeartRate(it.toInt().toShort()) }
                workout.totalAscent?.let { setTotalAscent(it.toInt()) }
                workout.totalDescent?.let { setTotalDescent(it.toInt()) }
            }
            encoder.write(lapMesg)
        }

        // 6. Events - Stop
        val stopEvent = EventMesg().apply {
            setTimestamp(DateTime(Date(workout.startTime + workout.durationSeconds * 1000L)))
            setEvent(Event.TIMER)
            setEventType(EventType.STOP_ALL)
            setEventGroup(0.toShort())
        }
        encoder.write(stopEvent)

        // 7. Session
        val sessionMesg = SessionMesg().apply {
            setStartTime(DateTime(Date(workout.startTime)))
            setTimestamp(DateTime(Date(workout.startTime + workout.durationSeconds * 1000L)))
            setTotalElapsedTime(workout.durationSeconds.toFloat())
            setTotalTimerTime(workout.durationSeconds.toFloat())
            workout.distanceGps?.let { setTotalDistance(it.toFloat()) }
            workout.avgSpeedGps?.let { setAvgSpeed(it.toFloat() / 3.6f) }
            workout.maxSpeed?.let { setMaxSpeed(it.toFloat() / 3.6f) }
            workout.avgBpm?.let { setAvgHeartRate(it.toInt().toShort()) }
            workout.maxBpm?.let { setMaxHeartRate(it.toInt().toShort()) }
            workout.totalAscent?.let { setTotalAscent(it.toInt()) }
            workout.totalDescent?.let { setTotalDescent(it.toInt()) }
            workout.totalCalories?.let { setTotalCalories(it.toInt()) }
            
            val (sport, subSport) = mapToFitSport(workout.baseType)
            setSport(sport)
            setSubSport(subSport)
            setFirstLapIndex(0)
            setNumLaps(if (laps.isNotEmpty()) laps.size else 1)
        }
        encoder.write(sessionMesg)

        encoder.close()
        
        val bytes = tempFile.readBytes()
        tempFile.delete()
        return bytes
    }

    private fun semicircles(degrees: Double): Int {
        return (degrees * (2147483648.0 / 180.0)).toInt()
    }

    private fun mapToFitSport(baseType: String): Pair<Sport, SubSport> {
        return when (baseType) {
            BaseType.RUNNING -> Sport.RUNNING to SubSport.GENERIC
            BaseType.TREADMILL_RUNNING -> Sport.RUNNING to SubSport.TREADMILL
            BaseType.CYCLING -> Sport.CYCLING to SubSport.GENERIC
            BaseType.CYCLING_STATIONARY -> Sport.CYCLING to SubSport.INDOOR_CYCLING
            BaseType.MOUNTAIN_BIKING -> Sport.CYCLING to SubSport.MOUNTAIN
            BaseType.ROAD_BIKING -> Sport.CYCLING to SubSport.ROAD
            BaseType.WALKING -> Sport.WALKING to SubSport.GENERIC
            BaseType.HIKING -> Sport.HIKING to SubSport.GENERIC
            BaseType.SWIMMING_POOL -> Sport.SWIMMING to SubSport.LAP_SWIMMING
            BaseType.SWIMMING_OPEN_WATER -> Sport.SWIMMING to SubSport.OPEN_WATER
            else -> Sport.GENERIC to SubSport.GENERIC
        }
    }
}
