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

@Composable
fun ThirdScreen(navController: NavHostController) {
    var brightness by remember { mutableFloatStateOf(0.5f) }
    val selectedColor = remember { mutableStateOf(Color.White) }
    val colors = listOf(Color.Black, Color.DarkGray, Color.Gray, Color.LightGray, Color.White)
    val autoMode = remember { mutableStateOf(false) }
    val manualMode = remember { mutableStateOf(false) }
    val voiceMode = remember { mutableStateOf(false) }
    val finalColor = remember { mutableStateOf(Color.White) } // MutableState<Color>
    val finalBrightness = remember { mutableFloatStateOf(0.5f) } // MutableState<Float>

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
                    text = "自动调节模式",
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


            ModeSelectionButtons { selectedMode ->
                println("用户选择了模式: $selectedMode")
            // TODO: Select to send the corresponding information to the hardware according to the mode passed.
            }


            Spacer(modifier = Modifier.height(16.dp))//Set interval
            ColorSelection(colors = colors, selectedColor = selectedColor)

            Spacer(modifier = Modifier.height(16.dp))
            BrightnessControl(brightness = remember { mutableFloatStateOf(brightness) })


            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        // When the button is clicked, store the selected color and brightness
                        finalColor.value = selectedColor.value
                        finalBrightness.floatValue = brightness

                        // Optionally, log or show a message for confirmation
                        println("最终选择的颜色: ${finalColor.value}, 最终亮度: ${finalBrightness.floatValue}")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(8.dp)
                ) {
                    Text("确认调节", fontFamily = loadCustomFont())
                }
            }
        }
    }
}
