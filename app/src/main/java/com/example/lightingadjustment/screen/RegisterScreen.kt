package com.example.lightingadjustment.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lightingadjustment.R
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var registerMessage by remember { mutableStateOf("") }
    var messageColor by remember { mutableStateOf(Color.Red) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景图
        Image(
            painter = painterResource(id = R.drawable.background4),
            contentDescription = "Register Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 标题栏
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "用户注册",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = loadCustomFont(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        registerMessage = ""  // 输入时清空提示
                    },
                    label = { Text("账号", fontFamily = loadCustomFont()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        registerMessage = ""  // 输入时清空提示
                    },
                    label = { Text("密码", fontFamily = loadCustomFont()) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val trimmedUsername = username.trim()
                            val trimmedPassword = password.trim()
                            val existingUser = userDao.getUserByUsername(trimmedUsername)

                            Log.d("RegisterScreen", "Checking user: $trimmedUsername, existingUser=$existingUser")

                            when {
                                trimmedUsername.isBlank() || trimmedPassword.isBlank() -> {
                                    registerMessage = "账号和密码不能为空"
                                    messageColor = Color.Red
                                }
                                existingUser != null -> {
                                    registerMessage = "账号已存在"
                                    messageColor = Color.Red
                                }
                                else -> {
                                    userDao.insertUser(User(trimmedUsername, trimmedPassword))
                                    registerMessage = "注册成功"
                                    messageColor = Color.Green
                                    username = ""
                                    password = ""
                                    navController.navigate("home") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("注册", fontFamily = loadCustomFont())
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("退出", fontFamily = loadCustomFont())
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = registerMessage,
                    color = messageColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
