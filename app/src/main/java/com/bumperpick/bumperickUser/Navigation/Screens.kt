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

    object OfferDetail:Screen("offerDetail/{$OFFER_ID}/{$is_offer_or_history}"){
        fun withOfferId(offerId:String,is_offer_or_history: Boolean =false):String{
            return "offerDetail/$offerId/$is_offer_or_history"
        }
    }
    object Location:Screen("location")

    object Cart:Screen("cart")

    companion object {
        const val MOBILE_KEY = "mobile"
        const val OFFER_ID ="offer_id"
        const val is_offer_or_history="is_offer_or_history"
        const val SUB_CAT_ID="sub_cat_id"
        const val SUB_CAT_NAME="sub_cat_name"
        const val CAT_ID="cat_id"
        const val CAT_NAME="cat_name"
        const val EVENT_NAME="event_name"
        const val EVENT_ID="event_id"
        const val Url="url"
        const val TICKET_ID="ticket_id"
    }
    object EditProfile:Screen("edit_profile")
    object Search:Screen("search")

    object Offer_subcat:Screen( "offer_subcat/{$SUB_CAT_ID}/{$SUB_CAT_NAME}/{$CAT_ID}"){
        fun withsubcatId(subcat_id:String,subCatName:String,catId:String):String{
            return "offer_subcat/$subcat_id/$subCatName/$catId"
        }
    }
    object SubCatPage: Screen("subcatpage/{$CAT_NAME}/{$CAT_ID}") {
        fun witcatId(cat_id: String, cat_name: String): String {
            return "subcatpage/$cat_name/$cat_id"
        }
    }

    object EventScreen:Screen("eventScreen")

    object EventForm:Screen("event_form/{$EVENT_NAME}/{$EVENT_ID}"){
        fun withid(eventName:String,eventId:String):String{
            return "event_form/$eventName/$eventId"
        }
    }

    object OfferHistoryScreen:Screen("offerHistoryScreen")

    object EventScreen2:Screen("eventScreen2")

    object EventScreenDetail:Screen("eventScreenDetail/{$EVENT_ID}"){
        fun withid(eventId:Int):String{
            return "eventScreenDetail/$eventId"
        }
    }

    object YoutubeView: Screen("youtubeview/{$Url}"){
        fun withurl(url:String):String{
            return "youtubeview/$url"
        }
    }

    object FavouriteScreen: Screen("favouritescreen")


    object Faq: Screen("faq")
    object emailadmin: Screen("emailadmin")

    object ticketdetail: Screen("ticket_detail/{$TICKET_ID}"){
        fun withid(id: String): String="ticket_detail/$id"
    }
    object Notification: Screen("notification")


}
