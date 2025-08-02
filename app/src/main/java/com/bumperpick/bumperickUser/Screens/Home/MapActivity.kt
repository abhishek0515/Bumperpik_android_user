// 1. LocationData.kt - Data class for location information
package com.bumperpick.bumperickUser.data

import DataStoreManager
import androidx.compose.material.icons.filled.LocationOn

import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.platform.LocalContext


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition

import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.util.*

// 2. LocationViewModel.kt - ViewModel for location management

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.SearchCard
import com.bumperpick.bumperickUser.data.LocationData
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.Screens.Home.InteractiveMapBottomSheet
import com.bumperpick.bumperickUser.ui.theme.grey

import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.*
data class LocationData(
    val address: String = "",
    val area: String = "",
    val city: String = "",
    val latLng: LatLng? = null,
    val fullAddress: String = ""
)

class LocationViewModel(val dataStoreManager: DataStoreManager) : ViewModel() {
    private val _selectedLocation = MutableStateFlow(LocationData())
    val selectedLocation: StateFlow<LocationData> = _selectedLocation.asStateFlow()

    private val _isMapBottomSheetVisible = MutableStateFlow(false)
    val isMapBottomSheetVisible: StateFlow<Boolean> = _isMapBottomSheetVisible.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun saveLocation(location: LocationData) {
        viewModelScope.launch {
            dataStoreManager.saveLocation(location)
        }
    }

    private val _mapdata = MutableStateFlow(LocationData())
    val mapdata: StateFlow<LocationData> = _mapdata.asStateFlow()

