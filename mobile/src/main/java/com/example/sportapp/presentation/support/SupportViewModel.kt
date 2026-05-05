package com.example.sportapp.presentation.support

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.example.sportapp.data.billing.BillingRepository
import com.example.sportapp.presentation.settings.IconManager
import com.example.sportapp.presentation.settings.MobileSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SupportTier(
    val level: Int,
    val monthlyId: String,
    val lifetimeId: String,
    val isPurchased: Boolean,
    val isActiveIcon: Boolean,
    val monthlyPrice: String = "",
    val lifetimePrice: String = ""
)

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    private val settingsManager: MobileSettingsManager,
    private val iconManager: IconManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<SupportTier>>(emptyList())
    val uiState: StateFlow<List<SupportTier>> = _uiState.asStateFlow()

    init {
        combine(
            billingRepository.purchasedTiers,
            billingRepository.productDetails,
            settingsManager.settingsFlow
        ) { purchased, details, settings ->
            (1..6).map { level ->
                val monthlyId = "tier${level}_monthly"
                val lifetimeId = "tier${level}_lifetime"
                
                val monthlyDetails = details[monthlyId]
                val lifetimeDetails = details[lifetimeId]
                
                SupportTier(
                    level = level,
                    monthlyId = monthlyId,
                    lifetimeId = lifetimeId,
                    isPurchased = purchased.contains(level),
                    isActiveIcon = settings.activeIconTier == level,
                    monthlyPrice = monthlyDetails?.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice ?: "",
                    lifetimePrice = lifetimeDetails?.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
                )
            }
        }.onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    fun purchaseMonthly(activity: Activity, tier: SupportTier) {
        billingRepository.purchaseProduct(activity, tier.monthlyId)
    }

    fun purchaseLifetime(activity: Activity, tier: SupportTier) {
        billingRepository.purchaseProduct(activity, tier.lifetimeId)
    }

    fun setIcon(tierLevel: Int) {
        viewModelScope.launch {
            settingsManager.updateActiveIconTier(tierLevel)
            iconManager.setActiveTier(tierLevel)
        }
    }

    fun debugUnlockAll() {
        billingRepository.debugUnlockAllTiers()
    }
}
