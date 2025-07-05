package com.bumperpick.bumperickUser.Screens.Event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.API.New_model.CustomerEventModel
import com.bumperpick.bumperickUser.API.New_model.DataXXXXXXXX
import com.bumperpick.bumperickUser.Repository.Event_campaign_Repository
import com.bumperpick.bumperickUser.Screens.Home.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.bumperpick.bumperickUser.Repository.Result

class EventViewmodel(private val eventRepository: Event_campaign_Repository): ViewModel() {

    private val _events_uistate = MutableStateFlow<UiState<List<DataXXXXXXXX>>>(UiState.Empty)
    val events_uistate: MutableStateFlow<UiState<List<DataXXXXXXXX>>> = _events_uistate
    private val _event_uistate = MutableStateFlow<UiState<DataXXXXXXXX>>(UiState.Empty)
    val event_uistate: MutableStateFlow<UiState<DataXXXXXXXX>> = _event_uistate


    fun getEvents(){
        _events_uistate.value=UiState.Loading

        viewModelScope.launch {

            val result=eventRepository.getEvent()
            when(result){
                is Result.Error -> _events_uistate.value=UiState.Error(result.message)
                Result.Loading -> _events_uistate.value=UiState.Loading
                is Result.Success -> {
                    val data=result.data
                    if(data.code>=200 && data.code<300){
                        _events_uistate.value = UiState.Success(data.data)
                    }else{
                        _events_uistate.value = UiState.Error(data.message)

                    }
                }
                   }

        }
    }
    fun getEvent(id:Int){
        _event_uistate.value=UiState.Loading
        viewModelScope.launch {
            val result=eventRepository.getEventByid(id)
            when(result){
                is Result.Error -> _event_uistate.value=UiState.Error(result.message)
                Result.Loading -> _event_uistate.value=UiState.Loading
                is Result.Success -> {
                  _event_uistate.value=UiState.Success(result.data)



                }
            }
        }
    }

}