package com.example.sportapp.presentation.stats

import android.app.DatePickerDialog
import android.text.Layout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sportapp.R
import com.example.sportapp.presentation.home.WidgetFactory
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallStatsScreen(
    viewModel: OverallStatsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOptions: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val widgets by viewModel.widgets.collectAsState()
    val activityTypes by viewModel.activityTypes.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    
    var showTypeMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statystyki ogólne") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToOptions) {
                        Icon(Icons.Default.Settings, contentDescription = "Opcje")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Filtry
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Filtry", style = MaterialTheme.typography.titleSmall)
                    
                    Box {
                        OutlinedButton(
                            onClick = { showTypeMenu = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedType ?: "Wszystkie typy")
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        DropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                            DropdownMenuItem(text = { Text("Wszystkie") }, onClick = { viewModel.onTypeSelected(null); showTypeMenu = false })
                            activityTypes.forEach { type ->
                                DropdownMenuItem(text = { Text(type) }, onClick = { viewModel.onTypeSelected(type); showTypeMenu = false })
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context, { _, y, m, d ->
                                    val date = Calendar.getInstance().apply { set(y, m, d, 0, 0, 0) }.time
                                    viewModel.onDateRangeSelected(date, endDate)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(startDate?.let { sdf.format(it) } ?: "Od")
                        }

                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context, { _, y, m, d ->
                                    val date = Calendar.getInstance().apply { set(y, m, d, 23, 59, 59) }.time
                                    viewModel.onDateRangeSelected(startDate, date)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(endDate?.let { sdf.format(it) } ?: "Do")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Widgety
            val activeWidgets = widgets.filter { it.isEnabled }
            if (activeWidgets.isEmpty()) {
                Text("Brak aktywnych widgetów. Włącz je w opcjach.")
            } else {
                activeWidgets.chunked(2).forEach { rowItems ->
                    if (rowItems.size == 2) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            WidgetFactory(rowItems[0].id, stats, Modifier.weight(1f))
                            WidgetFactory(rowItems[1].id, stats, Modifier.weight(1f))
                        }
                    } else {
                        WidgetFactory(rowItems[0].id, stats, Modifier.fillMaxWidth())
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Wykresy
            @Suppress("UNCHECKED_CAST")
            val rawData = stats["raw_data"] as? List<Map<String, String>>
            if (activeWidgets.isNotEmpty()) {
                if (!rawData.isNullOrEmpty()) {
                    Text(
                        text = "Wykresy trendów",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    
                    activeWidgets.forEach { widget ->
                        if (widget.id != "count") {
                            val producer = viewModel.chartProducers[widget.id]
                            if (producer != null) {
                                val maxVal = viewModel.getMaxValueForWidget(widget.id)
                                val unit = when(widget.id) {
                                    "distanceGps" -> if (maxVal > 6000) "km" else "m"
                                    "distanceSteps" -> if (maxVal > 6000) "km" else "m"
                                    "calories" -> "kcal"
                                    "steps" -> "kroków"
                                    "ascent", "descent" -> "m"
                                    else -> ""
                                }
                                val title = when(widget.id) {
                                    "distanceGps" -> if (maxVal > 6000) "Dystans (GPS) w kilometrach" else "Dystans (GPS) w metrach"
                                    "distanceSteps" -> if (maxVal > 6000) "Dystans (kroki) w kilometrach" else "Dystans (kroki) w metrach"
                                    "steps" -> "Kroki"
                                    else -> widget.label
                                }
                                ChartSection(title = title, producer = producer, rawData = rawData, unit = unit)
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                } else if (stats.containsKey("raw_data")) {
                    Text(
                        text = "Brak danych do wyświetlenia wykresów.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_emeref),
                contentDescription = "Logo Emeref",
                modifier = Modifier.height(40.dp).padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ChartSection(title: String, producer: ChartEntryModelProducer, rawData: List<Map<String, String>>, unit: String) {
    val axisValuesOverrider = remember {
        object : AxisValuesOverrider<ChartEntryModel> {
            override fun getMaxY(model: ChartEntryModel): Float {
                val max = model.maxY
                if (max.isNaN() || max <= 0f) return 8f
                val ceiling = ceil(max.toDouble()).toInt()
                val remainder = ceiling % 8
                val finalMax = if (remainder == 0) ceiling else ceiling + (8 - remainder)
                return finalMax.toFloat()
            }

            override fun getMinY(model: ChartEntryModel): Float = 0f
            
            override fun getMinX(model: ChartEntryModel): Float = model.minX
            override fun getMaxX(model: ChartEntryModel): Float = model.maxX
        }
    }

    val marker = rememberMarkerCustom(rawData, unit)

    Column {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        
        val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' }
        val formatter = DecimalFormat("#,###.#", symbols)
        val orangeColor = Color(0xFFFF9800)

        Chart(
            chart = lineChart(
                lines = listOf(
                    lineSpec(
                        lineColor = orangeColor,
                        lineBackgroundShader = DynamicShaders.fromBrush(
                            Brush.verticalGradient(
                                colors = listOf(orangeColor.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                    )
                ),
                axisValuesOverrider = axisValuesOverrider,
            ),
            chartModelProducer = producer,
            marker = marker,
            startAxis = rememberStartAxis(
                label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
                valueFormatter = { value, _ -> formatter.format(value.toLong()) },
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 9),
                guideline = null
            ),
            bottomAxis = rememberBottomAxis(
                label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
                valueFormatter = { value, _ -> 
                    if (value.isNaN()) "" else (value.toInt() + 1).toString() 
                },
                guideline = null
            ),
            chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
            modifier = Modifier.fillMaxWidth().height(320.dp)
        )
    }
}

@Composable
fun rememberMarkerCustom(rawData: List<Map<String, String>>, unit: String): Marker {
    val labelBackgroundColor = MaterialTheme.colorScheme.surface
    val labelTextColor = MaterialTheme.colorScheme.onSurface
    
    val symbols = DecimalFormatSymbols(Locale.US).apply { groupingSeparator = ' ' }
    val formatter = DecimalFormat("#,###.#", symbols)
    val inputSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val outputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val label = textComponent(
        color = labelTextColor,
        background = shapeComponent(
            shape = MarkerCorneredShape(Corner.FullyRounded),
            color = labelBackgroundColor,
            strokeColor = Color.Green,
            strokeWidth = 2.dp
        ),
        padding = MutableDimensions(horizontalDp = 12f, verticalDp = 8f),
        textAlignment = Layout.Alignment.ALIGN_CENTER,
        lineCount = 3 // WYMUSZENIE 3 LINII
    )
    val indicator = shapeComponent(shape = Shapes.pillShape, color = Color.Green)
    val guideline = lineComponent(
        color = Color.Green.copy(alpha = 0.5f),
        thickness = 2.dp
    )
    return remember(label, indicator, guideline, rawData, unit) {
        object : MarkerComponent(label, indicator, guideline) {
            override fun getInsets(
                context: com.patrykandpatrick.vico.core.context.MeasureContext,
                outInsets: com.patrykandpatrick.vico.core.chart.insets.Insets,
                horizontalDimensions: com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
            ) {
                with(context) {
                    // Rezerwujemy stałe miejsce na 3 linie tekstu nad wykresem
                    outInsets.top = label.getHeight(context, text = "Line 1\nLine 2\nLine 3") + (density * 32f)
                }
            }
        }.apply {
            labelFormatter = MarkerLabelFormatter { markedEntries, _ ->
                val entry = markedEntries.firstOrNull() ?: return@MarkerLabelFormatter ""
                val index = entry.entry.x.toInt()

                if (index in rawData.indices) {
                    val data = rawData[index]
                    val activityName = data["nazwa aktywnosci"] ?: "Aktywność"
                    val rawDate = data["data"] ?: ""
                    val formattedDate = try {
                        inputSdf.parse(rawDate)?.let { outputSdf.format(it) } ?: rawDate
                    } catch (e: Exception) {
                        rawDate
                    }

                    val value = formatter.format(entry.entry.y)
                    "$activityName\n$formattedDate\n$value $unit"
                } else {
                    ""
                }
            }
        }
    }
}
