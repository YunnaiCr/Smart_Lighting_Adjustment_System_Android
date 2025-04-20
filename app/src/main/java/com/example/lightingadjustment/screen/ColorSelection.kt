package com.example.lightingadjustment.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import com.example.lightingadjustment.mqtt.MqttLinking
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun ColorSelection(
    colors: List<Color>,
    selectedColor: MutableState<Color>,
    flag: Boolean,
    userPreferencesManager: UserPreferencesManager,
    mqttLinking: MqttLinking
) {
    if (!flag) {
        return
    }

    val scope = rememberCoroutineScope()
    val mutex = remember { Mutex() }

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
                    .clickable {
                        selectedColor.value = color
                        when (color) {
                            colors[0] -> scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "color", "night") }
                            colors[1] -> scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "color", "warm") }
                            colors[2] -> scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "color", "white") }
                        }
                    }
                    .border(
                        width = 2.dp,
                        color = if (selectedColor.value == color) Color.Black else Color.Transparent
                    )
            )
        }
    }
}

