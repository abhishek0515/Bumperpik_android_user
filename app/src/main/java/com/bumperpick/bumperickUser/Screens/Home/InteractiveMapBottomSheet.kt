package com.bumperpick.bumperickUser.Screens.Home

// InteractiveMapBottomSheet.kt - Full Screen Map Location Picker

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.bumperpick.bumperickUser.data.LocationData
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMapBottomSheet(
    savedLatLng: LatLng? = null,
    initialSearchQuery: String = "",
    onLocationSelected: (LocationData) -> Unit
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
                    val addresses = geocoder.getFromLocationName(query, 5) // Get up to 5 suggestions
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

    // Search function
    fun searchLocation() {
        if (searchQuery.isNotEmpty()) {
            isSearching = true
            showSuggestions = false
            scope.launch {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocationName(searchQuery, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        updateLocationFromLatLng(latLng, animate = true)
                    }
                } catch (e: Exception) {
                    // Handle error silently
                } finally {
                    isSearching = false
                }
            }
        }
    }

    // Initialize with saved location if provided
    LaunchedEffect(savedLatLng) {
        savedLatLng?.let { latLng ->
            updateLocationFromLatLng(latLng, animate = false)
            showConfirmFab = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with Search
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Location",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Search Row with Suggestions
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
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
                                        .offset(y = 56.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column {
                                        addressSuggestions.take(5).forEach { suggestion ->
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
                                                            maxLines = 1
                                                        )
                                                        if (suggestion.area.isNotEmpty() || suggestion.city.isNotEmpty()) {
                                                            Text(
                                                                text = "${suggestion.area}${if (suggestion.area.isNotEmpty() && suggestion.city.isNotEmpty()) ", " else ""}${suggestion.city}",
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = Color.Gray,
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            if (suggestion != addressSuggestions.last()) {
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

                        Button(
                            onClick = { searchLocation() },
                            enabled = !isSearching && searchQuery.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = BtnColor),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Full Screen Map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
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
                // Main selected location marker with animation
                Marker(
                    state = MarkerState(position = selectedPosition),
                    title = "Selected Location",
                    snippet = selectedLocationData.address.takeIf { it.isNotEmpty() },
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                    alpha = markerScale
                )

                // Current user location marker (if available)
                currentUserLocation?.let { userLoc ->
                    Marker(
                        state = MarkerState(position = userLoc),
                        title = "Your Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )

                    // Accuracy circle around user location
                    Circle(
                        center = userLoc,
                        radius = 50.0,
                        strokeColor = BtnColor.copy(alpha = 0.5f),
                        fillColor = BtnColor.copy(alpha = 0.1f),
                        strokeWidth = 2f
                    )
                }
            }

            // Floating Action Buttons
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Current Location FAB
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
                    modifier = Modifier.shadow(8.dp, CircleShape)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = "Current Location"
                    )
                }
            }

            // Confirm Location FAB
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
}

// Helper Functions and Data Classes
data class AddressSuggestion(
    val fullAddress: String,
    val area: String,
    val city: String,
    val latLng: LatLng
)
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