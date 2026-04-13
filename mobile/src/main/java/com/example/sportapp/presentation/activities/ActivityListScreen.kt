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
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.R
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
    val texts = LocalMobileTexts.current
    val activities by viewModel.activities.collectAsState()
    val activityTypes by viewModel.activityTypes.collectAsState()
    val definitions by viewModel.definitions.collectAsState()
    val selectedTypes by viewModel.selectedTypes.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val sortColumn by viewModel.sortColumn.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val importState by viewModel.importState.collectAsState()
    
    var showTypeMenu by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showImportTypeDialog by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

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
            title = { Text(texts.ACTIVITY_IMPORT_SELECT_TYPE) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(texts.ACTIVITY_IMPORT_SELECT_DESC)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(definitions) { definition ->
                            ListItem(
                                headlineContent = { Text(definition.name) },
                                leadingContent = { 
                                    Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = null)
                                },
                                modifier = Modifier.clickable {
                                    viewModel.importGpx(showImportTypeDialog!!, definition)
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
                    Text(texts.SETTINGS_CANCEL)
                }
            }
        )
    }

    // Import Success/Error/Warning logic
    LaunchedEffect(importState) {
        when (importState) {
            is ImportState.Success -> {
                snackbarHostState.showSnackbar((importState as ImportState.Success).message)
                viewModel.resetImportState()
            }
            is ImportState.Error -> {
                snackbarHostState.showSnackbar((importState as ImportState.Error).message)
                viewModel.resetImportState()
            }
            else -> {}
        }
    }

    if (importState is ImportState.Warning) {
        val state = importState as ImportState.Warning
        AlertDialog(
            onDismissRequest = { viewModel.resetImportState() },
            title = { Text(texts.ACTIVITY_IMPORT_WARNING) },
            text = {
                Column {
                    state.warnings.forEach { Text("• $it") }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(texts.ACTIVITY_IMPORT_CONTINUE)
                }
            },
            confirmButton = {
                TextButton(onClick = { state.onConfirm(); viewModel.resetImportState() }) {
                    Text(texts.ACTIVITY_IMPORT_CONTINUE)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetImportState() }) {
                    Text(texts.SETTINGS_CANCEL)
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
                    Text(texts.ACTIVITY_IMPORT_PROGRESS)
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
            context.startActivity(Intent.createChooser(intent, texts.ACTIVITY_SHARE_TITLE))
            viewModel.resetExportState()
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(texts.ACTIVITY_CONFIRM_DELETE_TITLE) },
            text = { Text(texts.ACTIVITY_DELETE_CONFIRM.replace("zaznaczone aktywności", "zaznaczone aktywności (${selectedIds.size})")) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSelectedActivities()
                    showDeleteConfirmation = false
                }) {
                    Text(texts.ACTIVITY_DELETE, color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(texts.SETTINGS_CANCEL)
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

    // Export Error Snackback/Dialog
    if (exportState is ExportState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.resetExportState() },
            title = { Text(texts.ACTIVITY_EXPORT_ERROR) },
            text = { Text((exportState as ExportState.Error).message) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetExportState() }) {
                    Text(texts.ACTIVITY_OK)
                }
            }
        )
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
                        Text(texts.ACTIVITY_LIST_TITLE)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.SETTINGS_CLOSE)
                    }
                },
                actions = {
                    if (selectedIds.isEmpty()) {
                        IconButton(onClick = { gpxLauncher.launch("application/gpx+xml") }) {
                            Icon(Icons.Default.UploadFile, contentDescription = texts.ACTIVITY_IMPORT_GPX)
                        }
                    } else {
                        IconButton(onClick = { viewModel.exportSelected() }) {
                            Icon(Icons.Default.Share, contentDescription = texts.ACTIVITY_EXPORT_GPX)
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = texts.ACTIVITY_CHART_SETTINGS)
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
                    Text(texts.ACTIVITY_FILTERS, style = MaterialTheme.typography.titleSmall)
                    
                    Box {
                        OutlinedButton(
                            onClick = { showTypeMenu = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = when {
                                    selectedTypes.isEmpty() -> texts.ACTIVITY_ALL_TYPES
                                    selectedTypes.size == 1 -> selectedTypes.first()
                                    else -> "${texts.ACTIVITY_FILTERS}: ${selectedTypes.size}"
                                }
                            )
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                        DropdownMenu(
                            expanded = showTypeMenu,
                            onDismissRequest = { showTypeMenu = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(checked = selectedTypes.isEmpty(), onCheckedChange = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(texts.ACTIVITY_ALL)
                                    }
                                },
                                onClick = { viewModel.clearTypeSelection() }
                            )
                            activityTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { 
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = selectedTypes.contains(type), onCheckedChange = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(type)
                                        }
                                    },
                                    onClick = { viewModel.toggleTypeSelection(type) }
                                )
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
                            Text(startDate?.let { sdf.format(it) } ?: texts.ACTIVITY_FROM)
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
                            Text(endDate?.let { sdf.format(it) } ?: texts.ACTIVITY_TO)
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
                                HeaderCell(texts.ACTIVITY_TYPE, 100.dp, SortColumn.TYPE, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.TYPE) }
                                HeaderCell(texts.ACTIVITY_DATE, 150.dp, SortColumn.DATE, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.DATE) }
                                HeaderCell(texts.ACTIVITY_DURATION, 100.dp, SortColumn.DURATION, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.DURATION) }
                                HeaderCell(texts.ACTIVITY_CALORIES, 80.dp, SortColumn.CALORIES, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.CALORIES) }
                                HeaderCell(texts.ACTIVITY_DISTANCE_GPS, 120.dp, SortColumn.DISTANCE_GPS, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.DISTANCE_GPS) }
                                HeaderCell(texts.ACTIVITY_DISTANCE_STEPS, 120.dp, SortColumn.DISTANCE_STEPS, sortColumn, sortOrder) { viewModel.onSortChanged(SortColumn.DISTANCE_STEPS) }
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
                            Text(texts.ACTIVITY_DETAIL, fontSize = 12.sp)
                        }
                        Button(
                            onClick = { onNavigateToTrim(selectedIds.first()) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(texts.ACTIVITY_EDIT, fontSize = 12.sp)
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
                            Text(texts.ACTIVITY_COMPARE, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                    ) {
                        Text(texts.ACTIVITY_DELETE, fontSize = 12.sp)
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
