package com.bumperpick.bumperickUser.Misc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import java.util.*

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Get current location (lat, lon)
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        // Ensure permission is granted
        if (!hasLocationPermission()) return null
        return fusedLocationClient.lastLocation.await()
    }

    // Convert lat, lon to address
    fun getAddressFromLatLon(latitude: Double, longitude: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) ?: emptyList()
            if (addressList.isNotEmpty()) {
                val address = addressList[0]
                buildString {
                    append(address.getAddressLine(0) ?: "")
                }
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error getting address"
        }
    }


    fun getAddressParts(latitude: Double, longitude: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            Log.d("address",addresses.toString())
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]

                // Part 1: Area & Road
                val areaAndRoad = buildString {
                    append(address.subLocality ?: "")
                    if (!address.thoroughfare.isNullOrEmpty()) {
                        if (isNotEmpty()) append(", ")
                        append(address.thoroughfare)
                    }
                }.ifEmpty { "Area/Road not available" }

                // Part 2: City & State
                val cityAndState = buildString {
                    append(address.locality ?: "") // City
                    if (!address.adminArea.isNullOrEmpty()) {
                        if (isNotEmpty()) append(", ")
                        append(address.adminArea) // State
                    }
                }.ifEmpty { "City/State not available" }

                Pair(areaAndRoad, cityAndState)
            } else {
                Pair("Area/Road not found", "City/State not found")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair("Error retrieving area/road", "Error retrieving city/state")
        }
    }

    // Combined function to get current address
    suspend fun getCurrentAddress(): Pair<String,String> {
        val location = getCurrentLocation() ?: return Pair("Location", "not available")
        return getAddressParts(location.latitude, location.longitude)
    }


    // Check if location permission is granted
    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PermissionChecker.PERMISSION_GRANTED || coarse == PermissionChecker.PERMISSION_GRANTED
    }
}
