package com.lastweek.sharing.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun FloatingCenterLoginScreen(navController: NavHostController) {
    var showLogin by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showLogin) {
            LoginDialog(
                onDismiss = {
                    showLogin = false
                },
                onLogin = { phone, smsCode ->

                }
            )
        }
    }
}