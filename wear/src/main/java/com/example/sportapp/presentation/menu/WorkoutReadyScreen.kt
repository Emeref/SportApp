package com.example.sportapp.presentation.menu

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow

@Composable
fun WorkoutReadyScreen(
    navController: NavHostController,
    initialDefinitionId: Long,
    viewModel: ChooseSportViewModel = hiltViewModel()
) {
    val definitions by viewModel.definitions.collectAsState()

    if (definitions == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val list = definitions!!
        val initialPage = remember(list) {
            val index = list.indexOfFirst { it.id == initialDefinitionId }
            if (index != -1) index else 0
        }
        
        val pagerState = rememberPagerState(initialPage = initialPage) { list.size }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val definition = list[pageIndex]
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Ikona i Nazwa
                Icon(
                    imageVector = getIconForName(definition.iconName),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(32.dp)
                )
                
                Text(
                    text = definition.name,
                    style = MaterialTheme.typography.title2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Przycisk Start
                Button(
                    onClick = {
                        navController.navigate("dynamic_workout/${definition.id}") {
                            popUpTo("main_menu") { inclusive = false }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                        Spacer(Modifier.width(8.dp))
                        Text("Start")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Przycisk Powrót
                CompactButton(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                }
            }
        }
    }
}
