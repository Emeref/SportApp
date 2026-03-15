package com.example.sportapp.presentation.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.example.sportapp.R

@Composable
fun MainMenuScreen(navController: NavHostController) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo na górze menu
        item {
            Image(
                painter = painterResource(id = R.drawable.logo_apki_biale),
                contentDescription = "SportApp Logo",
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 8.dp)
            )
        }

        item {
            Chip(
                label = { Text("Sport") },
                onClick = { navController.navigate("choose_sport") },
                icon = { Icon(Icons.Default.DirectionsRun, contentDescription = "Sport") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
        item {
            Chip(
                label = { Text("Statystyki") },
                onClick = { navController.navigate("statistics") },
                icon = { Icon(Icons.Default.BarChart, contentDescription = "Statystyki") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                colors = ChipDefaults.secondaryChipColors()
            )
        }
        item {
            Chip(
                label = { Text("Ustawienia") },
                onClick = { navController.navigate("settings") },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Ustawienia") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}
