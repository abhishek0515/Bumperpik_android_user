package com.bumperpick.bumperickUser.Screens.Faq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.bumperpick.bumperickUser.API.New_model.DataXXXXXXXXXX
import com.bumperpick.bumperickUser.Repository.OfferRepository
import com.bumperpick.bumperickUser.Repository.Result
import com.bumperpick.bumperickUser.Screens.Home.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FaqViewmodel(private val OfferRepository: OfferRepository) : ViewModel(){

    private val _faq_uistate = MutableStateFlow<UiState<List<DataXXXXXXXXXX>>>(UiState.Empty)
    val faq_uistate: MutableStateFlow<UiState<List<DataXXXXXXXXXX>>> = _faq_uistate

    fun loadFaq(){
        viewModelScope.launch {
            val result=OfferRepository.FaqModel()
            _faq_uistate.value=
            when(result){
                is Result.Error -> UiState.Error(result.message)
                Result.Loading -> UiState.Loading
                is Result.Success ->{
                    val res=result.data
                    if(res.code>=200 && res.code<300){
                        UiState.Success(res.data)
                    }
                    else{
                        UiState.Error(res.message)
                    }
                }
            }

        }
    }
}