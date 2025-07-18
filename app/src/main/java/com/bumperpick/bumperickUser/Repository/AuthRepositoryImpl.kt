package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.content.Context
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
    private val dataStoreManager: DataStoreManager,
    private val apiService: ApiService,
    private val context: Context,
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
            delay(1000)
            val generatedUserId = UUID.randomUUID().toString()
            Result.Success(generatedUserId)
        } catch (e: Exception) {
            Result.Error("Login failed", e)
        }
    }

    override suspend fun sendOtp(mobileNumber: String): Result<String> {
        return try {
            val sendOtpResponse = safeApiCall(
                context = context,
                api = { apiService.cust_send_otp(mobileNumber.replace(" ", "")) },
                refreshTokenApi = { token -> apiService.refresh_token(token) },
                dataStoreManager = dataStoreManager
            )

            when (sendOtpResponse) {
                is ApiResult.Success -> {
                    if (sendOtpResponse.data.code == 200) Result.Success(sendOtpResponse.data.message)
                    else Result.Error(sendOtpResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(sendOtpResponse.message)
            }

        } catch (e: Exception) {
            Result.Error("Failed to send OTP", e)
        }
    }

    override suspend fun resendOtp(mobileNumber: String): Result<String> {
        return try {
            val sendOtpResponse = safeApiCall(
                context = context,
                api = { apiService.cust_re_send_otp(mobileNumber.replace(" ", "")) },
                refreshTokenApi = { token -> apiService.refresh_token(token) },
                dataStoreManager = dataStoreManager
            )

            when (sendOtpResponse) {
                is ApiResult.Success -> {
                    if (sendOtpResponse.data.code == 200) Result.Success(sendOtpResponse.data.message)
                    else Result.Error(sendOtpResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(sendOtpResponse.message)
            }

        } catch (e: Exception) {
            Result.Error("Failed to resend OTP", e)
        }
    }

    override suspend fun verifyOtp(mobileNumber: String, otp: String): Result<Boolean> {
        return try {
            val verifyOtpResponse = safeApiCall(
                context = context,
                api = { apiService.cust_verify_otp(mobileNumber.replace(" ", ""), otp) },
                refreshTokenApi = { token -> apiService.refresh_token(token) },
                dataStoreManager = dataStoreManager
            )

            when (verifyOtpResponse) {
                is ApiResult.Success -> {
                    if (verifyOtpResponse.data.code == 200) {
                        dataStoreManager.saveUserId(
                            verifyOtpResponse.data.meta.token,
                            verifyOtpResponse.data.data.customer_id.toString()
                        )
                        Result.Success(true)
                    } else Result.Error(verifyOtpResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(verifyOtpResponse.message)
            }
        } catch (e: Exception) {
            Result.Error("OTP verification failed", e)
        }
    }

    override suspend fun getProfile(): Result<profile_model> {
        return try {
            val getProfileResponse = safeApiCall(
                context = context,
                api = {
                    val token = dataStoreManager.getToken.firstOrNull()
                    apiService.getProfile(token = token ?: "")
                },
                refreshTokenApi = { token -> apiService.refresh_token(token) },
                dataStoreManager = dataStoreManager
            )

            when (getProfileResponse) {
                is ApiResult.Success -> {
                    if (getProfileResponse.data.code == 200) {
                        Result.Success(getProfileResponse.data)
                    } else Result.Error(getProfileResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(getProfileResponse.message)
            }
        } catch (e: Exception) {
            Result.Error("Get profile failed", e)
        }
    }

    override suspend fun updateProfile(
        image: File?,
        name: String,
        email: String,
        phone: String
    ): Result<profile_model> {
        return try {
            val updateResponse = safeApiCall(
                context = context,
                api = {
                    val token = dataStoreManager.getToken.firstOrNull()?.toRequestBody("text/plain".toMediaType())
                    val map = mutableMapOf<String, RequestBody>()
                    map["token"] = token!!
                    map["name"] = name.toRequestBody("text/plain".toMediaType())
                    map["email"] = email.toRequestBody("text/plain".toMediaType())
                    map["phone_number"] = phone.toRequestBody("text/plain".toMediaType())

                    apiService.update_profile(
                        map,
                        image = image?.toMultipartPart()
                    )
                },
                refreshTokenApi = { token -> apiService.refresh_token(token) },
                dataStoreManager = dataStoreManager
            )

            when (updateResponse) {
                is ApiResult.Success -> {
                    if (updateResponse.data.code == 200) {
                        Result.Success(updateResponse.data)
                    } else Result.Error(updateResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(updateResponse.message)
            }
        } catch (e: Exception) {
            Result.Error("Update profile failed", e)
        }
    }

    fun File.toMultipartPart(
        partName: String = "image",
        contentType: String = "application/x-www-form-urlencoded"
    ): MultipartBody.Part {
        val requestBody = this.asRequestBody(contentType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, this.name, requestBody)
    }
}
