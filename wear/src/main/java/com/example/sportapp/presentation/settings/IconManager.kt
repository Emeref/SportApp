package com.example.sportapp.presentation.settings

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.example.sportapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IconManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val packageManager = context.packageManager
    private val packageName = context.packageName

    private val aliases = listOf(
        "$packageName.DefaultAlias",
        "$packageName.Tier1Alias",
        "$packageName.Tier2Alias",
        "$packageName.Tier3Alias",
        "$packageName.Tier4Alias",
        "$packageName.Tier5Alias",
        "$packageName.Tier6Alias"
    )

    fun getIconResourceForTier(tier: Int): Int {
        return when (tier.coerceIn(0, 6)) {
            1 -> R.drawable.logo_apki_niebieskie
            2 -> R.drawable.logo_apki_zielone
            3 -> R.drawable.logo_apki_zolte
            4 -> R.drawable.logo_apki_fioletowe
            5 -> R.drawable.logo_apki_czerwone
            6 -> R.drawable.logo_apki_czarne
            else -> R.drawable.logo_apki_biale
        }
    }

    fun setActiveTier(tier: Int) {
        val targetIndex = tier.coerceIn(0, 6)
        Log.d("IconManager", "Setting active tier to $tier (target index: $targetIndex)")
        
        aliases.forEachIndexed { index, aliasName ->
            val state = if (index == targetIndex) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            
            try {
                val currentConfig = packageManager.getComponentEnabledSetting(ComponentName(context, aliasName))
                if (currentConfig != state) {
                    packageManager.setComponentEnabledSetting(
                        ComponentName(context, aliasName),
                        state,
                        PackageManager.DONT_KILL_APP
                    )
                    Log.d("IconManager", "Alias $aliasName changed to state: $state")
                }
            } catch (e: Exception) {
                Log.e("IconManager", "Failed to set alias $aliasName", e)
            }
        }
    }
}
