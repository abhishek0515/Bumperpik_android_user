package com.bumperpick.bumperickUser.API.New_model

data class Category(
    val id: Int,
    val image_url: String?,
    val name: String,
    val slug: String,
    val sub_categories: List<sub_categories>
)