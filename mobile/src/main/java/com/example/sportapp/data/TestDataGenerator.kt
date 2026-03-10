package com.example.sportapp.data

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object TestDataGenerator {

    fun generateTestData(context: Context) {
        val testDir = File(context.filesDir, "test_activities")
        if (!testDir.exists()) testDir.mkdirs()

        val summaryFile = File(testDir, "Podsumowanie_cwiczen.csv")
        val activities = listOf("Bieg", "Spacer", "Rower", "Wspinaczka")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val header = "nazwa aktywnosci;data;dlugosc;kalorie;gps_dystans;kroki_dystans;przewyzszenia_gora;przewyzszenia_dol;kroki"
        
        val sb = StringBuilder()
        sb.append(header).append("\n")

        val random = Random()
        val calendar = Calendar.getInstance()

        // 1. Edge case: Large distance
        val date1 = Date()
        val dateStr1 = sdf.format(date1)
        sb.append("Bieg;$dateStr1;01:20:00;1200.55;12500.0;12450.0;150.0;145.0;15000\n")
        generateSessionFile(testDir, "Bieg", dateStr1, 80, 12500.0, 15000)

        // 2. Edge case: Small distance
        val date2 = Date(System.currentTimeMillis() - 100000)
        val dateStr2 = sdf.format(date2)
        sb.append("Spacer;$dateStr2;00:10:00;50.23;850.0;840.0;5.0;5.0;1200\n")
        generateSessionFile(testDir, "Spacer", dateStr2, 10, 850.0, 1200)

        // 3. Edge case: Corrupted/Missing columns test file (Summary entry exists, but session file will be partial)
        val date3 = Date(System.currentTimeMillis() - 200000)
        val dateStr3 = sdf.format(date3)
        sb.append("Wędrówka;$dateStr3;10:00:00;5000.0;50000.0;48000.0;2500.0;2400.0;1234567\n")
        generatePartialSessionFile(testDir, "Wędrówka", dateStr3)

        for (i in 1..10) { // Reduced count for faster generation, enough for testing
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -random.nextInt(14))
            calendar.add(Calendar.HOUR_OF_DAY, -random.nextInt(24))
            
            val dateStr = sdf.format(calendar.time)
            val type = activities[random.nextInt(activities.size)]
            val durationMin = 20 + random.nextInt(100)
            val durationStr = String.format("%02d:%02d:%02d", durationMin / 60, durationMin % 60, random.nextInt(60))
            
            val calories = 100 + random.nextFloat() * 800
            val distGps = 1000.0 + random.nextInt(5000)
            val distSteps = distGps + random.nextInt(500) - 250
            val ascent = random.nextInt(100).toDouble()
            val descent = random.nextInt(100).toDouble()
            val steps = (distSteps * 1.2).toLong()

            sb.append("$type;$dateStr;$durationStr;${String.format(Locale.US, "%.2f", calories)};$distGps;$distSteps;$ascent;$descent;$steps\n")
            generateSessionFile(testDir, type, dateStr, durationMin, distGps, steps)
        }

        try {
            summaryFile.writeText(sb.toString())
            Log.d("TestDataGenerator", "Generated test activities and session files in ${testDir.absolutePath}")
        } catch (e: Exception) {
            Log.e("TestDataGenerator", "Failed to generate test data", e)
        }
    }

    private fun generateSessionFile(dir: File, type: String, dateStr: String, durationMin: Int, totalDist: Double, totalSteps: Long) {
        val dateForFile = dateStr.replace("-", "_").replace(" ", "_").replace(":", "_")
        val fileName = "${type}_${dateForFile}_M_40_180_80.csv"
        val file = File(dir, fileName)
        
        val header = "czas;lat;lon;bpm;srednie_bpm;kroki;kroki_min;kroki_dystans;gps_dystans;predkosc_gps;wysokosc;przewyzszenia_gora;przewyzszenia_dol"
        val sb = StringBuilder(header).append("\n")
        
        val random = Random()
        val startLat = 52.2297
        val startLon = 21.0122
        
        for (i in 0 until durationMin) {
            val time = String.format("%02d:%02d:00", i / 60, i % 60)
            val lat = startLat + (i * 0.0001)
            val lon = startLon + (i * 0.0001)
            val bpm = 120 + random.nextInt(40)
            val steps = (totalSteps / durationMin) * i
            val dist = (totalDist / durationMin) * i
            
            sb.append("$time;$lat;$lon;$bpm;140;$steps;120;${dist*0.9};$dist;5.5;${150+i};${i*0.5};0\n")
        }
        
        file.writeText(sb.toString())
    }

    private fun generatePartialSessionFile(dir: File, type: String, dateStr: String) {
        val dateForFile = dateStr.replace("-", "_").replace(" ", "_").replace(":", "_")
        val fileName = "${type}_${dateForFile}_CORRUPTED.csv"
        val file = File(dir, fileName)
        
        // Missing some columns compared to normal header
        val header = "czas;bpm;kroki;gps_dystans" 
        val sb = StringBuilder(header).append("\n")
        sb.append("00:00:01;130;10;5.0\n")
        sb.append("00:00:02;135;25;12.0\n")
        
        file.writeText(sb.toString())
    }
}