    fun getLocation() {
        viewModelScope.launch {
            _mapdata.value= dataStoreManager.getLocation.firstOrNull()?: LocationData()
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

    fun searchLocation(context: Context, query: String, onResult: (List<LocationData>) -> Unit) {
        if (query.isEmpty()) {
            onResult(emptyList())
            return
        }

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

                onResult(locationList)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    fun updateSelectedLocation(locationData: LocationData) {
        _selectedLocation.value = locationData
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseLocation(
    onBackClick: () -> Unit,
    locationViewModel: LocationViewModel = koinViewModel ()
) {
    var search by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<LocationData>>(emptyList()) }

    val context = LocalContext.current
    val selectedLocation by locationViewModel.selectedLocation.collectAsState()
    val isMapBottomSheetVisible by locationViewModel.isMapBottomSheetVisible.collectAsState()
    val isLoading by locationViewModel.isLoading.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false }, // Prevents sliding to close

    )

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            locationViewModel.getCurrentLocation(context) { locationData ->
                if (locationData != null) {
                   locationViewModel.saveLocation(locationData)
                    Toast.makeText(context, "Current location: ${locationData.area}, ${locationData.city}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Unable to get current location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle search
    LaunchedEffect(search) {
        if (search.length > 2) {
            locationViewModel.searchLocation(context, search) { results ->
                searchResults = results
            }
        } else {
            searchResults = emptyList()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.White) {
        Box(modifier = Modifier.padding(it).fillMaxSize()) {
            Column(modifier = Modifier.background(Color.White).fillMaxSize()) {
                Spacer(modifier = Modifier.height(24.dp))

                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Choose location",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(modifier = Modifier.height(1.dp).fillMaxWidth())
                Spacer(modifier = Modifier.height(9.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable{
                            locationViewModel.showMapBottomSheet()
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(width = 1.dp, color = Color.Gray.copy(alpha = 0.3f)),
                    colors = CardDefaults.cardColors(containerColor = grey)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray,
                            modifier = Modifier.padding(start = 12.dp).size(30.dp)
                        )

                        Text("Search location (eg: Block B Malviya..)", color = Color.Gray,
                            modifier = Modifier
                                .weight(1f)
                                .padding( 8.dp),)
                    }
                }
                // Search Card

                Spacer(modifier = Modifier.height(8.dp))



                // Use Current Location
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = BtnColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.location),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Use my current location",
                        color = BtnColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.height(1.dp).fillMaxWidth().padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(8.dp))

                // Add New Address (Open Map)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { locationViewModel.showMapBottomSheet() }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.add),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Add new address",
                        color = BtnColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.height(1.dp).fillMaxWidth().padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(8.dp))

                // Show selected location if available
                if (selectedLocation.latLng != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = BtnColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Selected Location:",
                                style = MaterialTheme.typography.labelMedium,
                                color = BtnColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedLocation.fullAddress,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Area: ${selectedLocation.area}, City: ${selectedLocation.city}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    // Map Bottom Sheet
    if (isMapBottomSheetVisible) {
        FullScreenMapLocationPicker(
            onLocationSelected = { locationData ->
                locationViewModel.updateSelectedLocation(locationData)
                locationViewModel.hideMapBottomSheet()
                locationViewModel.saveLocation(locationData)
                Toast.makeText(context, "Location selected: ${locationData.area}, ${locationData.city}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { locationViewModel.hideMapBottomSheet() },
            savedLatLng = locationViewModel.mapdata.collectAsState().value.latLng
        )
    }
}
// Full Screen Map Location Picker Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenMapLocationPicker(
    onLocationSelected: (LocationData) -> Unit,
    onDismiss: () -> Unit,
    savedLatLng: LatLng? = null,
    initialSearchQuery: String = ""
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State management
    var searchQuery by remember { mutableStateOf(initialSearchQuery) }
    var selectedPosition by remember { mutableStateOf(savedLatLng ?: LatLng(28.6139, 77.2090)) }
    var selectedLocationData by remember { mutableStateOf(LocationData()) }
    var isSearching by remember { mutableStateOf(false) }
    var currentUserLocation by remember { mutableStateOf<LatLng?>(null) }
    var showConfirmFab by remember { mutableStateOf(false) }
    var addressSuggestions by remember { mutableStateOf<List<AddressSuggestion>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }

    // Animation states
    var markerBounce by remember { mutableStateOf(false) }
    val markerScale by animateFloatAsState(
        targetValue = if (markerBounce) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = 0.3f),
        label = "marker_bounce"
    )

    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedPosition, if (savedLatLng != null) 17f else 12f)
    }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getCurrentLocation(context) { latLng ->
                currentUserLocation = latLng
                selectedPosition = latLng
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(latLng, 18f),
                        durationMs = 1000
                    )
                }
                getLocationDataFromLatLng(context, latLng) { locationData ->
                    selectedLocationData = locationData
                    showConfirmFab = true
                }
            }
        }
    }

    // Location selection handler
    fun updateLocationFromLatLng(latLng: LatLng, animate: Boolean = true) {
        selectedPosition = latLng
        markerBounce = true
        showConfirmFab = true

        scope.launch {
            delay(200)
            markerBounce = false
        }

        getLocationDataFromLatLng(context, latLng) { locationData ->
            selectedLocationData = locationData
        }

        if (animate) {
            scope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(latLng, 16f),
                    durationMs = 800
                )
            }
        }
    }

    // Search function with suggestions
    fun searchAddressSuggestions(query: String) {
        if (query.length >= 3) {
            scope.launch {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocationName(query, 5)
                    val suggestions = addresses?.map { address ->
                        AddressSuggestion(
                            fullAddress = address.getAddressLine(0) ?: "",
                            area = address.subLocality ?: address.locality ?: "",
                            city = address.locality ?: address.adminArea ?: "",
                            latLng = LatLng(address.latitude, address.longitude)
                        )
                    } ?: emptyList()

                    addressSuggestions = suggestions
                    showSuggestions = suggestions.isNotEmpty()
                } catch (e: Exception) {
                    addressSuggestions = emptyList()
                    showSuggestions = false
                }
            }
        } else {
            addressSuggestions = emptyList()
            showSuggestions = false
        }
    }

    // Initialize with saved location if provided
    LaunchedEffect(savedLatLng) {
        savedLatLng?.let { latLng ->
            updateLocationFromLatLng(latLng, animate = false)
            showConfirmFab = true
        }
    }

    // Full screen overlay with proper z-index
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        // Background Map (Full Screen)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = currentUserLocation != null
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = true,
                rotationGesturesEnabled = true,
                scrollGesturesEnabled = true,
                tiltGesturesEnabled = true,
                zoomGesturesEnabled = true
            ),
            onMapClick = { latLng ->
                showSuggestions = false
                updateLocationFromLatLng(latLng)
            }
        ) {
            // Main selected location marker
            Marker(
                state = MarkerState(position = selectedPosition),
                title = "Selected Location",
                snippet = selectedLocationData.address.takeIf { it.isNotEmpty() },
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                alpha = markerScale
            )

            // Current user location marker
            currentUserLocation?.let { userLoc ->
                Marker(
                    state = MarkerState(position = userLoc),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
                Circle(
                    center = userLoc,
                    radius = 50.0,
                    strokeColor = BtnColor.copy(alpha = 0.5f),
                    fillColor = BtnColor.copy(alpha = 0.1f),
                    strokeWidth = 2f
                )
            }
        }

        // Top Header with Search (Fixed Position)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(
                                Color.Gray.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    Text(
                        text = "Choose Location",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(48.dp)) // Balance the back button
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Field with Suggestions
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue
                            searchAddressSuggestions(newValue)
                        },
                        label = { Text("Search location") },
                        placeholder = { Text("Enter address, landmark, or place") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = BtnColor
                            )
                        },
                        trailingIcon = {
                            Row {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        showSuggestions = false
                                        addressSuggestions = emptyList()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(4.dp),
                                        color = BtnColor,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BtnColor,
                            focusedLabelColor = BtnColor,
                            cursorColor = BtnColor
                        )
                    )

                    // Suggestions Dropdown
                    if (showSuggestions && addressSuggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                addressSuggestions.take(4).forEach { suggestion ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                searchQuery = suggestion.fullAddress
                                                showSuggestions = false
                                                updateLocationFromLatLng(suggestion.latLng, animate = true)
                                            },
                                        color = Color.Transparent
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = BtnColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = suggestion.fullAddress,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                if (suggestion.area.isNotEmpty() || suggestion.city.isNotEmpty()) {
                                                    Text(
                                                        text = "${suggestion.area}${if (suggestion.area.isNotEmpty() && suggestion.city.isNotEmpty()) ", " else ""}${suggestion.city}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Gray,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (suggestion != addressSuggestions.take(4).last()) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = Color.Gray.copy(alpha = 0.2f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Current Location FAB (Bottom Right)
        FloatingActionButton(
            onClick = {
                val hasLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasLocationPermission) {
                    getCurrentLocation(context) { latLng ->
                        currentUserLocation = latLng
                        updateLocationFromLatLng(latLng)
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(latLng, 18f)
                            )
                        }
                    }
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            containerColor = Color.White,
            contentColor = BtnColor,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = if (showConfirmFab) 80.dp else 16.dp)
                .shadow(8.dp, CircleShape)
        ) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = "Current Location"
            )
        }

        // Confirm Location FAB (Bottom Center)
        if (showConfirmFab) {
            ExtendedFloatingActionButton(
                onClick = {
                    onLocationSelected(selectedLocationData)
                },
                containerColor = BtnColor,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .shadow(12.dp, RoundedCornerShape(28.dp))
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Confirm Location"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirm This Location",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Center crosshair indicator
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = BtnColor.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp)
                .background(
                    Color.White.copy(alpha = 0.9f),
                    CircleShape
                )
                .padding(6.dp)
        )
    }
}


// Data class for address suggestions
data class AddressSuggestion(
    val fullAddress: String,
    val area: String,
    val city: String,
    val latLng: LatLng
)

// Helper functions (same as before)



@Composable
fun LocationResultItem(
    locationData: LocationData,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = locationData.address,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        if (locationData.area.isNotEmpty() || locationData.city.isNotEmpty()) {
            Text(
                text = "${locationData.area}, ${locationData.city}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = Color.Gray.copy(alpha = 0.3f)
        )
    }
}



// Helper Functions
fun getCurrentLocation(context: android.content.Context, onLocationReceived: (LatLng) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cancellationTokenSource = com.google.android.gms.tasks.CancellationTokenSource()

    try {
        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                onLocationReceived(LatLng(location.latitude, location.longitude))
            }
        }
    } catch (e: SecurityException) {
        // Handle permission error
    }
}

fun getLocationDataFromLatLng(
    context: android.content.Context,
    latLng: LatLng,
    onResult: (LocationData) -> Unit
) {
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



@Preview
@Composable
fun MapPreviewScreen() {
    ChooseLocation(
        onBackClick = { },

    )
}