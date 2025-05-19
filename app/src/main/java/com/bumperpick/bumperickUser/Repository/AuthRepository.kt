package com.bumperpick.bumperickUser.Repository

interface AuthRepository {
    suspend fun checkAlreadyLogin(): Result<Boolean>
    suspend fun login(mobileNumber: String): Result<String>
    suspend fun sendOtp(mobileNumber: String): Result<String>
    suspend fun verifyOtp(mobileNumber: String,otp: String): Result<Boolean>
}
