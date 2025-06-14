package com.bumperpick.bumperickUser.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bumperpick.bumperickUser.Screens.Home.Cart
import com.bumperpick.bumperickUser.Screens.Home.ChooseLocation
import com.bumperpick.bumperickUser.Screens.Home.HomeClick
import com.bumperpick.bumperickUser.Screens.Home.Homepage
import com.bumperpick.bumperickUser.Screens.Home.OfferDetails
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
                    popUpTo(Screen.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        // Start
        composable(Screen.StartScreen.route) {
            StartScreen(gotoLogin = {
                navController.navigate(Screen.Login.route) {
                    launchSingleTop = true
                }
            })
        }

        // Login
        composable(Screen.Login.route) {
            Login(onLoginSuccess = { mobile, isMobile ->
                if (isMobile) {
                    navController.navigate(Screen.Otp.withMobile(mobile)) {
                        launchSingleTop = true
                    }
                } else {
                    navController.navigate(Screen.HomePage.route) {
                        popUpTo(Screen.StartScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
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
                    navController.popBackStack()
                },
                onOtpVerify = {
                    navController.navigate(Screen.HomePage.route) {
                        popUpTo(Screen.StartScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // HomePage
        composable(route = Screen.HomePage.route) {
            Homepage(onHomeClick = {
                when(it){
                    HomeClick.CartClick -> {
                        navController.navigate(Screen.Cart.route)
                    }
                    is HomeClick.OfferClick -> {
                        navController.navigate(Screen.OfferDetail.withOfferId(it.offerId))
                    }

                    HomeClick.LocationClick -> {
                        navController.navigate(Screen.Location.route)
                    }
                }
            })
        }

        composable(route=Screen.Location.route){
            ChooseLocation{
                navController.popBackStack()
            }
        }
        composable(route =Screen.Cart.route){
            Cart(){
                navController.popBackStack()
            }
        }
        composable(route=Screen.OfferDetail.route,
            arguments = listOf(navArgument(Screen.OFFER_ID){
                type=NavType.StringType
            })
        ){ navBackStackEntry ->
            val offerId = navBackStackEntry.arguments?.getString(Screen.OFFER_ID)?:""
            OfferDetails(offerId){
                navController.popBackStack()
            }
        }
    }
}
