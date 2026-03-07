package com.example.sportapp.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.material.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import com.example.sportapp.presentation.theme.SportAppTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun WearApp() {
    SportAppTheme {
        val pagerState = rememberPagerState(pageCount = { 2 })
        val focusRequester = rememberActiveFocusRequester()

        Scaffold(
            timeText = {} // Usuwa systemowe wyszarzenie/nakładkę
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // 1. PAGER (Mapa i dane)
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

                // 2. CZERWONY ZEGAR (Bez tła i wyszarzenia)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    TimeText(
                        modifier = Modifier.padding(top = 5.dp),
                        timeSource = TimeTextDefaults.timeSource(
                            TimeTextDefaults.timeFormat()
                        ),
                        // Ustawiamy kolor tekstu na czerwony
                        timeTextStyle = androidx.wear.compose.material.Typography().caption1.copy(
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MainDataScreen() {
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = "MOJE DANE",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.caption1,
                color = Color.Gray
            )
        }
        item {
            SportDataRow(label = "Kroki", value = "1250", color = Color.Green)
        }
        item {
            SportDataRow(label = "Dystans", value = "0.85 km", color = Color.Cyan)
        }
        item {
            SportDataRow(label = "Tętno", value = "72 BPM", color = Color.Red, isBold = true)
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun MapScreen() {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isHybrid by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (hasLocationPermission) {
        val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(52.2297, 21.0122), 10f)
        }

        LaunchedEffect(hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(it.latitude, it.longitude),
                        15f
                    )
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
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                )
            )

            // Przycisk zmiany typu mapy na górze
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 5.dp, start = 95.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CompactButton(
                    onClick = { isHybrid = !isHybrid },
                    colors = ButtonDefaults.secondaryButtonColors(
                        backgroundColor = Color.Black.copy(alpha = 0.5f)
                    ),
                ) {
                    Text(
                        text = if (isHybrid) "NORM" else "HYB",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Kontener dla wskaźnika zoomu po prawej stronie
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                val zoomProgress = (cameraPositionState.position.zoom - 2f) / 18f
                // 1. WIZUALIZACJA (Łuk)
                CircularProgressIndicator(
                    // Odwracamy postęp: 1f - zoomProgress
                    // Dzięki temu przy zoomie 2 (progress 0) pasek jest "pełny",
                    // a przy zoomie 20 (progress 1) pasek "znika" w stronę góry.
                    progress = 1f - zoomProgress,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    startAngle = 320f, // Start na górze (godzina 2)
                    endAngle = 40f,    // Koniec na dole (godzina 4)
                    strokeWidth = 10.dp,

                    // KLUCZ KOLORÓW:
                    // indicatorColor (ten który się skraca od dołu do góry) ustawiamy na kolor tła.
                    indicatorColor = Color.DarkGray.copy(alpha = 0.5f),

                    // trackColor (tło, które jest pod spodem) ustawiamy na jasny.
                    // Ponieważ wskaźnik powyżej się skraca (odkrywa dół),
                    // jasny kolor pod spodem wygląda jakby "rósł" od dołu do góry.
                    trackColor = Color.LightGray.copy(alpha = 0.9f)
                )

                // 2. LOGIKA DOTYKU (Stepper)
                // 2. LOGIKA DOTYKU (Przeciąganie palcem po prawej krawędzi)
                val scope = rememberCoroutineScope()
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(50.dp) // Ograniczamy szerokość interakcji do krawędzi
                        .align(Alignment.CenterEnd)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()

                                // Obliczamy zmianę zoomu na podstawie przesunięcia (dragAmount)
                                // dragAmount jest ujemny przy przesuwaniu w górę
                                val sensitivity = 0.05f // Czułość przewijania
                                val newZoom = (cameraPositionState.position.zoom - dragAmount * sensitivity)
                                    .coerceIn(2f, 20f)

                                scope.launch {
                                    cameraPositionState.move(CameraUpdateFactory.zoomTo(newZoom))
                                }
                            }
                        }
                ){
                    // Puste wnętrze
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Brak uprawnień do lokalizacji",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(16.dp)
            )
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
