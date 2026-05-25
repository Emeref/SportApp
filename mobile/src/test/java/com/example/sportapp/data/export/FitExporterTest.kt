package com.example.sportapp.data.export

import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import com.example.sportapp.data.model.WorkoutLap
import com.garmin.fit.*
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream

class FitExporterTest {

    @Test
    fun testGenerateFitFileStructure_WithoutLaps() {
        val exporter = FitExporter()
        val workout = WorkoutEntity(
            id = 1L,
            activityName = "Bieganie",
            startTime = System.currentTimeMillis(),
            durationFormatted = "00:10:00",
            durationSeconds = 600,
            distanceGps = 1500.0,
            avgSpeedGps = 9.0,
            maxSpeed = 12.0,
            avgBpm = 140.0,
            maxBpm = 160,
            totalCalories = 120.0
        )
        val points = listOf(
            WorkoutPointEntity(id = 1, workoutId = 1, time = "12:00:00", latitude = 52.2297, longitude = 21.0122, bpm = 130, steps = 10, stepsMin = 160.0, distanceSteps = 1, distanceGps = 1, speedGps = 8.0, speedSteps = 8.0, altitude = 110.0, totalAscent = 0.0, totalDescent = 0.0, calorieMin = 10.0, calorieSum = 10.0),
            WorkoutPointEntity(id = 2, workoutId = 1, time = "12:00:01", latitude = 52.2298, longitude = 21.0123, bpm = 140, steps = 20, stepsMin = 165.0, distanceSteps = 2, distanceGps = 2, speedGps = 9.0, speedSteps = 9.0, altitude = 111.0, totalAscent = 1.0, totalDescent = 0.0, calorieMin = 11.0, calorieSum = 21.0)
        )

        val fitData = exporter.generateExport(workout, points, emptyList())
        assertNotNull(fitData)
        assertTrue(fitData.isNotEmpty())

        val decode = Decode()
        val mesgBroadcaster = MesgBroadcaster(decode)
        val bais = ByteArrayInputStream(fitData)
        
        var fileIdCount = 0
        var userProfileCount = 0
        var sessionCount = 0
        var lapCount = 0
        var recordCount = 0
        var eventCount = 0

        mesgBroadcaster.addListener(object : FileIdMesgListener {
            override fun onMesg(mesg: FileIdMesg) {
                fileIdCount++
                assertEquals(com.garmin.fit.File.ACTIVITY, mesg.type)
            }
        })

        mesgBroadcaster.addListener(object : UserProfileMesgListener {
            override fun onMesg(mesg: UserProfileMesg) {
                userProfileCount++
            }
        })

        mesgBroadcaster.addListener(object : SessionMesgListener {
            override fun onMesg(mesg: SessionMesg) {
                sessionCount++
                assertEquals(1500f, mesg.totalDistance)
            }
        })

        mesgBroadcaster.addListener(object : LapMesgListener {
            override fun onMesg(mesg: LapMesg) {
                lapCount++
            }
        })

        mesgBroadcaster.addListener(object : RecordMesgListener {
            override fun onMesg(mesg: RecordMesg) {
                recordCount++
            }
        })

        mesgBroadcaster.addListener(object : EventMesgListener {
            override fun onMesg(mesg: EventMesg) {
                eventCount++
            }
        })

        mesgBroadcaster.run(bais)

        assertEquals(1, fileIdCount)
        assertEquals(1, userProfileCount)
        assertEquals(1, sessionCount)
        assertEquals(1, lapCount)
        assertEquals(2, recordCount)
        assertEquals(2, eventCount)
    }

    @Test
    fun testGenerateFitFileStructure_WithLaps() {
        val exporter = FitExporter()
        val workout = WorkoutEntity(
            id = 1L,
            activityName = "Bieganie",
            startTime = System.currentTimeMillis(),
            durationFormatted = "00:10:00",
            durationSeconds = 600,
            distanceGps = 1500.0,
            avgSpeedGps = 9.0,
            maxSpeed = 12.0,
            avgBpm = 140.0,
            maxBpm = 160,
            totalCalories = 120.0
        )
        val points = listOf(
            WorkoutPointEntity(id = 1, workoutId = 1, time = "12:00:00", latitude = 52.2297, longitude = 21.0122, bpm = 130, steps = 10, stepsMin = 160.0, distanceSteps = 1, distanceGps = 1, speedGps = 8.0, speedSteps = 8.0, altitude = 110.0, totalAscent = 0.0, totalDescent = 0.0, calorieMin = 10.0, calorieSum = 10.0),
            WorkoutPointEntity(id = 2, workoutId = 1, time = "12:00:01", latitude = 52.2298, longitude = 21.0123, bpm = 140, steps = 20, stepsMin = 165.0, distanceSteps = 2, distanceGps = 2, speedGps = 9.0, speedSteps = 9.0, altitude = 111.0, totalAscent = 1.0, totalDescent = 0.0, calorieMin = 11.0, calorieSum = 21.0)
        )
        val laps = listOf(
            WorkoutLap(
                id = 1,
                workoutId = 1,
                lapNumber = 1,
                durationMillis = 300000,
                distanceMeters = 1000.0,
                avgPaceSecondsPerKm = 300,
                avgSpeed = 12.0,
                maxSpeed = 14.0,
                avgHeartRate = 140,
                maxHeartRate = 155,
                totalAscent = 5.0,
                totalDescent = 2.0,
                startLocationIndex = 0,
                endLocationIndex = 1
            )
        )

        val fitData = exporter.generateExport(workout, points, laps)
        assertNotNull(fitData)

        val decode = Decode()
        val mesgBroadcaster = MesgBroadcaster(decode)
        val bais = ByteArrayInputStream(fitData)
        
        var lapCount = 0

        mesgBroadcaster.addListener(object : LapMesgListener {
            override fun onMesg(mesg: LapMesg) {
                lapCount++
                assertEquals(1000f, mesg.totalDistance)
            }
        })

        mesgBroadcaster.run(bais)

        assertEquals(1, lapCount)
    }
}
