package com.bumperpick.bumperickUser.Screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.sub_categories
import com.bumperpick.bumperickUser.Repository.OfferRepository
import com.bumperpick.bumperickUser.Repository.Result
import com.bumperpick.bumperickUser.Screens.Home.Map.LocationData

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
// ImprovedCategoryViewModel.kt
class CategoryViewModel(
    private val offerRepository: OfferRepository
) : ViewModel() {

    private val _categoriesUiState = MutableStateFlow<UiState<List<Category>>>(UiState.Empty)
    val categoriesUiState: StateFlow<UiState<List<Category>>> = _categoriesUiState.asStateFlow()

    private val _subCategoriesUiState = MutableStateFlow<UiState<List<sub_categories>>>(UiState.Empty)
    val subCategoriesUiState: StateFlow<UiState<List<sub_categories>>> = _subCategoriesUiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    // Cache for categories to avoid unnecessary API calls


    init {
        getCategories()
    }

    fun getCategories() {


        viewModelScope.launch {
            _categoriesUiState.value = UiState.Loading

            try {
                when (val result = offerRepository.getCategories()) {
                    is Result.Success -> {

                        _categoriesUiState.value = UiState.Success(result.data)
                    }
                    is Result.Error -> {
                        _categoriesUiState.value = UiState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _categoriesUiState.value = UiState.Loading
                    }
                }
            } catch (e: Exception) {
                _categoriesUiState.value = UiState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }
    private val _getLocation=MutableStateFlow<UiState<LocationData>>(UiState.Empty)
    val getLocation:StateFlow<UiState<LocationData>> =_getLocation

    fun fetchLocation(){
        viewModelScope.launch {
            _getLocation.value=when(val result=offerRepository.get_locationData()){
                is Result.Error -> UiState.Error(result.message)
                Result.Loading -> UiState.Loading
                is Result.Success-> UiState.Success(result.data)
            }
        }
    }
    fun fetchSubCategories(categoryId: Int) {
        // Check cache first


        viewModelScope.launch {
            _subCategoriesUiState.value = UiState.Loading

            try {
                when (val result = offerRepository.getSubCategories(categoryId)) {
                    is Result.Success -> {

                        _subCategoriesUiState.value = UiState.Success(result.data)
                    }
                    is Result.Error -> {
                        _subCategoriesUiState.value = UiState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _subCategoriesUiState.value = UiState.Loading
                    }
                }
            } catch (e: Exception) {
                _subCategoriesUiState.value = UiState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        fetchSubCategories(category.id)
    }

    fun clearSelectedCategory() {
        _selectedCategory.value = null
        _subCategoriesUiState.value = UiState.Empty
    }

    fun refreshCategories() {

        getCategories()
    }

    fun refreshSubCategories(categoryId: Int) {

        fetchSubCategories(categoryId)
    }

    // Clear cache when needed
    fun clearCache() {
    }

    override fun onCleared() {
        super.onCleared()
        clearCache()
    }
}

// Improved UiState sealed class


