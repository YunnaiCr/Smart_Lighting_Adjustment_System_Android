package com.example.lightingadjustment.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable

@Composable
fun ColorSelection(
    colors: List<Color>,
    selectedColor: MutableState<Color>
) {
    Text("颜色选择", fontFamily = loadCustomFont(), color = Color.White)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color)
                    .padding(4.dp)
                    .clickable { selectedColor.value = color }
            )
        }
    }
}
