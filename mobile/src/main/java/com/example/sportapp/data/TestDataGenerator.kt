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
        if (summaryFile.exists() && summaryFile.length() > 0) return // Już wygenerowane

        val activities = listOf("Bieg", "Spacer", "Rower", "Wspinaczka")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val header = "data;nazwa aktywnosci;dlugosc;kalorie;gps_dystans;kroki_dystans;przewyzszenia_gora;przewyzszenia_dol;kroki"
        
        val sb = StringBuilder()
        sb.append(header).append("\n")

        val random = Random()
        val calendar = Calendar.getInstance()

        for (i in 1..50) {
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

            sb.append("$dateStr;$type;$durationStr;${String.format("%.1f", calories)};$distGps;$distSteps;$ascent;$descent;$steps\n")
            
            // Generuj plik szczegółowy (pusty dla testów list, ale o poprawnej nazwie)
            val detailFileName = "${type}_${dateStr.replace(" ", "_").replace(":", "_")}_M_30_184_87_83_56_190.csv"
            File(testDir, detailFileName).writeText("time;hr;lat;lon;alt;dist;kcal;steps\n")
        }

        try {
            summaryFile.writeText(sb.toString())
            Log.d("TestDataGenerator", "Generated 50 test activities in ${testDir.absolutePath}")
        } catch (e: Exception) {
            Log.e("TestDataGenerator", "Failed to generate test data", e)
        }
    }
}
