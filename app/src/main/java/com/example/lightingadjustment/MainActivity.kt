package com.example.lightingadjustment

import android.os.Bundle
import android.util.Log
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

        mqttLinking.connect(this) {}

        mqttLinking.subscribe("light/livingroom") { message ->
            lifecycleScope.launch { mqttLinking.handleReceivedData(message, userPreferencesManager) }
        }

        lifecycleScope.launch {
            mqttLinking.sendData("initialized", userPreferencesManager = userPreferencesManager)
            Log.d("test", "test the linking.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttLinking.disconnect()
    }
}