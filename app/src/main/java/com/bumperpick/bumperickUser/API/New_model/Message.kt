package com.bumperpick.bumperickUser.API.New_model

data class Message(
    val author: Author,
    val body: String,
    val created_at: String,
    val id: Int,

)