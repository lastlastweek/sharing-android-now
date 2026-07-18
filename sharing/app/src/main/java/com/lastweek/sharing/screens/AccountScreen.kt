package com.lastweek.sharing.screens

import android.content.Context
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

val TelegramBlue = Color(0xFF50A2E3)
val TelegramGreyBg = Color(0xFFF1F5F9)
val TelegramTextPrimary = Color(0xFF1F2937)
val TelegramTextSecondary = Color(0xFF707070)
val TelegramOnlineGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val activity = LocalActivity.current

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("账号", color = Color.Black, fontSize = 19.sp, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TelegramGreyBg
                )
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = TelegramGreyBg,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                TelegramCard {
                    Text(
                        text = "个人信息",
                        color = TelegramBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                    )
                    InfoItem(
                        icon = Icons.Default.PhoneIphone,
                        title = "+86 158****9275",
                        subtitle = "手机号码"
                    )
                    TelegramDivider()
                    InfoItem(
                        icon = Icons.Default.Person,
                        title = "Alex",
                        subtitle = "用户名"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TelegramCard {
                    Text(
                        text = "一次买断",
                        color = TelegramBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                    )
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "永久秘钥",
                        actionText = "Verified",
                        actionColor = TelegramBlue
                    )
                    TelegramDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically // 让图片和文字在水平线上面对齐居中
                    ) {
                        Spacer(modifier = Modifier.width(15.dp))
                        Icon(
                            imageVector = Icons.Default.CreditCard, // 使用 Material 默认的“添加”图标，你也可以选择其他更合适的
                            contentDescription = null, // 对于纯装饰图标，设为 null 是可访问性的好习惯
                            tint = Color.Gray, // 图标颜色
                            modifier = Modifier
                                .size(24.dp) // 控制图标大小
                        )
                        Spacer(modifier = Modifier.width(210.dp))
                        ActionItem(title = "立即开通", color = Color.Black, onClick = {
                            coroutineScope.launch {
                                navController.navigate(NavRoutes.AliPayScreen) {
                                    popUpTo(NavRoutes.AccountScreen) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        })
                    }
                }

                Spacer(modifier = Modifier.height(280.dp))

                ActionItem(title = "退出登录", color = Color.Black, onClick = {
                    coroutineScope.launch {
                        // 清数据
                        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit { clear() }
                        // 跳页面
                        navController.navigate(NavRoutes.LoginScreen) {
                            popUpTo(NavRoutes.AccountScreen) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                })
            }
        }
    }
}

@Composable
fun TelegramCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        content = content
    )
}

@Composable
fun TelegramDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 64.dp, end = 16.dp),
        thickness = 0.5.dp,
        color = Color.LightGray.copy(alpha = 0.5f)
    )
}

@Composable
fun InfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Copy or Action */ }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TelegramTextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TelegramTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TelegramTextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    actionText: String,
    actionColor: Color = TelegramTextSecondary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Toggle or Go */ }
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TelegramTextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = title,
            color = TelegramTextPrimary,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = actionText,
            color = actionColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActionItem(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}