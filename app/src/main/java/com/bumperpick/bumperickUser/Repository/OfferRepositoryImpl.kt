package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.CustomerOfferDetail
import com.bumperpick.bumperickUser.API.New_model.DataXX
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.API.New_model.cartDetails
import com.bumperpick.bumperickUser.API.New_model.deletemodel
import com.bumperpick.bumperickUser.API.New_model.sub_categories
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall
import kotlinx.coroutines.flow.firstOrNull

class OfferRepositoryImpl(val apiService: ApiService, val dataStoreManager: DataStoreManager):OfferRepository {
    override suspend fun getOffers( subcat_id:String,cat_id: String): Result<List<Offer>> {
        val token=dataStoreManager.getToken.firstOrNull()
        val response = safeApiCall { apiService.customer_offer(token!!,subcat_id,cat_id) }
        when(response){
            is ApiResult.Success->{
                if(response.data.code==200) return Result.Success(response.data.offers)
                else return Result.Error(response.data.message)
            }

            is ApiResult.Error ->{
                return Result.Error(response.message)
            }
        }

    }

    override suspend fun getCategories():Result< List<Category>>{
        val token=dataStoreManager.getToken.firstOrNull()
        val response = safeApiCall { apiService.customer_offer(token!!) }
        when(response){
            is ApiResult.Success->{
                if(response.data.code==200) return Result.Success(response.data.categories)
                else return Result.Error(response.data.message)
            }

            is ApiResult.Error ->{
                return Result.Error(response.message)
            }
        }
    }

    override suspend fun getSubCategories(cat_id: Int): Result<List<sub_categories>> {
         val token=dataStoreManager.getToken.firstOrNull()
        val response= safeApiCall { apiService.getCategory() }
        when(response){
            is ApiResult.Success->{
                if(response.data.code==200) {
                    val data=response.data.data
                    val subcat=data.find { it.id==cat_id }
                    if(subcat!=null){
                        return Result.Success(subcat.sub_categories)
                    }
                    else{
                        return Result.Error("Sub category not found")
                    }
                }
                else return Result.Error(response.data.message)
            }
            is ApiResult.Error->{
                return Result.Error(response.message)
            }
        }
    }

    override suspend fun getOfferDetails(id: String): Result<Offer> {
        val token =dataStoreManager.getToken.firstOrNull()
        val response = safeApiCall { apiService.offer_details(id,token!!) }
        when(response){
            is ApiResult.Success->{
                if(response.data.code==200) return Result.Success(response.data.data)
                else return Result.Error(response.data.message)
            }
            is ApiResult.Error->{
                return Result.Error(response.message)
            }
        }

    }

    override suspend fun addtoCart(id: String): Result<CustomerOfferDetail> {
        val token=dataStoreManager.getToken.firstOrNull()
        val response = safeApiCall { apiService.cart_add(token!!,id) }
        when(response){
            is ApiResult.Success->{
                if(response.data.code==200) return Result.Success(response.data)
                else return Result.Error(response.data.message)
            }
            is ApiResult.Error->{
                return Result.Error(response.message)
            }
        }

    }

    override suspend fun getCart(): Result<cartDetails> {
        val token=dataStoreManager.getToken.firstOrNull()
        val response = safeApiCall { apiService.cart_data(token!!) }
        when(response){
            is ApiResult.Success->{
                if(response.data.code==200) return Result.Success(response.data)
                else return Result.Error(response.data.message)
            }
            is ApiResult.Error->{
                return Result.Error(response.message)


            }
        }

    }

    override suspend fun getUserId(): Result<String> {
        return Result.Success(dataStoreManager.getUserId.firstOrNull()!!)
    }

    override suspend fun deletecart(id: String): Result<deletemodel> {
        val token =dataStoreManager.getToken.firstOrNull()
        val response = safeApiCall { apiService.deleteCart(id,token.toString()) }
        when(response){
            is ApiResult.Success->{
                if(response.data.code==200) return Result.Success(response.data)
                else return Result.Error(response.data.message)
            }
            is ApiResult.Error->{
                return Result.Error(response.message)
            }
        }

    }
}