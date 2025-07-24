package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoogleSignInRepository(
    private val context: Context,
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager // <-- Injected
)
{
    private val _signInState = MutableStateFlow<GoogleSignInState>(GoogleSignInState.Idle)
    val signInState: StateFlow<GoogleSignInState> = _signInState.asStateFlow()

    private fun getGoogleSignInClient(serverClientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestEmail()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(serverClientId: String): Intent {
        return getGoogleSignInClient(serverClientId).signInIntent
    }

    suspend fun processSignInResult(data: Intent?): kotlin.Result<GoogleUserData> {
        return try {
            _signInState.value = GoogleSignInState.Loading

            if (data == null) {
                throw Exception("Sign-in intent data is null")
            }

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            val userData = GoogleUserData(
                userId = account.id ?: "",
                displayName = account.displayName,
                email = account.email,
                profilePictureUrl = account.photoUrl?.toString(),
                idToken = account.idToken ?: ""
            )

            Log.d("GoogleSignIn", "Sign-in successful: email=${account.email}")

            val result = safeApiCall(
                context = context,
                api = { apiService.auth_google(account.email!!) },
                refreshTokenApi = { token -> apiService.refresh_token(token) },
                dataStoreManager = dataStoreManager
            )

            when (result) {
                is ApiResult.Error -> {
                    Log.e("GoogleSignIn", "Sign-in failed: ${result.message}")
                    _signInState.value = GoogleSignInState.Error("Sign-in failed: ${result.message}")
                    return kotlin.Result.failure(Exception(result.message))
                }

                is ApiResult.Success -> {
                    dataStoreManager.saveUserId(
                        result.data.meta.token,
                        result.data.data.customer_id.toString()
                    )
                    _signInState.value = GoogleSignInState.Success(userData)
                    return kotlin.Result.success(userData)
                }
            }

        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign-in failed: ${e.message}, statusCode: ${e.statusCode}")
            _signInState.value = GoogleSignInState.Error("Sign-in failed: ${e.message}")
            return kotlin.Result.failure(e)
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Unknown error: ${e.message}")
            _signInState.value = GoogleSignInState.Error("Unknown error: ${e.message}")
            return kotlin.Result.failure(e)
        }
    }

    fun signOut(serverClientId: String) {
        Log.d("GoogleSignIn", "Signing out")
        val googleSignInClient = getGoogleSignInClient(serverClientId)
        googleSignInClient.signOut()
        _signInState.value = GoogleSignInState.Idle
    }

    fun clearError() {
        if (_signInState.value is GoogleSignInState.Error) {
            Log.d("GoogleSignIn", "Clearing error state")
            _signInState.value = GoogleSignInState.Idle
        }
    }
}
