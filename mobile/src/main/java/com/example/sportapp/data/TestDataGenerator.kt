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
        // Always generate or overwrite to ensure fresh data for testing calories and formatting
        // if (summaryFile.exists() && summaryFile.length() > 0) return 

        val activities = listOf("Bieg", "Spacer", "Rower", "Wspinaczka")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val header = "nazwa aktywnosci;data;dlugosc;kalorie;gps_dystans;kroki_dystans;przewyzszenia_gora;przewyzszenia_dol;kroki"
        
        val sb = StringBuilder()
        sb.append(header).append("\n")

        val random = Random()
        val calendar = Calendar.getInstance()

        // Dodaj konkretne przypadki brzegowe
        // 1. Bardzo duży dystans (>6000m) do testu konwersji na km
        sb.append("Bieg;${sdf.format(Date())};01:20:00;1200.55;12500.0;12450.0;150.0;145.0;15000\n")
        // 2. Mały dystans (<1000m)
        sb.append("Spacer;${sdf.format(Date())};00:10:00;50.23;850.0;840.0;5.0;5.0;1200\n")
        // 3. Duże liczby do testu spacji co 3 znaki (np. kroki > 1000000)
        sb.append("Wędrówka;${sdf.format(Date())};10:00:00;5000.0;50000.0;48000.0;2500.0;2400.0;1234567\n")

        for (i in 1..47) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -random.nextInt(90))
            calendar.add(Calendar.HOUR_OF_DAY, -random.nextInt(24))
            
            val dateStr = sdf.format(calendar.time)
            val type = activities[random.nextInt(activities.size)]
            val durationMin = 20 + random.nextInt(100)
            val durationStr = String.format("%02d:%02d:%02d", durationMin / 60, durationMin % 60, random.nextInt(60))
            
            val calories = 100 + random.nextFloat() * 800
            val distGps = 1000 + random.nextInt(15000)
            val distSteps = distGps + random.nextInt(500) - 250
            val ascent = random.nextInt(300)
            val descent = random.nextInt(300)
            val steps = (distSteps * 1.2).toInt()

            sb.append("$type;$dateStr;$durationStr;${String.format(Locale.US, "%.2f", calories)};$distGps;$distSteps;$ascent;$descent;$steps\n")
        }

        try {
            summaryFile.writeText(sb.toString())
            Log.d("TestDataGenerator", "Generated test activities with edge cases in ${testDir.absolutePath}")
        } catch (e: Exception) {
            Log.e("TestDataGenerator", "Failed to generate test data", e)
        }
    }
}
