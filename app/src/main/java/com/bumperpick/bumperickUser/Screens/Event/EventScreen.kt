package com.bumperpick.bumperickUser.Screens.Event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.bumperpick.bumperickUser.API.New_model.DataXXXXXX
import com.bumperpick.bumperickUser.API.New_model.DataXXXXXXXX
import com.bumperpick.bumperickUser.API.New_model.EventModel
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Campaign.InfoRow
import com.bumperpick.bumperickUser.Screens.Home.UiState
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.satoshi_bold
import com.bumperpick.bumperickUser.ui.theme.satoshi_regular
import org.koin.androidx.compose.koinViewModel

@Composable
fun EventScreenMain(
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit = {},
    gotoEventDetail: (id:Int) -> Unit = {},
    viewmodel: EventViewmodel = koinViewModel(),

    ) {
    var searchQuery by remember { mutableStateOf("") }
    val events by viewmodel.events_uistate.collectAsState()
    LaunchedEffect(Unit) {
        viewmodel.getEvents()
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
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
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size = it },
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(backgroundModifier)
                    .padding(bottom = 0.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Top App Bar with improved spacing
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Event",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }

                    // Action Icons with improved spacing and backgrounds
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                    ) {
                        // Custom Icon Button
                        IconButton(
                            onClick = onNotificationClick,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.icon),
                                contentDescription = "Custom Action",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Notification Icon
                        IconButton(
                            onClick = onNotificationClick,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Search Field with improved styling
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color.Gray.copy(alpha = 0.7f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.Gray.copy(alpha = 0.7f)
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search Events",
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedBorderColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))


            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        when (events) {
            UiState.Empty -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No Events available",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Black
                    )
                }

            }

            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = (events as UiState.Error).message,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                        color = BtnColor
                    )
                }

            }
            UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        color = BtnColor,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

            }
            is UiState.Success ->{
                val eventList = (events as UiState.Success<List<DataXXXXXXXX>>).data
                val filteredList = eventList.filter {
                    it.title.contains(searchQuery, ignoreCase = true)
                }
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                    Image(painter = painterResource(R.drawable.left), contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${filteredList.size} Event",
                        letterSpacing = 2.sp, // Use sp for text spacing, not dp
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp),

                        )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(painter = painterResource(R.drawable.right), contentDescription = null, modifier = Modifier.size(12.dp))


                }
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    items(filteredList){event->
                        EventCard2(event){
                            gotoEventDetail(it)
                        }

                    }

                }



            }
        }

    }
}

@Composable
fun EventCard2(event: DataXXXXXXXX,onClick:(id:Int)->Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(event.id) }
            .padding(0.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box {
                AsyncImage(
                    model = event.banner_image_url,
                    contentDescription = "Event Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            // Event Details
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    fontFamily = satoshi_bold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(
                    icon = painterResource(R.drawable.calendar_svgrepo_com),
                    text = "Event date: ${event.start_date}"
                )
                InfoRow(
                    icon = painterResource(R.drawable.clock),
                    text = "Event time: ${event.start_time}"
                )
            }
        }
    }


}
