package com.example.lightingadjustment

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.lightingadjustment.databinding.MainPageBinding
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import com.example.lightingadjustment.mqtt.MqttLinking
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainPageBinding
    private lateinit var mqttLinking: MqttLinking
    private lateinit var userPreferencesManager: UserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

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