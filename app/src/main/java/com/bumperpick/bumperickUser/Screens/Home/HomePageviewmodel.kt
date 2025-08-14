package com.bumperpick.bumperickUser.Screens.Home

import FilterOption
import android.util.Log
import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.API.Model.DataXXXXXX
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.CustomerOfferDetail
import com.bumperpick.bumperickUser.API.New_model.DataXXXXXXXXXXXXX
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.API.New_model.OfferHistoryModel
import com.bumperpick.bumperickUser.API.New_model.cartDetails
import com.bumperpick.bumperickUser.API.New_model.trendingSearchModel
import com.bumperpick.bumperickUser.Repository.OfferRepository
import com.bumperpick.bumperickUser.Repository.Result
import com.bumperpick.bumperickUser.Screens.Home.Map.LocationData

import com.bumperpick.bumperpickvendor.API.Model.success_model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

data class OfferFilter(
    val subcatId: String = "",
    val categoriesId: List<String> = emptyList(),
    val sortBy: String = "",
    val distanceFilter: String = "",
    val search: String = ""
)
class HomePageViewmodel(val offerRepository: OfferRepository):ViewModel() {


    private val _offer_uiState = MutableStateFlow<UiState<List<Offer>>>(UiState.Empty)
    val offer_uiState: StateFlow<UiState<List<Offer>>> = _offer_uiState
    private val _fav_offer_uiState = MutableStateFlow<UiState<List<DataXXXXXX>>>(UiState.Empty)
    val fav_offer_uiState: StateFlow<UiState<List<DataXXXXXX>>> = _fav_offer_uiState

        private val _trendSearch_uiState = MutableStateFlow<UiState<trendingSearchModel>>(UiState.Empty)
    val trendSearch_uiState: StateFlow<UiState<trendingSearchModel>> = _trendSearch_uiState



    private val _offer_details_uiState = MutableStateFlow<UiState<Offer>>(UiState.Empty)
    val offer_details_uiState: StateFlow<UiState<Offer>> = _offer_details_uiState

    private val _add_to_cart_uiState = MutableStateFlow<UiState<CustomerOfferDetail>>(UiState.Empty)
    val add_to_cart_uiState: StateFlow<UiState<CustomerOfferDetail>> = _add_to_cart_uiState

    private val _cart_uiState = MutableStateFlow<UiState<cartDetails>>(UiState.Empty)
    val cart_uiState: StateFlow<UiState<cartDetails>> = _cart_uiState

   private val _history_uiState = MutableStateFlow<UiState<OfferHistoryModel>>(UiState.Empty)
    val history_uiState: StateFlow<UiState<OfferHistoryModel>> = _history_uiState

    private val _delete_cart_uiState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val delete_cart_uiState: StateFlow<UiState<String>> = _delete_cart_uiState
    
    private val _categories_uiState = MutableStateFlow<UiState<List<Category>>>(UiState.Empty)
    val categories_uiState: StateFlow<UiState<List<Category>>> = _categories_uiState

    private val _rating_uiState= MutableStateFlow<UiState<success_model>>(UiState.Empty)
    val rating_state:StateFlow<UiState<success_model>> =_rating_uiState

    private val _fav_toogle_uiState=MutableStateFlow<UiState<success_model>>(UiState.Empty)

    val fav_toogle_uiState: StateFlow<UiState<success_model>> =_fav_toogle_uiState

    private val _currentOfferFilter = MutableStateFlow(OfferFilter())
    val currentOfferFilter: StateFlow<OfferFilter> = _currentOfferFilter

    private val _getLocation=MutableStateFlow<UiState<LocationData>>(UiState.Empty)
    val getLocation:StateFlow<UiState<LocationData>> =_getLocation

    private val _banner= MutableStateFlow<UiState<List<DataXXXXXXXXXXXXX>>>(UiState.Empty)
    val banner: StateFlow<UiState<List<DataXXXXXXXXXXXXX>>> = _banner

    fun fetchBanner(){
        viewModelScope.launch {
            _banner.value=when(val result=offerRepository.banner()){
                is Result.Error -> UiState.Error(result.message)
                Result.Loading -> UiState.Loading
                is Result.Success -> UiState.Success(result.data.data)
        }
    }
        }
    fun fetchLocation(){
        viewModelScope.launch {
         _getLocation.value=when(val result=offerRepository.get_locationData()){
             is Result.Error -> UiState.Error(result.message)
             Result.Loading -> UiState.Loading
             is Result.Success-> UiState.Success(result.data)
         }
        }
    }
    fun getTrendingSearches(){
        viewModelScope.launch {
            val result=offerRepository.trendingSearch()
            _trendSearch_uiState.value=when(result){
                is Result.Error -> UiState.Error(result.message)
                Result.Loading -> UiState.Loading
                is Result.Success -> UiState.Success(result.data)
            }
        }
    }
    fun toogle_fav(offerId: String,fetch_data_again: Boolean=false){
        viewModelScope.launch {
            val result=offerRepository.fav_toogle(offerId)
            _fav_toogle_uiState.value =when(result){
                is Result.Error -> UiState.Error(result.message)
                Result.Loading -> UiState.Loading
                is Result.Success -> UiState.Success(result.data)
            }
            if(fetch_data_again){
                getOffers()
              //  _fav_toogle_uiState.value= UiState.Empty
            }
        }
    }
    
