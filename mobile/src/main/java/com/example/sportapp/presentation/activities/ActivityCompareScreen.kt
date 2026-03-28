package com.example.sportapp.presentation.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportapp.AppConstants
import com.example.sportapp.R
import com.example.sportapp.data.SessionData
import com.example.sportapp.data.model.HeartRateZoneResult
import com.example.sportapp.data.model.ZoneStat
import com.example.sportapp.presentation.settings.ThemeMode
import com.example.sportapp.presentation.settings.WidgetItem
import com.example.sportapp.presentation.stats.ActivityDetailSettings
import com.example.sportapp.presentation.stats.CommonChartSection
import com.example.sportapp.presentation.stats.DonutChart
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import java.util.Locale

private val Color1 = Color(0xFF2196F3) // Niebieski
private val Color2 = Color(0xFFFF9800) // Pomarańczowy

data class CompareWidgetConfig(
    val label: String,
    val v1: Any,
    val v2: Any,
    val higherIsBetter: Boolean?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityCompareScreen(
    viewModel: ActivityCompareViewModel,
    onNavigateBack: () -> Unit
) {
    val session1 by viewModel.session1.collectAsStateWithLifecycle()
    val session2 by viewModel.session2.collectAsStateWithLifecycle()
    val hrZones1 by viewModel.hrZones1.collectAsStateWithLifecycle()
    val hrZones2 by viewModel.hrZones2.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val mobileSettings by viewModel.mobileSettings.collectAsStateWithLifecycle()

    val isDarkTheme = when (mobileSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = session1?.let { "Porównanie: ${it.activityName}" } ?: "Porównanie aktywności",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        }
    ) { padding ->
        if (error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        } else if (session1 != null && session2 != null && settings != null) {
            val s1 = session1!!
            val s2 = session2!!
            val currentSettings = settings!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Sticky Dates Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .height(24.dp)
                        .padding(horizontal = 32.dp, vertical = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        s1.activityDate,
                        color = Color1,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        s2.activityDate,
                        color = Color2,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    CompareStatsSection(s1, s2, currentSettings.visibleWidgets)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Wyświetlamy wykresy zgodnie z ustawieniami
                    currentSettings.visibleCharts.filter { it.isEnabled }.forEach { widget ->
                        when (widget.id) {
                            "map" -> {
                                CompareMaps(s1, s2, isDarkTheme)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            "bpm" -> {
                                viewModel.chartProducers["bpm"]?.let { producer ->
                                    if (s1.charts["bpm"]?.filterNotNull()?.isNotEmpty() == true || s2.charts["bpm"]?.filterNotNull()?.isNotEmpty() == true) {
                                        CompareChart(
                                            title = "Tętno (bpm)", 
                                            producer = producer, 
                                            unit = "bpm", 
                                            times = s1.times.takeIf { it.size >= s2.times.size } ?: s2.times,
                                            hrZoneResult = hrZones1
                                        )
                                        if (hrZones1 != null && hrZones2 != null) {
                                            CompareHeartRateZones(hrZones1!!, hrZones2!!)
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }
                            else -> {
                                viewModel.chartProducers[widget.id]?.let { producer ->
                                    if (s1.charts[widget.id]?.filterNotNull()?.isNotEmpty() == true || s2.charts[widget.id]?.filterNotNull()?.isNotEmpty() == true) {
                                        CompareChart(
                                            title = widget.label,
                                            producer = producer,
                                            unit = getUnitForWidget(widget.id),
                                            times = s1.times.takeIf { it.size >= s2.times.size } ?: s2.times
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun CompareStatsSection(s1: SessionData, s2: SessionData, visibleWidgets: List<WidgetItem>) {
    val enabledWidgets = visibleWidgets.filter { it.isEnabled }
    
    val widgetConfigs = mapOf(
        "duration" to CompareWidgetConfig("Czas trwania", s1.duration, s2.duration, true),
        "max_bpm" to CompareWidgetConfig("Maksymalne tętno", s1.maxBpm, s2.maxBpm, null),
        "avg_bpm" to CompareWidgetConfig("Średnie tętno", s1.avgBpm, s2.avgBpm, null),
        "total_calories" to CompareWidgetConfig("Spalone kalorie", s1.totalCalories.toDouble(), s2.totalCalories.toDouble(), true),
        "max_calories_min" to CompareWidgetConfig("Maks spalanie kalorii", s1.maxCaloriesMin.toDouble(), s2.maxCaloriesMin.toDouble(), true),
        "avg_pace" to CompareWidgetConfig("Średnie tempo", s1.avgPace, s2.avgPace, false),
        "max_speed" to CompareWidgetConfig("Maks prędkość", s1.maxSpeed, s2.maxSpeed, true),
        "max_altitude" to CompareWidgetConfig("Maks wysokość", s1.maxAltitude, s2.maxAltitude, null),
        "total_ascent" to CompareWidgetConfig("Suma podejść", s1.totalAscent, s2.totalAscent, true),
        "total_descent" to CompareWidgetConfig("Suma zejść", s1.totalDescent, s2.totalDescent, true),
        "avg_step_length" to CompareWidgetConfig("Wyliczona długość kroku", s1.avgStepLength, s2.avgStepLength, null),
        "avg_cadence" to CompareWidgetConfig("Śr. kadencja", s1.avgCadence, s2.avgCadence, true),
        "max_cadence" to CompareWidgetConfig("Maks. kadencja", s1.maxCadence, s2.maxCadence, true),
        "total_steps" to CompareWidgetConfig("Liczba kroków", s1.totalSteps.toDouble(), s2.totalSteps.toDouble(), true),
        "total_distance_gps" to CompareWidgetConfig("Dystans (GPS)", s1.totalDistanceGps, s2.totalDistanceGps, true),
        "total_distance_steps" to CompareWidgetConfig("Dystans (kroki)", s1.totalDistanceSteps, s2.totalDistanceSteps, true),
        "pressure_start" to CompareWidgetConfig("Ciśnienie atm. (start)", s1.pressureStart ?: 0.0, s2.pressureStart ?: 0.0, null),
        "pressure_end" to CompareWidgetConfig("Ciśnienie atm. (koniec)", s1.pressureEnd ?: 0.0, s2.pressureEnd ?: 0.0, null),
        "max_pressure" to CompareWidgetConfig("Maks. ciśnienie atm.", s1.maxPressure ?: 0.0, s2.maxPressure ?: 0.0, null),
        "min_pressure" to CompareWidgetConfig("Min. ciśnienie atm.", s1.minPressure ?: 0.0, s2.minPressure ?: 0.0, null),
        "best_pace_1km" to CompareWidgetConfig("Najlepsze tempo (1km)", s1.bestPace1km ?: 0.0, s2.bestPace1km ?: 0.0, false)
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        enabledWidgets.forEach { widget ->
            widgetConfigs[widget.id]?.let { config ->
                CompareStatRow(
                    label = config.label,
                    v1 = config.v1,
                    v2 = config.v2,
                    formatter = { value -> formatValue(widget.id, value) },
                    higherIsBetter = config.higherIsBetter
                )
            }
        }
    }
}

private fun formatValue(id: String, value: Any): String {
    val n = when (value) {
        is Number -> value.toDouble()
        else -> return value.toString()
    }
    if (n == 0.0 && (id == "best_pace_1km" || id == "max_pressure" || id == "min_pressure")) return "--"

    return when (id) {
        "duration" -> value.toString()
        "total_calories" -> "${n.toInt()} kcal"
        "max_calories_min" -> String.format(Locale.US, "%.2f kcal/min", n)
        "avg_pace" -> formatPace(n)
        "max_speed" -> String.format(Locale.US, "%.1f km/h", n)
        "max_altitude", "total_ascent", "total_descent" -> String.format(Locale.US, "%.0f m", n)
        "avg_step_length" -> String.format(Locale.US, "%.2f m", n)
        "avg_cadence", "max_cadence" -> String.format(Locale.US, "%.0f kr/min", n)
        "total_steps" -> "${n.toInt()}"
        "total_distance_gps", "total_distance_steps" -> formatDistance(n)
        "pressure_start", "pressure_end", "max_pressure", "min_pressure" -> String.format(Locale.US, "%.1f hPa", n)
        "max_bpm", "avg_bpm" -> "${n.toInt()} bpm"
        "best_pace_1km" -> formatPace(n)
        else -> value.toString()
    }
}

@Composable
fun <T> CompareStatRow(
    label: String, 
    v1: T, 
    v2: T, 
    formatter: (T) -> String,
    higherIsBetter: Boolean?
) {
    val comparison = if (higherIsBetter != null && v1 is Number && v2 is Number) {
        val n1 = v1.toDouble()
        val n2 = v2.toDouble()
        if (n1 == 0.0 || n2 == 0.0) 0
        else if (n1 > n2) (if (higherIsBetter) 1 else -1)
        else if (n1 < n2) (if (higherIsBetter) -1 else 1)
        else 0
    } else 0

    val color1 = when (comparison) {
        1 -> Color(0xFF4CAF50) // Zielony
        -1 -> Color(0xFFE57373) // Czerwony
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val color2 = when (comparison) {
        -1 -> Color(0xFF4CAF50)
        1 -> Color(0xFFE57373)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox(formatter(v1), color1, modifier = Modifier.weight(1f))
            StatBox(formatter(v2), color2, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatBox(value: String, bgColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (bgColor == MaterialTheme.colorScheme.surfaceVariant) onSurfaceColor else Color.White)
    }
}

@Composable
fun CompareChart(
    title: String, 
    producer: com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer,
    unit: String,
    times: List<String>,
    hrZoneResult: HeartRateZoneResult? = null
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        CommonChartSection(
            title = "",
            producer = producer,
            unit = unit,
            detailTimes = times,
            isScrollEnabled = true,
            isZoomEnabled = true,
            hrZoneResult = hrZoneResult,
            lineColors = listOf(Color1, Color2)
        )
    }
}

@Composable
fun CompareHeartRateZones(hr1: HeartRateZoneResult, hr2: HeartRateZoneResult) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Strefy tętna", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                DonutChart(stats = hr1.zones, modifier = Modifier.fillMaxSize(0.8f))
            }
            Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                DonutChart(stats = hr2.zones, modifier = Modifier.fillMaxSize(0.8f))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            val onSurfaceColor = MaterialTheme.colorScheme.onSurface
            Column(modifier = Modifier.padding(8.dp)) {
                hr1.zones.indices.reversed().forEach { i ->
                    CompareZoneRow(hr1.zones[i], hr2.zones[i], onSurfaceColor)
                    if (i > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun CompareZoneRow(stat1: ZoneStat, stat2: ZoneStat, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Act 1 Result
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(formatSeconds(stat1.durationSeconds), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = textColor)
            Text(String.format(Locale.US, "%.1f%%", stat1.percentage), style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.7f))
        }
        
        // Zone Info
        Column(modifier = Modifier.width(120.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(stat1.zone.color))
            Text(stat1.zone.displayName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("${stat1.minBpm}-${stat1.maxBpm}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        // Act 2 Result
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(formatSeconds(stat2.durationSeconds), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = textColor)
            Text(String.format(Locale.US, "%.1f%%", stat2.percentage), style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun CompareMaps(s1: SessionData, s2: SessionData, isDarkTheme: Boolean) {
    val context = LocalContext.current
    val mapStyle = if (isDarkTheme) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark) else null
    
    val routesAreClose = areRoutesClose(s1.route, s2.route, AppConstants.MAP_COMPARISON_RADIUS_KM)
    
    if (routesAreClose) {
        // One map
        val boundsBuilder = LatLngBounds.Builder()
        s1.route.forEach { boundsBuilder.include(it) }
        s2.route.forEach { boundsBuilder.include(it) }
        val bounds = try { boundsBuilder.build() } catch(e: Exception) { null }

        val cameraPositionState = rememberCameraPositionState()
        
        if (bounds != null) {
            LaunchedEffect(bounds) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp).clip(RoundedCornerShape(12.dp))) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = MapProperties(mapStyleOptions = mapStyle),
                cameraPositionState = cameraPositionState
            ) {
                Polyline(points = s1.route, color = Color1, width = 8f)
                Polyline(points = s2.route, color = Color2, width = 8f)
            }
        }
    } else {
        // Two maps
        Row(modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MapSmall(s1.route, Color1, mapStyle, Modifier.weight(1f))
            MapSmall(s2.route, Color2, mapStyle, Modifier.weight(1f))
        }
    }
}

@Composable
fun MapSmall(route: List<LatLng>, color: Color, style: MapStyleOptions?, modifier: Modifier) {
    val boundsBuilder = LatLngBounds.Builder()
    route.forEach { boundsBuilder.include(it) }
    val bounds = try { boundsBuilder.build() } catch(e: Exception) { null }

    val cameraPositionState = rememberCameraPositionState()
    
    if (bounds != null) {
        LaunchedEffect(bounds) {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }
    }

    Box(modifier = modifier.clip(RoundedCornerShape(8.dp))) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(mapStyleOptions = style),
            uiSettings = MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false),
            cameraPositionState = cameraPositionState
        ) {
            Polyline(points = route, color = color, width = 6f)
        }
    }
}

private fun getComparisonPoints(route: List<LatLng>): List<LatLng> {
    if (route.isEmpty()) return emptyList()
    if (route.size < 6) return route
    
    return listOf(
        route[0],
        route[(route.size - 1) * 1 / 5],
        route[(route.size - 1) * 2 / 5],
        route[(route.size - 1) * 3 / 5],
        route[(route.size - 1) * 4 / 5],
        route.last()
    )
}

private fun areRoutesClose(route1: List<LatLng>, route2: List<LatLng>, radiusKm: Double): Boolean {
    val points1 = getComparisonPoints(route1)
    val points2 = getComparisonPoints(route2)
    
    if (points1.isEmpty() || points2.isEmpty()) return false
    
    // Okręgi nachodzą na siebie, jeśli odległość między środkami jest mniejsza lub równa sumie promieni.
    // Tutaj oba promienie to radiusKm, więc suma to 2 * radiusKm.
    val maxDistanceMeters = radiusKm * 2 * 1000 
    
    for (p1 in points1) {
        for (p2 in points2) {
            if (calculateDistance(p1, p2) <= maxDistanceMeters) {
                return true
            }
        }
    }
    return false
}

private fun calculateDistance(p1: LatLng, p2: LatLng): Float {
    val results = FloatArray(1)
    android.location.Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
    return results[0]
}

private fun formatDistance(distanceMeters: Double): String {
    return if (distanceMeters >= 1000) "%.2f km".format(Locale.US, distanceMeters / 1000.0)
    else "%.0f m".format(Locale.US, distanceMeters)
}

private fun formatPace(paceDecimal: Double): String {
    if (paceDecimal <= 0 || paceDecimal > 60) return "--:--"
    val minutes = paceDecimal.toInt()
    val seconds = ((paceDecimal - minutes) * 60).toInt()
    return "%02d:%02d/km".format(minutes, seconds)
}

private fun formatSeconds(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    else String.format(Locale.US, "%02d:%02d", m, s)
}

private fun getUnitForWidget(id: String): String {
    return when(id) {
        "bpm", "srednie_bpm" -> "bpm"
        "kalorie_min", "kalorie_suma" -> "kcal"
        "kroki_min" -> "kroków/min"
        "odl_kroki", "gps_dystans" -> "m"
        "predkosc", "predkosc_kroki" -> "km/h"
        "wysokosc", "przewyzszenia_gora", "przewyzszenia_dol" -> "m"
        "pressure" -> "hPa"
        else -> ""
    }
}
