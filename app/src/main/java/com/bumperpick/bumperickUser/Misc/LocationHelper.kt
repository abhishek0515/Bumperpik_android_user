package com.bumperpick.bumperickUser.Misc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.*
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Get current location with optimized timeout and fallback
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<Location?> {
        return withContext(Dispatchers.IO) {
            try {
                // Check permissions
                if (!hasLocationPermission()) {
                    Log.w("LocationHelper", "Location permission not granted")
                    return@withContext Result.failure(Exception("Location permission not granted"))
                }

                // Check if location services are enabled
                if (!isLocationEnabled()) {
                    Log.w("LocationHelper", "Location services not enabled")
                    return@withContext Result.failure(Exception("Location services not enabled"))
                }

                Log.d("LocationHelper", "Starting location request...")

                // Try to get fresh location
                val freshLocation = withTimeoutOrNull(5000) { // 5 seconds timeout
                    try {
                        val cancellationTokenSource = CancellationTokenSource()
                        val task = fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            cancellationTokenSource.token
                        )
                        task.await()
                    } catch (e: Exception) {
                        Log.w("LocationHelper", "Fresh location failed: ${e.message}")
                        null
                    }
                }

                if (freshLocation != null) {
                    Log.d("LocationHelper", "Got fresh location: ${freshLocation.latitude}, ${freshLocation.longitude}")
                    return@withContext Result.success(freshLocation)
                }

                // Fallback to last known location
                Log.d("LocationHelper", "Trying last known location...")
                val lastLocation = withTimeoutOrNull(3000) { // 3 seconds timeout
                    try {
                        fusedLocationClient.lastLocation.await()
                    } catch (e: Exception) {
                        Log.w("LocationHelper", "Last known location failed: ${e.message}")
                        null
                    }
                }

                Log.d("LocationHelper", "Last known location: $lastLocation")
                Result.success(lastLocation)
            } catch (e: Exception) {
                Log.e("LocationHelper", "Error getting location", e)
                Result.failure(e)
            }
        }
    }

    // Optimized geocoding
    suspend fun getAddressParts(latitude: Double, longitude: Double): Result<Pair<String, String>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("LocationHelper", "Starting geocoding for: $latitude, $longitude")

                val result = withTimeoutOrNull(3000) { // 3 seconds timeout
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        getAddressPartsAsync(latitude, longitude)
                    } else {
                        getAddressPartsLegacy(latitude, longitude)
                    }
                }

                result?.let { Result.success(it) } ?: run {
                    Log.w("LocationHelper", "Geocoding timed out")
                    Result.success(Pair("Location found", "Getting address..."))
                }
            } catch (e: Exception) {
                Log.e("LocationHelper", "Geocoding error", e)
                Result.success(Pair("Location found", "Address unavailable"))
            }
        }
    }

    // Modern async geocoding
    @SuppressLint("NewApi")
    private suspend fun getAddressPartsAsync(latitude: Double, longitude: Double): Pair<String, String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    try {
                        if (!addresses.isNullOrEmpty() && continuation.isActive) {
                            val address = addresses[0]
                            val result = parseAddressParts(address)
                            continuation.resume(result)
                        } else if (continuation.isActive) {
                            continuation.resume(Pair("Location found", "Address not found"))
                        }
                    } catch (e: Exception) {
                        if (continuation.isActive) {
                            continuation.resume(Pair("Location found", "Address error"))
                        }
                    }
                }
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resume(Pair("Location found", "Geocoding failed"))
                }
            }
        }
    }

    // Legacy geocoding
    private suspend fun getAddressPartsLegacy(latitude: Double, longitude: Double): Pair<String, String> {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    parseAddressParts(addresses[0])
                } else {
                    Pair("Location found", "Address not found")
                }
            } catch (e: Exception) {
                Log.e("LocationHelper", "Legacy geocoding error", e)
                Pair("Location found", "Address unavailable")
            }
        }
    }

    // Extract address parts
    private fun parseAddressParts(address: Address): Pair<String, String> {
        return try {
            val areaAndRoad = buildString {
                address.subLocality?.let { append(it) }
                if (isEmpty()) {
                    address.thoroughfare?.let { append(it) }
                }
                if (isEmpty()) {
                    address.featureName?.let { append(it) }
                }
            }.takeIf { it.isNotEmpty() } ?: "Current Location"

            val cityAndState = buildString {
                address.locality?.let { append(it) }
                address.adminArea?.let { state ->
                    if (isNotEmpty()) append(", ")
                    append(state)
                }
            }.takeIf { it.isNotEmpty() } ?: "Location Services"

            Pair(areaAndRoad, cityAndState)
        } catch (e: Exception) {
            Log.e("LocationHelper", "Error parsing address", e)
            Pair("Current Location", "Location Services")
        }
    }

    // Main function to get current address
    suspend fun getCurrentAddress(): Result<Pair<String, String>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("LocationHelper", "Getting current address...")
                val locationResult = getCurrentLocation()
                when {
                    locationResult.isSuccess -> {
                        val location = locationResult.getOrNull()
                        if (location != null) {
                            Log.d("LocationHelper", "Location obtained, getting address...")
                            getAddressParts(location.latitude, location.longitude)
                        } else {
                            Result.success(Pair("Enable Location", "Tap to retry"))
                        }
                    }
                    else -> Result.failure(locationResult.exceptionOrNull() ?: Exception("Unknown error"))
                }
            } catch (e: Exception) {
                Log.e("LocationHelper", "Error in getCurrentAddress", e)
                Result.success(Pair("Location Error", "Tap to retry"))
            }
        }
    }

    // Check location permission
    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PermissionChecker.PERMISSION_GRANTED || coarse == PermissionChecker.PERMISSION_GRANTED
    }

    // Check if location services are enabled
    fun isLocationEnabled(): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            locationManager?.let {
                it.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        it.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } ?: false
        } catch (e: Exception) {
            Log.e("LocationHelper", "Error checking location enabled", e)
            false
        }
    }
}