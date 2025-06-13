package com.bumperpick.bumperickUser.API.New_model

data class LoginModel(
    val code: Int,
    val `data`: Data,
    val message: String,
    val meta: Meta
)