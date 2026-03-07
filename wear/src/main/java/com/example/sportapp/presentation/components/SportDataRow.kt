package com.example.sportapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text

@Composable
fun SportDataRow(label: String, value: String, color: Color, isBold: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(value, color = color, fontSize = if (isBold) 20.sp else 16.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium)
        Text(label, color = Color.LightGray, fontSize = 10.sp)
    }
}
