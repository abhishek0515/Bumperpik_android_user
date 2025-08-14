package com.bumperpick.bumperickUser.Screens.Home

import DataStoreManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.API.New_model.profile_model
import com.bumperpick.bumperickUser.Repository.AuthRepository
import com.bumperpick.bumperickUser.Repository.GoogleSignInRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AccountViewmodel(
    private val dataStoreManager: DataStoreManager,
    private val authRepository: AuthRepository,
    private val googleSignInRepository: GoogleSignInRepository
) : ViewModel()
{

    private val _isLogout = MutableStateFlow(false)
    val isLogout: StateFlow<Boolean> = _isLogout

    private val _profileState = MutableStateFlow<UiState<profile_model>>(UiState.Empty)
    val profileState: StateFlow<UiState<profile_model>> = _profileState

    private val _updateProfileState = MutableStateFlow<UiState<profile_model>>(UiState.Empty)
    val updateProfileState: StateFlow<UiState<profile_model>> = _updateProfileState

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.clearToken()
            googleSignInRepository.signOut()
            _isLogout.value = true
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            try {
                when (val result = authRepository.getProfile()) {

                    is com.bumperpick.bumperickUser.Repository.Result.Error -> _profileState.value=UiState.Error(result.message)
                    com.bumperpick.bumperickUser.Repository.Result.Loading -> _profileState.value=UiState.Loading
                    is com.bumperpick.bumperickUser.Repository.Result.Success ->    _profileState.value = UiState.Success(result.data)
                }
            } catch (e: Exception) {
                _profileState.value = UiState.Error("Failed to fetch profile: ${e.message}")
            }
        }
    }

    fun updateProfile(image: File?, name: String, email: String,phone:String) {
        viewModelScope.launch {
            _updateProfileState.value = UiState.Loading
            try {
                when (val result = authRepository.updateProfile(image, name, email,phone)) {
                    is com.bumperpick.bumperickUser.Repository.Result.Error -> _updateProfileState.value=UiState.Error(result.message)
                    com.bumperpick.bumperickUser.Repository.Result.Loading -> _updateProfileState.value=UiState.Loading
                    is com.bumperpick.bumperickUser.Repository.Result.Success ->    _updateProfileState.value = UiState.Success(result.data)
                }
            } catch (e: Exception) {
                _updateProfileState.value = UiState.Error("Failed to update profile: ${e.message}")
            }
        }
    }

    fun clearProfileState() {
        _profileState.value = UiState.Empty
    }

    fun clearUpdateProfileState() {
        _updateProfileState.value = UiState.Empty
    }
}

