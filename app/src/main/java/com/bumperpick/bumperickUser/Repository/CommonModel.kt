package com.bumperpick.bumperickUser.Repository

import java.io.File



sealed class GoogleSignInState {
    data object Idle : GoogleSignInState()
    data object Loading : GoogleSignInState()
    data class Success(val userData: GoogleUserData) : GoogleSignInState()
    data class Error(val message: String) : GoogleSignInState()
}

