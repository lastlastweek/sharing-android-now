package com.lastweek.sharing.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AvatarScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> }
) {
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val enable = account.isNotBlank() && password.isNotBlank()

    val buttonColor by animateColorAsState(
        if (enable) Color.Black else Color.DarkGray,
        label = ""
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(10.dp))

            MonochromeCircularAvatar(
                name = "Alex",
                size = 70.dp,
                backgroundColor = Color(0xFFF2F2F2),
                textColor = Color(0xFF000000),
                customFontSize = 32.sp // 💡 强行指定字号：不管头像多大，文字死守 32.sp，留白更多
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text("Alex", fontWeight = FontWeight.Bold, fontSize = 18.sp)

//            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "+86 1586789275",
                color = Color.Gray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun MonochromeCircularAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    backgroundColor: Color = Color(0xFF111111),
    textColor: Color = Color(0xFFFFFFFF),
    // 💡 新增：字体调整参数
    fontWeight: FontWeight = FontWeight.Medium, // 默认中粗
    fontFamily: FontFamily? = null,             // 默认系统字体族，可传入自定义字体
    customFontSize: TextUnit? = null            // 允许外部强行指定字号，不传则自动按比例计算
) {
    val displayChar = remember(name) {
        name.trim().firstOrNull()?.toString()?.uppercase() ?: "?"
    }

    // 💡 如果外部传入了 customFontSize 就用外部的，否则继续用 0.45f 黄金比例自动计算
    val fontSize = customFontSize ?: (size.value * 0.45f).sp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayChar,
            color = textColor,
            fontSize = fontSize,
            fontWeight = fontWeight, // 💡 应用调整后的字重
            fontFamily = fontFamily, // 💡 应用调整后的字体族
            letterSpacing = 0.sp
        )
    }
}