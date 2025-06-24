package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.util.Log
import com.bumperpick.bumperickUser.API.New_model.profile_model
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.UUID

class AuthRepositoryImpl(
    private val dataStoreManager: DataStoreManager,val apiService: ApiService
) : AuthRepository {

    override suspend fun checkAlreadyLogin(): Result<Boolean> {
        return try {
            val userId = dataStoreManager.getToken.firstOrNull()
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

    override suspend fun resendOtp(mobileNumber: String): Result<String> {
        return try {
            val sendOtpResponse = safeApiCall {  apiService.cust_re_send_otp(mobileNumber.replace(" ",""))}
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
                    if(verify_Otp.data.code==200){
                        dataStoreManager.saveUserId(verify_Otp.data.meta.token,verify_Otp.data.data.customer_id.toString())
                        Result.Success(true)
                    }
                    else Result.Error(verify_Otp.data.message)


                }
                is ApiResult.Error-> Result.Error(verify_Otp.message)
            }
        } catch (e: Exception) {
            Result.Error("OTP verification failed", e)
        }
    }
    fun File.toMultipartPart(
        partName: String = "image",
        contentType: String = "application/x-www-form-urlencoded"
    ): MultipartBody.Part {
        val requestBody = this.asRequestBody(contentType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, this.name, requestBody)
    }
    override suspend fun getProfile(): Result<profile_model> {
        return try {
            val userId = dataStoreManager.getToken.firstOrNull()
            val get_profile = safeApiCall { apiService.getProfile(token = userId?:"") }
            when(get_profile) {
                is ApiResult.Success->{
                    if(get_profile.data.code==200){
                        Result.Success(get_profile.data)
                    }
                    else Result.Error(get_profile.data.message)


                }
                is ApiResult.Error-> Result.Error(get_profile.message)
            }
        }
        catch (e: Exception) {
            Result.Error("OTP verification failed", e)
        }
    }

    override suspend fun updateProfile(image: File?, name: String, email: String,phone:String):Result<profile_model> {
        return try {
            val userId = dataStoreManager.getToken.firstOrNull()
            val map = mutableMapOf<String, RequestBody>()
            map["token"]=userId!!.toRequestBody("text/plain".toMediaType())
            map["name"]=name.toRequestBody("text/plain".toMediaType())
            map["email"]=email.toRequestBody("text/plain".toMediaType())
            map["phone_number"]=phone.toRequestBody("text/plain".toMediaType())

            val update= safeApiCall { apiService.update_profile(map,image=
                if(image ==null) null else    image.toMultipartPart()
            ) }
            when(update){
                is ApiResult.Success->{
                    if(update.data.code==200){
                        Result.Success(update.data)
                    }
                    else Result.Error(update.data.message)


                }
                is ApiResult.Error-> Result.Error(update.message)
            }
        } catch (e: Exception) {
            Result.Error("OTP verification failed", e)
        }
    }
}
