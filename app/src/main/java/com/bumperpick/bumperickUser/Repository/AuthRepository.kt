package com.bumperpick.bumperickUser.Repository

import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.bumperpick.bumperickUser.API.New_model.profile_model
import java.io.File

interface AuthRepository {
    suspend fun checkAlreadyLogin(): Result<Boolean>
    suspend fun login(mobileNumber: String): Result<String>
    suspend fun sendOtp(mobileNumber: String): Result<String>
    suspend fun resendOtp(mobileNumber: String): Result<String>
    suspend fun verifyOtp(mobileNumber: String,otp: String): Result<Boolean>
    suspend fun getProfile():Result<profile_model>
    suspend fun updateProfile(image: File?,name:String,email: String,phone: String):Result<profile_model>
}
