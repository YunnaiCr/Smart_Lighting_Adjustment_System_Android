package com.example.lightingadjustment.screen

import androidx.activity.ComponentActivity
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lightingadjustment.R
import kotlinx.coroutines.launch



@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background4),
            contentDescription = "Login Interface Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = loadCustomFont(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("账号", fontFamily = loadCustomFont()) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码", fontFamily = loadCustomFont()) },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    coroutineScope.launch {
                        val user = userDao.getUser(username, password)
                        if (user != null) {
                            loginMessage = "登录成功"
                            navController.navigate("room")
                        } else {
                            loginMessage = "账号或密码错误"
                        }
                    }
                }) {
                    Text("登录", fontFamily = loadCustomFont())
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    navController.navigate("register")
                }) {
                    Text("注册", fontFamily = loadCustomFont())
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    (navController.context as? ComponentActivity)?.finish()
                }) {
                    Text("退出", fontFamily = loadCustomFont())
                }

                if (loginMessage.isNotEmpty()) {
                    Text(
                        text = loginMessage,
                        color = if (loginMessage == "登录成功") Color(0xFF2E7D32) else Color.Red, // 绿色 / 红色
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp),
                        fontFamily = loadCustomFont()
                    )
                }
            }
        }
    }
}
