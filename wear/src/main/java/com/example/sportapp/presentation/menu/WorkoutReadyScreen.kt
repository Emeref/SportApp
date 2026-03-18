package com.example.sportapp.presentation.menu

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
        
        val pageIndicatorState = remember {
            object : PageIndicatorState {
                override val pageOffset: Float
                    get() = pagerState.currentPageOffsetFraction
                override val selectedPage: Int
                    get() = pagerState.currentPage
                override val pageCount: Int
                    get() = list.size
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val definition = list[pageIndex]
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
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
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Przycisk Start
                    Button(
                        onClick = {
                            navController.navigate("dynamic_workout/${definition.id}") {
                                popUpTo("main_menu") { inclusive = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp)
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

                    Spacer(Modifier.height(4.dp))

                    // Przycisk Powrót z opisem
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CompactButton(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.secondaryButtonColors(),
                            modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Powrót",
                            style = MaterialTheme.typography.caption2
                        )
                    }
                }
            }

            // Dodatkowe strzałki boczne dla czytelności (tylko gdy jest więcej niż 1 strona)
            if (list.size > 1) {
                if (pagerState.currentPage > 0) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 2.dp)
                            .size(16.dp),
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                }
                if (pagerState.currentPage < list.size - 1) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 2.dp)
                            .size(16.dp),
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            // Indykator kropkowy na dole
            if (list.size > 1) {
                HorizontalPageIndicator(
                    pageIndicatorState = pageIndicatorState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                )
            }
        }
    }
}
