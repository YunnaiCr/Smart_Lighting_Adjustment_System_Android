package com.example.lightingadjustment.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.text.font.FontWeight




@Composable
fun ModeSelectionMenu(
    autoMode: MutableState<Boolean>,
    manualMode: MutableState<Boolean>,
    voiceMode: MutableState<Boolean>
) {

    // 设置初始状态为手动模式
    var expanded by remember { mutableStateOf(false) }

    // 默认初始状态
    LaunchedEffect(Unit) {
        autoMode.value = false
        manualMode.value = true // 手动模式为初始状态
        voiceMode.value = false
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "模式选择")
        }

        Box() {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ModeSwitchItem("自动模式", autoMode) {
                    if (it) {
                        autoMode.value = true
                        manualMode.value = false
                        voiceMode.value = false
                    }
                }
                ModeSwitchItem("手动模式", manualMode) {
                    if (it) {
                        autoMode.value = false
                        manualMode.value = true
                        voiceMode.value = false
                    }
                }
                ModeSwitchItem("语音模式", voiceMode) {
                    if (it) {
                        autoMode.value = false
                        manualMode.value = false
                        voiceMode.value = true
                    }
                }
            }
        }
    }
}

@Composable
fun ModeSwitchItem(label: String, state: MutableState<Boolean>, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f),
            fontFamily = loadCustomFont(), // Change font family for button text
            fontWeight = FontWeight.Bold // Optional: Make the text bold
        )
        Switch(
            checked = state.value,
            onCheckedChange = { onCheckedChange(it) }
        )
    }
}
