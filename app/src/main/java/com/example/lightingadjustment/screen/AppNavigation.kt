package com.example.lightingadjustment.screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lightingadjustment.datamanagement.UserPreferencesManager
import com.example.lightingadjustment.mqtt.MqttLinking

//Set screen
@Composable
fun AppNavigation(userPreferencesManager: UserPreferencesManager, mqttLinking: MqttLinking) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("third") { ThirdScreen(userPreferencesManager, mqttLinking) }
    }
}
