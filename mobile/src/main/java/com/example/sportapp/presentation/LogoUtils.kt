package com.example.sportapp.presentation

import com.example.sportapp.R

fun getAppLogoRes(tier: Int): Int {
    return when (tier) {
        1 -> R.drawable.logo_apki_niebieskie
        2 -> R.drawable.logo_apki_zielone
        3 -> R.drawable.logo_apki_zolte
        4 -> R.drawable.logo_apki_fioletowe
        5 -> R.drawable.logo_apki_czerwone
        6 -> R.drawable.logo_apki_czarne
        else -> R.drawable.logo_apki_biale
    }
}
