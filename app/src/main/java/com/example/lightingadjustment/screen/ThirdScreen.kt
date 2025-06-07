package com.example.lightingadjustment.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.example.lightingadjustment.R
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import com.example.lightingadjustment.datamanagement.*
import androidx.compose.ui.res.colorResource
import com.example.lightingadjustment.mqtt.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController


@Composable
fun ThirdScreen(navController: NavHostController, roomId: String) {
    val context = LocalContext.current.applicationContext
    val userPreferencesManager = UserPreferencesRepository.getManager(context, roomId)
    val mqttLinking = AppServices.mqtt(context)
    val preferences by userPreferencesManager.preferencesFlow.collectAsState(initial = TempUserPreferences(1.0f, "white", "Sleeping", "manualMode", false))

    val brightness = preferences.brightness
    val selectedMode = preferences.sceneMode
    val selectedColor = remember { mutableStateOf(Color.White) }
    val colors = listOf(
        colorResource(R.color.night),
        colorResource(R.color.warm),
        colorResource(R.color.white)
    )

    val autoMode = remember { mutableStateOf(false) }
    val manualMode = remember { mutableStateOf(false) }
    val voiceMode = remember { mutableStateOf(false) }
    val flag = remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val mutex = remember { Mutex() }

    LaunchedEffect(preferences.operationMode) {
        when (preferences.operationMode) {
            "autoMode" -> {
                autoMode.value = true
                manualMode.value = false
                voiceMode.value = false
                flag.value = false
            }
            "manualMode" -> {
                autoMode.value = false
                manualMode.value = true
                voiceMode.value = false
                flag.value = true
            }
            "voiceMode" -> {
                autoMode.value = false
                manualMode.value = false
                voiceMode.value = true
                flag.value = false
            }
            else -> {
                autoMode.value = false
                manualMode.value = true
                voiceMode.value = false
                flag.value = true
            }
        }
    }

    LaunchedEffect(preferences.part) {
        if (preferences.operationMode == "manualMode") {
            when (preferences.part) {
                false -> pagerState.scrollToPage(0)
                true -> pagerState.scrollToPage(1)
            }
        }
    }

    LaunchedEffect(preferences.color) {
        selectedColor.value = when (preferences.color) {
            "night" -> colors[0]
            "warm" -> colors[1]
            "white" -> colors[2]
            else -> colors[2]
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background5),
            contentDescription = "Main Interface",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .statusBarsPadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                TextButton(
                    onClick = { navController.navigate("room") },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Text("房间选择", color = Color.White,fontFamily = loadCustomFont())
                }

                // 居中标题
                Text(
                    text = "恒光",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = loadCustomFont(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )

                // 右上角操作模式按钮
                ModeSelectionMenu(
                    autoMode = autoMode,
                    manualMode = manualMode,
                    voiceMode = voiceMode,
                    userPreferencesManager,
                    mqttLinking
                )
            }

            if (voiceMode.value) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val voiceCommands = listOf(
                        "打开厨房灯", "关闭厨房灯",
                        "打开客厅灯", "关闭客厅灯",
                        "厨房灯调亮", "厨房灯调暗",
                        "客厅灯调亮", "客厅灯调暗",
                        "客厅柔光灯",
                        "打开阅读模式", "关闭阅读模式",
                        "打开睡眠模式", "关闭睡眠模式",
                        "声音调小", "声音调大",
                        "静音", "取消静音",
                        "打开自动模式", "打开手动模式"
                    )
                    items(voiceCommands) { command ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x33000000), shape = MaterialTheme.shapes.medium) // 半透明深色背景
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                text = command,
                                fontSize = 18.sp,
                                fontFamily = loadCustomFont(),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            if(flag.value)
            {
                // Tab buttons
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.LightGray,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    listOf("模式", "调节").forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                    val data = (index != 0)
                                    mqttLinking.updateAndSend(mutex, userPreferencesManager, "part", data)
                                }
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> {
                            ModeSelectionButtons(
                                userPreferencesManager,
                                mqttLinking,
                                selectedMode
                            )
                        }

                        1 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                ColorSelection(
                                    colors = colors,
                                    selectedColor = selectedColor,
                                    userPreferencesManager,
                                    mqttLinking
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                BrightnessControl(
                                    changedBrightness = brightness,
                                    userPreferencesManager,
                                    mqttLinking
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
