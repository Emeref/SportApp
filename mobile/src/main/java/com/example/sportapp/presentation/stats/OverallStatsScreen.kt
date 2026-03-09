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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sportapp.R
import com.example.sportapp.presentation.home.WidgetFactory
import java.text.SimpleDateFormat
import java.util.*

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

            // Filtry
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

            // Widgety
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

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_emeref),
                contentDescription = "Logo Emeref",
                modifier = Modifier.height(40.dp).padding(vertical = 8.dp)
            )
        }
    }
}
