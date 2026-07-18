package com.lastweek.sharing.screens

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {
    @Serializable
    data object LoginScreen : NavRoutes()

    @Serializable
    data object AccountScreen : NavRoutes()

    @Serializable
    data object AliPayScreen : NavRoutes()
}