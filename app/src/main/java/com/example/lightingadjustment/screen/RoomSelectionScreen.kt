package com.example.lightingadjustment.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.lightingadjustment.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontWeight



// DataStore 属性
private val Context.dataStore by preferencesDataStore(name = "room_prefs")

class RoomSelectionViewModel(private val context: Context) : ViewModel() {

    private val ROOM1_KEY = stringPreferencesKey("room1_name")
    private val ROOM2_KEY = stringPreferencesKey("room2_name")

    private val _room1Name = MutableStateFlow("房间1")
    val room1Name: StateFlow<String> = _room1Name

    private val _room2Name = MutableStateFlow("房间2")
    val room2Name: StateFlow<String> = _room2Name

    init {
        // 从 DataStore 读取初始数据
        viewModelScope.launch {
            val prefs = context.dataStore.data.first()
            _room1Name.value = prefs[ROOM1_KEY] ?: "房间1"
            _room2Name.value = prefs[ROOM2_KEY] ?: "房间2"
        }
    }

    fun renameRoom(roomNumber: Int, newName: String) {
        viewModelScope.launch {
            context.dataStore.edit { prefs ->
                when(roomNumber) {
                    1 -> {
                        prefs[ROOM1_KEY] = newName
                        _room1Name.value = newName
                    }
                    2 -> {
                        prefs[ROOM2_KEY] = newName
                        _room2Name.value = newName
                    }
                }
            }
        }
    }
}

class RoomSelectionViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RoomSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomSelectionViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun RoomSelectionScreen(
    navController: NavHostController,
    context: Context = LocalContext.current
) {
    val viewModel: RoomSelectionViewModel = viewModel(
        factory = RoomSelectionViewModelFactory(context)
    )

    val room1Name by viewModel.room1Name.collectAsState()
    val room2Name by viewModel.room2Name.collectAsState()

    var renameDialogVisible by remember { mutableStateOf(false) }
    var renameTargetRoom by remember { mutableStateOf(0) }
    var renameText by remember { mutableStateOf(TextFieldValue("")) }

    if (renameDialogVisible) {
        AlertDialog(
            onDismissRequest = { renameDialogVisible = false },
            confirmButton = {
                TextButton(onClick = {
                    if (renameText.text.isNotBlank()) {
                        viewModel.renameRoom(renameTargetRoom, renameText.text)
                        renameDialogVisible = false
                    }
                }) {
                    Text("确定", fontFamily = loadCustomFont())
                }
            },
            dismissButton = {
                TextButton(onClick = { renameDialogVisible = false }) {
                    Text("取消", fontFamily = loadCustomFont())
                }
            },
            title = { Text("重命名房间", fontFamily = loadCustomFont()) },
            text = {
                TextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("输入新房间名称", fontFamily = loadCustomFont()) }
                )
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 背景图
        Image(
            painter = painterResource(id = R.drawable.background1), // 替换为你的背景图资源
            contentDescription = "背景图",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 内容层
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)),
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
                Text(
                    text = "恒光",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = loadCustomFont(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoomCard(
                    roomName = room1Name,
                    backgroundRes = R.drawable.background2,
                    onClick = { navController.navigate("third") },
                    onLongClick = {
                        renameTargetRoom = 1
                        renameText = TextFieldValue(room1Name)
                        renameDialogVisible = true
                    }
                )
                RoomCard(
                    roomName = room2Name,
                    backgroundRes = R.drawable.background7,
                    onClick = { navController.navigate("third") },
                    onLongClick = {
                        renameTargetRoom = 2
                        renameText = TextFieldValue(room2Name)
                        renameDialogVisible = true
                    }
                )
            }
        }
    }

}

@Composable
fun RoomCard(
    roomName: String,
    backgroundRes: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 150.dp, height = 250.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() }, onLongPress = { onLongClick() })
            }
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(listOf(Color.LightGray, Color.DarkGray))
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = "房间背景",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clip(RoundedCornerShape(16.dp))
        )
        Text(
            text = roomName,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
