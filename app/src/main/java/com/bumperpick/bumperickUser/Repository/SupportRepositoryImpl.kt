package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.content.Context
import com.bumperpick.bumperickUser.API.New_model.tickerdetails
import com.bumperpick.bumperickUser.API.New_model.ticket_add_model
import com.bumperpick.bumperickUser.API.New_model.ticketmessage
import com.bumperpick.bumperpickvendor.API.Model.success_model
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall
import kotlinx.coroutines.flow.firstOrNull

class SupportRepositoryImpl( private val apiService: ApiService,
                             private val dataStoreManager: DataStoreManager,
                             private val context: Context ) : SupportRepository {
    override suspend fun ticketadd(
        subject: String,
        message: String
    ): Result<ticket_add_model> {
        val params = HashMap<String, String>()

        val token = dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        params["token"] =token
        params["subject"] =subject
        params["message"] =message


        val response= safeApiCall(context = context,
            api = {apiService.ticketAdd(params)},
            refreshTokenApi = {apiService.refresh_token(it)},
            dataStoreManager=dataStoreManager)

        return when(response){
            is ApiResult.Success -> {
                if (response.data.code  in 200..300) Result.Success(response.data)
                else Result.Error(response.data.message?:"")
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun tickets(): Result<ticketmessage> {
        val token =
            dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.tickets(token) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )
        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code  in 200..300) Result.Success(response.data)
                else Result.Error("Tickets not found")
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun tickerDetails(id: String): Result<tickerdetails> {
        val token =
            dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")
        val response = safeApiCall(
            context = context,
            api = { apiService.ticket_detail(id=id,token=token) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )
        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code  in 200..300) Result.Success(response.data)
                else Result.Error("Tickets not found")
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }

    override suspend fun ticketReply(id: String,message: String): Result<success_model> {
        val params = HashMap<String, String>()
        val token =
            dataStoreManager.getToken.firstOrNull() ?: return Result.Error("Token not found")

        params["token"]=token
        params["message"]=message
        val response = safeApiCall(
            context = context,
            api = { apiService.ticket_reply(id=id, map = params) },
            refreshTokenApi = { apiService.refresh_token(it) },
            dataStoreManager = dataStoreManager
        )
        return when (response) {
            is ApiResult.Success -> {
                if (response.data.code in 200..300) Result.Success(response.data)
                else Result.Error("Tickets not found")
            }

            is ApiResult.Error -> Result.Error(response.message)
        }
    }


}