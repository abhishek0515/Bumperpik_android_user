package com.bumperpick.bumperickUser.Screens.Home.Map
import DataStoreManager
import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.Repository.AuthRepository

import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
data class LocationData(
    val address: String = "",
    val area: String = "",
    val city: String = "",
    val latLng: LatLng? = null,
    val fullAddress: String = ""
)
class LocationViewModel(
    application: Application,
    val  authRepository: AuthRepository,
    val dataStoreManager: DataStoreManager,


    ) : AndroidViewModel(application) {


    private val context = application.applicationContext

    private lateinit var placesClient: PlacesClient
    private val sessionToken = AutocompleteSessionToken.newInstance()

    init {
        initializePlacesClient()
    }
    private fun initializePlacesClient() {
        if (!Places.isInitialized()) {
            Places.initialize(context, "AIzaSyDw3WJAKHXiTG95PFFABnulYNso8AIHyEw")
        }
        placesClient = Places.createClient(context)
    }
    private val _selectedLocation = MutableStateFlow(LocationData())
    val selectedLocation: StateFlow<LocationData> = _selectedLocation.asStateFlow()

    private val _isMapBottomSheetVisible = MutableStateFlow(false)
    val isMapBottomSheetVisible: StateFlow<Boolean> = _isMapBottomSheetVisible.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchResults = MutableStateFlow<List<LocationData>>(emptyList())
    val searchResults: StateFlow<List<LocationData>> = _searchResults.asStateFlow()

    private val _showSearchResults = MutableStateFlow(false)
    val showSearchResults: StateFlow<Boolean> = _showSearchResults.asStateFlow()

    private val _mapdata = MutableStateFlow(LocationData())
    val mapdata: StateFlow<LocationData> = _mapdata.asStateFlow()

    fun saveLocation(location: LocationData) {
        viewModelScope.launch {
            dataStoreManager.saveLocation(location)
            location.latLng?.let {
                authRepository.sendLocation(
                    lat = location.latLng.latitude,
                    long = location.latLng.longitude
                )
            }

        }
    }

    fun getLocation() {
        viewModelScope.launch {
            _mapdata.value = dataStoreManager.getLocation.firstOrNull() ?: LocationData()
        }
    }

    fun showMapBottomSheet() {
        _isMapBottomSheetVisible.value = true
    }

    fun hideMapBottomSheet() {
        _isMapBottomSheetVisible.value = false
    }

    fun getCurrentLocation(context: Context, onResult: (LocationData?) -> Unit) {
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            onResult(null)
            return
        }

        _isLoading.value = true
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationTokenSource = CancellationTokenSource()

        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    getAddressFromLatLng(context, latLng) { locationData ->
                        _selectedLocation.value = locationData
                        _isLoading.value = false
                        onResult(locationData)
                    }
                } else {
                    _isLoading.value = false
                    onResult(null)
                }
            }.addOnFailureListener {
                _isLoading.value = false
                onResult(null)
            }
        } catch (e: SecurityException) {
            _isLoading.value = false
            onResult(null)
        }
    }

    fun getAddressFromLatLng(context: Context, latLng: LatLng, onResult: (LocationData) -> Unit) {
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val locationData = LocationData(
                        address = address.getAddressLine(0) ?: "",
                        area = address.subLocality ?: address.locality ?: "",
                        city = address.locality ?: address.adminArea ?: "",
                        latLng = latLng,
                        fullAddress = address.getAddressLine(0) ?: ""
                    )
                    onResult(locationData)
                } else {
                    val locationData = LocationData(
                        address = "Unknown location",
                        area = "Unknown area",
                        city = "Unknown city",
                        latLng = latLng,
                        fullAddress = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
                    )
                    onResult(locationData)
                }
            } catch (e: Exception) {
                val locationData = LocationData(
                    address = "Unknown location",
                    area = "Unknown area",
                    city = "Unknown city",
                    latLng = latLng,
                    fullAddress = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
                )
                onResult(locationData)
            }
        }
    }

    // Updated searchLocation function using Places API
    fun searchLocation(context: Context, query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            _showSearchResults.value = false
            return
        }

        if (query.length < 2) {
            return
        }

        // Use Places API if initialized, otherwise fallback to Geocoder
        if (::placesClient.isInitialized) {
            Log.d("searchLocation","searchLocationWithPlacesAPI")
            searchLocationWithPlacesAPI(query)
        } else {
            Log.d("searchLocation","searchLocationWithGeocoder")
            searchLocationWithGeocoder(context, query)
        }
    }

    // New Places API search implementation
    private fun searchLocationWithPlacesAPI(query: String) {
        viewModelScope.launch {
            try {
                val predictions = getPredictions(query)
                val locationList = mutableListOf<LocationData>()

                predictions.take(5).forEach { prediction ->
                    getPlaceDetails(prediction.placeId)?.let { place ->
                        createLocationDataFromPlace(place)?.let { locationData ->
                            locationList.add(locationData)
                        }
                    }
                }

                _searchResults.value = locationList
                _showSearchResults.value = locationList.isNotEmpty()
            } catch (e: Exception) {
                // Fallback to geocoder if Places API fails
                searchLocationWithGeocoder(context, query)
            }
        }
    }

    // Coroutine-based Places API methods
    private suspend fun getPredictions(query: String): List<AutocompletePrediction> {
        return suspendCancellableCoroutine { continuation ->
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(query)
                // Optional: Add country restrictions if needed
                // .setCountries("IN") // Add your preferred countries
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    continuation.resume(response.autocompletePredictions)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    private suspend fun getPlaceDetails(placeId: String): Place? {
        return suspendCancellableCoroutine { continuation ->
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS_COMPONENTS
            )

            val request = FetchPlaceRequest.newInstance(placeId, placeFields)

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    continuation.resume(response.place)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    private fun createLocationDataFromPlace(place: Place): LocationData? {
        return try {
            val latLng = place.latLng ?: return null
            val addressComponents = place.addressComponents?.asList()

            // Extract area and city from address components
            val area = extractAddressComponent(addressComponents, "sublocality")
                ?: extractAddressComponent(addressComponents, "locality")
                ?: ""

            val city = extractAddressComponent(addressComponents, "locality")
                ?: extractAddressComponent(addressComponents, "administrative_area_level_2")
                ?: extractAddressComponent(addressComponents, "administrative_area_level_1")
                ?: ""

            LocationData(
                address = place.name ?: "",
                area = area,
                city = city,
                latLng = latLng,
                fullAddress = place.address ?: place.name ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun extractAddressComponent(
        components: List<com.google.android.libraries.places.api.model.AddressComponent>?,
        type: String
    ): String? {
        return components?.find { component ->
            component.types.contains(type)
        }?.name
    }

    // Fallback to original Geocoder implementation
    private fun searchLocationWithGeocoder(context: Context, query: String) {
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(query, 5)

                val locationList = addresses?.map { address ->
                    LocationData(
                        address = address.getAddressLine(0) ?: "",
                        area = address.subLocality ?: address.locality ?: "",
                        city = address.locality ?: address.adminArea ?: "",
                        latLng = LatLng(address.latitude, address.longitude),
                        fullAddress = address.getAddressLine(0) ?: ""
                    )
                } ?: emptyList()

                _searchResults.value = locationList
                _showSearchResults.value = locationList.isNotEmpty()
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _showSearchResults.value = false
            }
        }
    }

    // Alternative callback-based Places API implementation (if you prefer callbacks)
    private fun searchLocationWithPlacesAPICallbacks(query: String) {
        viewModelScope.launch {
            try {
                val request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(sessionToken)
                    .setQuery(query)
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        val predictions = response.autocompletePredictions
                        fetchPlaceDetailsCallbacks(predictions)
                    }
                    .addOnFailureListener { exception ->
                        // Fallback to geocoder
                        searchLocationWithGeocoder(context, query)
                    }
            } catch (e: Exception) {
                searchLocationWithGeocoder(context, query)
            }
        }
    }

    private fun fetchPlaceDetailsCallbacks(predictions: List<AutocompletePrediction>) {
        val locationList = mutableListOf<LocationData>()
        var completedRequests = 0
        val totalRequests = predictions.size.coerceAtMost(5)

        if (totalRequests == 0) {
            _searchResults.value = emptyList()
            _showSearchResults.value = false
            return
        }

        predictions.take(5).forEach { prediction ->
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS_COMPONENTS
            )

            val placeRequest = FetchPlaceRequest.newInstance(prediction.placeId, placeFields)

            placesClient.fetchPlace(placeRequest)
                .addOnSuccessListener { response ->
                    val place = response.place
                    createLocationDataFromPlace(place)?.let { locationData ->
                        locationList.add(locationData)
                    }

                    completedRequests++
                    if (completedRequests == totalRequests) {
                        _searchResults.value = locationList
                        _showSearchResults.value = locationList.isNotEmpty()
                    }
                }
                .addOnFailureListener {
                    completedRequests++
                    if (completedRequests == totalRequests) {
                        _searchResults.value = locationList
                        _showSearchResults.value = locationList.isNotEmpty()
                    }
                }
        }
    }

    fun hideSearchResults() {
        _showSearchResults.value = false
    }

    fun updateSelectedLocation(locationData: LocationData) {
        _selectedLocation.value = locationData
    }
}