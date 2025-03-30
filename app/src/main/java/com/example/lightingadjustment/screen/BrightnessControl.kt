package com.example.lightingadjustment.screen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun BrightnessControl(
    brightness: MutableState<Float>
) {
    val step = 0.1f // 每次调整的步长
    val steps = (1f / step).toInt() - 1 // 计算步数

    Text("亮度调节", fontFamily = loadCustomFont(), color = Color.White)
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = { if (brightness.value > 0) brightness.value = (brightness.value - step).coerceAtLeast(0f) }) {
            Text("-")
        }
        Slider(
            value = brightness.value,
            onValueChange = { newValue ->
                // 将亮度值四舍五入到最近的 0.1 值
                brightness.value = (newValue / step).toInt() * step
            },
            valueRange = 0f..1f,
            steps = steps, // 限制步长
            modifier = Modifier.width(200.dp)
        )
        Button(onClick = { if (brightness.value < 1) brightness.value = (brightness.value + step).coerceAtMost(1f) }) {
            Text("+")
        }
    }
}

