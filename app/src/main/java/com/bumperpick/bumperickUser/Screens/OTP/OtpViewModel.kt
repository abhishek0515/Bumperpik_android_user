package com.bumperpick.bumperickUser.Screens.OTP

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.Repository.AuthRepository
import com.bumperpick.bumperickUser.Repository.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OtpUiState(
    val otp: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val success:Boolean=false
)
class OtpViewModel(val authRepository: AuthRepository):ViewModel() {
    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState

    fun onOtpChanged(newOtp: String) {
        _uiState.update { it.copy(otp = newOtp, error = null, message = null) }
    }

    fun verifyOtp(mobile: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, message = null) }

            val otpVerify=authRepository.verifyOtp(mobileNumber = mobile,otp=_uiState.value.otp)
            when(otpVerify){
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = otpVerify.message) }
                Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Result.Success -> _uiState.update { it.copy(isLoading = false, message = if(otpVerify.data) "OTP Verified Successfully" else "Invalid OTP",success = otpVerify.data) }
            }
        }
    }
    fun resendOtp(mobile: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, message = null) }

            val otpVerify=authRepository.resendOtp(mobileNumber = mobile)
            when (otpVerify) {
                is Result.Success -> {
                    _uiState.update { it.copy(

                        message = otpVerify.data,
                        isLoading = false
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        error = otpVerify.message,
                        isLoading = false
                    )}
                }

                Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true, error = "", ) }

                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }



}