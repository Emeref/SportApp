package com.example.sportapp.presentation.support

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sportapp.LocalMobileTexts
import com.example.sportapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBack: () -> Unit,
    viewModel: SupportViewModel = hiltViewModel()
) {
    val texts = LocalMobileTexts.current
    val tiers by viewModel.uiState.collectAsState()
    val context = LocalContext.current as Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(texts.SUPPORT_TITLE) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = texts.SUPPORT_DISCLAIMER,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(tiers) { tier ->
                SupportTierItem(
                    tier = tier,
                    onMonthly = { viewModel.purchaseMonthly(context, tier) },
                    onLifetime = { viewModel.purchaseLifetime(context, tier) },
                    onActivate = { viewModel.setIcon(tier.level) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = texts.SUPPORT_ICON_CHANGE_NOTICE,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
fun SupportTierItem(
    tier: SupportTier,
    onMonthly: () -> Unit,
    onLifetime: () -> Unit,
    onActivate: () -> Unit
) {
    val texts = LocalMobileTexts.current
    val iconRes = getTierIconRes(tier.level)

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Tier ${tier.level}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                if (tier.isActiveIcon) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!tier.isPurchased) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onMonthly,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(texts.SUPPORT_MONTHLY_SUB, fontSize = 10.sp)
                            Text(tier.monthlyPrice.ifEmpty { "---" }, fontWeight = FontWeight.Bold)
                        }
                    }
                    Button(
                        onClick = onLifetime,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(texts.SUPPORT_LIFETIME_BUY, fontSize = 10.sp)
                            Text(tier.lifetimePrice.ifEmpty { "---" }, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Button(
                    onClick = onActivate,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !tier.isActiveIcon,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (tier.isActiveIcon) "Current Icon" else "Activate Icon")
                }
            }
        }
    }
}

private fun getTierIconRes(level: Int): Int {
    return when (level) {
        1 -> R.drawable.logo_apki_niebieskie
        2 -> R.drawable.logo_apki_zielone
        3 -> R.drawable.logo_apki_zolte
        4 -> R.drawable.logo_apki_fioletowe
        5 -> R.drawable.logo_apki_czerwone
        6 -> R.drawable.logo_apki_czarne
        else -> R.drawable.logo_apki_biale
    }
}
