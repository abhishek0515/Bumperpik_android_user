package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.content.Context
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.CustomerOfferDetail
import com.bumperpick.bumperickUser.API.New_model.DataXX
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.API.New_model.OfferHistoryModel
import com.bumperpick.bumperickUser.API.New_model.cartDetails
import com.bumperpick.bumperickUser.API.New_model.deletemodel
import com.bumperpick.bumperickUser.API.New_model.sub_categories
import com.bumperpick.bumperpickvendor.API.Model.success_model
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall
import kotlinx.coroutines.flow.firstOrNull
class OfferRepositoryImpl(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
    private val context: Context // Required for safeApiCall
) : OfferRepository {

    override suspend fun getOffers(subcat_id: String, cat_id: String): Result<List<Offer>> {
        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.customer_offer(token, subcat_id, cat_id) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data.offers)
                else Result.Error(response.data.message)
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.customer_offer(token) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )

        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data.categories)
                else Result.Error(response.data.message)
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
        map["review"] =rating
        map["token"]=token.toString()

        val result= safeApiCall(context=context,api={apiService.review_offer(map)}, refreshTokenApi ={apiService.refresh_token(it)},dataStoreManager=dataStoreManager)
         when(result){
             is ApiResult.Error -> {
                 return Result.Error(result.message)
             }
             is ApiResult.Success -> return Result.Success(result.data)
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
}
