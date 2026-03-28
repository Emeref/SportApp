package com.example.sportapp.data

import com.example.sportapp.data.db.WorkoutEntity
import com.example.sportapp.data.db.WorkoutPointEntity
import java.text.SimpleDateFormat
import java.util.*

class GpxGenerator {
    private val iso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun generateGpx(workout: WorkoutEntity, points: List<WorkoutPointEntity>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<gpx version=\"1.1\" creator=\"SportApp\" \n")
        sb.append("  xmlns=\"http://www.topografix.com/GPX/1/1\" \n")
        sb.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n")
        sb.append("  xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" \n")
        sb.append("  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd \n")
        sb.append("  http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">\n")
        
        sb.append("  <metadata>\n")
        sb.append("    <time>${iso8601Format.format(Date(workout.startTime))}</time>\n")
        sb.append("  </metadata>\n")
        
        sb.append("  <trk>\n")
        sb.append("    <name>${workout.activityName}</name>\n")
        sb.append("    <type>${mapActivityType(workout.activityName)}</type>\n")
        sb.append("    <trkseg>\n")
        
        points.forEachIndexed { index, point ->
            val lat = point.latitude
            val lon = point.longitude
            
            // Nawet jeśli nie ma GPS, GPX wymaga punktów czasowych dla HR/Kadencji w rozszerzeniach, 
            // ale standardowe tagi <trkpt> wymagają lat/lon. 
            // Wiele platform akceptuje 0,0 jeśli dane to tylko HR/Steps, ale lepiej pominąć jeśli to ma być czysty GPS.
            // Tutaj zakładamy, że eksportujemy to co mamy.
            if (lat != null && lon != null) {
                sb.append("      <trkpt lat=\"$lat\" lon=\"$lon\">\n")
                point.altitude?.let { sb.append("        <ele>$it</ele>\n") }
                
                // Czas punktu: startTime + index sekund (zakładamy 1s interwał)
                val pointTime = workout.startTime + (index * 1000L)
                sb.append("        <time>${iso8601Format.format(Date(pointTime))}</time>\n")
                
                if (point.bpm != null || point.stepsMin != null) {
                    sb.append("        <extensions>\n")
                    sb.append("          <gpxtpx:TrackPointExtension>\n")
                    point.bpm?.let { sb.append("            <gpxtpx:hr>$it</gpxtpx:hr>\n") }
                    point.stepsMin?.let { sb.append("            <gpxtpx:cadence>${it.toInt()}</gpxtpx:cadence>\n") }
                    sb.append("          </gpxtpx:TrackPointExtension>\n")
                    sb.append("        </extensions>\n")
                }
                sb.append("      </trkpt>\n")
            }
        }
        
        sb.append("    </trkseg>\n")
        sb.append("  </trk>\n")
        sb.append("</gpx>")
        
        return sb.toString()
    }

    private fun mapActivityType(name: String): String {
        return when (name.uppercase()) {
            "BIEGANIE", "RUNNING", "BIEG" -> "running"
            "ROWER", "CYCLING", "JAZDA NA ROWERZE" -> "cycling"
            "SPACER", "WALKING", "CHODZENIE" -> "walking"
            "WĘDRÓWKA", "HIKING" -> "hiking"
            else -> "other"
        }
    }
}
