package com.bumperpick.bumperickUser.Screens.Component

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.satoshi_regular

import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.DataXX
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.Misc.LocationHelper
import com.bumperpick.bumperickUser.Screens.Home.HomePageViewmodel
import com.bumperpick.bumperickUser.Screens.Home.UiState
import com.bumperpick.bumperickUser.Screens.Home.imageurls
import com.bumperpick.bumperickUser.ui.theme.grey
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun TextFieldView(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Placeholder...",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = LocalTextStyle.current,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFF5F5F5), // Default light gray background
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = textStyle.copy(color = Color.Gray)
            )
        },
        keyboardOptions = keyboardOptions,
        textStyle = textStyle,
        modifier = modifier,
        singleLine = singleLine,
        enabled = isEnabled,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = containerColor,
            disabledTextColor = Color.Black,
            focusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            cursorColor =  BtnColor,
            focusedBorderColor =  BtnColor,
            unfocusedBorderColor = Color.Gray,

        )
    )
}

@Composable
fun ButtonView(text:String,
               enabled:Boolean=true,
               modifier: Modifier=Modifier, textColor: Color=Color.White, color: Color=BtnColor, horizontal_padding:Dp=16.dp, onClick:()->Unit) {
    Button(
        onClick = { onClick() },
        enabled = enabled,
        modifier = modifier

            .fillMaxWidth()
            .height(75.dp)
            .padding(bottom = 20.dp, start = horizontal_padding, end = horizontal_padding),

        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(16.dp)

    ) {
        Text(text, color = textColor, fontFamily = satoshi_regular, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun Google_SigInButton(modifier: Modifier=Modifier,onCLick:()->Unit) {


    OutlinedButton(
        onClick = { onCLick()},
        border = BorderStroke(0.dp, Color.Transparent),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        modifier = modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(
                color = Color(0xFFF0F0F0),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 16.dp,)
        ) {
            Image(
                painter = painterResource(R.drawable.google_icon_logo_svgrepo_com),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(end = 10.dp,)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .width(18.dp)
                    .height(18.dp)
            )
            Text(
                "Sign in with Google",
                color = Color(0xFF212427),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
@Composable
fun OtpView(
    numberOfOtp: Int,
    value: String,
    onValueChange: (String) -> Unit,
    otpCompleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequesters = List(numberOfOtp) { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(numberOfOtp) { index ->
            val char = if (index < value.length) value[index].toString() else ""
            OutlinedTextField(
                value = char,
                onValueChange = { newChar ->
                    if (newChar.length <= 1) {
                        val newValue = buildString {
                            append(value.take(index))
                            append(newChar)
                            append(value.drop(index + 1))
                        }.take(numberOfOtp)
                        onValueChange(newValue)
                        if (newChar.isNotEmpty() && index < numberOfOtp - 1) {
                            focusRequesters[index + 1].requestFocus()
                        } else if (newChar.isEmpty() && index > 0) {
                            focusRequesters[index - 1].requestFocus()
                        }
                        if (newValue.length == numberOfOtp) {
                            otpCompleted(newValue)
                            keyboardController?.hide()
                        }
                    }
                },
                modifier = Modifier
                    .width(48.dp)
                    .focusRequester(focusRequesters[index]),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BtnColor,
                    unfocusedBorderColor = Color.Gray,
                    disabledBorderColor = Color.Black,
                    focusedContainerColor = grey,
                    unfocusedContainerColor = grey,
                    disabledContainerColor = grey,
                    cursorColor = BtnColor
                ),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        if (index < numberOfOtp - 1) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    }
                ),
                singleLine = true
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }
}

data class NavigationItem(
    val label: String,
    val icon: ImageVector? = null,
    val painter: Painter? = null,
    val contentDescription: String,
)
@Composable
fun BottomNavigationBar(
    items: List<NavigationItem>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.shadow(0.dp)
    ) {
        items.forEachIndexed { index, item ->

            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {


                        // Icon itself
                        item.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = item.contentDescription,
                                tint = if (selectedTab == index) Color(0xFF3B82F6) else Color.Gray
                            )
                        }
                        item.painter?.let {
                            Icon(
                                painter = it,
                                contentDescription = item.contentDescription,
                                tint = if (selectedTab == index) Color(0xFF3B82F6) else Color.Gray
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selectedTab == index) Color(0xFF3B82F6) else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Medium else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                ),
                modifier = if (selectedTab == index) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.1f),
                                Color.White
                            )
                        )
                    )
                } else Modifier
            )
        }
    }
}

