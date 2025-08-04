package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.content.Context
import android.util.Log
import com.bumperpick.bumperickUser.API.Model.DataXXXXXX
import com.bumperpick.bumperickUser.API.Model.fav_model
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.CustomerOffer
import com.bumperpick.bumperickUser.API.New_model.CustomerOfferDetail
import com.bumperpick.bumperickUser.API.New_model.DataXX
import com.bumperpick.bumperickUser.API.New_model.Faqmodel
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.API.New_model.OfferHistoryModel
import com.bumperpick.bumperickUser.API.New_model.banner_model
import com.bumperpick.bumperickUser.API.New_model.cartDetails
import com.bumperpick.bumperickUser.API.New_model.deletemodel
import com.bumperpick.bumperickUser.API.New_model.sub_categories
import com.bumperpick.bumperickUser.API.New_model.trendingSearchModel
import com.bumperpick.bumperickUser.Repository.Result.*
import com.bumperpick.bumperickUser.Screens.Home.Map.LocationData
import com.bumperpick.bumperickUser.Screens.Home.OfferFilter

import com.bumperpick.bumperpick_Vendor.API.FinalModel.Notification_model
import com.bumperpick.bumperpickvendor.API.Model.success_model
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
class OfferRepositoryImpl(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
    private val context: Context // Required for safeApiCall
) : OfferRepository {

    override suspend fun getOffers(
        filter: OfferFilter,
        showAds: Boolean,
    ): Result<List<Offer>> {
        val params = HashMap<String, String>()
        if (!filter.subcatId.isNullOrBlank()) {
            params["sub_category_id"] = filter.subcatId
        }

        // Add category_id if non-empty (for single category_id field)

        if (filter.categoriesId.isNotEmpty()) {
                Log.d("category_id",filter.categoriesId.toString())
            filter.categoriesId
                .filter { !it.equals("all") }
                .forEachIndexed { index, id ->
                params["category_id[$index]"] = id
            }
        }
        if(filter.sortBy.isNotEmpty()){
            params["sort_by"]=filter.sortBy
        }
        if(filter.distanceFilter.isNotEmpty()){
            Log.d("distance_filter",filter.distanceFilter)
            if(!filter.distanceFilter.equals("all")) {
                params["distance_filter"] = filter.distanceFilter
            }
        }
        if(filter.search.isNotEmpty()){
            params["search"] =filter.search
        }








        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.customer_offer(token,params) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(

                    response.data.offers
                )
                else Result.Error(response.data.message?:"")
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        val params = HashMap<String, String>()
        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.customer_offer(token,params) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data.categories)
                else Result.Error(response.data.message?:"")
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun getSubCategories(cat_id: Int): Result<List<sub_categories>> {
        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.getCategory() },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) {
                    val subcat = response.data.data.find { it.id == cat_id }
                    if (subcat != null) Result.Success(subcat.sub_categories)
                    else Result.Error("Subcategory not found")
                } else Result.Error(response.data.message)
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun getOfferDetails(id: String): Result<Offer> {
        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.offer_details(id, token) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data.data)
                else Result.Error(response.data.message)
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun addtoCart(id: String): Result<CustomerOfferDetail> {
        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.cart_add(token, id) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data)
                else Result.Error(response.data.message)
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun getCart(): Result<cartDetails> {
        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.cart_data(token) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data)
                else Result.Error(response.data.message)
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override  suspend fun getOfferHisotry(): Result<OfferHistoryModel> {

        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.offer_history(token) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data)
                else Result.Error(response.data.message)
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun reviewtheoffer(offerID:String,rating:String,review:String,): Result<success_model> {
        val map:HashMap<String,String> = hashMapOf()
        val token=dataStoreManager.getToken.firstOrNull()
        map["promotion_id"]=offerID
        map["rating"] =rating
        map["review"] =review
        map["token"]=token.toString()

        val result= safeApiCall(context=context,api={apiService.review_offer(map)}, refreshTokenApi ={apiService.refresh_token(it)},dataStoreManager=dataStoreManager)
         when(result){
             is ApiResult.Error -> {
                 return Result.Error(result.message)
             }
             is ApiResult.Success -> return Result.Success(result.data)
         }


    }

    override suspend fun fav_toogle(offerId: String): Result<success_model> {
        val map: HashMap<String, String> =hashMapOf()
        val token=dataStoreManager.getToken.firstOrNull()
        map["token"] =token.toString()
        map["promotion_id"] =offerId

        val result=safeApiCall(context=context,api={apiService.fav_toggle(map)},
            refreshTokenApi = {apiService.refresh_token(it)},dataStoreManager)
        when(result){
            is ApiResult.Error -> {
                return Result.Error(result.message)
            }
            is ApiResult.Success -> return Result.Success(result.data)
        }
    }

    override suspend fun fav_offer():  Result<List<DataXXXXXX>> {
        val token=dataStoreManager.getToken.firstOrNull()?:""
        val response=safeApiCall(context=context,api={apiService.getFavioutes(token)},
            refreshTokenApi = {apiService.refresh_token(it)},dataStoreManager)
        return when (response) {
            is ApiResult.Success -> {
                Log.d("fav_offer",response.data.toString())
                if (response.data.code == 200) Result.Success(response.data.data)
                else Result.Error(response.data.code.toString())
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun trendingSearch(): Result<trendingSearchModel> {
        val token=dataStoreManager.getToken.firstOrNull()?:""
        val response=safeApiCall(context=context,api={apiService.trendingSearch(token)},
            refreshTokenApi = {apiService.refresh_token(it)},dataStoreManager)
        when(response){
            is ApiResult.Error -> {
                return Result.Error(response.message)
            }
            is ApiResult.Success -> return Result.Success(response.data)
        }
    }

    override suspend fun FaqModel(): Result<Faqmodel> {
        val token=dataStoreManager.getToken.firstOrNull()?:""
        val response=safeApiCall(context=context, api = {
            apiService.faqs(token) },
            refreshTokenApi = {apiService.refresh_token(it)},dataStoreManager
            )
        return when(response){
            is ApiResult.Error -> {
                 Result.Error(response.message)
            }
            is ApiResult.Success ->  Result.Success(response.data)
        }
    }

    override suspend fun getUserId(): Result<String> {
        val userId = dataStoreManager.getUserId.firstOrNull()
        return if (!userId.isNullOrEmpty()) Result.Success(userId)
        else Result.Error("User ID not found")
    }

    override suspend fun deletecart(id: String): Result<deletemodel> {
        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.deleteCart(id, token) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data)
                else Result.Error(response.data.message)
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun notification():Result<Notification_model> {
        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")

        val response=safeApiCall(
            context = context,
            api = {apiService.notification(token)},
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )
        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data)
                else Result.Error(response.data.code.toString())
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun get_locationData(): Result<LocationData> {
        val locationData = dataStoreManager.getLocation.firstOrNull()
        Log.d("LocationData",locationData.toString())
        return if (locationData != null) Result.Success(locationData)
        else Result.Error("Location data not found")
    }

    override suspend fun banner(): Result<banner_model> {
        val response = safeApiCall(
            context = context,
            api = { apiService.bannerAPi() },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )
        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Success(response.data)
                else Error(response.data.code.toString())
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
}
    }
