package com.bumperpick.bumperickUser.Screens.Home

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.CustomerOfferDetail
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.API.New_model.cartDetails
import com.bumperpick.bumperickUser.Repository.OfferRepository
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

class HomePageViewmodel(val offerRepository: OfferRepository):ViewModel() {


    private val _offer_uiState = MutableStateFlow<UiState<List<Offer>>>(UiState.Empty)
    val offer_uiState: StateFlow<UiState<List<Offer>>> = _offer_uiState

    private val _offer_details_uiState = MutableStateFlow<UiState<Offer>>(UiState.Empty)
    val offer_details_uiState: StateFlow<UiState<Offer>> = _offer_details_uiState

    private val _add_to_cart_uiState = MutableStateFlow<UiState<CustomerOfferDetail>>(UiState.Empty)
    val add_to_cart_uiState: StateFlow<UiState<CustomerOfferDetail>> = _add_to_cart_uiState

    private val _cart_uiState = MutableStateFlow<UiState<cartDetails>>(UiState.Empty)
    val cart_uiState: StateFlow<UiState<cartDetails>> = _cart_uiState

    private val _delete_cart_uiState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val delete_cart_uiState: StateFlow<UiState<String>> = _delete_cart_uiState
    
    private val _categories_uiState = MutableStateFlow<UiState<List<Category>>>(UiState.Empty)
    val categories_uiState: StateFlow<UiState<List<Category>>> = _categories_uiState
    
    fun getCategory(){
      viewModelScope.launch {
          val result=offerRepository.getCategories()
          when(result){
              is Result.Error -> _categories_uiState.value=UiState.Error(result.message)
              Result.Loading -> _categories_uiState.value=UiState.Loading
              is Result.Success -> _categories_uiState.value=UiState.Success(result.data)
          }

      }
    }



    fun getOffers() {
        viewModelScope.launch {
           val result= offerRepository.getOffers()
            when(result){
                is Result.Error -> {
                    _offer_uiState.value=UiState.Error(result.message)
                }
                Result.Loading -> _offer_uiState.value=UiState.Loading
                is Result.Success ->_offer_uiState.value=UiState.Success(result.data)
            }
        }
    }

    fun getOfferDetails(id:String) {
        viewModelScope.launch {
            val result= offerRepository.getOfferDetails(id)
            when(result){
                is Result.Error -> {
                    _offer_details_uiState.value = UiState.Error(result.message)
                }
                Result.Loading -> _offer_details_uiState.value = UiState.Loading
                is Result.Success -> _offer_details_uiState.value = UiState.Success(result.data)
            }
        }

    }

    fun addToCart(id:String) {
        viewModelScope.launch {
            _add_to_cart_uiState.value=UiState.Loading
            val result = offerRepository.addtoCart(id)
            when (result) {
                is Result.Error -> {
                    _add_to_cart_uiState.value = UiState.Error(result.message)
                }

                Result.Loading -> _add_to_cart_uiState.value = UiState.Loading
                is Result.Success -> _add_to_cart_uiState.value = UiState.Success(result.data)
            }
        }
    }

    fun getCart() {
        viewModelScope.launch {
            val result = offerRepository.getCart()
            when (result) {
                is Result.Error -> {
                    _cart_uiState.value = UiState.Error(result.message)
                }
                Result.Loading -> _cart_uiState.value = UiState.Loading
                is Result.Success -> _cart_uiState.value = UiState.Success(result.data)
            }
        }


    }
    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId
    fun fetchUserId() {
        viewModelScope.launch {
            when (val result = offerRepository.getUserId()) {
                is Result.Error -> _userId.value = ""
                is Result.Loading -> _userId.value = "" // or show loading
                is Result.Success -> _userId.value = result.data
            }
        }
    }

    fun resetaddtocart() {
        _add_to_cart_uiState.value=UiState.Empty
        _delete_cart_uiState.value=UiState.Empty
    }

    fun deleteCart(it: String) {
        viewModelScope.launch {
            _delete_cart_uiState.value=UiState.Loading
            val result = offerRepository.deletecart(it)

            when(result){
                is Result.Error -> _delete_cart_uiState.value=UiState.Error(result.message)
                Result.Loading -> _delete_cart_uiState.value=UiState.Loading
                is Result.Success -> _delete_cart_uiState.value=UiState.Success(result.data.message)
            }
        }



    }


}

