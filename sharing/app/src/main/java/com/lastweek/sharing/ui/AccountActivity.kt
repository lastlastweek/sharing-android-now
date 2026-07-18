package com.lastweek.sharing.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lastweek.sharing.screens.AccountScreen
import com.lastweek.sharing.screens.AliPayScreen
import com.lastweek.sharing.screens.LoginScreen
import com.lastweek.sharing.screens.NavRoutes

class AccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = NavRoutes.AccountScreen
            ){
                composable<NavRoutes.LoginScreen> {
                    LoginScreen(navController)
                }

                composable<NavRoutes.AccountScreen> {
                    AccountScreen(navController)
                }

                composable<NavRoutes.AliPayScreen> {
                    AliPayScreen(navController)
                }
            }
        }
    }
}