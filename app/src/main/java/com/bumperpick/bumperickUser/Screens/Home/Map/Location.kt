package com.bumperpick.bumperickUser.Screens.Home.Map


import DataStoreManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.focus.onFocusChanged

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.R

import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.grey
import org.koin.androidx.compose.koinViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
// 2. LocationViewModel.kt - Updated ViewModel
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumperpick.bumperickUser.ui.theme.satoshi_medium
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*





// 3. ChooseLocation.kt - Main Screen with Search
@Preview
@Composable
fun ChooseLocationPreview() {
    ChooseLocation(onBackClick = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseLocation(
    onBackClick: () -> Unit,
    locationViewModel: LocationViewModel = koinViewModel()
) {


    var search by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        locationViewModel.getLocation()
    }
    var isSearchFocused by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val selectedLocation by locationViewModel.selectedLocation.collectAsState()
    val isMapBottomSheetVisible by locationViewModel.isMapBottomSheetVisible.collectAsState()
    val isLoading by locationViewModel.isLoading.collectAsState()
    val searchResults by locationViewModel.searchResults.collectAsState()
    val showSearchResults by locationViewModel.showSearchResults.collectAsState()

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            locationViewModel.getCurrentLocation(context) { locationData ->
                if (locationData != null) {
                    locationViewModel.saveLocation(locationData)
                    Toast.makeText(
                        context,
                        "Current location: ${locationData.area}, ${locationData.city}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, "Unable to get current location", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle search
    LaunchedEffect(search) {
        if (search.isNotEmpty()) {
            locationViewModel.searchLocation(context, search)
        } else {
            locationViewModel.hideSearchResults()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.White) {
        Box(modifier = Modifier.padding(it).fillMaxSize()) {
            Column(modifier = Modifier.background(Color.White).fillMaxSize()) {
                Spacer(modifier = Modifier.height(16.dp))

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
                        fontSize = 18.sp,
                        fontFamily = satoshi_medium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.height(1.dp).fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))

                // Search Field with active input
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search location", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingIcon = {
                        if (search.isNotEmpty()) {
                            IconButton(onClick = {
                                search = ""
                                locationViewModel.hideSearchResults()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .onFocusChanged { focusState ->
                            isSearchFocused = focusState.isFocused
                        },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BtnColor,
                        focusedLabelColor = BtnColor,
                        cursorColor = BtnColor,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Show search results or main content

                // Search Results
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Column {
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
                            HorizontalDivider(
                                modifier = Modifier.height(1.dp).fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            )
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
                            HorizontalDivider(
                                modifier = Modifier.height(1.dp).fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Show selected location if available
           /*                 if (selectedLocation.latLng != null) {
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
                            }*/
                        }
                    }
                    if (showSearchResults && searchResults.isNotEmpty()) {
                        items(searchResults) { locationData ->
                            LocationResultItem(
                                locationData = locationData,
                                onClick = {
                                    locationViewModel.updateSelectedLocation(locationData)
                                    locationViewModel.saveLocation(locationData)
                                    locationViewModel.hideSearchResults()
                                    search = ""
                                    Toast.makeText(
                                        context,
                                        "Location selected: ${locationData.area}, ${locationData.city}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }

    }

    // Map Bottom Sheet - No search box, only map selection
    if (isMapBottomSheetVisible) {
        MapOnlyLocationPicker(
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

// 4. LocationResultItem.kt - Search result item component
@Composable
fun LocationResultItem(
    locationData: LocationData,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = locationData.address,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (locationData.area.isNotEmpty() || locationData.city.isNotEmpty()) {
                    Text(
                        text = "${locationData.area}${if (locationData.area.isNotEmpty() && locationData.city.isNotEmpty()) ", " else ""}${locationData.city}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            color = Color.Gray.copy(alpha = 0.2f)
        )
    }
}

// 5. MapOnlyLocationPicker.kt - Map screen without search box


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapOnlyLocationPicker(
    onLocationSelected: (LocationData) -> Unit,
    onDismiss: () -> Unit,
    savedLatLng: LatLng? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedPosition by remember { mutableStateOf(savedLatLng ?: LatLng(28.6139, 77.2090)) }
    var selectedLocationData by remember { mutableStateOf(LocationData()) }
    var currentUserLocation by remember { mutableStateOf<LatLng?>(null) }
    var showConfirmFab by remember { mutableStateOf(savedLatLng != null) }

    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(selectedPosition, if (savedLatLng != null) 17f else 12f)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            getCurrentLocation(context) { latLng ->
                currentUserLocation = latLng
                selectedPosition = latLng
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(latLng, 18f),
                        durationMs = 1000
                    )
                }
                getLocationDataFromLatLng(context, latLng) {
                    selectedLocationData = it
                    showConfirmFab = true
                }
            }
        }
    }

    fun updateLocationFromLatLng(latLng: LatLng) {
        selectedPosition = latLng
        showConfirmFab = true
        getLocationDataFromLatLng(context, latLng) { locationData ->
            selectedLocationData = locationData
        }
    }

    LaunchedEffect(savedLatLng) {
        savedLatLng?.let {
            updateLocationFromLatLng(it)
        }
    }


    val secondaryColor = Color(0xFFD32F2F)
    val surfaceColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .systemBarsPadding()
    ) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color.White,
            )
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp)) // For symmetry
                    Text(
                        text = "Choose Location",
                        fontFamily = satoshi_medium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Google Map
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
                            compassEnabled = true
                        ),
                        onMapClick = { latLng ->
                            updateLocationFromLatLng(latLng)
                        }
                    ) {
                        // Markers
                        Marker(
                            state = MarkerState(position = selectedPosition),
                            title = "Selected Location",
                            snippet = selectedLocationData.address.takeIf { it.isNotEmpty() },
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                        currentUserLocation?.let {
                            Marker(
                                state = MarkerState(position = it),
                                title = "Your Location",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                            )
                        }
                    }

                    // Bottom center white button
                    Button(
                        onClick = {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                getCurrentLocation(context) { latLng ->
                                    currentUserLocation = latLng
                                    updateLocationFromLatLng(latLng)
                                    scope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(latLng, 18f),
                                            durationMs = 1000
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BtnColor),
                        shape = RoundedCornerShape(12.dp),

                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        Row(modifier = Modifier.align(Alignment.CenterVertically).padding(3.dp)) {
                            Icon(
                                painterResource(R.drawable.location_crosshairs_svgrepo_com),
                                contentDescription = null,

                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Use current location",
                                fontSize = 16.sp,
                                modifier = Modifier.align(
                                Alignment.CenterVertically),)
                        }

                    }
                }
            }


            // Location for inspection
            Column(
                modifier = Modifier

                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 80.dp)
            )
            {
                Text(
                    text = "Location for inspection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp, start = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = grey),

                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Column {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedLocationData.area,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black
                                    )
                                    Text(
                                        text = selectedLocationData.address,
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                    )
                                }
                            }
                        }

                    }


                }


            }

        }
        Button(
            onClick = { onLocationSelected(selectedLocationData) },
            colors = ButtonDefaults.buttonColors(containerColor = BtnColor),
            modifier = Modifier
                .align(Alignment.BottomCenter)

                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),

            ) {
            Text(
                text = "Confirm Location",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

        }
    }
}


            // 6. Helper Functions
            fun getCurrentLocation(context: Context, onLocationReceived: (LatLng) -> Unit) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val cancellationTokenSource = com.google.android.gms.tasks.CancellationTokenSource()

                try {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
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
                context: Context,
                latLng: LatLng,
                onResult: (LocationData) -> Unit
            ) {
                try {
                    val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
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


