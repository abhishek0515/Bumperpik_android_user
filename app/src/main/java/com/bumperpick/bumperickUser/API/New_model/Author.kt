package com.bumperpick.bumperickUser.API.New_model

data class Author(
    val email: String,
    val id: Int,
    val name: String?="",
    val image:String,
    val type: String
)