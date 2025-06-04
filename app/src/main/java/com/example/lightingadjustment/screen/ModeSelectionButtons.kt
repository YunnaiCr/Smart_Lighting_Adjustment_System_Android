package com.example.lightingadjustment.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import com.example.lightingadjustment.mqtt.MqttLinking
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex


@Composable
fun ModeSelectionButtons(
    userPreferencesManager: UserPreferencesManager,
    mqttLinking: MqttLinking,
    selectedMode: MutableState<String?>
    ) {
    val scope = rememberCoroutineScope()
    val mutex = remember { Mutex() }


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = {
                selectedMode.value = "Reading"
                scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "sceneMode", "Reading") }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMode.value == "Reading") Color.Black else Color.Gray
            )
        ) {
            Text("阅读模式", fontFamily = loadCustomFont())
        }
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = {
                selectedMode.value = "Sleeping"
                scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "sceneMode", "Sleeping") }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMode.value == "Sleeping") Color.Black else Color.Gray
            )
        ) {
            Text("睡眠模式", fontFamily = loadCustomFont())
        }
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = {
                selectedMode.value = "Film"
                scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "sceneMode", "Film") }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMode.value == "Film") Color.Black else Color.Gray
            )
        ) {
            Text("影视模式", fontFamily = loadCustomFont())
        }
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = {
                selectedMode.value = "Party"
                scope.launch { mqttLinking.updateAndSend(mutex, userPreferencesManager, "sceneMode", "Party") }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMode.value == "Party") Color.Black else Color.Gray
            )
        ) {
            Text("聚会模式", fontFamily = loadCustomFont())
        }
    }
}

