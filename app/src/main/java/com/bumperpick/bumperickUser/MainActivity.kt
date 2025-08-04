package com.bumperpick.bumperickUser

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.bumperpick.bumperickUser.Navigation.AppNavigation
import com.bumperpick.bumperickUser.ui.theme.BumperickTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted - proceed with your app logic
            onNotificationPermissionGranted()
        } else {
            // Permission denied - handle accordingly
            onNotificationPermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseInstance= FirebaseMessaging.getInstance()

        firebaseInstance .subscribeToTopic("user")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM", "Subscribed to 'user' topic")
                    }
                }

        checkNotificationPermission()
        enableEdgeToEdge()
        setContent {
            BumperickTheme {
              AppNavigation()
            }
        }
    }
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    onNotificationPermissionGranted()
                }
                else -> {
                    // Request permission
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android 12 and below - permission automatically granted
            onNotificationPermissionGranted()
        }
    }

    private fun onNotificationPermissionGranted() {
        // Permission granted - continue with your app logic
        println("Notification permission granted")
        // You can proceed with notification setup or other operations
    }

    private fun onNotificationPermissionDenied() {
        // Permission denied - handle accordingly
        println("Notification permission denied")
        // You might want to show a dialog or continue without notifications
    }
}

