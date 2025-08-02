package com.bumperpick.bumperickUser.Screens.favourite

import DataStoreManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.API.Model.DataXXXXXX
import com.bumperpick.bumperickUser.Navigation.show_toast
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.AutoImageSlider
import com.bumperpick.bumperickUser.Screens.Component.DottedDivider
import com.bumperpick.bumperickUser.Screens.Component.MediaSlider
import com.bumperpick.bumperickUser.Screens.Component.SearchCard
import com.bumperpick.bumperickUser.Screens.Home.HomeClick
import com.bumperpick.bumperickUser.Screens.Home.HomePageViewmodel
import com.bumperpick.bumperickUser.Screens.Home.UiState
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.grey
import com.bumperpick.bumperickUser.ui.theme.satoshi_regular
import com.bumperpick.bumperpickvendor.API.Model.success_model
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    onBackClick: () -> Unit,
    homeClick: (HomeClick) -> Unit,
    viewModel: HomePageViewmodel = koinViewModel()
) {
    val context = LocalContext.current
    val offerUiState by viewModel.fav_offer_uiState.collectAsState()
    val favToggle by viewModel.fav_toogle_uiState.collectAsState()

    var selectedId by remember { mutableStateOf<String?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    // Fetch favorites on screen load
    LaunchedEffect(Unit) {
        viewModel.getfav()
    }

    // Handle favorite toggle response
    LaunchedEffect(favToggle) {
        when (favToggle) {
            UiState.Empty -> {
                // Do nothing
            }
            is UiState.Error -> {
                showBottomSheet = false
                selectedId = null
                show_toast((favToggle as UiState.Error).message, context)
            }
            UiState.Loading -> {
                // Show loading if needed
            }
            is UiState.Success -> {
                showBottomSheet = false
                selectedId = null
                val result = (favToggle as UiState.Success<success_model>).data as success_model
                if (result.code in 200..300) {
                    show_toast(result.message, context)
                    viewModel.getfav()
                } else {
                    show_toast(result.message, context)
                }
            }
        }
    }

    Scaffold(
        containerColor = grey,
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top bar
                    TopBar(
                        onBackClick = onBackClick,
                        search = search,
                        onSearchChange = { search = it }
                    )

                    // Content based on state
                    when (offerUiState) {
                        UiState.Empty -> {
                            EmptyState()
                        }
                        is UiState.Error -> {
                            ErrorState(message = (offerUiState as UiState.Error).message)
                        }
                        UiState.Loading -> {
                            LoadingState()
                        }
                        is UiState.Success -> {
                            val list = (offerUiState as UiState.Success<List<DataXXXXXX>>).data as List<DataXXXXXX>
                            val filteredList = list.filter {
                                it.title.contains(search, ignoreCase = true)
                            }

                            FavouritesList(
                                offers = filteredList,
                                onOfferClick = { offerId ->
                                    homeClick(HomeClick.OfferClick(offerId))
                                },
                                onRemoveClick = { offerId ->
                                    selectedId = offerId
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
            }
        }
    )

    // Bottom Sheet
    if (showBottomSheet) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedId = null
            },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.3f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        ) {
            RemoveOfferContent(
                onCancel = {
                    showBottomSheet = false
                    selectedId = null
                },
                onRemove = {
                    selectedId?.let { id ->
                        viewModel.toogle_fav(id)
                    } ?: run {
                        show_toast("Error selecting offer", context)
                    }
                }
            )
        }
    }
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    search: String,
    onSearchChange: (String) -> Unit
) {
    Column(modifier = Modifier.background(Color.White)) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Favourites",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(18.dp))

        SearchCard(
            query = search,
            onQueryChange = onSearchChange
        ) { }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.artwork),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            "No offers are in favourites.",
            modifier = Modifier.padding(horizontal = 0.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = BtnColor
        )
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
            color = BtnColor,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            color = BtnColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun FavouritesList(
    offers: List<DataXXXXXX>,
    onOfferClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.left),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${offers.size} OFFER SAVED",
                    letterSpacing = 2.sp,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(R.drawable.right),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        if(offers.isEmpty()){
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.artwork),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "No offers found",
                        modifier = Modifier.padding(horizontal = 0.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = BtnColor
                    )
                }
            }
        }
        else{

        items(offers) { offer ->
            FavOfferView(
                offerModel = offer,
                offerClick = onOfferClick,
                showBottomSheet = onRemoveClick
            )
        }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RemoveOfferContent(
    onCancel: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Character illustration
        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.artwork),
                contentDescription = "Character illustration",
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )
        }

        // Title text
        Text(
            text = "Are you sure you want to remove this offer from favourites?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D2D2D),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cancel button
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFFDF2F2),
                    contentColor = BtnColor
                ),
                border = null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Remove button
            Button(
                onClick = onRemove,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Remove",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        // Add bottom padding for safe area
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun FavOfferView(
    offerModel: DataXXXXXX,
    offerClick: (String) -> Unit,
    showBottomSheet: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                offerClick(offerModel.id.toString())
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                val imageList = offerModel.media
                MediaSlider(mediaList = imageList, height = 180.dp)

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            color = Color.Gray.copy(0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            0.5.dp,
                            Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { showBottomSheet(offerModel.id.toString()) }
                        .padding(4.dp),
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete offer",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(26.dp),
                        tint = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Spacer(Modifier.height(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = offerModel.title,
                        fontSize = 22.sp,
                        fontFamily = satoshi_regular,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Rating stars
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) { index ->
                            Box(modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.fillMaxSize()
                                )

                                if (index < offerModel.average_rating) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star ${index + 1}",
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = offerModel.description,
                    fontSize = 14.sp,
                    fontFamily = satoshi_regular,
                )

                Spacer(modifier = Modifier.height(8.dp))

                DottedDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Gray,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.percentage_red),
                            contentDescription = "Percentage icon",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = offerModel.discount,
                            fontSize = 15.sp,
                            fontFamily = satoshi_regular,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}