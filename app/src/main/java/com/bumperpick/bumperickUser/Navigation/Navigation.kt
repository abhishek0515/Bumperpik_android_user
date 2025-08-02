package com.bumperpick.bumperickUser.Navigation


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.bumperpick.bumperickUser.Screens.Campaign.EventForm
import com.bumperpick.bumperickUser.Screens.Campaign.EventScreen
import com.bumperpick.bumperickUser.Screens.Component.YouTubeLiveVideoPlayer
import com.bumperpick.bumperickUser.Screens.EditProfile.EditProfile
import com.bumperpick.bumperickUser.Screens.Event.EventDetailScreen
import com.bumperpick.bumperickUser.Screens.Event.EventScreenMain
import com.bumperpick.bumperickUser.Screens.Faq.faq
import com.bumperpick.bumperickUser.Screens.Home.AccountClick
import com.bumperpick.bumperickUser.Screens.Home.Cart

import com.bumperpick.bumperickUser.Screens.Home.HomeClick
import com.bumperpick.bumperickUser.Screens.Home.Homepage
import com.bumperpick.bumperickUser.Screens.Home.OfferDetails

import com.bumperpick.bumperickUser.Screens.Home.OfferSearchScreen
import com.bumperpick.bumperickUser.Screens.Home.SubCategoryPage
import com.bumperpick.bumperickUser.Screens.Home.offer_subcat

import com.bumperpick.bumperickUser.Screens.Login.Login
import com.bumperpick.bumperickUser.Screens.OTP.OtpScreen
import com.bumperpick.bumperickUser.Screens.Splash.Splash
import com.bumperpick.bumperickUser.Screens.StartScreen.StartScreen
import com.bumperpick.bumperickUser.Screens.Support.SupportTicketsScreen
import com.bumperpick.bumperickUser.Screens.Support.TicketDetailsScreen
import com.bumperpick.bumperickUser.Screens.favourite.FavouriteScreen
import com.bumperpick.bumperickUser.data.ChooseLocation
import com.bumperpick.bumperpick_Vendor.Screens.NotificationScreen.NotificationScreen


