 package com.bumperpick.bumperickUser.Screens.Event

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bumperpick.bumperickUser.API.New_model.DataXXXXXXXX
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Home.UiState
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.satoshi_bold
import com.bumperpick.bumperickUser.ui.theme.satoshi_regular
import org.koin.androidx.compose.koinViewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.SideEffect

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bumperpick.bumperickUser.Screens.Home.formatDate
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventDetailScreen(
    onBackClick: () -> Unit,
    eventId: Int,
    viewModel: EventViewmodel = koinViewModel(),
    onOpenWebView: (String) -> Unit // New parameter for opening web view
) {
    val event by viewModel.event_uistate.collectAsState()
    val context = LocalContext.current
    val statusBarColor = Color(0xFF5A0E26) // Your desired color
    val systemUiController = rememberSystemUiController()    // Change status bar color
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false // true for dark icons on light background
        )
    }
    LaunchedEffect(Unit) {
        viewModel.getEvent(eventId)
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            // Enhanced Header with gradient background
            var size by remember { mutableStateOf(IntSize.Zero) }
            val backgroundModifier = remember(size) {
                if (size.width > 0 && size.height > 0) {
                    val radius = maxOf(size.width, size.height) / 1.2f
                    Modifier.background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8B1538),
                                Color(0xFF5A0E26),
                                Color(0xFF3D0A1B)
                            ),
                            center = Offset(size.width / 2f, size.height / 3f),
                            radius = radius
                        )
                    )
                } else {
                    Modifier.background(Color(0xFF8B1538))
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { size = it },
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(backgroundModifier)
                        .padding(bottom = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Enhanced Top App Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Event Details",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (event) {
                UiState.Empty -> {
                    EmptyStateContent()
                }

                is UiState.Error -> {
                    ErrorStateContent((event as UiState.Error).message)
                }

                UiState.Loading -> {
                    LoadingStateContent()
                }

                is UiState.Success -> {
                    val eventData = (event as UiState.Success<DataXXXXXXXX>).data
                    EventContent(
                        eventData = eventData,
                        context = context,
                        onOpenWebView = onOpenWebView
                    )
                }
            }
        }
    }
}

@Composable
private fun EventContent(
    eventData: DataXXXXXXXX,
    context: Context,
    onOpenWebView: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Enhanced Banner Image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = FastOutSlowInEasing
                        )
                    )
            ) {
                AsyncImage(
                    model = eventData.banner_image_url,
                    contentDescription = "Event Banner",
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.image_1),
                    placeholder = painterResource(R.drawable.image_1),
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay for better text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Enhanced Event Title
        Text(
            text = eventData.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        eventData.description?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(containerColor = Color.White),

                ) {

                Text(
                    text = eventData.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF666666),
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Enhanced Event Info Section
        Text(
            text = "Event Information",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Enhanced Event Info Cards
        EnhancedEventInfoCard(
            icon = R.drawable.location_svgrepo_com,
            text = eventData.address,
            label = "Location"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Enhanced Date Range Card
        EnhancedDateRangeCard(
            startDate = eventData.start_date,
            endDate = eventData.end_date , // Fallback to start date if end date is null
            startTime = eventData.start_time,
            endTime = eventData.end_time // Fallback if end time is null
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Enhanced Live Stream Section
        Text(
            text = "Live Stream",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        EnhancedLiveStreamSection(
            youtubeVideoId = eventData.youtube_link ?: "",
            facebookVideoUrl = eventData.facebook_link ?: "",
            instagramUrl = eventData.instagram_link ?: "", // Assuming this field exists
            context = context,
            onOpenWebView = onOpenWebView
        )
    }
}

@Composable
private fun EnhancedEventInfoCard(
    icon: Int,
    text: String,
    label: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(containerColor = Color.White),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BtnColor.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = label,
                        tint = BtnColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = label,
                    fontFamily = satoshi_bold,
                    fontSize = 14.sp,
                    color = BtnColor.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = text,
                    fontFamily = satoshi_regular,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun EnhancedDateRangeCard(
    startDate: String?,
    endDate: String?,
    startTime: String?,
    endTime: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(48.dp),

                    colors = CardDefaults.cardColors(containerColor = BtnColor.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_svgrepo_com),
                            contentDescription = "Date",
                            tint = BtnColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Event Start At",
                        fontFamily = satoshi_bold,
                        fontSize = 14.sp,
                        color = BtnColor.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text ="${formatDate( startDate)} $startTime",
                        fontFamily = satoshi_regular,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A),
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            endDate?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_svgrepo_com),
                            contentDescription = "Time",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Event End At",
                        fontFamily = satoshi_bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2196F3).copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))



                        Text(
                            text = "${formatDate(endDate)} ${endTime?:""}",
                            fontFamily = satoshi_regular,
                            fontSize = 16.sp,
                            color = Color(0xFF1A1A1A),
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EnhancedLiveStreamSection(
    youtubeVideoId: String,
    facebookVideoUrl: String,
    instagramUrl: String,
    context: Context,
    onOpenWebView: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical=16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // YouTube Live Card with Thumbnail
        if (youtubeVideoId.isNotBlank()) {
            EnhancedYouTubeLiveCard(
                videoId = youtubeVideoId,
                onOpenWebView = onOpenWebView
            )
        } else {
            Text(
                text = "No YouTube Live Stream Available",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        // Social Media Live Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Facebook Live Button
            if (facebookVideoUrl.isNotBlank()) {
                SocialMediaLiveButton(
                    modifier = Modifier.fillMaxWidth(),
                    icon = R.drawable.facebook_icon,
                    title = "Facebook Live",
                    backgroundColor = Color(0xFF1877F2),
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookVideoUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handle invalid URL or no app to handle intent
                            onOpenWebView(facebookVideoUrl)
                        }
                    }
                )
            }

            // Instagram Live Button
            if (instagramUrl.isNotBlank()) {
                SocialMediaLiveButton(
                    modifier = Modifier.fillMaxWidth(),
                    icon = R.drawable.instagram_1_svgrepo_com,
                    title = "Instagram Live",
                    backgroundColor = Color(0xFFE4405F),
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handle invalid URL or no app to handle intent
                            onOpenWebView(instagramUrl)
                        }
                    }
                )
            }
        }

        // Placeholder if no social media buttons are visible
        if (facebookVideoUrl.isBlank() && instagramUrl.isBlank()) {
            Text(
                text = "No Social Media Live Streams Available",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EnhancedYouTubeLiveCard(
    videoId: String,
    onOpenWebView: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpenWebView(videoId) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.youtube_icon),
                                contentDescription = "YouTube Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "YouTube Live Stream",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "Tap to watch live",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }
                }

                Card(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF0000))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // YouTube Thumbnail
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
                        contentDescription = "YouTube Thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Play button overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color(0xFFFF0000),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialMediaLiveButton(
    modifier: Modifier = Modifier,
    icon: Int,
    title: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}
@Composable
private fun EmptyStateContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.calendar_svgrepo_com),
                contentDescription = "No Events",
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Events Available",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorStateContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.calendar_svgrepo_com),
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun LoadingStateContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        CircularProgressIndicator(
            color = BtnColor,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center)
        )
    }
}