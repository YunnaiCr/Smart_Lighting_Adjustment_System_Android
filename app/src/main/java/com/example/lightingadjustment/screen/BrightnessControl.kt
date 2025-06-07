package com.example.lightingadjustment.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import com.example.lightingadjustment.mqtt.MqttLinking
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.*

@Composable
fun BrightnessControl(
    changedBrightness: Float,
    userPreferencesManager: UserPreferencesManager,
    mqttLinking: MqttLinking
) {


    val scope = rememberCoroutineScope()
    val mutex = remember { Mutex() }
    val step = 1f  // 步长设置为 1
    val steps = 3  // 0到6之间有7个步长（ 1, 2, 3, 4, 5）
    var brightness = changedBrightness

    Text("亮度调节", fontFamily = loadCustomFont(), color = Color.White)
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 减少亮度按钮
        Button(onClick = {
            if (brightness > 0) {
                brightness = (brightness - step).coerceAtLeast(1f)
                brightness = brightness.toInt().toFloat() // 确保亮度值为整数
                scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "brightness", brightness) }
            }
        }) {
            Text("-")
        }

        // 亮度调节滑动条
        Slider(
            value = brightness,
            onValueChange = { newValue ->
                // 限制滑动条值为0, 1, 2, 3, 4, 5, 6
                brightness = (newValue).toInt().coerceIn(1, 5).toFloat()
            },
            onValueChangeFinished = {
                scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "brightness", brightness) }
            },
            valueRange = 1f..5f,
            steps = steps, // 设置步长为6，确保只有0到6之间的数值
            modifier = Modifier.width(200.dp)
        )

        // 增加亮度按钮
        Button(onClick = {
            if (brightness < 5) {
                brightness = (brightness + step).coerceAtMost(5f)
                brightness = brightness.toInt().toFloat() // 确保亮度值为整数
                scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "brightness", brightness) }
            }
        }) {
            Text("+")
        }
    }
}



