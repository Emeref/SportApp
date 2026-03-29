package com.example.sportapp.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.SwipeToDismissValue
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.*
import com.example.sportapp.core.i18n.LocalAppStrings

@Composable
fun WorkoutSummaryScreen(
    title: String,
    summaryData: List<Pair<String, String>>,
    onConfirm: () -> Unit
) {
    val listState = rememberScalingLazyListState()
    val strings = LocalAppStrings.current
    // Używamy foundation.rememberSwipeToDismissBoxState i blokujemy zmianę stanu, aby wyłączyć gest wyjścia
    val swipeState = rememberSwipeToDismissBoxState(
        confirmStateChange = { it != SwipeToDismissValue.Dismissed }
    )
    
    SwipeToDismissBox(
        state = swipeState
    ) { isBackground ->
        if (!isBackground) {
            Scaffold(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                positionIndicator = {
                    PositionIndicator(scalingLazyListState = listState)
                }
            ) {
                ScalingLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = strings.confirm
                            )
                        }
                    }
                    item {
                        Text(
                            text = strings.summary,
                            style = MaterialTheme.typography.caption1,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                    
                    item {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.title2,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(summaryData.size) { index ->
                        val (label, value) = summaryData[index]
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.caption2,
                                color = Color.LightGray
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = strings.confirm
                            )
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
