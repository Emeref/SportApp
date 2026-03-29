package com.example.sportapp.presentation.stats

import android.app.DatePickerDialog
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportapp.R
import com.example.sportapp.core.i18n.LocalAppStrings
import com.example.sportapp.presentation.home.WidgetFactory
import com.example.sportapp.presentation.settings.WidgetItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OverallStatsScreen(
    viewModel: OverallStatsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOptions: () -> Unit
) {
    val strings = LocalAppStrings.current
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val widgets by viewModel.getWidgets(strings).collectAsStateWithLifecycle(initialValue = emptyList())
    val charts by viewModel.getCharts(strings).collectAsStateWithLifecycle(initialValue = emptyList())
    val activityTypes by viewModel.activityTypes.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()
    val chartMaxValues by viewModel.chartMaxValues.collectAsStateWithLifecycle()

    OverallStatsContent(
        stats = stats,
        widgets = widgets,
        charts = charts,
        activityTypes = activityTypes,
        selectedType = selectedType,
        startDate = startDate,
        endDate = endDate,
        chartProducers = viewModel.chartProducers,
        chartMaxValues = chartMaxValues,
        onTypeSelected = { viewModel.onTypeSelected(it) },
        onDateRangeSelected = { start, end -> viewModel.onDateRangeSelected(start, end) },
        onNavigateBack = onNavigateBack,
        onNavigateToOptions = onNavigateToOptions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallStatsContent(
    stats: Map<String, Any>,
    widgets: List<WidgetItem>,
    charts: List<WidgetItem>,
    activityTypes: List<String>,
    selectedType: String?,
    startDate: Date?,
    endDate: Date?,
    chartProducers: Map<String, ChartEntryModelProducer>,
    chartMaxValues: Map<String, Double>,
    onTypeSelected: (String?) -> Unit,
    onDateRangeSelected: (Date?, Date?) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToOptions: () -> Unit
) {
    var showTypeMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val locale = remember(strings.localeCode) { Locale(strings.localeCode) }
    val sdf = remember(locale) { SimpleDateFormat("dd.MM.yyyy", locale) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_apki_biale),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text(strings.generalStats)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToOptions) {
                        Icon(Icons.Default.Settings, contentDescription = strings.options)
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
                    Text(strings.filters, style = MaterialTheme.typography.titleSmall)
                    
                    Box {
                        OutlinedButton(
                            onClick = { showTypeMenu = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedType ?: strings.allTypes)
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        DropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                            DropdownMenuItem(text = { Text(strings.allTypes) }, onClick = { onTypeSelected(null); showTypeMenu = false })
                            activityTypes.forEach { type ->
                                DropdownMenuItem(text = { Text(type) }, onClick = { onTypeSelected(type); showTypeMenu = false })
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance(locale)
                                DatePickerDialog(context, R.style.CustomDatePickerDialog, { _, y, m, d ->
                                    val date = Calendar.getInstance(locale).apply { 
                                        set(y, m, d, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }.time
                                    onDateRangeSelected(date, endDate)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(startDate?.let { sdf.format(it) } ?: strings.from)
                        }

                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance(locale)
                                DatePickerDialog(context, R.style.CustomDatePickerDialog, { _, y, m, d ->
                                    val date = Calendar.getInstance(locale).apply { 
                                        set(y, m, d, 23, 59, 59)
                                        set(Calendar.MILLISECOND, 999)
                                    }.time
                                    onDateRangeSelected(startDate, date)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(endDate?.let { sdf.format(it) } ?: strings.to)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Widgety
            val activeWidgets = widgets.filter { it.isEnabled }
            if (activeWidgets.isEmpty()) {
                Text(strings.noWidgetsSelected)
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
            val activeCharts = charts.filter { it.isEnabled }
            @Suppress("UNCHECKED_CAST")
            val rawData = stats["raw_data"] as? List<Any>
            if (activeCharts.isNotEmpty()) {
                if (!rawData.isNullOrEmpty()) {
                    Text(
                        text = strings.charts,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    
                    activeCharts.forEach { chart ->
                        val producer = chartProducers[chart.id]
                        if (producer != null) {
                            val maxVal = chartMaxValues[chart.id] ?: 0.0
                            val unit = when(chart.id) {
                                "distanceGps" -> if (maxVal > 6000) strings.kmUnit else strings.metersUnit
                                "distanceSteps" -> if (maxVal > 6000) strings.kmUnit else strings.metersUnit
                                "calories" -> strings.kcalUnit
                                "steps" -> strings.steps.lowercase()
                                "avg_cadence" -> strings.cadenceUnit
                                "ascent", "descent" -> strings.metersUnit
                                "maxPressure", "minPressure" -> strings.hpaUnit
                                "bestPace1km" -> strings.paceUnit
                                else -> ""
                            }
                            val title = when(chart.id) {
                                "distanceGps" -> if (maxVal > 6000) "${strings.distanceGpsLabel} [${strings.kmUnit}]" else "${strings.distanceGpsLabel} [${strings.metersUnit}]"
                                "distanceSteps" -> if (maxVal > 6000) "${strings.distanceStepsLabel} [${strings.kmUnit}]" else "${strings.distanceStepsLabel} [${strings.metersUnit}]"
                                "steps" -> strings.steps
                                else -> chart.label
                            }
                            CommonChartSection(
                                title = title,
                                producer = producer,
                                unit = unit,
                                isScrollEnabled = true,
                                isZoomEnabled = true,
                                isTimestampX = true
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                } else if (stats.containsKey("raw_data")) {
                    Text(
                        text = strings.noData,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_apki_biale),
                contentDescription = "Logo SportApp",
                modifier = Modifier.height(40.dp).padding(vertical = 8.dp)
            )
        }
    }
}
