package com.bumperpick.bumperickUser.Repository

import com.bumperpick.bumperickUser.API.New_model.CustomerEventModel
import com.bumperpick.bumperickUser.API.New_model.DataXXXXXXXX
import com.bumperpick.bumperickUser.API.New_model.EventModel
import com.bumperpick.bumperickUser.API.New_model.EventRegisterModel

interface Event_campaign_Repository {
    suspend  fun  getCampaign():Result<EventModel>
    suspend  fun   registerCampaign(eventId:String,name:String,email:String,phone:String):Result<EventRegisterModel>

    suspend fun getEvent():Result<CustomerEventModel>

    suspend fun getEventByid(id:Int):Result<DataXXXXXXXX>
}