package com.lastweek.sharing.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lastweek.sharing.R

// 微信经典配色
val WeChatGreen = Color(0xFF07C160)
val WeChatBg = Color(0xFFF7F7F7)
val WeChatTextBlack = Color(0xFF191919)
val WeChatLine = Color(0xFFEAEAEA)
val AlipayBlue = Color(0xFF1677FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AliPayScreen(navController: NavHostController) {
    // 控制底部支付收银台弹窗的显示状态
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 模拟账单数据
    val merchantName = "杭州因果论科技有限公司"
    val amount = "50.00"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack() // ✅ 仅仅把当前支付宝页面销毁，自然回到上一层
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WeChatBg
                )
            )
        },
        containerColor = WeChatBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Text(text = merchantName, fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "¥$amount", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = WeChatTextBlack)

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically // 让图片和文字在水平线上面对齐居中
            ) {
                // 支付宝图标
                Image(
                    painter = painterResource(R.drawable.ic_alipay),
                    contentDescription = "支付宝",
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.width(4.dp)) // 保持合适的间距

                // 支付宝字样
                Text(
                    text = "支付宝支付",
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 微信标志性的绿色大按钮
            Button(
                onClick = { showBottomSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AlipayBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("确认并支付", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // 底部收银台弹窗
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            dragHandle = null // 隐藏默认的顶部横条，更接近微信视觉
        ) {
            WeChatCashierContent(
                merchantName = merchantName,
                amount = amount,
                onClose = { showBottomSheet = false }
            )
        }
    }
}

/**
 * 微信底部收银台具体内容
 */
@Composable
fun WeChatCashierContent(
    merchantName: String,
    amount: String,
    onClose: () -> Unit
) {
    // 记住输入的密码
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // 防虚拟导航栏遮挡
    ) {
        // 1. 顶部标题栏
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterStart).size(24.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "关闭", tint = WeChatTextBlack)
            }
            Text(
                text = "请输入支付密码",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        HorizontalDivider(color = WeChatLine, thickness = 0.5.dp)

        // 2. 商家信息与金额
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = merchantName, fontSize = 14.sp, color = WeChatTextBlack)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "¥$amount", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = WeChatTextBlack)
        }

        // 3. 支付方式选择条
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* 切换银行卡逻辑 */ }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "支付方式", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "招商银行储蓄卡(8888)", fontSize = 14.sp, color = WeChatTextBlack)
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "更多", tint = Color.Gray)
        }

        // 4. 六位方格密码输入框
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(4.dp)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0 until 6) {
                val hasChar = password.length > i
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasChar) {
                        // 经典密码圆点
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(WeChatTextBlack, shape = RoundedCornerShape(5.dp))
                        )
                    }
                    // 绘制格子之间的分割线
                    if (i < 5) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(0.5.dp)
                                .background(Color(0xFFE0E0E0))
                                .align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}