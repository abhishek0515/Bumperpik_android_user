package com.bumperpick.bumperickUser.API.New_model

data class OfferX(
    val approval: String,
    val brand_logo_url: String,
    val brand_name: String,
    val description: String,
    val discount: String,
    val end_date: String,
    val heading: String,
    val id: Int,
    val media: List<MediaXX>,
    val quantity: Int,
    val start_date: String,
    val subheading: Any,
    val terms: String,
    val title: String?="",
    val vendor_id: Int
)