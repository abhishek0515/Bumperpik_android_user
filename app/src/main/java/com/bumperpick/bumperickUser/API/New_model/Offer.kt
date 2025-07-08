package com.bumperpick.bumperickUser.API.New_model

data class Offer(
    val approval: String = "",
    val brand_logo_url: String = "",
    val brand_name: String? = null,
    val description: String = "",
    val discount: String = "",
    val end_date: String = "",
    val expire: Boolean = false,
    val heading: String = "",
    val id: Int = 0,
    val media: List<Media> = emptyList(),
    val quantity: Int = 0,
    val start_date: String = "",
    val status: String = "",
    val subheading: String = "",
    val terms: String = "",
    val title: String = "",
    val average_rating:Int=2,
    val vendor_id: Int = 0,
    val is_ads:Boolean=false,
)