    fun getfav(){
        viewModelScope.launch { 
            val result=offerRepository.fav_offer()
            _fav_offer_uiState.value= when(result){
                
                 is Result.Error -> {
                     UiState.Error(result.message)
                 }
                 Result.Loading -> {
                     UiState.Loading
                 }
                 is Result.Success-> {
                     UiState.Success(result.data)
                 }
             }
        }
    }
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




    fun getOffers(
        removeads: Boolean = false
    ) {
        viewModelScope.launch {
            val currentFilter = _currentOfferFilter.value
            _offer_uiState.value = UiState.Loading
            Log.d("getoffers", "Remove Ads: $removeads")

            val result = offerRepository.getOffers(currentFilter, showads = removeads)

            when (result) {
                is Result.Error -> {
                    _offer_uiState.value = UiState.Error(result.message)
                }
                Result.Loading -> {
                    _offer_uiState.value = UiState.Loading
                }
                is Result.Success -> {
                    val filteredData = if (removeads) {
                        result.data.filter { !it.is_ads }
                    } else {
                        result.data
                    }
                    _offer_uiState.value = UiState.Success(filteredData)
                }
            }
        }
    }
    fun clearFilter(){

        _currentOfferFilter.value = OfferFilter()
        getOffers(
            removeads = false
        ) // Reload offers with the updated filter
    }
    fun updateFilterAndLoadOffers(
        subcatId: String? = null,
        categoriesId: List<String>? = null,
        sortBy: String? = null,
        distanceFilter: String? = null,
        search: String? = null
    ) {
        var currentFilter = _currentOfferFilter.value
        Log.d("Filters", "${categoriesId} || ${distanceFilter}")

        subcatId?.let {
            currentFilter = currentFilter.copy(subcatId = it)
        }
        categoriesId?.let {
            currentFilter = currentFilter.copy(categoriesId = it)
        }
        sortBy?.let {
            currentFilter = currentFilter.copy(sortBy = it)
        }
        if(distanceFilter ==null){
            currentFilter = currentFilter.copy(distanceFilter = "")
        }
        else{
            currentFilter = currentFilter.copy(distanceFilter = distanceFilter)
        }

        search?.let {
            currentFilter = currentFilter.copy(search = it)
        }

        _currentOfferFilter.value = currentFilter
        Log.d("filter", currentFilter.toString())

        getOffers(
            removeads = currentFilter.search.isNotEmpty()
        ) // Reload offers with the updated filter
    }

    // Convenience functions for UI components to update specific filters
    fun updateCategories_and_sub_cat(newCategories: List<String>,subcatId:String) {
        Log.d("updateCategories",newCategories.toString())
        updateFilterAndLoadOffers(categoriesId = newCategories, subcatId = subcatId)
    }

    fun updateSortBy(newSortBy: String) {
        updateFilterAndLoadOffers(sortBy = newSortBy)
    }

    fun updateFilters(it:List<FilterOption>) {
        val cat_list = it
            .filter { it.type==Type.Category }
            .map { it.id }
        Log.d("cat_list",cat_list.size.toString())

        val distlist = it
            .filter { it.type==Type.Distance }
            .map { it.id }
        Log.d("distlist",distlist.size.toString())

        updateFilterAndLoadOffers(categoriesId = cat_list,distanceFilter =
            if(distlist.isNotEmpty()) {
                distlist.get(0)
            } else null
        )
    }


    fun updateSearch(newSearch: String) {
        updateFilterAndLoadOffers(search = newSearch)
    }

    fun updateSubcatid(subcatId: String) {
        updateFilterAndLoadOffers(subcatId  = subcatId)
    }

    // You can also add a function to reset all filters
    fun resetFilters() {
        _currentOfferFilter.value = OfferFilter()
        getOffers()
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
    fun getOffer_history() {
        viewModelScope.launch {
            val result = offerRepository.getOfferHisotry()
            when (result) {
                is Result.Error -> {
                    _history_uiState.value = UiState.Error(result.message)
                }
                Result.Loading -> _history_uiState.value = UiState.Loading
                is Result.Success -> _history_uiState.value = UiState.Success(result.data)
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

    fun giverating(offerId:String,rating:String,feedback:String){
        viewModelScope.launch {
            _rating_uiState.value=UiState.Loading
            val result=offerRepository.reviewtheoffer(offerId,rating,feedback)
            when(result){
                is Result.Error -> _rating_uiState.value=UiState.Error(result.message)
                Result.Loading -> _rating_uiState.value=UiState.Loading
                is Result.Success -> {

                    _rating_uiState.value = UiState.Success(result.data)
                    getOfferDetails(offerId)
                }
            }
        }
    }

    fun free_fav() {
      viewModelScope.launch {
          _fav_toogle_uiState.value= UiState.Empty
      }
    }


}

