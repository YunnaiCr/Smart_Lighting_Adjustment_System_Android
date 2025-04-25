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
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import com.example.lightingadjustment.mqtt.MqttLinking
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex


@Composable
fun ModeSelectionMenu(
    autoMode: MutableState<Boolean>,
    manualMode: MutableState<Boolean>,
    voiceMode: MutableState<Boolean>,
    userPreferencesManager: UserPreferencesManager,
    mqttLinking: MqttLinking
) {
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val mutex = remember { Mutex() }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "模式选择")
        }

        Box {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ModeSwitchItem("自动模式", autoMode) {
                    if (it) {
                        autoMode.value = true
                        manualMode.value = false
                        voiceMode.value = false
                        scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "operationMode", "autoMode") }
                    }
                }
                ModeSwitchItem("手动模式", manualMode) {
                    if (it) {
                        autoMode.value = false
                        manualMode.value = true
                        voiceMode.value = false
                        scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "operationMode", "manualMode") }
                    }
                }
                ModeSwitchItem("语音模式", voiceMode) {
                    if (it) {
                        autoMode.value = false
                        manualMode.value = false
                        voiceMode.value = true
                        scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "operationMode", "voiceMode") }
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
