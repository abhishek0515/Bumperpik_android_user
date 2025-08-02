package com.bumperpick.bumperickUser.Repository

import com.bumperpick.bumperickUser.API.Model.DataXXXXXX
import com.bumperpick.bumperickUser.API.Model.fav_model
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.CustomerOffer
import com.bumperpick.bumperickUser.API.New_model.CustomerOfferDetail
import com.bumperpick.bumperickUser.API.New_model.DataXX
import com.bumperpick.bumperickUser.API.New_model.Faqmodel
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.API.New_model.OfferHistoryModel
import com.bumperpick.bumperickUser.API.New_model.cartDetails
import com.bumperpick.bumperickUser.API.New_model.deletemodel
import com.bumperpick.bumperickUser.API.New_model.sub_categories
import com.bumperpick.bumperickUser.API.New_model.trendingSearchModel
import com.bumperpick.bumperickUser.Screens.Home.OfferFilter
import com.bumperpick.bumperickUser.data.LocationData
import com.bumperpick.bumperpick_Vendor.API.FinalModel.Notification_model
import com.bumperpick.bumperpickvendor.API.Model.success_model

interface OfferRepository {
    suspend fun getOffers(filter: OfferFilter,showads: Boolean): Result<List<Offer>>
     suspend fun getOfferDetails( id:String):Result<Offer>
     suspend fun addtoCart(id:String):Result<CustomerOfferDetail>
     suspend fun getCart():Result<cartDetails>
     suspend fun getUserId():Result<String>
    suspend fun deletecart(id: String):Result<deletemodel>
   suspend fun getCategories():Result< List<Category>>
    suspend fun getSubCategories(cat_id:Int):Result<List<sub_categories>>
    suspend fun getOfferHisotry(): Result<OfferHistoryModel>
    suspend fun reviewtheoffer(offerID:String,rating:String,review:String):Result<success_model>

    suspend fun fav_toogle(offerId:String): Result<success_model>

    suspend fun fav_offer(): Result<List<DataXXXXXX>>
    suspend fun trendingSearch(): Result<trendingSearchModel>

    suspend fun FaqModel(): Result<Faqmodel>
    suspend fun notification():Result<Notification_model>

    suspend fun get_locationData():Result<LocationData>

}