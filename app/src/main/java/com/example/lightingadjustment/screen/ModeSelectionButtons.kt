package com.example.lightingadjustment.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



@Composable
fun ModeSelectionButtons(onModeSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onModeSelected("阅读模式") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp)
        ) {
            Text("阅读模式", fontFamily = loadCustomFont())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onModeSelected("睡眠模式") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp)
        ) {
            Text("睡眠模式", fontFamily = loadCustomFont())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onModeSelected("影视模式") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp)
        ) {
            Text("影视模式", fontFamily = loadCustomFont())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onModeSelected("聚会模式") },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp)
        ) {
            Text("聚会模式", fontFamily = loadCustomFont())
        }
    }
}

