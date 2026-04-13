package com.example.sportapp.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.data.strava.StravaStorage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StravaSettingsScreen(
    stravaStorage: StravaStorage,
    onBack: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isConnected by stravaStorage.isConnected.collectAsState(initial = false)
    val athleteName by stravaStorage.athleteName.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.STRAVA_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isConnected) texts.STRAVA_CONNECTED else texts.STRAVA_NOT_CONNECTED,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    athleteName?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (!isConnected) {
                Button(
                    onClick = {
                        val clientId = "224679"
                        val redirectUri = "sportapp://strava" // Uproszczony URI
                        
                        val authUrl = Uri.parse("https://www.strava.com/oauth/authorize")
                            .buildUpon()
                            .appendQueryParameter("client_id", clientId)
                            .appendQueryParameter("redirect_uri", redirectUri)
                            .appendQueryParameter("response_type", "code")
                            .appendQueryParameter("approval_prompt", "auto")
                            .appendQueryParameter("scope", "activity:write,activity:read_all")
                            .build()
                        
                        val intent = CustomTabsIntent.Builder().build()
                        intent.launchUrl(context, authUrl)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(texts.STRAVA_CONNECT)
                }
            } else {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            stravaStorage.clearTokens()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(texts.STRAVA_DISCONNECT)
                }
            }

            Text(
                text = texts.SETTINGS_STRAVA_DESC,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
