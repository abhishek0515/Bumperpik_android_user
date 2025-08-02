package com.bumperpick.bumperickUser.Screens.NotificationScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.Repository.OfferRepository
import com.bumperpick.bumperickUser.Repository.Result
import com.bumperpick.bumperickUser.Screens.Home.UiState
import com.bumperpick.bumperickUser.Screens.Home.UiState.*
import com.bumperpick.bumperpick_Vendor.API.FinalModel.Notification_model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NotificationViewmodel(val offerRepository: OfferRepository): ViewModel() {
private val _notifiacation= MutableStateFlow<UiState<Notification_model>>(UiState.Empty)
    val notification: MutableStateFlow<UiState<Notification_model>> =_notifiacation

    fun fetchNotifcation(){
        viewModelScope.launch {
            val result=offerRepository.notification()
            _notifiacation.value=when(result){
                is Result.Error -> UiState.Error(result.message)
                Result.Loading ->UiState.Loading
                is Result.Success -> UiState.Success(result.data)
            }
        }
    }

}
