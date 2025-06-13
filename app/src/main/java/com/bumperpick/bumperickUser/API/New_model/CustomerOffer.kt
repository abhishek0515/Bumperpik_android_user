package com.bumperpick.bumperickUser.API.New_model

data class CustomerOffer(
    val categories: List<Category>,
    val code: Int,
    val message: String,
    val offers: List<Offer>
)