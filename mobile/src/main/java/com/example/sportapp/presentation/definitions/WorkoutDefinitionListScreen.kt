package com.example.sportapp.presentation.definitions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.data.model.WorkoutDefinition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDefinitionListScreen(
    viewModel: WorkoutDefinitionViewModel,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val definitions by viewModel.definitions.collectAsState()
    var definitionToDelete by remember { mutableStateOf<WorkoutDefinition?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.DEF_LIST_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = texts.SETTINGS_CLOSE)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToEdit(0L) }) {
                Icon(Icons.Default.Add, contentDescription = texts.DEF_ADD)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(definitions) { index, definition ->
                WorkoutDefinitionItem(
                    definition = definition,
                    onEdit = { onNavigateToEdit(definition.id) },
                    onDelete = { if (!definition.isDefault) definitionToDelete = definition },
                    onMoveUp = if (index > 0) { { viewModel.moveUp(definition) } } else null,
                    onMoveDown = if (index < definitions.size - 1) { { viewModel.moveDown(definition) } } else null
                )
            }
        }

        if (definitionToDelete != null) {
            AlertDialog(
                onDismissRequest = { definitionToDelete = null },
                title = { Text(texts.DEF_DELETE_TITLE) },
                text = { Text(texts.defDeleteConfirm(definitionToDelete?.name ?: "")) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            definitionToDelete?.let { viewModel.deleteDefinition(it) }
                            definitionToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text(texts.DEF_DELETE)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { definitionToDelete = null }) {
                        Text(texts.SETTINGS_CANCEL)
                    }
                }
            )
        }
    }
}

@Composable
fun WorkoutDefinitionItem(
    definition: WorkoutDefinition,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    val texts = LocalMobileTexts.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getIconForName(definition.iconName), 
                    contentDescription = null, 
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = definition.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    IconButton(onClick = onMoveUp ?: {}, enabled = onMoveUp != null, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = texts.DEF_MOVE_UP)
                    }
                    IconButton(onClick = onMoveDown ?: {}, enabled = onMoveDown != null, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = texts.DEF_MOVE_DOWN)
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = texts.DEF_EDIT)
                }
                if (!definition.isDefault) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = texts.DEF_DELETE, tint = Color.Red)
                    }
                } else {
                    // Spacer to keep layout consistent when delete button is missing
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }
    }
}
