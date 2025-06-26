package com.bumperpick.bumperickUser.Screens.Home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import org.koin.androidx.compose.koinViewModel
// Required imports for the above code
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.HomeOffer
import com.bumperpick.bumperickUser.Screens.Component.HomeOfferView
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.grey
import androidx.compose.ui.graphics.vector.ImageVector as ImageVector1
val trendingItems = listOf(
    TrendingItem("Burger king", R.drawable.arrow_growth),
    TrendingItem("Fab Hotels", R.drawable.arrow_growth),
    TrendingItem("D Mart", R.drawable.arrow_growth),
    TrendingItem("Zudio", R.drawable.arrow_growth),
    TrendingItem("H&M", R.drawable.arrow_growth),
    TrendingItem("OYO", R.drawable.arrow_growth),
    TrendingItem("Lacoste", R.drawable.arrow_growth),
    TrendingItem("Nike", R.drawable.arrow_growth)
)
data class TrendingItem(
    val text: String,
    val icon: Int
)

@Composable
fun OfferSearchPage(
    viewmodel: HomePageViewmodel = koinViewModel(),
    onHomeClick: (HomeClick) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val offerDetails = viewmodel.offer_uiState.collectAsState().value
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewmodel.getOffers()
    }
    Scaffold(containerColor = Color.White) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)

                .background(grey)
        ) {
            // Top Search Bar
            SearchTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSearchActive = isSearchActive,
                onSearchActiveChange = { isSearchActive = it },
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Trending searches ", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(120.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                items(trendingItems) { item ->
                    TrendingPillbox(
                        text = item.text,
                        icon = item.icon
                    )
                }
            }
        }
    }
}


@Composable
fun TrendingPillbox(
    text: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ },
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BtnColor),
        color = Color.White,

    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Trending",
                tint = Color.Red,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Search TextField with modern styling
            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,

                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color(0xFFF8F9FA),
                        shape = RoundedCornerShape(12.dp)
                    )

                    .padding(horizontal = 16.dp, vertical = 12.dp),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search for \"Reliance mart\"",
                                    color = Color(0xFF9E9E9E),
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }

                        if (searchQuery.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF9E9E9E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    offers: List<Offer>,
    searchQuery: String,
    onOfferClick: (HomeOffer) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),

        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Search Results Header
        item {
            SearchResultsHeader(
                resultCount = offers.size,
                searchQuery = searchQuery
            )
        }

        // Offer Items
        if (offers.isEmpty() && searchQuery.isNotEmpty()) {
            item {
                NoResultsFound(searchQuery = searchQuery)
            }
        } else {
            items(offers) { offer ->
                HomeOfferView(
                    offerModel = offer,
                    offerClick = { onOfferClick(HomeOffer(it)) }
                )
            }
        }
    }
}

@Composable
private fun SearchResultsHeader(
    resultCount: Int,
    searchQuery: String
) {
    Column(
        modifier = Modifier.padding(bottom = 12.dp, start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (searchQuery.isNotEmpty()) {
            Text(
                text = "Results for \"$searchQuery\"",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(4.dp))
        }

        Text(
            text = "$resultCount ${if (resultCount == 1) "offer" else "offers"} found",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE5E7EB)
        )
    }
}

@Composable
private fun NoResultsFound(searchQuery: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "No results",
            modifier = Modifier.size(72.dp),
            tint = Color(0xFFD1D5DB)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No offers found",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try searching with different keywords",
            fontSize = 16.sp,
            color = Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )

        if (searchQuery.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "for \"$searchQuery\"",
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF),
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun EmptySearchState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Start searching for offers",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Use the search bar above to find amazing deals and offers",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircularProgressIndicator(
                color = BtnColor,
                modifier = Modifier.size(44.dp),
                strokeWidth = 3.dp
            )
            Text(
                text = "Searching offers...",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,

                contentDescription = "Error",
                modifier = Modifier.size(72.dp),
                tint = Color(0xFFEF4444)
            )

            Text(
                text = "Something went wrong",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )

            Text(
                text = message,
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}