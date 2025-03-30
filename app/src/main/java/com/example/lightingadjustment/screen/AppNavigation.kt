package com.example.lightingadjustment.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.*
//set screen
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val manualModeSwitchState = rememberSaveable { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("third") { ThirdScreen(navController, manualModeSwitchState) }
    }
}
