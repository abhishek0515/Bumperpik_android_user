package com.bumperpick.bumperickUser.API.New_model

data class DataXXXXXXXX(
    val address: String,
    val approval: String,
    val banner_image_url: String,
    val description: String?=null,
    val end_date: String?="",
    val end_time:String?="",
    val expire: Boolean,
    val facebook_link: String?="",
    val instagram_link: String?="",
    val id: Int,
    val start_date: String?="",
    val start_time: String?="",
    val status: String,
    val title: String,
    val vendor_id: Int,
    val youtube_link: String?=""
)