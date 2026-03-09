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
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.CorneredShape
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.marker.Marker
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
            if (activeWidgets.isNotEmpty()) {
                Text(
                    text = "Wykresy trendów",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                
                activeWidgets.forEach { widget ->
                    if (widget.id != "count") {
                        val producer = viewModel.chartProducers[widget.id]
                        if (producer != null) {
                            ChartSection(title = widget.label, producer = producer)
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
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
fun ChartSection(title: String, producer: ChartEntryModelProducer) {
    val axisValuesOverrider = remember {
        object : AxisValuesOverrider<ChartEntryModel> {
            override fun getMaxY(model: ChartEntryModel): Float {
                val max = model.maxY
                if (max <= 0f) return 8f
                val ceiling = ceil(max.toDouble()).toInt()
                val remainder = ceiling % 8
                val finalMax = if (remainder == 0) ceiling else ceiling + (8 - remainder)
                return finalMax.toFloat()
            }

            override fun getMinY(model: ChartEntryModel): Float = 0f
        }
    }

    val marker = rememberMarkerCustom()

    Column {
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Chart(
            chart = lineChart(
                axisValuesOverrider = axisValuesOverrider
            ),
            chartModelProducer = producer,
            marker = marker,
            startAxis = rememberStartAxis(
                label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
                valueFormatter = { value, _ -> value.toInt().toString() },
                itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 9)
            ),
            bottomAxis = rememberBottomAxis(
                label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
                valueFormatter = { value, _ -> (value.toInt() + 1).toString() }
            ),
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )
    }
}

@Composable
fun rememberMarkerCustom(): Marker {
    val labelBackgroundColor = MaterialTheme.colorScheme.surface
    val labelTextColor = MaterialTheme.colorScheme.onSurface
    val labelBackgroundShape = CorneredShape(Corner.FullyRounded)
    val label = textComponent(
        color = labelTextColor,
        background = shapeComponent(shape = labelBackgroundShape, color = labelBackgroundColor),
        textAlignment = Layout.Alignment.ALIGN_CENTER
    )
    val indicator = shapeComponent(shape = Shapes.pillShape, color = MaterialTheme.colorScheme.primary)
    val guideline = lineComponent(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        thickness = 2.dp
    )
    return remember(label, indicator, guideline) {
        MarkerComponent(
            label = label,
            indicator = indicator,
            guideline = guideline,
        )
    }
}
