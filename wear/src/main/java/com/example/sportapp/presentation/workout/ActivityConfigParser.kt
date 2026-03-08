package com.example.sportapp.presentation.workout

import android.content.Context
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

object ActivityConfigParser {

    fun parse(context: Context, fileName: String): WorkoutConfig? {
        val inputStream: InputStream = try {
            context.assets.open(fileName)
        } catch (e: Exception) {
            return null
        }

        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)

        var workoutName = ""
        val sensorConfigs = mutableListOf<Triple<String, Int, Int>>()

        var eventType = parser.eventType
        var currentSensorId = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (tagName == "workout") {
                        workoutName = parser.getAttributeValue(null, "name") ?: "Aktywność"
                    } else if (tagName == "sensor") {
                        currentSensorId = parser.getAttributeValue(null, "id") ?: ""
                    }
                }
                XmlPullParser.TEXT -> {
                    val text = parser.text.trim()
                    if (text.isNotEmpty() && parser.name == null) {
                        // We are inside a tag, but XmlPullParser text event is tricky.
                        // Let's handle it in START_TAG instead for better reliability.
                    }
                }
            }
            
            // Re-evaluating text handling for simplicity with XmlPullParser
            if (eventType == XmlPullParser.START_TAG && tagName == "position") {
                val posText = parser.nextText()
                val parts = posText.split("_")
                val row = parts[0].toIntOrNull() ?: 0
                val col = if (parts.size > 1) parts[1].toIntOrNull() ?: 1 else 1
                sensorConfigs.add(Triple(currentSensorId, row, col))
            }

            eventType = parser.next()
        }
        inputStream.close()

        val rows = sensorConfigs.groupBy { it.second }
            .map { (rowIndex, sensors) ->
                WorkoutRow(
                    rowIndex = rowIndex,
                    sensors = sensors.map { SensorConfig(it.first, it.third) }
                        .sortedBy { it.positionInRow }
                )
            }.sortedBy { it.rowIndex }

        return WorkoutConfig(workoutName, rows)
    }
}
