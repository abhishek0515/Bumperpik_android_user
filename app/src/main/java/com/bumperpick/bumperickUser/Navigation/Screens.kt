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

    object OfferDetail:Screen("offerDetail/{$OFFER_ID}"){
        fun withOfferId(offerId:String):String{
            return "offerDetail/$offerId"
        }
    }
    object Location:Screen("location")

    object Cart:Screen("cart")

    companion object {
        const val MOBILE_KEY = "mobile"
        const val OFFER_ID ="offer_id"
        const val SUB_CAT_ID="sub_cat_id"
        const val SUB_CAT_NAME="sub_cat_name"
    }
    object EditProfile:Screen("edit_profile")
    object Search:Screen("search")

    object Offer_subcat:Screen( "offer_subcat/{$SUB_CAT_ID}/{$SUB_CAT_NAME}"){
        fun withsubcatId(subcat_id:String,subCatName:String):String{
            return "offer_subcat/$subcat_id/$subCatName"
        }
    }
}
