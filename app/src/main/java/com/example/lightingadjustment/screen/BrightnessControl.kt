package com.example.lightingadjustment.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import kotlinx.coroutines.launch

@Composable
fun BrightnessControl(
    brightness: MutableState<Float>,
    userPreferencesManager: UserPreferencesManager
) {
    val tag = "Data Changed"
    val step = 0.1f
    val steps = (1f / step).toInt() - 1
    val scope = rememberCoroutineScope()

    Text("亮度调节", fontFamily = loadCustomFont(), color = Color.White)
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { if (brightness.value > 0) brightness.value = (brightness.value - step).coerceAtLeast(0f)
                           scope.launch{userPreferencesManager.updateUserPreferences(brightness = brightness.value)}    // Store the brightness
        }) {
            Text("-")
        }
        Slider(
            value = brightness.value,
            // Round the brightness value to the nearest scale value when user drags the slider
            onValueChange = { newValue ->
                brightness.value = (newValue / step).toInt() * step
            },
            // Store the brightness value when user raise finger
            onValueChangeFinished = {
                scope.launch {
                    userPreferencesManager.updateUserPreferences(brightness = brightness.value)
                    val updateValue = userPreferencesManager.getUserPreferences("brightness")
                    Log.d(tag, "Brightness has been changed to $updateValue")
                }
            },
            valueRange = 0f..1f,
            steps = steps, // 限制步长
            modifier = Modifier.width(200.dp)
        )
        Button(onClick = { if (brightness.value < 1) brightness.value = (brightness.value + step).coerceAtMost(1f)
                           scope.launch{userPreferencesManager.updateUserPreferences(brightness = brightness.value)}    // Store the brightness
        }) {
            Text("+")
        }
    }
}

