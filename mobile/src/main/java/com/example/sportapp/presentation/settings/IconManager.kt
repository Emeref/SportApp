package com.example.sportapp.presentation.settings

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
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

    fun setActiveTier(tier: Int) {
        val targetIndex = tier.coerceIn(0, 6)
        
        aliases.forEachIndexed { index, aliasName ->
            val state = if (index == targetIndex) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            
            try {
                packageManager.setComponentEnabledSetting(
                    ComponentName(context, aliasName),
                    state,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                // Log or handle error if an alias is missing in manifest
            }
        }
    }
}
