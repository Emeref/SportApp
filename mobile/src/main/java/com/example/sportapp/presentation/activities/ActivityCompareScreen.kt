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
import com.example.sportapp.core.i18n.AppStrings
import com.example.sportapp.core.i18n.LocalAppStrings
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
    val strings = LocalAppStrings.current

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
                        text = session1?.let { strings.comparisonTitle(it.activityName) } ?: strings.activityComparisonLabel,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
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

                    CompareStatsSection(s1, s2, currentSettings.visibleWidgets, strings)

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
                                            title = "${strings.heartRate} (${strings.bpmUnit.lowercase()})", 
                                            producer = producer, 
                                            unit = strings.bpmUnit.lowercase(), 
                                            times = s1.times.takeIf { it.size >= s2.times.size } ?: s2.times,
                                            hrZoneResult = hrZones1
                                        )
                                        if (hrZones1 != null && hrZones2 != null) {
                                            CompareHeartRateZones(hrZones1!!, hrZones2!!, strings)
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
                                            unit = getUnitForWidget(widget.id, strings),
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
fun CompareStatsSection(s1: SessionData, s2: SessionData, visibleWidgets: List<WidgetItem>, strings: AppStrings) {
    val enabledWidgets = visibleWidgets.filter { it.isEnabled }
    
    val widgetConfigs = mapOf(
        "duration" to CompareWidgetConfig(strings.durationLabel, s1.duration, s2.duration, true),
        "max_bpm" to CompareWidgetConfig(strings.maxHeartRateLabel, s1.maxBpm, s2.maxBpm, null),
        "avg_bpm" to CompareWidgetConfig(strings.avgHeartRateLabel, s1.avgBpm, s2.avgBpm, null),
        "total_calories" to CompareWidgetConfig(strings.totalCaloriesLabel, s1.totalCalories.toDouble(), s2.totalCalories.toDouble(), true),
        "max_calories_min" to CompareWidgetConfig(strings.maxCaloriesBurnLabel, s1.maxCaloriesMin.toDouble(), s2.maxCaloriesMin.toDouble(), true),
        "avg_pace" to CompareWidgetConfig(strings.avgPaceLabel, s1.avgPace, s2.avgPace, false),
        "max_speed" to CompareWidgetConfig(strings.maxSpeedLabel, s1.maxSpeed, s2.maxSpeed, true),
        "max_altitude" to CompareWidgetConfig(strings.maxAltitudeLabel, s1.maxAltitude, s2.maxAltitude, null),
        "total_ascent" to CompareWidgetConfig(strings.totalAscentLabel, s1.totalAscent, s2.totalAscent, true),
        "total_descent" to CompareWidgetConfig(strings.totalDescentLabel, s1.totalDescent, s2.totalDescent, true),
        "avg_step_length" to CompareWidgetConfig(strings.avgStepLengthLabel, s1.avgStepLength, s2.avgStepLength, null),
        "avg_cadence" to CompareWidgetConfig(strings.avgCadenceLabel, s1.avgCadence, s2.avgCadence, true),
        "max_cadence" to CompareWidgetConfig(strings.maxCadenceLabel, s1.maxCadence, s2.maxCadence, true),
        "total_steps" to CompareWidgetConfig(strings.totalStepsLabel, s1.totalSteps.toDouble(), s2.totalSteps.toDouble(), true),
        "total_distance_gps" to CompareWidgetConfig("${strings.distance} (GPS)", s1.totalDistanceGps, s2.totalDistanceGps, true),
        "total_distance_steps" to CompareWidgetConfig("${strings.distance} (${strings.steps.lowercase()})", s1.totalDistanceSteps, s2.totalDistanceSteps, true),
        "pressure_start" to CompareWidgetConfig(strings.pressureStartLabel, s1.pressureStart ?: 0.0, s2.pressureStart ?: 0.0, null),
        "pressure_end" to CompareWidgetConfig(strings.pressureEndLabel, s1.pressureEnd ?: 0.0, s2.pressureEnd ?: 0.0, null),
        "max_pressure" to CompareWidgetConfig(strings.maxPressureLabel, s1.maxPressure ?: 0.0, s2.maxPressure ?: 0.0, null),
        "min_pressure" to CompareWidgetConfig(strings.minPressureLabel, s1.minPressure ?: 0.0, s2.minPressure ?: 0.0, null),
        "best_pace_1km" to CompareWidgetConfig(strings.bestPace1kmLabel, s1.bestPace1km ?: 0.0, s2.bestPace1km ?: 0.0, false)
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        enabledWidgets.forEach { widget ->
            widgetConfigs[widget.id]?.let { config ->
                CompareStatRow(
                    label = config.label,
                    v1 = config.v1,
                    v2 = config.v2,
                    formatter = { value -> formatValue(widget.id, value, strings) },
                    higherIsBetter = config.higherIsBetter
                )
            }
        }
    }
}

private fun formatValue(id: String, value: Any, strings: AppStrings): String {
    val n = when (value) {
        is Number -> value.toDouble()
        else -> return value.toString()
    }
    if (n == 0.0 && (id == "best_pace_1km" || id == "max_pressure" || id == "min_pressure")) return "--"

    return when (id) {
        "duration" -> value.toString()
        "total_calories" -> "${n.toInt()} ${strings.kcalUnit}"
        "max_calories_min" -> String.format(Locale.US, "%.2f %s", n, strings.kcalPerMinUnit)
        "avg_pace", "best_pace_1km" -> {
            val minutes = n.toInt()
            val seconds = ((n - minutes) * 60).toInt()
            String.format(Locale.US, "%02d:%02d %s", minutes, seconds, strings.paceUnit)
        }
        "max_speed" -> String.format(Locale.US, "%.1f %s", n, strings.kmhUnit)
        "max_altitude", "total_ascent", "total_descent" -> String.format(Locale.US, "%.0f %s", n, strings.metersUnit)
        "avg_step_length" -> String.format(Locale.US, "%.2f %s", n, strings.metersUnit)
        "avg_cadence", "max_cadence" -> String.format(Locale.US, "%.0f %s", n, strings.cadenceUnit)
        "total_steps" -> "${n.toInt()}"
        "total_distance_gps", "total_distance_steps" -> {
            if (n >= 1000) String.format(Locale.US, "%.2f %s", n / 1000.0, strings.kmUnit)
            else String.format(Locale.US, "%.0f %s", n, strings.metersUnit)
        }
        "pressure_start", "pressure_end", "max_pressure", "min_pressure" -> String.format(Locale.US, "%.1f %s", n, strings.hpaUnit)
        else -> n.toString()
    }
}

private fun getUnitForWidget(id: String, strings: AppStrings): String {
    return when(id) {
        "bpm" -> strings.bpmUnit.lowercase()
        "total_calories", "max_calories_min" -> strings.kcalUnit
        "avg_cadence", "max_cadence" -> strings.cadenceUnit
        "total_distance_gps", "total_distance_steps" -> strings.kmUnit
        "max_speed" -> strings.kmhUnit
        "max_altitude", "total_ascent", "total_descent", "avg_step_length" -> strings.metersUnit
        "pressure_start", "pressure_end", "max_pressure", "min_pressure" -> strings.hpaUnit
        else -> ""
    }
}

@Composable
fun CompareHeartRateZones(r1: HeartRateZoneResult, r2: HeartRateZoneResult, strings: AppStrings) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(strings.heartRateZones, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                r1.zones.reversed().forEachIndexed { index, z1 ->
                    val z2 = r2.zones.reversed()[index]
                    CompareZoneRow(z1, z2, strings)
                    if (index < r1.zones.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun CompareZoneRow(z1: ZoneStat, z2: ZoneStat, strings: AppStrings) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(z1.zone.color))
            Spacer(modifier = Modifier.width(8.dp))
            Text(z1.zone.getName(strings), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text("${z1.minBpm}-${z1.maxBpm} ${strings.bpmUnit.lowercase()}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                Text(formatSeconds(z1.durationSeconds), style = MaterialTheme.typography.bodyMedium, color = Color1, fontWeight = FontWeight.Bold)
                Text(String.format(Locale.US, "%.1f%%", z1.percentage), style = MaterialTheme.typography.labelSmall, color = Color1)
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(formatSeconds(z2.durationSeconds), style = MaterialTheme.typography.bodyMedium, color = Color2, fontWeight = FontWeight.Bold)
                Text(String.format(Locale.US, "%.1f%%", z2.percentage), style = MaterialTheme.typography.labelSmall, color = Color2)
            }
        }
    }
}

private fun formatSeconds(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format(Locale.US, "%d:%02d:%02d", h, m, s)
    else String.format(Locale.US, "%02d:%02d", m, s)
}

@Composable
fun CompareStatRow(
    label: String,
    v1: Any,
    v2: Any,
    formatter: (Any) -> String,
    higherIsBetter: Boolean?
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val text1 = formatter(v1)
            val text2 = formatter(v2)
            
            val isBetter1 = if (higherIsBetter == null) false else if (higherIsBetter) compareValues(v1, v2) > 0 else compareValues(v1, v2) < 0
            val isBetter2 = if (higherIsBetter == null) false else if (higherIsBetter) compareValues(v2, v1) > 0 else compareValues(v2, v1) < 0

            Text(
                text1, 
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge, 
                color = if (isBetter1) MaterialTheme.colorScheme.primary else Color1,
                fontWeight = if (isBetter1) FontWeight.ExtraBold else FontWeight.Bold
            )
            
            Text(
                text2, 
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge, 
                textAlign = TextAlign.End,
                color = if (isBetter2) MaterialTheme.colorScheme.primary else Color2,
                fontWeight = if (isBetter2) FontWeight.ExtraBold else FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { calculateProgress(v1, v2) },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(4.dp).clip(CircleShape),
            color = Color1,
            trackColor = Color2
        )
    }
}

