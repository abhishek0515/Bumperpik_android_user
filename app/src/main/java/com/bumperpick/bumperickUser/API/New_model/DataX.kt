package com.bumperpick.bumperickUser.API.New_model

data class DataX(
    val approval: String,
    val brand_logo_url: String,
    val brand_name: String,
    val description: String,
    val discount: String,
    val end_date: String,
    val expire: Boolean,
    val heading: String,
    val id: Int,
    val media: List<MediaX>,
    val quantity: Int,
    val start_date: String,
    val status: String,
    val subheading: Any,
    val terms: String,
    val title: String,
    val vendor_id: Int
)