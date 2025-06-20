package com.bumperpick.bumperickUser.Screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.sub_categories
import com.bumperpick.bumperickUser.Repository.OfferRepository
import com.bumperpick.bumperickUser.Repository.Result
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
    private var categoriesCache: List<Category>? = null
    private val subCategoriesCache = mutableMapOf<Int, List<sub_categories>>()

    init {
        getCategories()
    }

    fun getCategories() {
        if (categoriesCache != null) {
            _categoriesUiState.value = UiState.Success(categoriesCache!!)
            return
        }

        viewModelScope.launch {
            _categoriesUiState.value = UiState.Loading

            try {
                when (val result = offerRepository.getCategories()) {
                    is Result.Success -> {
                        categoriesCache = result.data
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

    fun fetchSubCategories(categoryId: Int) {
        // Check cache first
        subCategoriesCache[categoryId]?.let { cachedSubCategories ->
            _subCategoriesUiState.value = UiState.Success(cachedSubCategories)
            return
        }

        viewModelScope.launch {
            _subCategoriesUiState.value = UiState.Loading

            try {
                when (val result = offerRepository.getSubCategories(categoryId)) {
                    is Result.Success -> {
                        subCategoriesCache[categoryId] = result.data
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
        categoriesCache = null
        getCategories()
    }

    fun refreshSubCategories(categoryId: Int) {
        subCategoriesCache.remove(categoryId)
        fetchSubCategories(categoryId)
    }

    // Clear cache when needed
    fun clearCache() {
        categoriesCache = null
        subCategoriesCache.clear()
    }

    override fun onCleared() {
        super.onCleared()
        clearCache()
    }
}

// Improved UiState sealed class


