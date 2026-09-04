package com.lastweek.sharing.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lastweek.sharing.ui.MainActivity
import kotlinx.coroutines.delay

// 纯粹黑白无色系
private val MonoBlack = Color(0xFF000000)
private val MonoWhite = Color(0xFFFFFFFF)
private val MonoGrayDark = Color(0xFF999999)
private val MonoDisabled = Color(0xFFF3F3F3)

/**
 * 💡 纯 2D 平面微阴影修饰符（0 偏移量，4% 透明度均匀扩散，绝无 3D 立体下沉感）
 */
fun Modifier.flatSubtleShadow(borderRadius: Dp): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().asFrameworkPaint().apply {
            color = android.graphics.Color.WHITE
            setShadowLayer(
                6.dp.toPx(),
                0f,
                0f,
                Color(0x0A000000).toArgb()
            )
        }
        canvas.nativeCanvas.drawRoundRect(
            0f, 0f, size.width, size.height,
            borderRadius.toPx(), borderRadius.toPx(),
            paint
        )
    }
}

@Composable
fun LoginScreen(
    navController: NavHostController
) {
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var countdown by remember { mutableIntStateOf(0) }
    var isCounting by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000L)
            countdown--
        } else {
            isCounting = false
        }
    }

    Scaffold(
        containerColor = MonoWhite,
        // contentWindowInsets 确保 Scaffold 自动为主流全面屏留出状态栏高度，防止内容顶死到最上方
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 36.dp)
        ) {

            Spacer(modifier = Modifier.weight(1f))

            // ====== 2. 核心内容区（定格在屏幕上方 1/3 处） ======
            Text(
                text = "手机号登录",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MonoBlack
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ====== 3. 手机号输入框 ======
            TextField(
                value = phone,
                onValueChange = { input ->
                    if (input.length <= 11 && input.all { c -> c.isDigit() }) phone = input
                },
                placeholder = { Text("请输入手机号", color = MonoGrayDark, fontSize = 15.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "+86",
                            color = MonoBlack,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MonoWhite,
                    unfocusedContainerColor = MonoWhite,
                    disabledContainerColor = MonoWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MonoBlack
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .flatSubtleShadow(borderRadius = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ====== 4. 验证码输入框 ======
            TextField(
                value = code,
                onValueChange = { input ->
                    if (input.length <= 6 && input.all { c -> c.isDigit() }) code = input
                },
                placeholder = { Text("请输入验证码", color = MonoGrayDark, fontSize = 15.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    TextButton(
                        onClick = {
                            countdown = 60
                            isCounting = true
                        },
                        enabled = !isCounting && phone.length == 11,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MonoBlack,
                            disabledContentColor = MonoGrayDark
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = if (isCounting) "${countdown}s" else "获取验证码",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MonoWhite,
                    unfocusedContainerColor = MonoWhite,
                    disabledContainerColor = MonoWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MonoBlack
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .flatSubtleShadow(borderRadius = 8.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ====== 5. 登录按钮 ======
            val isButtonEnabled = phone.length == 11 && code.length >= 4

            Button(
                onClick = {
                    // 登录成功后调用
                    isLoading = true
                    val intent = Intent(context, MainActivity::class.java).apply {
                        // 3. 核心：清空登录页的任务栈，确保用户进入主页后按返回键不会再看到登录页
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                },
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(26.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonoBlack,
                    contentColor = MonoWhite,
                    disabledContainerColor = MonoDisabled,
                    disabledContentColor = MonoGrayDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .flatSubtleShadow(borderRadius = 26.dp)
            ) {
                Text(
                    text = "登录",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            // 💡 弹簧 B：底部给 2.5f 的巨大权重，强行把上面的核心内容往上顶
            Spacer(modifier = Modifier.weight(2.5f))
        }
    }
}