import com.bumperpick.bumperpickvendor.Screens.OfferhistoryScreen.offerhistoryScreen
fun show_toast(message:String,context: Context){
    Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context= LocalContext.current


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

        composable(
            route=Screen.Offer_subcat.route,
            arguments = listOf(navArgument(Screen.SUB_CAT_ID){
                type=NavType.StringType
            },
                navArgument(Screen.SUB_CAT_NAME){
                type=NavType.StringType
            },
                    navArgument(Screen.CAT_ID){
                type=NavType.StringType
            },

                )
        ){backStackEntry->
            val sub_catid = backStackEntry.arguments?.getString(Screen.SUB_CAT_ID) ?: ""
            val catid = backStackEntry.arguments?.getString(Screen.CAT_ID) ?: ""
            val sub_catname = backStackEntry.arguments?.getString(Screen.SUB_CAT_NAME) ?: ""
            offer_subcat(subcatId = sub_catid, subcatName =sub_catname,cat_id=catid,
                onBackClick = {navController.popBackStack()},
                homeclick = {
                    when(it){
                        HomeClick.CartClick -> {}
                        HomeClick.LocationClick -> {}
                        is HomeClick.OfferClick -> { navController.navigate(Screen.OfferDetail.withOfferId(it.offerId))}
                        HomeClick.SearchClick -> {}
                        is HomeClick.CategoryClick -> {}
                        HomeClick.FavClick -> {}
                        HomeClick.NotifyClick -> {
                            navController.navigate(Screen.Notification.route)
                        }
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

                    HomeClick.SearchClick -> {
                       navController.navigate(Screen.Search.route)
                    }

                    is HomeClick.CategoryClick -> {
                        val category=it.cat
                        val categoryIdInt = category.id.toString()
                        navController.navigate(Screen.SubCatPage.witcatId(categoryIdInt, category.name))
                    }

                    HomeClick.FavClick -> {
                        navController.navigate(Screen.FavouriteScreen.route)
                    }

                    HomeClick.NotifyClick -> {
                        navController.navigate(Screen.Notification.route)
                    }
                }
            },
                open_subID = {sub_cat_id, sub_cat_name,cat_id ->
                    navController.navigate(Screen.Offer_subcat.withsubcatId(sub_cat_id,sub_cat_name,cat_id))
                },
                onEventClick = {
                    navController.navigate(Screen.EventScreen2.route)
                },
                onCampaignClick = {
                    navController.navigate(Screen.EventScreen.route)
                },
                onAccountClick = {
                    when(it){
                        AccountClick.Logout -> {
                            navController.navigate(Screen.Splash.route){
                                popUpTo(0) { inclusive = true }
                            }
                        }

                        AccountClick.EditAccount -> {
                            navController.navigate(Screen.EditProfile.route)
                        }

                        AccountClick.OfferHistory -> {
                            navController.navigate(Screen.OfferHistoryScreen.route)
                        }

                        AccountClick.EventClick -> {
                            navController.navigate(Screen.EventScreen2.route)
                        }

                        AccountClick.CampaignClick ->{navController.navigate(Screen.EventScreen.route)}
                        AccountClick.FavClick ->{
                            navController.navigate(Screen.FavouriteScreen.route)
                        }

                        AccountClick.FaqClick -> {
                            navController.navigate(Screen.Faq.route)
                        }
                        AccountClick.mailToAdmin -> {
                            navController.navigate(Screen.emailadmin.route)
                        }
                    }
                }
            )
        }
        composable(route=Screen.EditProfile.route){
            EditProfile(onBackClick = {
                navController.popBackStack()
            })
        }

        composable(route=Screen.Location.route){
            ChooseLocation(onBackClick = {
                navController.popBackStack()
            })
        }
        composable(route =Screen.Cart.route){
            Cart(){
                navController.popBackStack()
            }
        }
        composable(route=Screen.OfferDetail.route,
            arguments = listOf(navArgument(Screen.OFFER_ID){
                type=NavType.StringType
            },
                navArgument(Screen.is_offer_or_history){type=NavType.BoolType}),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://bumperick.com/offer/{${Screen.OFFER_ID}}/{${Screen.is_offer_or_history}}"
                }
            )
        ){ navBackStackEntry ->
            val offerId = navBackStackEntry.arguments?.getString(Screen.OFFER_ID)?:""
            val is_offer_or_history=navBackStackEntry.arguments?.getBoolean(Screen.is_offer_or_history)

            OfferDetails(offerId, onBackClick = {navController.popBackStack()},is_offer_or_history?:false)
        }
        composable(route=Screen.Search.route){
            OfferSearchScreen( onBackClick = {navController.popBackStack()},homeClick = {
                when(it){
                    HomeClick.CartClick -> {

                    }
                    HomeClick.LocationClick -> {

                    }
                    is HomeClick.OfferClick -> {
                        navController.navigate(Screen.OfferDetail.withOfferId(it.offerId))
                    }

                    HomeClick.SearchClick ->{

                    }
                    is HomeClick.CategoryClick -> {

                    }

                    HomeClick.FavClick -> {
                        
                    }

                    HomeClick.NotifyClick -> {
                        navController.navigate(Screen.Notification.route)
                    }
                }
            },
               )
        }

        composable(
            route = Screen.SubCatPage.route,
            arguments = listOf(
                navArgument(Screen.CAT_ID) {
                    type = NavType.StringType
                },
                navArgument(Screen.CAT_NAME) {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val catid = navBackStackEntry.arguments?.getString(Screen.CAT_ID) ?: ""
            val catname = navBackStackEntry.arguments?.getString(Screen.CAT_NAME) ?: ""

            SubCategoryPage(
                cat_id = catid.toInt(),
                selectedCategoryName = catname,
                onBackClick = {
                    navController.popBackStack()
                },
                open_subID = { sub_cat_id, sub_cat_name, cat_id ->
                    navController.navigate(
                        Screen.Offer_subcat.withsubcatId(sub_cat_id, sub_cat_name, cat_id)
                    )
                }
            )
        }


        composable(route=Screen.EventScreen.route){
            EventScreen(onBackClick = {
                navController.popBackStack()
            },
                gotoEventRegister = {
                    navController.navigate(Screen.EventForm.withid(eventId = it.id.toString(), eventName = it.title))
                },
                onFavClick = {
                    navController.navigate(Screen.FavouriteScreen.route)
                })
        }
        composable(route=Screen.EventForm.route,
            arguments = listOf(navArgument(Screen.EVENT_NAME){
                type=NavType.StringType
            },
                navArgument(Screen.EVENT_ID){
                    type=NavType.StringType
                }
            )
        ){ navBackStackEntry ->
            val eventname=navBackStackEntry.arguments?.getString(Screen.EVENT_NAME)?:""
            val eventId=navBackStackEntry.arguments?.getString(Screen.EVENT_ID)?:""
            EventForm(eventName = eventname, eventId = eventId, onBackClick = {
                navController.popBackStack()
            }, onRegistrationSuccess = {
                show_toast("Registered Successfully",context)
                navController.popBackStack()
            })

        }

        composable(route=Screen.OfferHistoryScreen.route){
            offerhistoryScreen(onBackClick = {
                navController.popBackStack()
            }, openOfferDetail = {
               navController.navigate(Screen.OfferDetail.withOfferId(it,true))
            })
        }
        composable(route=Screen.EventScreen2.route){
            EventScreenMain(
                onBackClick = {
                    navController.popBackStack()
                },
                onFavClick = {
                    navController.navigate(Screen.FavouriteScreen.route)
                },
                gotoEventDetail = {
                    navController.navigate(Screen.EventScreenDetail.withid(it))
                }
            )
        }
        composable(route= Screen.YoutubeView.route,
            arguments = listOf(navArgument(Screen.Url, builder = {type= NavType.StringType})))
        {
                navBackStackEntry->
            val url=navBackStackEntry.arguments?.getString(Screen.Url)?:""
            YouTubeLiveVideoPlayer(url)
        }
        composable(route=Screen.EventScreenDetail.route,
            arguments = listOf(navArgument(Screen.EVENT_ID, builder = {type=NavType.StringType}
        ))){navBackStackEntry->
            val eventId=navBackStackEntry.arguments?.getString(Screen.EVENT_ID)?:""
           EventDetailScreen(onBackClick = {navController.popBackStack()},eventId=eventId.toInt(),
               onOpenWebView = {
                   Log.d("navUrl",it)
                   navController.navigate(Screen.YoutubeView.withurl(it))

           })


            }
        composable(route= Screen.Faq.route){
            faq(onBackClick = {navController.popBackStack()})
        }
        composable(route= Screen.FavouriteScreen.route){
            FavouriteScreen(onBackClick = {navController.popBackStack()}, homeClick = {
                when(it){
                    HomeClick.CartClick -> {

                    }
                    HomeClick.LocationClick -> {

                    }
                    is HomeClick.OfferClick -> {
                        navController.navigate(Screen.OfferDetail.withOfferId(it.offerId))
                    }

                    HomeClick.SearchClick ->{

                    }
                    is HomeClick.CategoryClick -> {

                    }

                    HomeClick.FavClick -> {}
                    HomeClick.NotifyClick -> {
                        navController.navigate(Screen.Notification.route)
                    }
                }
            })
        }
        composable(route= Screen.emailadmin.route){

            SupportTicketsScreen(onBackPressed = {navController.popBackStack()},
                gototicketdetail ={id->
                    navController.navigate(Screen.ticketdetail.withid(id))

        })
        }

        composable(route= Screen.ticketdetail.route,
            arguments = listOf(
                navArgument(Screen.TICKET_ID,
                builder ={ NavType.StringType}
                )
            )
        )
        { navBackStackEntry->
            val ticketId=navBackStackEntry.arguments?.getString(Screen.TICKET_ID)?:""

            TicketDetailsScreen(ticketId = ticketId,
                onBackPressed = {
                navController.popBackStack()
            })

        }
        composable(route= Screen.Notification.route){
           NotificationScreen(onBackClick = {
                navController.popBackStack()
            })
        }

    }
}
