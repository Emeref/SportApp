package com.example.sportapp.presentation.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.sportapp.R
import com.example.sportapp.core.i18n.LocalAppStrings
import com.example.sportapp.data.model.WorkoutDefinition
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(
    viewModel: ActivityListViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToTrim: (String) -> Unit,
    onNavigateToCompare: (String, String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()
    val activityTypes by viewModel.activityTypes.collectAsState()
    val definitions by viewModel.definitions.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val sortColumn by viewModel.sortColumn.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val importState by viewModel.importState.collectAsState()
    
    val strings = LocalAppStrings.current
    var showTypeMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showImportTypeDialog by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    val locale = remember(strings.localeCode) { Locale(strings.localeCode) }
    val sdf = remember(locale) { SimpleDateFormat("dd.MM.yyyy", locale) }

    val horizontalScrollState = rememberScrollState()
    val lazyListState = rememberLazyListState()

    val selectedActivities = activities.filter { it.id in selectedIds }
    val canCompare = selectedActivities.size == 2 && 
                   selectedActivities[0].type == selectedActivities[1].type

    val visibleIds = remember(activities) { activities.map { it.id }.toSet() }
    val visibleSelectedCount = remember(selectedIds, visibleIds) { selectedIds.count { it in visibleIds } }
    
    val triState = when {
        activities.isEmpty() -> ToggleableState.Off
        visibleSelectedCount == 0 -> ToggleableState.Off
        visibleSelectedCount == activities.size -> ToggleableState.On
        else -> ToggleableState.Indeterminate
    }

    val gpxLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { showImportTypeDialog = it }
    }

    // Import Type Selection Dialog
    if (showImportTypeDialog != null) {
        AlertDialog(
            onDismissRequest = { showImportTypeDialog = null },
            title = { Text(strings.chooseActivityType) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(strings.chooseActivityType)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(definitions) { definition ->
                            ListItem(
                                headlineContent = { Text(definition.name) },
                                leadingContent = { 
                                    Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = null)
                                },
                                modifier = Modifier.clickable {
                                    viewModel.importGpx(showImportTypeDialog!!, definition, strings)
                                    showImportTypeDialog = null
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImportTypeDialog = null }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Import Success/Error/Warning logic
    LaunchedEffect(importState) {
        when (importState) {
            is ImportState.Success -> {
                snackbarHostState.showSnackbar((importState as ImportState.Success).message)
                viewModel.refreshActivityTypes() // Added to ensure UI updates after import
            }
            is ImportState.Error -> {
                snackbarHostState.showSnackbar((importState as ImportState.Error).message)
            }
            else -> {}
        }
    }

    if (importState is ImportState.Warning) {
        val state = importState as ImportState.Warning
        AlertDialog(
            onDismissRequest = { /* Handle cancellation in VM if needed */ },
            title = { Text(strings.warning) },
            text = {
                Column {
                    state.warnings.forEach { Text("• $it") }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${strings.continueLabel}?")
                }
            },
            confirmButton = {
                TextButton(onClick = { state.onConfirm() }) {
                    Text(strings.continueLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = { /* Reset state if needed */ }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    if (importState is ImportState.Loading) {
        Dialog(onDismissRequest = {}) {
            Card {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(strings.importingData)
                }
            }
        }
    }

    // Handle Export Success
    LaunchedEffect(exportState) {
        if (exportState is ExportState.Success) {
            val state = exportState as ExportState.Success
            val intent = if (state.isZip) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/zip"
                    putExtra(Intent.EXTRA_STREAM, state.uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/gpx+xml"
                    putExtra(Intent.EXTRA_STREAM, state.uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }
            context.startActivity(Intent.createChooser(intent, strings.exportGpx))
            viewModel.resetExportState()
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(strings.delete) },
            text = { Text(strings.confirmDelete) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSelectedActivities()
                    showDeleteConfirmation = false
                }) {
                    Text(strings.delete, color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // Export Progress Dialog
    if (exportState is ExportState.Exporting) {
        val state = exportState as ExportState.Exporting
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(progress = { state.progress })
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(state.message, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

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
                        Text(strings.activityList)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
                actions = {
                    if (selectedIds.isEmpty()) {
                        IconButton(onClick = { gpxLauncher.launch("application/gpx+xml") }) {
                            Icon(Icons.Default.UploadFile, contentDescription = strings.importGpx)
                        }
                    } else {
                        IconButton(onClick = { viewModel.exportSelected(strings) }) {
                            Icon(Icons.Default.Share, contentDescription = strings.exportGpx)
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = strings.options)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                            DropdownMenuItem(text = { Text(strings.allTypes) }, onClick = { viewModel.onTypeSelected(null, strings.allTypes); showTypeMenu = false })
                            activityTypes.forEach { type ->
                                DropdownMenuItem(text = { Text(type) }, onClick = { viewModel.onTypeSelected(type, strings.allTypes); showTypeMenu = false })
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
                                    viewModel.onDateRangeSelected(date, endDate)
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
                                    viewModel.onDateRangeSelected(startDate, date)
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

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Tabela aktywności
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Box(modifier = Modifier.weight(1f).horizontalScroll(horizontalScrollState)) {
                        Column {
                            // Header
                            Row(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TriStateCheckbox(
                                    state = triState,
                                    onClick = { viewModel.toggleAllVisibleSelection() },
                                    modifier = Modifier.width(48.dp)
                                )
                                HeaderCell(strings.theme, 100.dp, SortColumn.TYPE, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.TYPE) }
                                HeaderCell(strings.today, 150.dp, SortColumn.DATE, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.DATE) }
                                HeaderCell(strings.activeTime, 100.dp, SortColumn.DURATION, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.DURATION) }
                                HeaderCell(strings.calories, 80.dp, SortColumn.CALORIES, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.CALORIES) }
                                HeaderCell("${strings.distance} (GPS)", 120.dp, SortColumn.DISTANCE_GPS, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.DISTANCE_GPS) }
                                HeaderCell("${strings.distance} (${strings.steps})", 120.dp, SortColumn.DISTANCE_STEPS, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.DISTANCE_STEPS) }
                            }
                            
                            LazyColumn(modifier = Modifier.fillMaxWidth(), state = lazyListState) {
                                items(activities) { activity ->
                                    val isSelected = activity.id in selectedIds
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.toggleSelection(activity.id) }
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { viewModel.toggleSelection(activity.id) },
                                            modifier = Modifier.width(48.dp)
                                        )
                                        DataCell(activity.type, 100.dp)
                                        DataCell(activity.date, 150.dp)
                                        DataCell(activity.duration, 100.dp)
                                        DataCell(activity.calories, 80.dp)
                                        DataCell(activity.distanceGps, 120.dp)
                                        DataCell(activity.distanceSteps, 120.dp)
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                    
                    VerticalScrollbar(
                        lazyListState = lazyListState,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                
                HorizontalScrollbar(
                    scrollState = horizontalScrollState,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedIds.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedIds.size == 1) {
                        Button(
                            onClick = { onNavigateToDetail(selectedIds.first()) },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text(strings.details, fontSize = 12.sp)
                        }
                        Button(
                            onClick = { onNavigateToTrim(selectedIds.first()) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(strings.edit, fontSize = 12.sp)
                        }
                    } else if (selectedIds.size == 2) {
                        Button(
                            onClick = { 
                                val ids = selectedIds.toList()
                                onNavigateToCompare(ids[0], ids[1])
                            },
                            enabled = canCompare,
                            modifier = Modifier.weight(2.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text(strings.compare, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                    ) {
                        Text(strings.delete, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun HeaderCell(
    text: String, 
    width: androidx.compose.ui.unit.Dp,
    column: SortColumn,
    currentSortColumn: SortColumn,
    sortOrder: SortOrder,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(width)
            .clickable { onClick() }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f, fill = false),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (currentSortColumn == column) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        if (currentSortColumn == column) {
            Icon(
                imageVector = if (sortOrder == SortOrder.ASC) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun DataCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        modifier = Modifier.width(width).padding(horizontal = 4.dp),
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 14.sp
    )
}

@Composable
fun HorizontalScrollbar(
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier
) {
    val scrollValue = scrollState.value.toFloat()
    val maxScroll = scrollState.maxValue.toFloat()
    
    if (maxScroll > 0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), shape = CircleShape)
        ) {
            val viewPortRatio = 1f / (1f + maxScroll / 400f)
            val offsetRatio = scrollValue / maxScroll
            val thumbWidthFraction = viewPortRatio.coerceIn(0.1f, 1f)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth(thumbWidthFraction)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .offset(x = (offsetRatio * (1f - thumbWidthFraction) * 300).dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), shape = CircleShape)
            )
        }
    }
}

@Composable
fun VerticalScrollbar(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    val layoutInfo = lazyListState.layoutInfo
    val totalItems = layoutInfo.totalItemsCount
    
    if (totalItems > 0) {
        val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
        val visibleItemsCount = layoutInfo.visibleItemsInfo.size
        
        if (visibleItemsCount < totalItems) {
            Box(
                modifier = modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), shape = CircleShape)
            ) {
                val thumbHeightFraction = (visibleItemsCount.toFloat() / totalItems).coerceIn(0.1f, 1f)
                val thumbOffsetFraction = firstVisibleItemIndex.toFloat() / totalItems
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(thumbHeightFraction)
                        .offset(y = (thumbOffsetFraction * 400).dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), shape = CircleShape)
                )
            }
        }
    }
}
