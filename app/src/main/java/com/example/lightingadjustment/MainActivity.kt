package com.example.lightingadjustment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import com.example.lightingadjustment.mqtt.MqttLinking
import com.example.lightingadjustment.ui.theme.LightingAdjustmentTheme
import com.example.lightingadjustment.screen.AppNavigation
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var mqttLinking: MqttLinking
    private lateinit var userPreferencesManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize data manager and MQTT linking
        userPreferencesManager = UserPreferencesManager(this)
        mqttLinking = MqttLinking(this)

        setContent {
            LightingAdjustmentTheme {
                AppNavigation(userPreferencesManager, mqttLinking)
            }
        }

        // To initialize uninitialized data
        lifecycleScope.launch {
            if(!userPreferencesManager.isInitialized()){
                userPreferencesManager.initialize()
            }
        }

        mqttLinking.connect(this) {
            mqttLinking.sendMessage("light/livingroom/esp/status", "sync")
        }

        mqttLinking.subscribe("light/livingroom/app/status") { message ->
            lifecycleScope.launch { mqttLinking.handleReceivedSign(message, userPreferencesManager) }
        }
        mqttLinking.subscribe("light/livingroom/app") { message ->
            lifecycleScope.launch { mqttLinking.handleReceivedData(message, userPreferencesManager) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttLinking.disconnect()
    }
}