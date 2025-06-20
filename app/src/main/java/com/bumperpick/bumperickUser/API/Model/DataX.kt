package com.bumperpick.bumperpickvendor.API.Model

import com.bumperpick.bumperickUser.API.New_model.sub_categories

data class DataX(
    val id: Int,
    val name: String,
    val slug: String,
    val sub_categories: List<sub_categories>
)