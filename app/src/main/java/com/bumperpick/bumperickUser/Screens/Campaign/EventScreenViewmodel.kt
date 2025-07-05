package com.bumperpick.bumperickUser.Screens.Campaign

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.API.New_model.EventModel
import com.bumperpick.bumperickUser.API.New_model.EventRegisterModel
import com.bumperpick.bumperickUser.Repository.Event_campaign_Repository
import com.bumperpick.bumperickUser.Repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}
class EventScreenViewmodel(private val eventRepository: Event_campaign_Repository):ViewModel() {

    private val _eventstate= MutableStateFlow<UiState<EventModel>>(UiState.Empty)
    val eventstate: StateFlow<UiState<EventModel>> =_eventstate


    private val _user_reg_eventstate= MutableStateFlow<UiState<EventRegisterModel>>(UiState.Empty)
    val user_reg_eventstate: StateFlow<UiState<EventRegisterModel>> =_user_reg_eventstate


    fun getEvents(){
        _eventstate.value=UiState.Loading
        viewModelScope.launch {
            val result=eventRepository.getCampaign()
            when(result){
                is Result.Error -> {
                    _eventstate.value=UiState.Error(result.message)
                }
                Result.Loading -> _eventstate.value=UiState.Loading
                is Result.Success -> _eventstate.value=UiState.Success(result.data)
            }
        }
    }


    fun registerEvent(id: String, name: String, phone: String, email: String) {
        // Validate input parameters
        if (id.isBlank()) {
            _user_reg_eventstate.value = UiState.Error("Event ID is required")
            return
        }
        if (name.isBlank()) {
            _user_reg_eventstate.value = UiState.Error("Name is required")
            return
        }
        if (email.isBlank()) {
            _user_reg_eventstate.value = UiState.Error("Email is required")
            return
        }

        _user_reg_eventstate.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = eventRepository.registerCampaign(id, name, email, phone)
                when (result) {
                    is Result.Error -> {
                        _user_reg_eventstate.value = UiState.Error(result.message)
                    }
                    Result.Loading -> {
                        _user_reg_eventstate.value = UiState.Loading
                    }
                    is Result.Success -> {
                        _user_reg_eventstate.value = UiState.Success(result.data)
                    }
                }
            } catch (e: Exception) {
                _user_reg_eventstate.value = UiState.Error(e.message ?: "Registration failed")
            }
        }
    }
    fun clearRegistrationState() {
        _user_reg_eventstate.value = UiState.Empty
    }

    /**
     * Clears the events state
     */
    fun clearEventsState() {
        _eventstate.value = UiState.Empty
    }










}