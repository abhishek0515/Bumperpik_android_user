package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.content.Context
import com.bumperpick.bumperickUser.API.New_model.CustomerEventModel
import com.bumperpick.bumperickUser.API.New_model.DataXXXXXXXX
import com.bumperpick.bumperickUser.API.New_model.EventModel
import com.bumperpick.bumperickUser.API.New_model.EventRegisterModel
import com.bumperpick.bumperickUser.API.New_model.error_model
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.EventRegisterRequest
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull

class EventRepositoryImpl(val dataStoreManager: DataStoreManager,val apiService: ApiService,val context: Context):Event_campaign_Repository {
    override suspend fun getCampaign(): Result<EventModel> {
       val token=dataStoreManager.getToken.firstOrNull()?:return Result.Error("Token not found")

        val response= safeApiCall(
            context=context,
            api = {apiService.customer_campaign(token)},
            errorBodyParser = {
                    try {
                        Gson().fromJson(it, error_model::class.java)
                    } catch (e: Exception) {
                        error_model(message = "Unknown error format: $it")
                    }
                }

        )
        return when(response){
            is ApiResult.Success->{
                if(response.data.code==200) Result.Success(response.data)
                else Result.Error(response.data.message)
            }
            is ApiResult.Error-> Result.Error(response.error.message)

        }
    }



    override suspend fun registerCampaign(
        eventId: String,
        name: String,
        email: String,
        phone: String
    ): Result<EventRegisterModel> {
        val token=dataStoreManager.getToken.firstOrNull()?:return Result.Error("Token not found")

        val response= safeApiCall(
            context=context,
            api = {apiService.campaign_register(
                EventRegisterRequest(
                    token=token,
                    campaign_id = eventId,
                    name = name,
                    email = email,
                    phone = phone
                )
            )
             },
            errorBodyParser = {
                    try {
                        Gson().fromJson(it, error_model::class.java)
                    } catch (e: Exception) {
                        error_model(message = "Unknown error format: $it")
                    }
                }

        )
        return when(response){
            is ApiResult.Success->{
                if(response.data.code in 200..299) Result.Success(response.data)
                else Result.Error(response.data.message)
            }
            is ApiResult.Error-> Result.Error(response.error.message)

        }

    }

    override suspend fun getEvent(): Result<CustomerEventModel> {
        val token=dataStoreManager.getToken.firstOrNull()?:return Result.Error("Token not found")
        val response= safeApiCall(
            context=context,
            api = {apiService.get_event(token)},
            errorBodyParser = {
                    try {
                        Gson().fromJson(it, error_model::class.java)
                    } catch (e: Exception) {
                        error_model(message = "Unknown error format: $it")
                    }
                })
        return when(response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) Result.Success(response.data)
                else Result.Error(response.data.message)
            }
            is ApiResult.Error -> Result.Error(response.error.message)

        }
    }

    override suspend fun getEventByid(id: Int): Result<DataXXXXXXXX> {
        val token=dataStoreManager.getToken.firstOrNull()?:return Result.Error("Token not found")
        val response= safeApiCall(
            context=context,
            api = {apiService.get_event(token)},
            errorBodyParser = {
                    try {
                        Gson().fromJson(it, error_model::class.java)
                    } catch (e: Exception) {
                        error_model(message = "Unknown error format: $it")
                    }
                })
        return when(response) {
            is ApiResult.Success -> {
                if (response.data.code == 200) {
                    val event = response.data.data.find { it.id == id }
                    if (event != null) {
                        Result.Success(event)
                    } else {
                        Result.Error("Event not found")
                    }
                }
                else Result.Error(response.data.message)
            }
            is ApiResult.Error -> Result.Error(response.error.message)

        }

    }


}