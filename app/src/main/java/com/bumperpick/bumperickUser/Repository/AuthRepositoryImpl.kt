package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.content.Context
import android.util.Log
import com.bumperpick.bumperickUser.API.New_model.error_model
import com.bumperpick.bumperickUser.API.New_model.profile_model
import com.bumperpick.bumperpickvendor.API.Model.success_model
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
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
             
                api = { apiService.cust_send_otp(mobileNumber.replace(" ", "")) },
                    errorBodyParser = {
                try {
                    Gson().fromJson(it, error_model::class.java)
                } catch (e: Exception) {
                    error_model(message = "Unknown error format: $it")
                }
            }
            )

            when (sendOtpResponse) {
                is ApiResult.Success -> {
                    if (sendOtpResponse.data.code == 200) Result.Success(sendOtpResponse.data.message)
                    else Result.Error(sendOtpResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(sendOtpResponse.error.message)
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
                errorBodyParser = {
                    try {
                        Gson().fromJson(it, error_model::class.java)
                    } catch (e: Exception) {
                        error_model(message = "Unknown error format: $it")
                    }
                }
            )

            when (sendOtpResponse) {
                is ApiResult.Success -> {
                    if (sendOtpResponse.data.code == 200) Result.Success(sendOtpResponse.data.message)
                    else Result.Error(sendOtpResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(sendOtpResponse.error.message)
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
                    errorBodyParser = {
                try {
                    Gson().fromJson(it, error_model::class.java)
                } catch (e: Exception) {
                    error_model(message = "Unknown error format: $it")
                }
            }
            )

            when (verifyOtpResponse) {
                is ApiResult.Success -> {
                    if (verifyOtpResponse.data.code == 200) {
                        dataStoreManager.saveUserId(
                            verifyOtpResponse.data.meta.token,
                            verifyOtpResponse.data.data.customer_id.toString()
                        )
                        FirebaseMessaging.getInstance().token.addOnCompleteListener {
                                task ->
                            if(task.isSuccessful){
                                CoroutineScope(Dispatchers.IO).launch {


                                    val send_fcm_token = safeApiCall(
                                        context = context,
                                        api = {
                                            apiService.send_token(
                                                token = verifyOtpResponse.data.meta.token,
                                                vendorId = verifyOtpResponse.data.data.customer_id.toString(),
                                                device_token = task.result
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

                                    when (send_fcm_token) {
                                        is ApiResult.Error -> {
                                            Log.d("Error_Fcm_token", send_fcm_token.error.message)
                                        }

                                        is ApiResult.Success -> {
                                            Log.d("Success_Fcm_token", send_fcm_token.data.message)
                                        }
                                    }
                                }
                            }
                        }
                        Result.Success(true)
                    } else Result.Error(verifyOtpResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(verifyOtpResponse.error.message)
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
                    errorBodyParser = {
                try {
                    Gson().fromJson(it, error_model::class.java)
                } catch (e: Exception) {
                    error_model(message = "Unknown error format: $it")
                }
            }
            )

            when (getProfileResponse) {
                is ApiResult.Success -> {
                    if (getProfileResponse.data.code == 200) {
                        Result.Success(getProfileResponse.data)
                    } else Result.Error(getProfileResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(getProfileResponse.error.message)
            }
        } catch (e: Exception) {
            Result.Error("Get profile failed", e)
        }
    }

    override suspend fun sendLocation(lat: Double, long: Double): Result<success_model> {
        return try {
            val sendLocatrion=safeApiCall(
                context=context,
                api = {
                    val token = dataStoreManager.getToken.firstOrNull()?:""
                    apiService.location_update(
                        token = token,
                        latitude = lat.toString(),
                        longitude = long.toString()
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
            when (sendLocatrion) {
                is ApiResult.Success -> {
                    if (sendLocatrion.data.code == 200) {
                        Result.Success(sendLocatrion.data)
                    } else Result.Error(sendLocatrion.data.message)
                }

                is ApiResult.Error -> Result.Error(sendLocatrion.error.message)
            }

        }
        catch (e: Exception) {
            Result.Error("sendLocation", e)
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
                    errorBodyParser = {
                try {
                    Gson().fromJson(it, error_model::class.java)
                } catch (e: Exception) {
                    error_model(message = "Unknown error format: $it")
                }
            }
            )

            when (updateResponse) {
                is ApiResult.Success -> {
                    if (updateResponse.data.code == 200) {
                        Result.Success(updateResponse.data)
                    } else Result.Error(updateResponse.data.message)
                }

                is ApiResult.Error -> Result.Error(updateResponse.error.message)
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
