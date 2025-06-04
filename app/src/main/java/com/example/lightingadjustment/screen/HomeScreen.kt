package com.example.lightingadjustment.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lightingadjustment.R
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign


@Composable
fun HomeScreen(navController: NavHostController) {
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
                Text(text = "恒光",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = loadCustomFont(),
                    textAlign = TextAlign.Center,
                    color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(32.dp))
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
                Button(onClick = { navController.navigate("room") }) {
                    Text("登录", fontFamily = loadCustomFont())
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* Navigate to Registration Screen */ }) {
                    Text("注册", fontFamily = loadCustomFont())
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { (navController.context as? ComponentActivity)?.finish() }) {
                    Text("退出", fontFamily = loadCustomFont())
                }
            }
        }
    }
}
