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
import com.google.gson.Gson
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var mqttLinking: MqttLinking
    private lateinit var userPreferencesManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LightingAdjustmentTheme {
                AppNavigation()
            }
        }

        // Initialize data manager
        userPreferencesManager = UserPreferencesManager(this)

        // To initialize uninitialized data
        lifecycleScope.launch {
            if(!userPreferencesManager.isInitialized()){
                userPreferencesManager.updateUserPreferences(initialized = true)
            }
        }

        mqttLinking = MqttLinking(this)
        mqttLinking.connect(this) {
            lifecycleScope.launch {
                sendData("initialized")
                Log.d("test", "test the linking.")
            }
        }

        mqttLinking.subscribe("bedroom/lighting") { message ->
            mqttLinking.handleReceivedData(message)
        }

    }

    // Send data by one field
    private suspend fun sendData(field: String) {
        val data = userPreferencesManager.getUserPreferences(field)
        val jsonData = Gson().toJson(data)
        mqttLinking.sendMessage("bedroom/lighting", jsonData)
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttLinking.disconnect()
    }
}