package com.example.sportapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import com.example.sportapp.presentation.theme.SportAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    SportAppTheme {
        val listState = rememberScalingLazyListState()

        // Scaffold zapewnia poprawne rozmieszczenie elementów systemowych (godzina, pasek przewijania)
        Scaffold(
            timeText = { TimeText() },
            positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
        ) {
            // ScalingLazyColumn sprawia, że elementy na górze i dole ekranu się zmniejszają, co wygląda świetnie na Pixel Watch
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Nagłówek aplikacji
                item {
                    Text(
                        text = "MOJE DANE",
                        modifier = Modifier.padding(bottom = 8.dp),
                        style = MaterialTheme.typography.caption1,
                        color = Color.Gray
                    )
                }

                // Liczba kroków
                item {
                    SportDataRow(label = "Kroki", value = "1250", color = Color.Green)
                }

                // Dystans
                item {
                    SportDataRow(label = "Dystans", value = "0.85 km", color = Color.Cyan)
                }

                // Tętno
                item {
                    SportDataRow(label = "Tętno", value = "72 BPM", color = Color.Red, isBold = true)
                }
            }
        }
    }
}

@Composable
fun SportDataRow(label: String, value: String, color: Color, isBold: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = value,
            color = color,
            fontSize = if (isBold) 22.sp else 18.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}