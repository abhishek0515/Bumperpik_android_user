package com.bumperpick.bumperickUser.Repository

import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.CustomerOfferDetail
import com.bumperpick.bumperickUser.API.New_model.DataXX
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.API.New_model.cartDetails
import com.bumperpick.bumperickUser.API.New_model.deletemodel
import com.bumperpick.bumperickUser.API.New_model.sub_categories

interface OfferRepository {
    suspend fun getOffers(): Result<List<Offer>>
     suspend fun getOfferDetails( id:String):Result<Offer>
     suspend fun addtoCart(id:String):Result<CustomerOfferDetail>
     suspend fun getCart():Result<cartDetails>
     suspend fun getUserId():Result<String>
    suspend fun deletecart(id: String):Result<deletemodel>
   suspend fun getCategories():Result< List<Category>>
    suspend fun getSubCategories(cat_id:Int):Result<List<sub_categories>>
}