private fun calculateProgress(v1: Any, v2: Any): Float {
    val n1 = (v1 as? Number)?.toDouble() ?: 0.0
    val n2 = (v2 as? Number)?.toDouble() ?: 0.0
    if (n1 + n2 == 0.0) return 0.5f
    return (n1 / (n1 + n2)).toFloat()
}

private fun compareValues(v1: Any, v2: Any): Int {
    if (v1 is String && v2 is String) {
        return v1.compareTo(v2)
    }
    val n1 = (v1 as? Number)?.toDouble() ?: 0.0
    val n2 = (v2 as? Number)?.toDouble() ?: 0.0
    return n1.compareTo(n2)
}

@Composable
fun CompareChart(
    title: String,
    producer: com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer,
    unit: String,
    times: List<String>,
    hrZoneResult: HeartRateZoneResult? = null
) {
    CommonChartSection(
        title = title,
        producer = producer,
        unit = unit,
        detailTimes = times,
        isScrollEnabled = true,
        isZoomEnabled = true,
        hrZoneResult = hrZoneResult,
        customColors = listOf(Color1, Color2)
    )
}

@Composable
fun CompareMaps(s1: SessionData, s2: SessionData, isDarkTheme: Boolean) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val mapProperties = remember(isDarkTheme) {
        MapProperties(
            mapStyleOptions = if (isDarkTheme) {
                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
            } else null
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(300.dp).padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapProperties,
            cameraPositionState = rememberCameraPositionState {
                val boundsBuilder = LatLngBounds.Builder()
                s1.route.forEach { boundsBuilder.include(it) }
                s2.route.forEach { boundsBuilder.include(it) }
                val bounds = try { boundsBuilder.build() } catch (e: Exception) { null }
                if (bounds != null) {
                    position = CameraPosition.fromLatLngZoom(bounds.center, 10f)
                }
            }
        ) {
            Polyline(points = s1.route, color = Color1, width = 8f)
            Polyline(points = s2.route, color = Color2, width = 8f)
        }
    }
}
