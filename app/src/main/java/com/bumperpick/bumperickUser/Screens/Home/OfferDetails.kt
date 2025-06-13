package com.bumperpick.bumperickUser.Screens.Home

import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.ButtonView
import com.bumperpick.bumperickUser.Screens.Component.QRCodeBottomSheet
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import java.util.Locale



@Composable
fun OfferDetails(offerid:String,onBackClick:()->Unit) {
    val homePageViewmodel:HomePageViewmodel= koinViewModel()
    val context= LocalContext.current
    LaunchedEffect(Unit) {
        homePageViewmodel.getOfferDetails(offerid)
    }
    val offer_detail=homePageViewmodel.offer_details_uiState.collectAsState().value
   Box(modifier = Modifier.fillMaxSize()) {

       when (offer_detail) {
           is UiState.Error -> {
               Log.d("error", offer_detail.message)
               Toast.makeText(context, offer_detail.message, Toast.LENGTH_SHORT).show()
           }

           is UiState.Loading -> {
               CircularProgressIndicator(color = BtnColor, modifier = Modifier.align(Alignment.Center))
           }

           is UiState.Success -> {
               offerDetail(offer_detail.data, onBackClick)
           }

           UiState.Empty -> {
               CircularProgressIndicator(color = BtnColor, modifier = Modifier.align(Alignment.Center))
               Log.d("error", "empty")
           }
       }
   }


}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun offerDetail( offer:Offer,onBackClick: () -> Unit){
    val viewmodel:HomePageViewmodel= koinViewModel()
    val userid=viewmodel.userId.collectAsState().value
    LaunchedEffect(Unit) {
        viewmodel.fetchUserId()
    }


    var is_saved by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column (   modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .verticalScroll(rememberScrollState()),
        ){
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
                            .padding(vertical = 30.dp, horizontal = 12.dp),
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
                            listOf(Icons.Outlined.FavoriteBorder, Icons.Outlined.Share).forEach {
                                Spacer(modifier = Modifier.width(8.dp))
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
                                        imageVector = it,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.clock),
                                        contentDescription = null,
                                        tint = BtnColor
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "OPEN FROM 09:00AM TO 11:00PM",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BtnColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = offer.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = offer.description,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )

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
                                    Text(
                                        text = offer.brand_name,
                                        fontSize = 12.sp,
                                        color = Color.Red,
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 6.dp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = Color.LightGray)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        tint = BtnColor
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Location",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
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
            ) {
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
            ) {
                Text(text = "Photos", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Divider(color = Color.LightGray)
            }
            Spacer(modifier = Modifier.height(8.dp))

            val media=
                if(offer.media.isEmpty()) emptyList() else  offer.media.map { it.url }
            PhotoGridScreen(imageUrls = media)


        }
        ButtonView(if(is_saved) "Open QR" else "Avail Offer", modifier = Modifier.align(Alignment.BottomCenter)) {
            showBottomSheet=true

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


    }
}



fun formatDate(input: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
    val outputFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)

    val date = LocalDate.parse(input, inputFormatter)
    return date.format(outputFormatter).uppercase() // to get "JAN" in uppercase
}

@Composable
fun CouponCard(offer: Offer) {
    println(offer)
    val date=formatDate(offer.end_date)

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
                    // Valid till tag
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
                    Column {
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
                                Text(
                                    text = "${offer.quantity} left",
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
                    text = offer.heading,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = offer.terms,
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
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    var visibleItemCount by remember { mutableStateOf(6) }
    var showExpandButton by remember { mutableStateOf(imageUrls.size > 6) }

    val displayedImages = imageUrls.take(visibleItemCount)
    val remainingCount = imageUrls.size - displayedImages.size

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
            }
        )
    }
}


@Composable
fun PhotoGrid(
    imageUrls: List<String>,
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
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model =imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}


