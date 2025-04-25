package com.example.lightingadjustment.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.example.lightingadjustment.R
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import androidx.compose.ui.res.colorResource
import com.example.lightingadjustment.mqtt.MqttLinking


@Composable
fun ThirdScreen(userPreferencesManager: UserPreferencesManager,
                mqttLinking: MqttLinking) {
    var brightness = remember { mutableFloatStateOf(1.0f) }
    var selectedMode = remember { mutableStateOf<String?>(null) }
    var selectedColor = remember { mutableStateOf(Color.White) }

    val colors = listOf(
        colorResource(R.color.night),
        colorResource(R.color.warm),
        colorResource(R.color.white)
    )

    val autoMode = remember { mutableStateOf(false) }
    val manualMode = remember { mutableStateOf(false) }
    val voiceMode = remember { mutableStateOf(false) }
    val flag = remember { mutableStateOf(false) }

    // Retrieve configuration settings when the page is launched
    LaunchedEffect(Unit) {
        brightness.floatValue = userPreferencesManager.getUserPreferences("brightness")["brightness"] as Float
        selectedColor.value = when (userPreferencesManager.getUserPreferences("color")["color"] as String) {
            "night" -> colors[0]
            "warm" -> colors[1]
            "white" -> colors[2]
            else -> colors[2]
        }
        when (userPreferencesManager.getUserPreferences("operationMode")["operationMode"] as String) {
            "autoMode" -> { autoMode.value = true; manualMode.value = false; voiceMode.value = false }
            "manualMode" -> { autoMode.value = false; manualMode.value = true; voiceMode.value = false; flag.value = true }
            "voiceMode" -> { autoMode.value = false; manualMode.value = false; voiceMode.value = true }
            else -> { autoMode.value = false; manualMode.value = true; voiceMode.value = false; flag.value = true }
        }
        selectedMode.value = userPreferencesManager.getUserPreferences("sceneMode")["sceneMode"] as String
    }

    // Disable control in non manual mode
    LaunchedEffect(manualMode.value) {
        if (manualMode.value) {
            flag.value = true
            brightness.floatValue = userPreferencesManager.getUserPreferences("brightness")["brightness"] as Float
        } else {
            flag.value = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background5),
            contentDescription = "Main Interface",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )//set background


        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )  {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "恒光",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = loadCustomFont(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                ModeSelectionMenu(
                    autoMode = autoMode,
                    manualMode = manualMode,
                    voiceMode = voiceMode,
                    userPreferencesManager, mqttLinking
                )
            }

            ModeSelectionButtons(flag.value, userPreferencesManager, mqttLinking, selectedMode)

            Spacer(modifier = Modifier.height(16.dp))//Set interval
            ColorSelection(
                colors = colors,
                selectedColor = selectedColor,
                flag = flag.value,
                userPreferencesManager, mqttLinking)

            Spacer(modifier = Modifier.height(16.dp))
            BrightnessControl(
                brightness = brightness,
                userPreferencesManager,
                mqttLinking,
                flag = flag.value)
        }
    }
}
