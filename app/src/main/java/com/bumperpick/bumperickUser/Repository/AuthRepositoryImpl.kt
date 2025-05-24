package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.util.Log
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class AuthRepositoryImpl(
    private val dataStoreManager: DataStoreManager,val apiService: ApiService
) : AuthRepository {

    override suspend fun checkAlreadyLogin(): Result<Boolean> {
        return try {
            val userId = dataStoreManager.getUserId.firstOrNull()
            println("USERID $userId")
            Result.Success(!userId.isNullOrEmpty())
        } catch (e: Exception) {
            Result.Error("Failed to check login status", e)
        }
    }


    override suspend fun login(mobileNumber: String): Result<String> {
        return try {
            // Simulate API call or auth logic
            delay(1000)
            
            
            
            val generatedUserId = UUID.randomUUID().toString()
            dataStoreManager.saveUserId(generatedUserId)
            Result.Success(generatedUserId)
        } catch (e: Exception) {
            Result.Error("Login failed", e)
        }
    }

    override suspend fun sendOtp(mobileNumber: String): Result<String> {
        return try {
            val sendOtpResponse = safeApiCall {  apiService.cust_send_otp(mobileNumber.replace(" ",""))}
            Log.d("Phone Number",mobileNumber)
            when(sendOtpResponse) {

                is ApiResult.Success -> {
                    if(sendOtpResponse.data.code==200)Result.Success(sendOtpResponse.data.message)
                    else Result.Error(sendOtpResponse.data.message)
                }

                is ApiResult.Error -> {
                    Result.Error(sendOtpResponse.message)
                }
            }

        } catch (e: Exception) {
            Result.Error("Failed to send OTP", e)
        }
    }

    override suspend fun verifyOtp(mobileNumber: String,otp: String): Result<Boolean> {
        return try {
            val verify_Otp= safeApiCall { apiService.cust_verify_otp(mobileNumber.replace(" ",""),otp) }
            when(verify_Otp) {
                is ApiResult.Success->{
                    if(verify_Otp.data.code==200) Result.Success(true)
                    else Result.Error(verify_Otp.data.message)


                }
                is ApiResult.Error-> Result.Error(verify_Otp.message)
            }
        } catch (e: Exception) {
            Result.Error("OTP verification failed", e)
        }
    }
}
