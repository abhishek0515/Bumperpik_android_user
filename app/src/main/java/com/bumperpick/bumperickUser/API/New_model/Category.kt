package com.bumperpick.bumperickUser.API.New_model

data class Category(
    val id: Int,
    val image_url: Any,
    val name: String,
    val slug: String,
    val sub_categories: List<Any>
)