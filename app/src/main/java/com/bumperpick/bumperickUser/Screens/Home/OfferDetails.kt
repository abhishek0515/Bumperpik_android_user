package com.bumperpick.bumperickUser.Screens.Home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.bumperpick.bumperickUser.API.New_model.Media
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.API.New_model.Review
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.ButtonView
import com.bumperpick.bumperickUser.Screens.Component.QRCodeBottomSheet
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.satoshi_regular
import com.bumperpick.bumperpickvendor.API.Model.success_model
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import java.util.Locale




@Composable
fun OfferDetails(offerid:String,onBackClick:()->Unit,is_offer_or_history:Boolean) {
    val homePageViewmodel:HomePageViewmodel= koinViewModel()
    val context= LocalContext.current
    LaunchedEffect(Unit) {
        homePageViewmodel.getOfferDetails(offerid)
    }
    val offer_detail=homePageViewmodel.offer_details_uiState.collectAsState().value
    Scaffold {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {

            when (offer_detail) {
                is UiState.Error -> {
                    Log.d("error", offer_detail.message)
                    Toast.makeText(context, offer_detail.message, Toast.LENGTH_SHORT).show()
                }

                is UiState.Loading -> {
                    CircularProgressIndicator(
                        color = BtnColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Success -> {
                    offerDetail(offer_detail.data, onBackClick, is_offer_or_history)
                }

                UiState.Empty -> {
                    CircularProgressIndicator(
                        color = BtnColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Log.d("error", "empty")
                }
            }
        }
    }


}


private fun openMap(context: Context, address: String) {
    try {
        val encodedAddress = Uri.encode(address)
        val gmmIntentUri = Uri.parse("geo:0,0?q=$encodedAddress")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback to web browser
            val webIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedAddress"))
            context.startActivity(webIntent)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open map", Toast.LENGTH_SHORT).show()
    }
}

private fun makePhoneCall(context: Context, phoneNumber: String) {
    try {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(callIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not make call", Toast.LENGTH_SHORT).show()
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun offerDetail(offer: Offer, onBackClick: () -> Unit, is_offer_or_history: Boolean){
    val viewmodel:HomePageViewmodel= koinViewModel()
    val ratingstate by viewmodel.rating_state.collectAsState()
    val favtoogle by viewmodel.fav_toogle_uiState.collectAsState()
    var isFavourite by remember { mutableStateOf(offer.is_favourited) }
    val userid=viewmodel.userId.collectAsState().value
    LaunchedEffect(Unit) {
        viewmodel.fetchUserId()
    }
    LaunchedEffect(favtoogle) {
        when(favtoogle){
            UiState.Empty ->{}
            is UiState.Error ->{}
            UiState.Loading ->{}
            is UiState.Success ->{
                val data=(favtoogle as UiState.Success<success_model>).data
                if(data.code>=200 && data.code<300){
                    if(data.status.equals("removed")) isFavourite=false
                    else isFavourite=true
                }
            }
        }
    }


    var is_saved by remember { mutableStateOf(false) }
    val context=LocalContext.current
    var showRatingSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column (   modifier = Modifier
            .fillMaxSize()
            .padding(bottom =
                if(offer.is_reviewed && is_offer_or_history) 12.dp else 80.dp)
            .verticalScroll(rememberScrollState()),
        )
        {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Background Image
                    AsyncImage(
                        model = offer.brand_logo_url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient Overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 1f),
                                        Color.Black.copy(alpha = 0.8f),
                                        Color.Transparent
                                    ),
                                    startY = Float.POSITIVE_INFINITY,
                                    endY = 0f
                                )
                            )
                    )

                    // Top Icons (Back, Favorite, Share)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, Color.Gray),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowLeft,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.padding(10.dp).clickable {
                                    onBackClick()
                                }
                            )
                        }

                        Row {
                            if(!is_offer_or_history) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.Black.copy(
                                            alpha = 0.3f
                                        )
                                    ),
                                    border = BorderStroke(1.dp, Color.Gray),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        imageVector =
                                            if (isFavourite) {
                                                Icons.Outlined.Favorite
                                            } else {
                                                Icons.Outlined.FavoriteBorder
                                            },
                                        contentDescription = null,
                                        tint = if (isFavourite) {
                                            Color.Red
                                        } else {
                                            Color.White
                                        },
                                        modifier = Modifier.padding(10.dp).clickable {
                                            viewmodel.toogle_fav(offer.id.toString())
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Black.copy(
                                        alpha = 0.3f
                                    )
                                ),
                                border = BorderStroke(1.dp, Color.Gray),
                                shape = CircleShape,
                                onClick = {shareReferral(context,
                                    "Check out this amazing offer: ${offer.title} - ${offer.heading}\nLocation: ${offer.address}\nOffer ID: ${offer.id}"
                                )

                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                        }
                    }

                    // Content Card
                    Column(modifier = Modifier.padding(top = 150.dp)) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {

                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Time Row
                                    if(!offer.opening_time.isNullOrEmpty()|| !offer.closing_time.isNullOrEmpty()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                painter = painterResource(R.drawable.clock),
                                                contentDescription = null,
                                                tint = BtnColor
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(

                                                text =
                                                    if (offer.opening_time.isNullOrEmpty() || offer.closing_time.isNullOrEmpty()) "" else "OPEN FROM ${offer.opening_time} TO ${offer.closing_time}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = BtnColor
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    )
                                    {
                                        Text(
                                            text = offer.title ?: "",
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
                                                    // Black outline star
                                                    Icon(
                                                        imageVector = Icons.Outlined.Star,
                                                        contentDescription = null,
                                                        tint = Color.Gray,
                                                        modifier = Modifier.fillMaxSize()
                                                    )

                                                    // Golden filled star (only if rating covers this star)
                                                    if (index < offer.average_rating) {
                                                        Icon(
                                                            imageVector = Icons.Default.Star,
                                                            contentDescription = "Star ${index + 1}",
                                                            tint = Color(0xFFFFD700),
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .padding(1.dp) // Small padding to show black outline
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = offer.address,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )

                                    if ( !offer.brand_name.isNullOrEmpty()) {

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Card(
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color.Red.copy(
                                                    alpha = 0.1f
                                                )
                                            ),
                                            border = BorderStroke(1.dp, color = Color.Red)
                                        ) {
                                            println("brandname :${offer.brand_name}")
                                            Text(
                                                text = offer.brand_name ?: "",
                                                fontSize = 12.sp,
                                                color = Color.Red,
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp,
                                                    vertical = 6.dp
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(color = Color.LightGray)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Location section - takes equal weight
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.weight(1f).clickable {
                                                openMap(context, offer.address)

                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.LocationOn,
                                                contentDescription = "Location",
                                                tint = BtnColor
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Location",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Vertical divider
                                        Box(
                                            modifier = Modifier
                                                .height(24.dp)
                                                .width(1.dp)
                                                .background(Color.Gray.copy(alpha = 0.3f))

                                        )

                                        // Call section - takes equal weight
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.weight(1f)
                                                .clickable {
                                                    makePhoneCall(context, offer.phone_number ?: "")
                                                }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Call,
                                                contentDescription = "Call",
                                                tint = BtnColor
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Call",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }


                                }
                            }

                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Offer Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Divider(color = Color.LightGray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            CouponCard(offer)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text(text = "How to redeem offer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Divider(color = Color.LightGray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            HowToRedeem()
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text(text = "Photos", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Divider(color = Color.LightGray)
            }
            Spacer(modifier = Modifier.height(8.dp))

            val media=offer.media
            PhotoGridScreen(imageUrls = media)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Customer Feedback & Ratings ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Divider(color = Color.LightGray)
            }
            Spacer(modifier = Modifier.height(8.dp))

            ReviewScreen(offer.reviews)






        }
        val context = LocalContext.current

        if (is_offer_or_history) {
            when (ratingstate) {
                UiState.Empty -> {
                    // Handle empty state if needed
                }
                is UiState.Error -> {
                    Toast.makeText(
                        context,
                        (ratingstate as UiState.Error).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                UiState.Loading -> {
                    // Loading is handled below
                }
                is UiState.Success -> {
                    Toast.makeText(
                        context,
                        (ratingstate as UiState.Success<success_model>).data.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            if(!offer.is_reviewed) {
                if (ratingstate == UiState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        CircularProgressIndicator(
                            color = BtnColor,
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
                else {
                    ButtonView(
                        "Rate & FeedBack",
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        showRatingSheet = true
                    }
                }
            }
        }
        else {
            ButtonView(
                if (is_saved) "Open QR" else "Avail Offer",
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                showBottomSheet = true

            }
        }


        if (showBottomSheet) {

            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                QRCodeBottomSheet(
                    jsonData = "{user_id:$userid, offer_id:${offer.id}}",
                    offerId = offer.id.toString(),
                    is_saved = is_saved,
                    onAddToCart = {
                        showBottomSheet = false
                        is_saved=true

                    },
                    goback={
                        showBottomSheet=false
                        onBackClick()

                    }
                )

            }

        }

        if(showRatingSheet){
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {

                RateOfferBottomSheet(offerTitle = offer.title?:"", offerDescription = offer.description, onCancel = {showRatingSheet=false},
                    onSubmit = {rating: Int, feedback: String ->
                        viewmodel.giverating(offer.id.toString(),rating.toString(),feedback.toString())
                        showRatingSheet=false

                    })
            }
        }


    }
}

@Composable
fun ReviewScreen(reviews: List<Review>) {
    if (reviews.isEmpty()) {
        // Empty state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "No reviews yet",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black.copy(alpha = 0.6f)
                )
                Text(
                    text = "Reviews will appear here once customers start leaving feedback",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reviews.forEach { review ->
                ReviewItem(review = review)
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
    ) {
        // Top bar with customer ID and star rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Customer ID on the left
            Text(
                text = "Customer Name: ${review.customer_name}",
                fontFamily = satoshi_regular,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            // Star rating on the right
            StarRating(rating = review.rating)
        }

        Spacer(modifier = Modifier.padding(top = 12.dp))

        Text(
            text = review.review,
            fontFamily = satoshi_regular,
            color = Color.Black,
            lineHeight = 20.sp,
            fontSize = 18.sp,
           fontWeight = FontWeight.SemiBold
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            thickness = 1.dp,
            color =Color.Gray.copy(alpha = 0.3f)
        )

    }
}

@Composable
fun StarRating(rating: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }

        // Rating number
        Text(
            text = "($rating)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}



@Composable
fun RateOfferBottomSheet(
    offerTitle: String = "Special Discount Offer",
    offerDescription: String = "Get 20% off on your next purchase. This exclusive offer is valid for a limited time only.",
    onCancel: () -> Unit = {},
    onSubmit: (rating: Int, feedback: String) -> Unit = { _, _ -> }
) {
    var rating by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top=12.dp, bottom = 12.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "Rate this offer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Offer Title
        Text(
            text = offerTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Offer Description
        Text(
            text = offerDescription,
            fontSize = 14.sp,
            color = Color.Black,
            lineHeight = 20.sp
        )

        // Star Rating
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    IconButton(
                        onClick = { rating = index + 1 },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (index < rating) Icons.Outlined.Star else Icons.Outlined.Star,
                            contentDescription = "Star ${index + 1}",
                            tint = if (index < rating) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        // Feedback Text Field
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Feedback ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            OutlinedTextField(
                value = feedback,
                onValueChange = { feedback = it },
                placeholder = { Text("Share your thoughts about this offer...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )
        }

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cancel Button

            TextButton (
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 0.dp
                )
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = BtnColor,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Submit Button
            Button(
                onClick = { onSubmit(rating, feedback) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BtnColor),
                shape = RoundedCornerShape(12.dp),
                enabled = rating > 0
            ) {
                Text(
                    text = "Submit",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Bottom spacing for gesture indicator
        Spacer(modifier = Modifier.height(16.dp))
    }
}


fun formatDate(input: String?): String {
    if(input.isNullOrEmpty()) return ""
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
    val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)

    val date = LocalDate.parse(input, inputFormatter)
    return date.format(outputFormatter).uppercase() // to get "JAN" in uppercase
}

@Composable
fun CouponCard(offer: Offer) {
    println(offer)
    val date= offer.end_date?.let { formatDate(it) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column {
            // Box 1 with background image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)

            ) {
                // Background image
                Image(
                    painter = painterResource(R.drawable.union), // Replace with your background image
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()

                )

                // Foreground content
                Column (
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.BottomStart)
                        .padding(0.dp)
                ) {
                    date?.let {
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .background(
                                Color(0xFFFF6D00),
                                shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 0.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {

                            Text(
                                text = "VALID TILL $date",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Column (modifier = Modifier.padding(top=
                        if(date.isNullOrEmpty()) 24.dp else 0.dp
                    )){
                        // Offer Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.percentage_red),
                                contentDescription = null,
                                modifier = Modifier.size(51.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = offer.discount,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(0.dp))
                                val test=if(offer.is_unlimited==1)"Until stock last" else
                                    "${offer.quantity} left"
                                Text(
                                    text = test,
                                    fontSize = 15.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }


                }
            }

            // Box 2
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = offer.description?:"",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = offer.terms?:"",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 2.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun HowToRedeem() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // First Card - Step 1
        Card(
            modifier = Modifier.weight(1f).height(120.dp).background(color = Color.White),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, color =  BtnColor.copy(0.5f))
        ) {
            Box(
                modifier = Modifier // Maintains square aspect ratio
            ) {
                Image(
                    painter = painterResource(R.drawable.how1),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Step 1",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Go to cart to access the QR code",
                        color = BtnColor,
                        fontSize = 14.sp,

                    )
                }
            }
        }

        // Second Card - Step 2
        Card(
            modifier = Modifier.weight(1f).height(120.dp).background(color = Color.White),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, color =  Color(0xff1F76B8).copy(0.5f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize() // Maintains square aspect ratio
            ) {
                Image(
                    painter = painterResource(R.drawable.how2),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Step 2",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Present the QR code at the outlet",
                        color = Color(0xff1F76B8),
                        fontSize = 14.sp,

                    )
                }
            }
        }
    }
}
@Composable
fun PhotoGridScreen(
    imageUrls: List<Media>,
    modifier: Modifier = Modifier
) {
    var visibleItemCount by remember { mutableStateOf(6) }
    var showExpandButton by remember { mutableStateOf(imageUrls.size > 6) }

    val displayedImages = imageUrls.take(visibleItemCount)
    val remainingCount = imageUrls.size - displayedImages.size

    var show_image_dialog by remember { mutableStateOf<Media?>(null) }

    show_image_dialog?.let { TransparentImageDialog(it, onDismiss = {show_image_dialog=null}) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        PhotoGrid(
            imageUrls = displayedImages,
            isExpanded = !showExpandButton,
            remainingCount = if (showExpandButton) remainingCount else 0,
            onExpandClick = {
                visibleItemCount = imageUrls.size
                showExpandButton = false
            },
            onPhotoClick = {
               show_image_dialog=it
            }
        )
    }
}

@Composable
fun TransparentImageDialog(
    media: Media,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.1f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .clickable(enabled = false) { }, // Prevent click propagation
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box {
                    if (media.type.equals("image", ignoreCase = true)) {
                        // Show Image
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(media.url)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Dialog Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // Show Video using ExoPlayer
                        val exoPlayer = remember {
                            ExoPlayer.Builder(context).build().apply {
                                setMediaItem(MediaItem.fromUri(media.url))
                                prepare()
                                playWhenReady = true
                            }
                        }

                        DisposableEffect(
                            AndroidView(
                                factory = {
                                    PlayerView(it).apply {
                                        player = exoPlayer
                                        useController = true
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        ) {
                            onDispose { exoPlayer.release() }
                        }
                    }

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun PhotoGrid(
    imageUrls: List<Media>,
    onPhotoClick:(Media)-> Unit={},
    isExpanded: Boolean = false,
    remainingCount: Int = 0,
    onExpandClick: () -> Unit = {}
) {
    if (imageUrls.isEmpty()) return

    if (!isExpanded) {
        // Show first 6 images in custom layout
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (imageUrls.isNotEmpty()) {
                PhotoItem(
                    imageUrl = imageUrls[0],
                    onPhotoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 1..3) {
                    if (imageUrls.size > i) {
                        PhotoItem(
                            imageUrl = imageUrls[i],
                            onPhotoClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(140.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (imageUrls.size > 4) {
                    PhotoItem(
                        imageUrl = imageUrls[4],
                        onPhotoClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                    )
                }

                if (imageUrls.size > 5) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                    ) {
                        PhotoItem(

                            imageUrl = imageUrls[5],
                            onPhotoClick,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (remainingCount > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .clickable { onExpandClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+$remainingCount",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Expanded layout: alternate rows of 2 and 3 images
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            var index = 0
            var toggle = true

            while (index < imageUrls.size) {
                val count = if (toggle) 2 else 3
                val rowImages = imageUrls.drop(index).take(count)
                toggle = !toggle
                index += rowImages.size

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowImages.forEach { imageUrl ->
                        PhotoItem(
                            imageUrl = imageUrl,
                            onPhotoClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(150.dp)
                        )
                    }

                    // Fill empty slots for incomplete row
                    repeat(count - rowImages.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


@Composable
fun PhotoItem(
    imageUrl: Media,
    onPhotoClick: (Media) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onPhotoClick(imageUrl) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (imageUrl.type.equals("image", ignoreCase = true)) {
            AsyncImage(
                model = imageUrl.url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            val thumbnailBitmap = remember(imageUrl.url) {
                getVideoThumbnail(imageUrl.url)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                thumbnailBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .padding(4.dp)
                )
            }
        }
    }
}

fun getVideoThumbnail(videoUrl: String): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoUrl, HashMap()) // Network URL
        val bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        retriever.release()
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}



