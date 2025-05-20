package com.bumperpick.bumperickUser.Screens.Splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.Navigation.Screen
import com.bumperpick.bumperickUser.Repository.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
 sealed class SplashState {
     object Loading : SplashState()
     data class Success(val screen: Screen) : SplashState()
     data class Error(val message: String) : SplashState()
 }

class SplashViewmodel (val authRepository: AuthRepository):ViewModel(){

    private val _loginCheckState = MutableStateFlow<SplashState>(SplashState.Loading)
    val loginCheckState: StateFlow<SplashState> = _loginCheckState
    init {
        checkAlreadyLoggedIn()
    }
    private fun checkAlreadyLoggedIn() {
        viewModelScope.launch {
            delay(5)
            when (val result = authRepository.checkAlreadyLogin()) {
                is Result.Success -> {
                    _loginCheckState.value = SplashState.Success(if (result.data) Screen.HomePage else Screen.StartScreen)
                }
                is Result.Error -> _loginCheckState.value = SplashState.Error(result.message)
                Result.Loading -> _loginCheckState.value = SplashState.Loading
            }

        }
    }

}