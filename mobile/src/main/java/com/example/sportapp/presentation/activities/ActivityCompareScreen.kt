package com.example.sportapp.presentation.activities

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
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
import com.example.sportapp.presentation.stats.CommonChartSection
import com.example.sportapp.presentation.stats.DonutChart
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import java.util.Locale
import kotlin.math.pow

private val Color1 = Color(0xFF2196F3) // Niebieski
private val Color2 = Color(0xFFFF9800) // Pomarańczowy

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

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var isHrZonesExpanded by remember { mutableStateOf(false) }
    var isMapFullScreen by remember { mutableStateOf(false) }

    val isDarkTheme = when (mobileSettings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    if (isMapFullScreen && session1 != null && session2 != null) {
        BackHandler { isMapFullScreen = false }
        FullScreenCompareMap(
            s1 = session1!!,
            s2 = session2!!,
            isDarkTheme = isDarkTheme,
            selectedIndex = selectedIndex,
            onClose = { isMapFullScreen = false }
        )
    } else {
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

                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    // Sticky Dates Header
                    Row(
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).height(24.dp).padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(s1.activityDate, color = Color1, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(s2.activityDate, color = Color2, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    // ZMIANA: LazyColumn dla wydajności
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            CompareStatsSection(s1, s2, currentSettings.visibleWidgets)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(currentSettings.visibleCharts.filter { it.isEnabled }) { widget ->
                            when (widget.id) {
                                "map" -> {
                                    CompareMaps(
                                        s1 = s1, 
                                        s2 = s2, 
                                        isDarkTheme = isDarkTheme, 
                                        selectedIndex = selectedIndex,
                                        onExpandClick = { isMapFullScreen = true }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                "bpm" -> {
                                    viewModel.chartProducers["bpm"]?.let { producer ->
                                        if (s1.charts["bpm"]?.isNotEmpty() == true || s2.charts["bpm"]?.isNotEmpty() == true) {
                                            CompareChart(
                                                title = "Tętno (bpm)", 
                                                producer = producer, 
                                                unit = "bpm", 
                                                times = if (s1.times.size >= s2.times.size) s1.times else s2.times,
                                                hrZoneResult = hrZones1,
                                                onMarkerShown = { selectedIndex = it }
                                            )
                                            if (hrZones1 != null && hrZones2 != null && hrZones1!!.zones.isNotEmpty() && hrZones2!!.zones.isNotEmpty()) {
                                                CompareHeartRateZones(
                                                    hr1 = hrZones1!!, 
                                                    hr2 = hrZones2!!,
                                                    isExpanded = isHrZonesExpanded,
                                                    onToggleExpanded = { isHrZonesExpanded = !isHrZonesExpanded }
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }
                                    }
                                }
                                else -> {
                                    viewModel.chartProducers[widget.id]?.let { producer ->
                                        if (s1.charts[widget.id]?.isNotEmpty() == true || s2.charts[widget.id]?.isNotEmpty() == true) {
                                            CompareChart(
                                                title = widget.label,
                                                producer = producer,
                                                unit = getUnitForWidget(widget.id),
                                                times = if (s1.times.size >= s2.times.size) s1.times else s2.times,
                                                onMarkerShown = { selectedIndex = it }
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun CompareStatsSection(s1: SessionData, s2: SessionData, visibleWidgets: List<WidgetItem>) {
    val enabledWidgets = visibleWidgets.filter { it.isEnabled }
    val widgetConfigs = mapOf(
        "duration" to ("Czas trwania" to true),
        "max_bpm" to ("Maksymalne tętno" to null),
        "avg_bpm" to ("Średnie tętno" to null),
        "total_calories" to ("Spalone kalorie" to true),
        "max_calories_min" to ("Maks spalanie kalorii" to true),
        "avg_pace" to ("Średnie tempo" to false),
        "max_speed" to ("Maks prędkość" to true),
        "max_altitude" to ("Maks wysokość" to null),
        "total_ascent" to ("Suma podejść" to true),
        "total_descent" to ("Suma zejść" to true),
        "avg_step_length" to ("Wyliczona długość kroku" to null),
        "avg_cadence" to ("Śr. kadencja" to true),
        "max_cadence" to ("Maks. kadencja" to true),
        "total_steps" to ("Liczba kroków" to true),
        "total_distance_gps" to ("Dystans (GPS)" to true),
        "total_distance_steps" to ("Dystans (kroki)" to true),
        "pressure_start" to ("Ciśnienie atm. (start)" to null),
        "pressure_end" to ("Ciśnienie atm. (koniec)" to null),
        "max_pressure" to ("Maks. ciśnienie" to null),
        "min_pressure" to ("Min. ciśnienie" to null),
        "best_pace_1km" to ("Najlepsze tempo (1km)" to false)
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        enabledWidgets.forEach { widget ->
            widgetConfigs[widget.id]?.let { (label, higherIsBetter) ->
                val v1 = getSessionValue(s1, widget.id)
                val v2 = getSessionValue(s2, widget.id)
                CompareStatRow(label, v1, v2, { formatValue(widget.id, it) }, higherIsBetter)
            }
        }
    }
}

private fun getSessionValue(s: SessionData, id: String): Any {
    return when(id) {
        "duration" -> s.duration; "max_bpm" -> s.maxBpm; "avg_bpm" -> s.avgBpm
        "total_calories" -> s.totalCalories.toDouble(); "max_calories_min" -> s.maxCaloriesMin.toDouble()
        "avg_pace" -> s.avgPace; "max_speed" -> s.maxSpeed; "max_altitude" -> s.maxAltitude
        "total_ascent" -> s.totalAscent; "total_descent" -> s.totalDescent; "avg_step_length" -> s.avgStepLength
        "avg_cadence" -> s.avgCadence; "max_cadence" -> s.maxCadence; "total_steps" -> s.totalSteps.toDouble()
        "total_distance_gps" -> s.totalDistanceGps; "total_distance_steps" -> s.totalDistanceSteps
        "pressure_start" -> s.pressureStart ?: 0.0; "pressure_end" -> s.pressureEnd ?: 0.0
        "max_pressure" -> s.maxPressure ?: 0.0; "min_pressure" -> s.minPressure ?: 0.0
        "best_pace_1km" -> s.bestPace1km ?: 0.0
        else -> ""
    }
}

private fun formatValue(id: String, value: Any): String {
    val n = (value as? Number)?.toDouble() ?: return value.toString()
    if (n == 0.0 && (id == "best_pace_1km" || id == "max_pressure" || id == "min_pressure")) return "--"
    return when (id) {
        "duration" -> value.toString()
        "total_calories" -> "${n.toInt()} kcal"
        "max_calories_min" -> "%.2f kcal/min".format(Locale.US, n)
        "avg_pace", "best_pace_1km" -> formatPace(n)
        "max_speed" -> "%.1f km/h".format(Locale.US, n)
        "max_altitude", "total_ascent", "total_descent" -> "%.0f m".format(Locale.US, n)
        "avg_step_length" -> "%.2f m".format(Locale.US, n)
        "avg_cadence", "max_cadence" -> "%.0f kr/min".format(Locale.US, n)
        "total_steps" -> "${n.toInt()}"
        "total_distance_gps", "total_distance_steps" -> formatDistance(n)
        "pressure_start", "pressure_end", "max_pressure", "min_pressure" -> "%.1f hPa".format(Locale.US, n)
        "max_bpm", "avg_bpm" -> "${n.toInt()} bpm"
        else -> value.toString()
    }
}

@Composable
fun <T> CompareStatRow(label: String, v1: T, v2: T, formatter: (T) -> String, higherIsBetter: Boolean?) {
    val comparison = if (higherIsBetter != null && v1 is Number && v2 is Number) {
        val n1 = v1.toDouble(); val n2 = v2.toDouble()
        if (n1 == 0.0 || n2 == 0.0) 0 else if (n1 > n2) (if (higherIsBetter) 1 else -1) else if (n1 < n2) (if (higherIsBetter) -1 else 1) else 0
    } else 0
    val color1 = when (comparison) { 1 -> Color(0xFF4CAF50); -1 -> Color(0xFFE57373); else -> MaterialTheme.colorScheme.surfaceVariant }
    val color2 = when (comparison) { -1 -> Color(0xFF4CAF50); 1 -> Color(0xFFE57373); else -> MaterialTheme.colorScheme.surfaceVariant }
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox(formatter(v1), color1, Modifier.weight(1f))
            StatBox(formatter(v2), color2, Modifier.weight(1f))
        }
    }
}

@Composable
fun StatBox(value: String, bgColor: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(8.dp)).background(bgColor).padding(8.dp), contentAlignment = Alignment.Center) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (bgColor == MaterialTheme.colorScheme.surfaceVariant) MaterialTheme.colorScheme.onSurface else Color.White)
    }
}

@Composable
fun CompareChart(
    title: String, 
    producer: com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer, 
    unit: String, 
    times: List<String>, 
    hrZoneResult: HeartRateZoneResult? = null,
    onMarkerShown: (Int?) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        CommonChartSection(
            title = "", 
            producer = producer, 
            unit = unit, 
            detailTimes = times, 
            hrZoneResult = hrZoneResult, 
            lineColors = listOf(Color1, Color2),
            onMarkerShown = onMarkerShown
        )
    }
}

@Composable
fun CompareHeartRateZones(
    hr1: HeartRateZoneResult,
    hr2: HeartRateZoneResult,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    // Safety check: if either is empty or they don't match in size, don't attempt to draw comparison rows
    if (hr1.zones.isEmpty() || hr2.zones.isEmpty() || hr1.zones.size != hr2.zones.size) return

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onToggleExpanded() },
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Zwiń" else "Rozwiń",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Strefy tętna",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) { DonutChart(hr1.zones, Modifier.fillMaxSize(0.8f)) }
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) { DonutChart(hr2.zones, Modifier.fillMaxSize(0.8f)) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        hr1.zones.indices.reversed().forEach { i ->
                            CompareZoneRow(hr1.zones[i], hr2.zones[i], MaterialTheme.colorScheme.onSurface)
                            if (i > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun CompareZoneRow(stat1: ZoneStat, stat2: ZoneStat, textColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(formatSeconds(stat1.durationSeconds), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = textColor)
            Text("%.1f%%".format(Locale.US, stat1.percentage), style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.7f))
        }
        Column(modifier = Modifier.width(120.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(stat1.zone.color))
            Text(stat1.zone.displayName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text("${stat1.minBpm}-${stat1.maxBpm}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(formatSeconds(stat2.durationSeconds), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = textColor)
            Text("%.1f%%".format(Locale.US, stat2.percentage), style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun CompareMaps(
    s1: SessionData, 
    s2: SessionData, 
    isDarkTheme: Boolean, 
    selectedIndex: Int? = null,
    onExpandClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val mapStyle = if (isDarkTheme) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark) else null
    val routesAreClose = areRoutesClose(s1.route, s2.route, AppConstants.MAP_COMPARISON_RADIUS_KM)
    if (routesAreClose) {
        val cameraPositionState = rememberCameraPositionState()
        var isMapLoaded by remember { mutableStateOf(false) }
        val bounds = remember(s1.route, s2.route) {
            // Filtracja punktów (0,0) przed obliczeniami
            val combined = (s1.route + s2.route).filter { it.latitude != 0.0 && it.longitude != 0.0 }
            if (combined.isEmpty()) null
            else {
                // Jawne szukanie punktów ekstremalnych (N, E, W, S) z obu tras
                val n = combined.maxBy { it.latitude }.latitude
                val s = combined.minBy { it.latitude }.latitude
                val e = combined.maxBy { it.longitude }.longitude
                val w = combined.minBy { it.longitude }.longitude
                
                // Minimalna rozpiętość (ok. 100m), aby uniknąć ucinania trasy przy dużym zoomie
                val minSpan = 0.001 
                val finalN = if (n - s < minSpan) n + (minSpan / 2) else n
                val finalS = if (n - s < minSpan) s - (minSpan / 2) else s
                val finalE = if (e - w < minSpan) e + (minSpan / 2) else e
                val finalW = if (e - w < minSpan) w - (minSpan / 2) else w

                LatLngBounds.Builder()
                    .include(LatLng(finalN, finalE))
                    .include(LatLng(finalS, finalW))
                    .build()
            }
        }
        
        // Kamera ustawiana tylko gdy mapa jest w pełni załadowana (onMapLoaded)
        LaunchedEffect(bounds, isMapLoaded) {
            if (isMapLoaded && bounds != null) {
                cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 20))
            }
        }
        
        Box(modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp).clip(RoundedCornerShape(12.dp))) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(), 
                properties = MapProperties(mapStyleOptions = mapStyle), 
                cameraPositionState = cameraPositionState,
                onMapLoaded = { isMapLoaded = true }
            ) {
                Polyline(s1.route, color = Color1, width = 8f); Polyline(s2.route, color = Color2, width = 8f)
                
                if (selectedIndex != null) {
                    val zoom = cameraPositionState.position.zoom
                    val adaptiveRadius = 20.0 * 2.0.pow((15.0 - zoom))
                    if (selectedIndex in s1.route.indices) {
                        Circle(center = s1.route[selectedIndex], radius = adaptiveRadius, fillColor = Color.White.copy(alpha = 0.7f), strokeColor = Color1, strokeWidth = 2f, zIndex = 10f)
                    }
                    if (selectedIndex in s2.route.indices) {
                        Circle(center = s2.route[selectedIndex], radius = adaptiveRadius, fillColor = Color.White.copy(alpha = 0.7f), strokeColor = Color2, strokeWidth = 2f, zIndex = 11f)
                    }
                }
            }
            
            IconButton(
                onClick = onExpandClick,
                modifier = Modifier.padding(8.dp).align(Alignment.TopEnd).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(Icons.Default.Fullscreen, contentDescription = "Powiększ mapę")
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MapSmall(s1.route, Color1, mapStyle, Modifier.weight(1f), selectedIndex, onExpandClick)
            MapSmall(s2.route, Color2, mapStyle, Modifier.weight(1f), selectedIndex, onExpandClick)
        }
    }
}

@Composable
fun FullScreenCompareMap(
    s1: SessionData,
    s2: SessionData,
    isDarkTheme: Boolean,
    selectedIndex: Int?,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val mapStyle = if (isDarkTheme) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark) else null
    val cameraPositionState = rememberCameraPositionState()
    var isMapLoaded by remember { mutableStateOf(false) }

    val bounds = remember(s1.route, s2.route) {
        val combined = (s1.route + s2.route).filter { it.latitude != 0.0 && it.longitude != 0.0 }
        if (combined.isEmpty()) null
        else {
            val n = combined.maxBy { it.latitude }.latitude
            val s = combined.minBy { it.latitude }.latitude
            val e = combined.maxBy { it.longitude }.longitude
            val w = combined.minBy { it.longitude }.longitude
            
            val minSpan = 0.001 
            val finalN = if (n - s < minSpan) n + (minSpan / 2) else n
            val finalS = if (n - s < minSpan) s - (minSpan / 2) else s
            val finalE = if (e - w < minSpan) e + (minSpan / 2) else e
            val finalW = if (e - w < minSpan) w - (minSpan / 2) else w

            LatLngBounds.Builder()
                .include(LatLng(finalN, finalE))
                .include(LatLng(finalS, finalW))
                .build()
        }
    }

    LaunchedEffect(bounds, isMapLoaded) {
        if (isMapLoaded && bounds != null) {
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(mapStyleOptions = mapStyle),
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true }
        ) {
            Polyline(s1.route, color = Color1, width = 8f)
            Polyline(s2.route, color = Color2, width = 8f)
            
            if (selectedIndex != null) {
                val zoom = cameraPositionState.position.zoom
                val adaptiveRadius = 20.0 * 2.0.pow((15.0 - zoom))
                if (selectedIndex in s1.route.indices) {
                    Circle(center = s1.route[selectedIndex], radius = adaptiveRadius, fillColor = Color.White.copy(alpha = 0.7f), strokeColor = Color1, strokeWidth = 2f, zIndex = 10f)
                }
                if (selectedIndex in s2.route.indices) {
                    Circle(center = s2.route[selectedIndex], radius = adaptiveRadius, fillColor = Color.White.copy(alpha = 0.7f), strokeColor = Color2, strokeWidth = 2f, zIndex = 11f)
                }
            }
        }
        
        IconButton(
            onClick = onClose,
            modifier = Modifier.padding(top = 50.dp, end = 16.dp).align(Alignment.TopEnd).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
        ) {
            Icon(Icons.Default.FullscreenExit, contentDescription = "Zmniejsz mapę")
        }
    }
}

@Composable
fun MapSmall(
    route: List<LatLng>, 
    color: Color, 
    style: MapStyleOptions?, 
    modifier: Modifier, 
    selectedIndex: Int? = null,
    onExpandClick: () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState()
    var isMapLoaded by remember { mutableStateOf(false) }
    val bounds = remember(route) {
        val filtered = route.filter { it.latitude != 0.0 && it.longitude != 0.0 }
        if (filtered.isEmpty()) null
        else {
            val n = filtered.maxBy { it.latitude }.latitude
            val s = filtered.minBy { it.latitude }.latitude
            val e = filtered.maxBy { it.longitude }.longitude
            val w = filtered.minBy { it.longitude }.longitude
            
            val minSpan = 0.001
            val finalN = if (n - s < minSpan) n + (minSpan / 2) else n
            val finalS = if (n - s < minSpan) s - (minSpan / 2) else s
            val finalE = if (e - w < minSpan) e + (minSpan / 2) else e
            val finalW = if (e - w < minSpan) w - (minSpan / 2) else w

            LatLngBounds.Builder()
                .include(LatLng(finalN, finalE))
                .include(LatLng(finalS, finalW))
                .build()
        }
    }

    LaunchedEffect(bounds, isMapLoaded) {
        if (isMapLoaded && bounds != null) {
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 20))
        }
    }

    Box(modifier = modifier.clip(RoundedCornerShape(8.dp))) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(), 
            properties = MapProperties(mapStyleOptions = style), 
            uiSettings = MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false), 
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true }
        ) {
            Polyline(route, color = color, width = 6f) 
            if (selectedIndex != null && selectedIndex in route.indices) {
                val zoom = cameraPositionState.position.zoom
                val adaptiveRadius = 20.0 * 2.0.pow((15.0 - zoom))
                Circle(center = route[selectedIndex], radius = adaptiveRadius, fillColor = Color.White.copy(alpha = 0.7f), strokeColor = color, strokeWidth = 2f, zIndex = 10f)
            }
        }
        
        IconButton(
            onClick = onExpandClick,
            modifier = Modifier.padding(4.dp).size(32.dp).align(Alignment.TopEnd).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
        ) {
            Icon(Icons.Default.Fullscreen, contentDescription = "Powiększ mapę", modifier = Modifier.size(20.dp))
        }
    }
}

