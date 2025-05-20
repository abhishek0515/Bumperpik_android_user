package com.bumperpick.bumperickUser.Screens.Home

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.bumperpick.bumperickUser.Screens.Component.LocationPermissionScreen


@Composable
fun Homepage(){
    Text("THIS IS HOME")

    val context = LocalContext.current
    val locationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )



if (locationPermission != PackageManager.PERMISSION_GRANTED) {
    LocationPermissionScreen(
        onPermissionGranted = {
            // Handle permission granted
            println("Location permission granted")
        },
        onPermissionDenied = {
            // Handle permission denied
            println("Location permission denied")
        }
    )
}

}