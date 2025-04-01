package com.example.lightingadjustment.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp



@Composable
fun ModeSelectionButtons(
    onModeSelected: (String) -> Unit,
    flag: Boolean
    ) {


    var selectedMode by remember { mutableStateOf<String?>(null) }

    if (!flag) {
        return
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = {
                selectedMode = "阅读模式"
                onModeSelected("阅读模式")
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMode == "阅读模式") Color.Black else Color.Gray
            )
        ) {
            Text("阅读模式", fontFamily = loadCustomFont())
        }
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = {
                selectedMode = "睡眠模式"
                onModeSelected("睡眠模式")
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMode == "睡眠模式") Color.Black else Color.Gray
            )
        ) {
            Text("睡眠模式", fontFamily = loadCustomFont())
        }
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = {
                selectedMode = "影视模式"
                onModeSelected("影视模式")
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMode == "影视模式") Color.Black else Color.Gray
            )
        ) {
            Text("影视模式", fontFamily = loadCustomFont())
        }
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = {
                selectedMode = "聚会模式"
                onModeSelected("聚会模式")
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMode == "聚会模式") Color.Black else Color.Gray
            )
        ) {
            Text("聚会模式", fontFamily = loadCustomFont())
        }
    }
}

