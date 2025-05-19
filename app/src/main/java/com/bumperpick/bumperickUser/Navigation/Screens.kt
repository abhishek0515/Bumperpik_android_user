package com.bumperpick.bumperickUser.Navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object StartScreen : Screen("startScreen")
    object Login : Screen("login")
    object Otp : Screen("otp/{$MOBILE_KEY}") {
        fun withMobile(mobile: String): String {
            return "otp/$mobile"
        }
    }
    object HomePage : Screen("homePage")

    companion object {
        const val MOBILE_KEY = "mobile"
    }
}