private fun areRoutesClose(route1: List<LatLng>, route2: List<LatLng>, radiusKm: Double): Boolean {
    if (route1.isEmpty() || route2.isEmpty()) return false
    val p1 = route1[0]; val p2 = route2[0]
    val results = FloatArray(1)
    android.location.Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
    return results[0] <= radiusKm * 1000 * 5 // Uproszczone: sprawdzamy tylko start (lub środek)
}

private fun formatDistance(distanceMeters: Double): String = if (distanceMeters >= 1000) "%.2f km".format(Locale.US, distanceMeters / 1000.0) else "%.0f m".format(Locale.US, distanceMeters)
private fun formatPace(paceDecimal: Double): String {
    if (paceDecimal <= 0 || paceDecimal > 120) return "--:--"
    return "%02d:%02d/km".format(paceDecimal.toInt(), ((paceDecimal - paceDecimal.toInt()) * 60).toInt())
}
private fun formatSeconds(seconds: Long): String {
    val h = seconds / 3600; val m = (seconds % 3600) / 60; val s = seconds % 60
    return if (h > 0) "%d:%02d:%02d".format(Locale.US, h, m, s) else "%02d:%02d".format(Locale.US, m, s)
}
private fun getUnitForWidget(id: String): String = when(id) { "bpm", "srednie_bpm" -> "bpm"; "kalorie_min", "kalorie_suma" -> "kcal"; "kroki_min" -> "kroków/min"; "odl_kroki", "gps_dystans" -> "m"; "predkosc", "predkosc_kroki" -> "km/h"; "wysokosc", "przewyzszenia_gora", "przewyzszenia_dol" -> "m"; "pressure" -> "hPa"; else -> "" }
