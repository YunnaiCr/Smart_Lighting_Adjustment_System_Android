package com.example.lightingadjustment.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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


@Composable
fun ThirdScreen(navController: NavHostController, userPreferencesManager: UserPreferencesManager) {
    var brightness by remember { mutableFloatStateOf(0.5f) }
    val selectedColor = remember { mutableStateOf(Color.White) }

    val colors = listOf(
        colorResource(R.color.night),  // 无需 context
        colorResource(R.color.warm),
        colorResource(R.color.white)
    )

    val autoMode = remember { mutableStateOf(false) }
    val manualMode = remember { mutableStateOf(false) }
    val voiceMode = remember { mutableStateOf(false) }
    val flag = remember { mutableStateOf(true) }

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
                    voiceMode = voiceMode
                )
            }


            ModeSelectionButtons(onModeSelected = { selectedMode ->
                println("用户选择了模式: $selectedMode")
                // TODO: 根据模式选择发送相应信息到硬件
            }, flag = flag.value)


            Spacer(modifier = Modifier.height(16.dp))//Set interval
            ColorSelection(colors = colors, selectedColor = selectedColor,flag = flag.value)

            Spacer(modifier = Modifier.height(16.dp))
            BrightnessControl(brightness = remember { mutableFloatStateOf(brightness)}, userPreferencesManager,flag = flag.value)
            // 根据模式选择显示内容
            if (manualMode.value) {
                flag.value = true
            } else if (autoMode.value || voiceMode.value) {
                flag.value = false
            }
        }
    }
}
