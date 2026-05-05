package com.example.sportapp.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener, BillingClientStateListener {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val _isBillingReady = MutableStateFlow(false)
    val isBillingReady: StateFlow<Boolean> = _isBillingReady.asStateFlow()

    private val _purchasedTiers = MutableStateFlow<Set<Int>>(emptySet())
    val purchasedTiers: StateFlow<Set<Int>> = _purchasedTiers.asStateFlow()

    private val _productDetails = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val productDetails: StateFlow<Map<String, ProductDetails>> = _productDetails.asStateFlow()

    companion object {
        val SUBS_IDS = (1..6).map { "tier${it}_monthly" }
        val INAPP_IDS = (1..6).map { "tier${it}_lifetime" }
        val ALL_IDS = SUBS_IDS + INAPP_IDS
    }

    init {
        startConnection()
    }

    fun startConnection() {
        billingClient.startConnection(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _isBillingReady.value = true
            queryProductDetails()
            queryPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        _isBillingReady.value = false
    }

    private fun queryProductDetails() {
        val productList = ALL_IDS.map { productId ->
            val productType = if (productId.contains("monthly")) {
                BillingClient.ProductType.SUBS
            } else {
                BillingClient.ProductType.INAPP
            }
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, detailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetails.value = detailsResult.productDetailsList.associateBy { it.productId }
            }
        }
    }

    fun queryPurchases() {
        if (!billingClient.isReady) return

        // Query In-App
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchases)
            }
        }

        // Query Subs
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchases)
            }
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        val activeTiers = mutableSetOf<Int>()
        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
                
                purchase.products.forEach { productId ->
                    val tier = productId.filter { it.isDigit() }.toIntOrNull()
                    if (tier != null) {
                        activeTiers.add(tier)
                    }
                }
            }
        }
        _purchasedTiers.value += activeTiers
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                queryPurchases()
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            processPurchases(purchases)
        }
    }

    fun purchaseProduct(activity: Activity, productId: String) {
        val details = _productDetails.value[productId] ?: return
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .apply {
                    if (details.productType == BillingClient.ProductType.SUBS) {
                        // Assuming the first offer is the one we want
                        details.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let {
                            setOfferToken(it)
                        }
                    }
                }
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun debugUnlockAllTiers() {
        _purchasedTiers.value = (1..6).toSet()
    }
}
