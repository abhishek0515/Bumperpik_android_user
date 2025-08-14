package com.bumperpick.bumperpick_Vendor.API.FinalModel

import com.bumperpick.bumperickUser.API.New_model.Meta
import com.bumperpick.bumperpickvendor.API.Model.DataXXXXX

data class refresh_token_data(
    val code: Int,
    val `data`: DataXXXXX,
    val is_approved: Int,
    val is_registered: Int,
    val message: String,
    val meta: Meta
)