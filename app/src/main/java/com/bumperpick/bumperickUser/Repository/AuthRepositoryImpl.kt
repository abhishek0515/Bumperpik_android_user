package com.bumperpick.bumperickUser.Repository

import DataStoreManager

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class AuthRepositoryImpl(
    private val dataStoreManager: DataStoreManager
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
            delay(500) // Simulate OTP send
            Result.Success("Otp Send Successfully")
        } catch (e: Exception) {
            Result.Error("Failed to send OTP", e)
        }
    }

    override suspend fun verifyOtp(mobileNumber: String,otp: String): Result<Boolean> {
        return try {
            delay(500)
            println(otp)
            if(otp.equals("1234")){
                val generatedUserId = UUID.randomUUID().toString()
                dataStoreManager.saveUserId(generatedUserId)
                Result.Success(otp .equals("1234")) // Fake OTP check
            }
            else{
                Result.Success(false)
            }

        } catch (e: Exception) {
            Result.Error("OTP verification failed", e)
        }
    }
}
