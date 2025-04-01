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
    userPreferencesManager: UserPreferencesManager,
    flag: Boolean
) {
    if (!flag) {
        return
    }

    val tag = "Data Changed"
    val step = 1f  // 步长设置为 1
    val steps = 3  // 0到6之间有7个步长（ 1, 2, 3, 4, 5）
    val scope = rememberCoroutineScope()

    Text("亮度调节", fontFamily = loadCustomFont(), color = Color.White)
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 减少亮度按钮
        Button(onClick = {
            if (brightness.value > 0) {
                brightness.value = (brightness.value - step).coerceAtLeast(1f)
                brightness.value = brightness.value.toInt().toFloat() // 确保亮度值为整数
                scope.launch { userPreferencesManager.updateUserPreferences(brightness = brightness.value) }
            }
        }) {
            Text("-")
        }

        // 亮度调节滑动条
        Slider(
            value = brightness.value,
            onValueChange = { newValue ->
                // 限制滑动条值为0, 1, 2, 3, 4, 5, 6
                brightness.value = (newValue).toInt().coerceIn(1, 5).toFloat()
            },
            onValueChangeFinished = {
                scope.launch {
                    userPreferencesManager.updateUserPreferences(brightness = brightness.value)
                    val updateValue = userPreferencesManager.getUserPreferences("brightness")
                    Log.d(tag, "亮度已调整为 $updateValue")
                }
            },
            valueRange = 1f..5f,
            steps = steps, // 设置步长为6，确保只有0到6之间的数值
            modifier = Modifier.width(200.dp)
        )

        // 增加亮度按钮
        Button(onClick = {
            if (brightness.value < 5) {
                brightness.value = (brightness.value + step).coerceAtMost(5f)
                brightness.value = brightness.value.toInt().toFloat() // 确保亮度值为整数
                scope.launch { userPreferencesManager.updateUserPreferences(brightness = brightness.value) }
            }
        }) {
            Text("+")
        }
    }
}



