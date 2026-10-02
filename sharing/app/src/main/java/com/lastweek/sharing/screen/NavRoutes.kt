package com.lastweek.sharing.screen

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoutes {
    @Serializable
    data object LoginScreen : NavRoutes()

    @Serializable
    data object AccountScreen : NavRoutes()

    @Serializable
    data object AliPayScreen : NavRoutes()

    @Serializable
    data object FloatingCenterLoginScreen : NavRoutes()
}