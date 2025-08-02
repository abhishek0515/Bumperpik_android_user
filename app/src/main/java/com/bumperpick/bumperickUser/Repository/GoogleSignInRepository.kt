package com.bumperpick.bumperickUser.Repository

import DataStoreManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bumperpick.bumperpickvendor.API.Provider.ApiResult
import com.bumperpick.bumperpickvendor.API.Provider.ApiService
import com.bumperpick.bumperpickvendor.API.Provider.safeApiCall
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
class GoogleSignInRepository(
    private val context: Context,
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) {
    private val firebaseAuth = FirebaseAuth.getInstance()

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

            // Create Firebase credential with Google ID token
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            // Sign in to Firebase with the Google credential
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: throw Exception("Firebase authentication failed")

            val userData = GoogleUserData(
                userId = firebaseUser.uid,
                displayName = firebaseUser.displayName,
                email = firebaseUser.email,
                profilePictureUrl = firebaseUser.photoUrl?.toString(),
                idToken = account.idToken ?: ""
            )

            Log.d("GoogleSignIn", "Sign-in successful: email=${firebaseUser.email}")

            // Call your backend API with the user's email
            val result = safeApiCall(
                context = context,
                api = { apiService.auth_google(firebaseUser.email!!) },
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
            Log.e("GoogleSignIn", "Google Sign-in failed: ${e.message}, statusCode: ${e.statusCode}")
            _signInState.value = GoogleSignInState.Error("Google Sign-in failed: ${e.message}")
            return kotlin.Result.failure(e)
        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Unknown error: ${e.message}")
            _signInState.value = GoogleSignInState.Error("Unknown error: ${e.message}")
            return kotlin.Result.failure(e)
        }
    }

    // Silent Sign-In (auto sign-in)
    suspend fun silentSignIn(serverClientId: String): kotlin.Result<GoogleUserData> {
        return try {
            _signInState.value = GoogleSignInState.Loading

            val googleSignInClient = getGoogleSignInClient(serverClientId)
            val task = googleSignInClient.silentSignIn()
            val account = task.await()

            // Create Firebase credential with Google ID token
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            // Sign in to Firebase with the Google credential
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: throw Exception("Firebase authentication failed")

            val userData = GoogleUserData(
                userId = firebaseUser.uid,
                displayName = firebaseUser.displayName,
                email = firebaseUser.email,
                profilePictureUrl = firebaseUser.photoUrl?.toString(),
                idToken = account.idToken ?: ""
            )

            Log.d("GoogleSignIn", "Silent sign-in successful: email=${firebaseUser.email}")

            // Call your backend API with the user's email
            val result = safeApiCall(
                context = context,
                api = { apiService.auth_google(firebaseUser.email!!) },
                refreshTokenApi = { token -> apiService.refresh_token(token) },
                dataStoreManager = dataStoreManager
            )

            when (result) {
                is ApiResult.Error -> {
                    Log.e("GoogleSignIn", "Silent sign-in failed: ${result.message}")
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

        } catch (e: Exception) {
            Log.d("GoogleSignIn", "Silent sign-in failed, user needs to sign in manually")
            _signInState.value = GoogleSignInState.Idle
            return kotlin.Result.failure(e)
        }
    }

    suspend fun signOut(serverClientId: String) {
        try {
            Log.d("GoogleSignIn", "Signing out")

            // Sign out from Firebase
            firebaseAuth.signOut()

            // Sign out from Google Sign-In
            getGoogleSignInClient(serverClientId).signOut().await()

            // Clear stored data
      //      dataStoreManager.clearUserData()

            _signInState.value = GoogleSignInState.Idle

        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Sign out error: ${e.message}")
        }
    }

    suspend fun revokeAccess(serverClientId: String) {
        try {
            Log.d("GoogleSignIn", "Revoking access")

            // Revoke access from Google Sign-In
            getGoogleSignInClient(serverClientId).revokeAccess().await()

            // Sign out from Firebase
            firebaseAuth.signOut()

            // Clear stored data
          //  dataStoreManager.clearUserData()

            _signInState.value = GoogleSignInState.Idle

        } catch (e: Exception) {
            Log.e("GoogleSignIn", "Revoke access error: ${e.message}")
        }
    }

    fun clearError() {
        if (_signInState.value is GoogleSignInState.Error) {
            Log.d("GoogleSignIn", "Clearing error state")
            _signInState.value = GoogleSignInState.Idle
        }
    }

    // Firebase-specific methods
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.removeAuthStateListener(listener)
    }

    // Get current Google account
    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    // Check if user is signed in with Google
    fun isSignedInWithGoogle(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(context) != null
    }
}

// Data Classes
data class GoogleUserData(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val idToken: String
)

