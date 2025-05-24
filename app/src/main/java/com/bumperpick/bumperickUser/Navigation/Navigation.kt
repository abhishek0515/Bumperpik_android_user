package com.bumperpick.bumperickUser.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bumperpick.bumperickUser.Screens.Home.Homepage
import com.bumperpick.bumperickUser.Screens.Login.Login
import com.bumperpick.bumperickUser.Screens.OTP.OtpScreen
import com.bumperpick.bumperickUser.Screens.Splash.Splash
import com.bumperpick.bumperickUser.Screens.StartScreen.StartScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        // Splash
        composable(Screen.Splash.route) {
            Splash(gotoScreen = {
                navController.navigate(it.route) {

                }
            })
        }

        // Start
        composable(Screen.StartScreen.route) {
            StartScreen(gotoLogin = {
                navController.navigate(Screen.Login.route) {

                }
            })
        }

        // Login
        composable(Screen.Login.route) {
            Login(onLoginSuccess = { mobile ,isMobile->
                if(isMobile)navController.navigate(Screen.Otp.withMobile(mobile))
                else navController.navigate(Screen.HomePage.route)


            })
        }

        // OTP
        composable(
            route = Screen.Otp.route,
            arguments = listOf(navArgument(Screen.MOBILE_KEY) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val mobile = backStackEntry.arguments?.getString(Screen.MOBILE_KEY) ?: ""

            OtpScreen(
                mobile = mobile,
                onBackClick = {
                    navController.popBackStack() // ✅ Will now correctly go back to Login
                },
                onOtpVerify = {
                   navController.navigate(Screen.HomePage.route)
                }
            )
        }



        composable(route=Screen.HomePage.route){
            Homepage()
        }
    }
}