@Composable
fun LocationCard(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    var locationTitle by remember { mutableStateOf("") }
    var locationSubtitle by remember { mutableStateOf("") }
    var isLocationLoading by remember { mutableStateOf(true) }
    var locationError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val locationPair = locationHelper.getCurrentAddress()
            locationTitle = locationPair.first.ifEmpty { "Unknown Location" }
            locationSubtitle = locationPair.second.ifEmpty { "Tap to set location" }
            isLocationLoading = false
            locationError = false
        } catch (e: Exception) {
            locationTitle = "Location Error"
            locationSubtitle = "Tap to retry"
            isLocationLoading = false
            locationError = true
        }
    }

    var size by remember { mutableStateOf(IntSize.Zero) }

    val backgroundModifier = remember(size) {
        if (size.width > 0 && size.height > 0) {
            val radius = maxOf(size.width, size.height) / 1.5f
            Modifier.background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF8B1538), Color(0xFF5A0E26)),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = radius
                )
            )
        } else {
            Modifier.background(Color(0xFF8B1538))
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { size = it },
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.then(backgroundModifier)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onLocationClick() }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Enhanced Location Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Location Icon with loading state
                            Box(
                                modifier = Modifier.size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLocationLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {

                                    Icon(
                                        imageVector = if (locationError) {
                                            Icons.Outlined.LocationOn
                                        } else {
                                            Icons.Outlined.LocationOn
                                        },
                                        contentDescription = "Location",
                                        tint = if (locationError) {
                                            Color.White.copy(alpha = 0.7f)
                                        } else {
                                            Color.White
                                        },
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Location Text with better styling
                            Column(
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                Row {


                                    Text(
                                        text = if (isLocationLoading) "Getting location..." else locationTitle,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))

                                    // Animated dropdown arrow
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowDropDown,
                                        contentDescription = "Expand location options",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(start = 2.dp)
                                    )
                                }

                                AnimatedVisibility(
                                    visible = !isLocationLoading,
                                    enter = fadeIn() + slideInVertically(),
                                    exit = fadeOut() + slideOutVertically()
                                ) {
                                    Text(
                                        text = locationSubtitle,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp,
                                        letterSpacing = 0.3.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }


                        }

                        // Action Icons with better spacing
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Cart Icon with subtle background
                            IconButton(
                                onClick = onCartClick,
                                modifier = Modifier
                                    .size(40.dp)

                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingCart,
                                    contentDescription = "Shopping Cart",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Custom Icon Button
                            IconButton(
                                onClick = onNotificationClick,
                                modifier = Modifier
                                    .size(40.dp)

                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.icon),
                                    contentDescription = "Custom Action",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Notification Icon with badge support
                            Box {
                                IconButton(
                                    onClick = onNotificationClick,
                                    modifier = Modifier
                                        .size(40.dp)

                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }


                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}


data class category (
        val catid:String,
        val icon:String,
        val name:String
        )
val categorylist= listOf(
    category("1","","Fashion"),
    category("2","","Hotel"),
    category("3","","Cafe"),
    category("4","","Dinning"),
    category("5","","Dinning"),
)


@Composable
fun CategoryItem(category: Category,onClick: (Category) -> Unit) {
    Log.d("category", category.image_url ?: "")

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6B0221)),
        border = BorderStroke(0.5.dp, Color(0xFFFFD700)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .height(110.dp)
            .width(90.dp)
            .clickable {
                onClick(category)
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = category.image_url,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (2).dp, y = 2.dp)
            )

            Column(
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = category.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
private fun ImageSliderItem(
    imageUrl: String,
    modifier: Modifier = Modifier
) {



    AsyncImage(
        model=imageUrl,
        contentDescription = "Slider Image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

}

@Composable
fun AutoImageSlider(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    autoSlideInterval: Long = 5000L, // 5 seconds
    slideAnimationDuration: Int = 800 // milliseconds
) {
    if (imageUrls.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { imageUrls.size }
    )

    // Auto-scroll effect
    LaunchedEffect(pagerState) {
        while (true) {
            delay(autoSlideInterval)
            val nextPage = (pagerState.currentPage + 1) % imageUrls.size
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(durationMillis = slideAnimationDuration)
            )
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),

        ) {
        // Image Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .height(175.dp),
            pageSpacing = 8.dp
        ) { page ->
            ImageSliderItem(
                imageUrl = imageUrls[page],
                modifier = Modifier.fillMaxSize()
            )
        }



        // Red Dot Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        ) {
            repeat(imageUrls.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) BtnColor else BtnColor.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}



@Composable
fun ChipRowWithSelectiveIcons() {
    val chips = listOf(
        ChipData("Filter", Icons.Default.List),
        ChipData("Sort by", Icons.Default.ArrowDropDown),
        ChipData("Offers", null),
        ChipData("Distance", null)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            ChipWithOptionalIcon(label = chip.label, icon = chip.icon)
        }
    }
}

@Composable
fun ChipWithOptionalIcon(label: String, icon: ImageVector?) {
    Surface(
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White ,
        border = BorderStroke(1.dp, Color.Gray),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clickable { /* Handle click */ },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$label icon",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

data class ChipData(val label: String, val icon: ImageVector?)
enum class OfferValidation{
    Valid,Expired
}
enum class MarketingOption(val title: String) {
    OFFERS("Offers"),
    CUSTOMER_ENGAGEMENT("Customer engagement"),
    CONTEST_FOR_CUSTOMERS("Contest for customers"),
    SCRATCH_AND_WIN("Scratch & win"),
    LUCKY_DRAW("Lucky draw"),
    CAMPAIGNS("Campaigns"),
    EVENTS("Events");

    companion object {
        val allOptions = values().toList()
    }
}
data class Media(
    val id: String,
    val type: String,
    val url: String,
    val s: String
)
data class HomeOffer(
    val offerId:String="",
    val Type:MarketingOption?=null,
    val offerValid:OfferValidation?=null,
    val Media_list:List<String> = emptyList(),
    val discount:String="",
    val startDate:String="",
    val media:List<Media> =ArrayList(),
    val approval:String="",
    val endDate:String="",
    val active:String="",
    val offerTitle:String="",
    val brand_logo_url:String?="",
    val offerTag:String="",
    val offerDescription:String="",
    val termsAndCondition:String="",
)
private fun drawHorizontalDots(
    drawScope: DrawScope,
    color: Color,
    dotSizePx: Float,
    spacingPx: Float,
    dotCount: Int?
) {
    val width = drawScope.size.width
    val height = drawScope.size.height
    val centerY = height / 2
    val radius = dotSizePx / 2

    val totalDotWidth = dotSizePx + spacingPx
    val calculatedDotCount = dotCount ?: (width / totalDotWidth).toInt()
    val actualWidth = calculatedDotCount * totalDotWidth - spacingPx
    val startX = (width - actualWidth) / 2

    repeat(calculatedDotCount) { index ->
        val x = startX + index * totalDotWidth + radius
        drawScope.drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, centerY)
        )
    }
}

private fun drawVerticalDots(
    drawScope: DrawScope,
    color: Color,
    dotSizePx: Float,
    spacingPx: Float,
    dotCount: Int?
) {
    val width = drawScope.size.width
    val height = drawScope.size.height
    val centerX = width / 2
    val radius = dotSizePx / 2

    val totalDotHeight = dotSizePx + spacingPx
    val calculatedDotCount = dotCount ?: (height / totalDotHeight).toInt()
    val actualHeight = calculatedDotCount * totalDotHeight - spacingPx
    val startY = (height - actualHeight) / 2

    repeat(calculatedDotCount) { index ->
        val y = startY + index * totalDotHeight + radius
        drawScope.drawCircle(
            color = color,
            radius = radius,
            center = Offset(centerX, y)
        )
    }
}
@Composable
fun DottedDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    dotSize: Dp = 2.dp,
    spacing: Dp = 4.dp,
    isVertical: Boolean = false,
    dotCount: Int? = null
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val dotSizePx = with(density) { dotSize.toPx() }
        val spacingPx = with(density) { spacing.toPx() }

        if (isVertical) {
            drawVerticalDots(
                drawScope = this,
                color = color,
                dotSizePx = dotSizePx,
                spacingPx = spacingPx,
                dotCount = dotCount
            )
        } else {
            drawHorizontalDots(
                drawScope = this,
                color = color,
                dotSizePx = dotSizePx,
                spacingPx = spacingPx,
                dotCount = dotCount
            )
        }
    }
}
@Composable
fun HomeOfferView(offerModel: Offer, offerClick:(String)->Unit  ){

    Card (
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { offerClick(offerModel.id.toString()) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

        ){
        Column() {
            Box(modifier = Modifier.fillMaxWidth()){
                val imagelist=offerModel.media.map {
                    it.url
                }
                AutoImageSlider(imageUrls = imagelist)
                Box(
                    modifier = Modifier

                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 12.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .background(Color.White)
                        .align(Alignment.BottomStart)
                ) {
                    Text(text = "${offerModel.quantity} left", color = Color.Black, fontSize = 14.sp, modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp))

                }




            }


            Column(modifier = Modifier.padding(12.dp)) {
                Spacer(Modifier.height(5.dp))
                Text(
                    text = offerModel.title,
                    fontSize = 22.sp,
                    fontFamily = satoshi_regular,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
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
                val is_approved=offerModel.approval.equals("accepted")
                val is_active=offerModel.approval.equals("active")
                val color = when {
                    is_approved && is_active -> Color.Green
                    is_approved && !is_active -> Color.Red
                    !is_approved -> Color(0xFFFFA500) // Orange color
                    else -> Color.Gray // fallback (optional)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    // Discount row (start)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.percentage_red),
                            contentDescription = "percentage",
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
@Composable
fun CartOfferView(offerModel: DataXX, openQr: (id: String) -> Unit,deleteCart:(String)->Unit,showOpenQrBtn:Boolean=true) {

    val context= LocalContext.current
    var loading by remember { mutableStateOf(false) }

    val offer = offerModel.offer
    Log.d("offer",offer.toString())
    val media = if (offer.media.isEmpty()) emptyList() else offerModel.offer.media.map { it.url }

    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        if(loading){
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(color = BtnColor)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Deleting...", color = BtnColor)
            }

        }
        else {
            Column {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Delete button


                    // Image slider
                    AutoImageSlider(imageUrls = media)
                    if(showOpenQrBtn) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(24.dp)
                                .background(
                                    color = Color.Black.copy(0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    0.5.dp,
                                    Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "Delete offer",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .clickable {
                                        deleteCart(offerModel.id.toString())
                                    }
                                    .padding(4.dp)
                                    .size(30.dp),
                                tint = Color.White
                            )
                        }
                    }
                    // Quantity badge
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 12.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            )
                            .background(Color.White)
                            .align(Alignment.BottomStart)
                    ) {
                        Text(
                            text = "${offer.quantity} left",
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(12.dp)) {
                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = offer.title?:"",
                        fontSize = 22.sp,
                        fontFamily = satoshi_regular,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = offer.description,
                        fontSize = 14.sp,
                        fontFamily = satoshi_regular,
                        color = Color.Black,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(modifier = Modifier.height(12.dp))

                    DottedDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Gray,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Discount row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.percentage_red),
                                contentDescription = "percentage",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = offer.discount,
                                fontSize = 15.sp,
                                fontFamily = satoshi_regular,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                if(showOpenQrBtn) {
                    ButtonView(
                        text = "Open QR",
                        color = BtnColor.copy(alpha = 0.2f),
                        textColor = BtnColor,
                        modifier = Modifier.padding(vertical = 0.dp)
                    ) {
                        openQr(offer.id.toString())
                    }
                }
            }
        }
    }
}
fun generateQRCodeBitmap(content: String): Bitmap {
    val size = 512
    val bits = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    for (x in 0 until size) {
        for (y in 0 until size) {
            val color = if (bits.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            bitmap.setPixel(x, y, color)
        }
    }

    return bitmap
}
@Composable
fun CartBottomSheet(jsonData: String,offerId: String,onBack:()->Unit){
    val bitmap = remember(jsonData) { generateQRCodeBitmap(jsonData) }
    val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                bitmap = imageBitmap,
                contentDescription = "QR Code",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Present this QR code at the outlet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

                ButtonView("Go Back") {
                    onBack()

            }

        }
    }
}

@Composable
fun QRCodeBottomSheet(jsonData: String,offerId:String, onAddToCart: () -> Unit,goback:()->Unit,is_saved:Boolean=false) {
    val context = LocalContext.current
    val bitmap = remember(jsonData) { generateQRCodeBitmap(jsonData) }
    var showloading by remember { mutableStateOf(false) }
    val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
    val viewnodel:HomePageViewmodel= koinViewModel()
    val cart=viewnodel.add_to_cart_uiState.collectAsState().value
    Log.d("is_saved",is_saved.toString())
    LaunchedEffect(cart) {
        when(cart){
            UiState.Empty -> {
                Log.d("error","empty")
            }
            is UiState.Error -> {
                showloading=false
                Toast.makeText(context, cart.message, Toast.LENGTH_SHORT).show()
            }
            UiState.Loading ->{
                showloading=true
            }
            is UiState.Success ->{
                showloading=false
                Log.d("success","s")
                    viewnodel.resetaddtocart()
                    onAddToCart()
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                bitmap = imageBitmap,
                contentDescription = "QR Code",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if(is_saved)"Present this QR code at the outlet" else "QR Code Generated",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))
            if(!is_saved) {
                Text(
                    text = "Now visit the outlet and show this QR code",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if(showloading){
                CircularProgressIndicator(color = BtnColor)
            }
            else {
                ButtonView(if (!is_saved) "Save to the cart" else "Go Back") {
                    if(!is_saved) {
                        viewnodel.addToCart(offerId)
                        }
                    else{
                        goback()
                    }
                }
            }

        }
    }
}
@Composable
fun SearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "Search...",
    onSearch: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(width = 1.dp, color = Color.Gray.copy(alpha = 0.3f)),
          colors = CardDefaults.cardColors(containerColor = grey)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray,
                modifier = Modifier.padding(start = 12.dp).size(30.dp)
            )


            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(hint, color = Color.Gray) },
                textStyle = TextStyle(fontSize = 18.sp),

                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch()
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            )

            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear text",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun SignOutDialog(

    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Sign Out",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )

}



