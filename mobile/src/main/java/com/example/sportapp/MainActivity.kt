package com.example.sportapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.TextView

class MainActivity : ComponentActivity() {

    // Te zmienne będą przechowywać referencje do widoków
    private lateinit var heartRateText: TextView
    private lateinit var distanceText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ustawiamy widok (upewnij się, że layout zostanie zaktualizowany pod zegarek)
        setContentView(R.layout.activity_main)

        // Inicjalizacja widoków (zakładając, że masz takie ID w XML)
        // heartRateText = findViewById(R.id.heartRateText)
        // distanceText = findViewById(R.id.distanceText)
    }
}