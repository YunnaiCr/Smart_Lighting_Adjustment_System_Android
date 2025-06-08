package com.example.lightingadjustment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.lightingadjustment.datamanagement.UserPreferencesRepository
import com.example.lightingadjustment.mqtt.*
import com.example.lightingadjustment.ui.theme.LightingAdjustmentTheme
import com.example.lightingadjustment.screen.AppNavigation
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var mqttLinking: MqttLinking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize data manager and MQTT linking
        val context = applicationContext
        val room1Manager = UserPreferencesRepository.getManager(context, "room1")
        val room2Manager = UserPreferencesRepository.getManager(context, "room2")
        mqttLinking = AppServices.mqtt(context)

        setContent {
            LightingAdjustmentTheme {
                AppNavigation()
            }
        }

        // To initialize uninitialized data
        lifecycleScope.launch {
            if (!room1Manager.isInitialized()) {
                room1Manager.initialize()
            }
            if (!room2Manager.isInitialized()) {
                room2Manager.initialize()
            }
        }

        mqttLinking.connect(context) {
            mqttLinking.sendMessage("light/room1/esp/status", "sync")
            mqttLinking.sendMessage("light/room2/esp/status", "sync")
        }

        mqttLinking.subscribe("light/room1/app/status") { message ->
            lifecycleScope.launch { mqttLinking.handleReceivedSign(message, room1Manager) }
        }
        mqttLinking.subscribe("light/room1/app") { message ->
            lifecycleScope.launch { mqttLinking.handleReceivedData(message, room1Manager) }
        }
        mqttLinking.subscribe("light/room2/app/status") { message ->
            lifecycleScope.launch { mqttLinking.handleReceivedSign(message, room2Manager) }
        }
        mqttLinking.subscribe("light/room2/app") { message ->
            lifecycleScope.launch { mqttLinking.handleReceivedData(message, room2Manager) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mqttLinking.disconnect()
    }
}