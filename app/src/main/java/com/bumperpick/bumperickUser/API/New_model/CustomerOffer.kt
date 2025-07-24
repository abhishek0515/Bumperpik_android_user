package com.bumperpick.bumperickUser.API.New_model

data class CustomerOffer(
    val categories: List<Category> =emptyList(),
    val code: Int,
    val message: String?=null,
    val offers: List<Offer>
)