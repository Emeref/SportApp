package com.example.sportapp.presentation.activities

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(
    viewModel: ActivityListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val activityTypes by viewModel.activityTypes.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    
    var selectedActivityId by remember { mutableStateOf<String?>(null) }
    var showTypeMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

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
                        Text("Lista aktywności")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Ustawienia wykresów")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
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
                                    val date = Calendar.getInstance().apply { 
                                        set(y, m, d, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }.time
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
                                    val date = Calendar.getInstance().apply { 
                                        set(y, m, d, 23, 59, 59)
                                        set(Calendar.MILLISECOND, 999)
                                    }.time
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

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Tabela aktywności
            Box(modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState())) {
                Column {
                    // Header
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("", modifier = Modifier.width(48.dp))
                        Text("Typ", modifier = Modifier.width(100.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Data", modifier = Modifier.width(150.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Czas", modifier = Modifier.width(100.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Kalorie", modifier = Modifier.width(80.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Dystans (GPS)", modifier = Modifier.width(120.dp), style = MaterialTheme.typography.titleSmall)
                        Text("Dystans (Kroki)", modifier = Modifier.width(120.dp), style = MaterialTheme.typography.titleSmall)
                    }
                    HorizontalDivider()
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(activities) { activity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedActivityId = activity.id }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedActivityId == activity.id,
                                    onClick = { selectedActivityId = activity.id },
                                    modifier = Modifier.width(48.dp)
                                )
                                Text(activity.type, modifier = Modifier.width(100.dp))
                                Text(activity.date, modifier = Modifier.width(150.dp))
                                Text(activity.duration, modifier = Modifier.width(100.dp))
                                Text(activity.calories, modifier = Modifier.width(80.dp))
                                Text(activity.distanceGps, modifier = Modifier.width(120.dp))
                                Text(activity.distanceSteps, modifier = Modifier.width(120.dp))
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedActivityId != null) {
                Button(
                    onClick = { onNavigateToDetail(selectedActivityId!!) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pokaż szczegóły")
                }
            }
        }
    }
}
