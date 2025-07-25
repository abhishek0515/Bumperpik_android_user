package com.bumperpick.bumperickUser.API.Model

data class DataXXXXXX(
    val address: String,
    val approval: String,
    val average_rating: Double=0.0,
    val brand_logo_url: String,
    val brand_name: String,
    val closing_time: String,
    val description: String,
    val discount: String,
    val end_date: String,
    val expire: Boolean,
    val heading: String,
    val id: Int,
    val image_appearance: String,
    val is_favourited: Boolean,
    val is_reviewed: Boolean,
    val is_unlimited: Int,
    val media: List<Media>,
    val offer_template: String,
    val opening_time: String,
    val phone_number: String,
    val quantity: Int,
    val reviews: List<Any>,
    val start_date: String,
    val status: String,
    val sub_category_id: Int,
    val subheading: String,
    val terms: String,
    val title: String,
    val vendor_id: Int
)