package com.lastweek.sharing.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLogin: (mobile: String, smsCode: String) -> Unit
) {
    var mobile by remember {
        mutableStateOf("")
    }

    var smsCode by remember {
        mutableStateOf("")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.45f)
                ),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .clip(
                        RoundedCornerShape(26.dp)
                    )
                    .background(Color.White)
                    .padding(
                        horizontal = 24.dp,
                        vertical = 26.dp
                    )
            ) {

                // 标题 + 关闭按钮
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "登录",
                        modifier = Modifier.align(
                            Alignment.CenterStart
                        ),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "登录后继续使用",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier.height(28.dp)
                )

                // 手机号
                LoginTextField(
                    value = mobile,
                    onValueChange = { input ->
                        // 过滤非数字字符，并限制最多 11 位
                        mobile = input.filter { it.isDigit() }.take(11)
                    },
                    placeholder = "手机号",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // 验证码
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    LoginTextField(
                        value = smsCode,
                        onValueChange = { input ->
                            // 过滤非数字字符，并限制最多 6 位
                            smsCode = input.filter { it.isDigit() }.take(6)
                        },
                        placeholder = "验证码",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 100.dp)
                    )

                    TextButton(
                        onClick = {
                            // TODO: 发送验证码
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 6.dp)
                    ) {
                        Text(
                            text = "获取验证码",
                            color = Color.Black,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                // 登录按钮
                Button(
                    onClick = {
                        onLogin(
                            mobile,
                            smsCode
                        )
                    },
                    enabled = mobile.isNotBlank()
                            && smsCode.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        disabledContainerColor =
                            Color(0xFFE5E5E5),
                        contentColor = Color.White,
                        disabledContentColor =
                            Color(0xFF999999)
                    ),
                    elevation = ButtonDefaults
                        .buttonElevation(
                            defaultElevation = 0.dp
                        )
                ) {
                    Text(
                        text = "登录",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(52.dp)
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(
                Color(0xFFF5F5F5)
            )
            .padding(
                horizontal = 16.dp
            ),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black,
            fontSize = 15.sp
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF8A8A8A),
                        fontSize = 15.sp
                    )
                }
                innerTextField()
            }
        }
    )
}
