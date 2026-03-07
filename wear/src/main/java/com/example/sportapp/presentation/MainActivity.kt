package com.example.sportapp.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.sportapp.presentation.theme.SportAppTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberSwipeDismissableNavController()
            SportAppTheme {
                Scaffold(
                    timeText = { TimeText() }
                ) {
                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = "main_menu"
                    ) {
                        composable("main_menu") { MainMenuScreen(navController) }
                        composable("choose_sport") { ChooseSportScreen(navController) }
                        composable("statistics") { PlaceholderScreen("Statystyki") }
                        composable("settings") { PlaceholderScreen("Ustawienia") }
                        
                        // Sporty
                        composable("workout_walking") { WalkingWorkoutScreen() }
                        composable("workout_climbing") { PlaceholderScreen("Wspinaczka") }
                        composable("workout_tennis") { PlaceholderScreen("Tenis") }
                        composable("workout_gym") { PlaceholderScreen("Siłownia") }
                        composable("workout_pool") { PlaceholderScreen("Basen") }
                        composable("workout_kayak") { PlaceholderScreen("Kajak") }
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenuScreen(navController: NavHostController) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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

@Composable
fun ChooseSportScreen(navController: NavHostController) {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ListHeader { Text("Wybierz sport") } }
        
        val sports = listOf(
            Triple("Spacer", Icons.Default.DirectionsWalk, "workout_walking"),
            Triple("Wspinaczka", Icons.Default.Terrain, "workout_climbing"),
            Triple("Tenis", Icons.Default.SportsTennis, "workout_tennis"),
            Triple("Siłownia", Icons.Default.FitnessCenter, "workout_gym"),
            Triple("Basen", Icons.Default.Pool, "workout_pool"),
            Triple("Kajak", Icons.Default.Rowing, "workout_kayak")
        )

        sports.forEach { (name, icon, route) ->
            item {
                Chip(
                    label = { Text(name) },
                    onClick = { navController.navigate(route) },
                    icon = { Icon(icon, contentDescription = name) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.title2)
    }
}

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun WalkingWorkoutScreen() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val focusRequester = rememberActiveFocusRequester()

    Box(modifier = Modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .rotaryScrollable(
                    behavior = RotaryScrollableDefaults.behavior(pagerState),
                    focusRequester = focusRequester
                )
        ) { page ->
            when (page) {
                0 -> MainDataScreen()
                1 -> MapScreen()
            }
        }
        
        // Czerwony zegar na wierzchu
        Box(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            TimeText(
                timeTextStyle = MaterialTheme.typography.caption1.copy(
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun MainDataScreen() {
    val listState = rememberScalingLazyListState()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = "DANE TRENINGU",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.caption2,
                color = Color.Gray
            )
        }
        item { SportDataRow("Kroki", "1250", Color.Green) }
        item { SportDataRow("Dystans", "0.85 km", Color.Cyan) }
        item { SportDataRow("Tętno", "72 BPM", Color.Red, true) }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapScreen() {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    var isHybrid by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasLocationPermission = it }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    if (hasLocationPermission) {
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(52.2297, 21.0122), 10f)
        }

        LaunchedEffect(hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapType = if (isHybrid) MapType.HYBRID else MapType.NORMAL
                ),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
            )

            // Przycisk zmiany typu mapy
            Box(modifier = Modifier.fillMaxSize().padding(top = 25.dp, start = 95.dp), contentAlignment = Alignment.TopCenter) {
                CompactButton(
                    onClick = { isHybrid = !isHybrid },
                    colors = ButtonDefaults.secondaryButtonColors(backgroundColor = Color.Black.copy(alpha = 0.5f))
                ) {
                    Text(if (isHybrid) "NORM" else "HYB", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Zoom Control
            val scope = rememberCoroutineScope()
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                val zoomProgress = (cameraPositionState.position.zoom - 2f) / 18f
                CircularProgressIndicator(
                    progress = 1f - zoomProgress,
                    modifier = Modifier.fillMaxSize().padding(6.dp),
                    startAngle = 320f,
                    endAngle = 40f,
                    strokeWidth = 8.dp,
                    indicatorColor = Color.DarkGray.copy(alpha = 0.5f),
                    trackColor = Color.LightGray.copy(alpha = 0.9f)
                )

                Box(
                    modifier = Modifier.fillMaxHeight().width(50.dp).align(Alignment.CenterEnd)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()
                                val newZoom = (cameraPositionState.position.zoom - dragAmount * 0.05f).coerceIn(2f, 20f)
                                scope.launch { cameraPositionState.move(CameraUpdateFactory.zoomTo(newZoom)) }
                            }
                        }
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Brak uprawnień do lokalizacji", textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun SportDataRow(label: String, value: String, color: Color, isBold: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(value, color = color, fontSize = if (isBold) 20.sp else 16.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium)
        Text(label, color = Color.LightGray, fontSize = 10.sp)
    }
}
