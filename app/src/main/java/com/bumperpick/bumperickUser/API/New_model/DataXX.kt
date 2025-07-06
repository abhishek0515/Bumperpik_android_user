package com.bumperpick.bumperickUser.API.New_model

data class DataXX(
    val create_at: String,
    val customer_id: Int,
    val id: Int,
    val offer: OfferX?=null,
    val offer_id: Int,
    val status: Int
)