package com.bumperpick.bumperickUser.Screens.Home

import FilterSortScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.API.New_model.trendingSearchModel
import com.bumperpick.bumperickUser.Navigation.show_toast
import org.koin.androidx.compose.koinViewModel
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Home.UiState
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.grey
import com.bumperpick.bumperickUser.ui.theme.satoshi
import com.bumperpick.bumperickUser.ui.theme.satoshi_bold
import com.bumperpick.bumperickUser.ui.theme.satoshi_medium
import com.bumperpick.bumperpickvendor.API.Model.success_model


@Composable
fun OfferSearchScreen(
    homeClick: (HomeClick) -> Unit,
    onBackClick: () -> Unit,
    viewModel: HomePageViewmodel = koinViewModel()
) {
    val offerDetails by viewModel.offer_uiState.collectAsState()
    val trendingSearches by viewModel.trendSearch_uiState.collectAsState()
    val context = LocalContext.current
    val fav_toogle by viewModel.fav_toogle_uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    LaunchedEffect(fav_toogle) {
        when(fav_toogle){
            UiState.Empty -> {}
            is UiState.Error -> {
                show_toast((fav_toogle as UiState.Error).message,context)
            }
            UiState.Loading -> {}
            is UiState.Success -> {
                show_toast((fav_toogle as UiState.Success<success_model>).data.message,context)
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getOffers(removeads = true)
        viewModel.getTrendingSearches()
        viewModel.getCategory()
    }

    LaunchedEffect(searchQuery) {
        viewModel.updateSearch(searchQuery)
    }

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(0.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Column (modifier = Modifier.background(color = Color.White)){
                Spacer(modifier = Modifier.height(12.dp))
                SearchCard(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearchClick = {
                        viewModel.updateSearch(searchQuery)
                        homeClick(HomeClick.SearchClick)
                    },
                    onBackClick = onBackClick,
                    showBack = searchQuery.isNotEmpty()
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Divider(modifier = Modifier.height(0.5.dp), color = Color.Gray)
            }


            Column(modifier = Modifier.fillMaxSize().background(grey)) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Trending searches",
                        style = MaterialTheme.typography.titleMedium.copy(

                            fontSize = 20.sp,
                            fontFamily = satoshi_medium,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(
                            bottom = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp
                        )
                    )

                    when (trendingSearches) {
                        UiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is UiState.Success -> {
                            val data =
                                (trendingSearches as UiState.Success<trendingSearchModel>).data.data
                            FlowLayoutTrendingSearches(
                                trendingSearches = data,
                                onTrendingSearchClick = { term ->
                                    searchQuery = term
                                    viewModel.updateSearch(term)
                                    homeClick(HomeClick.SearchClick)
                                }
                            )
                        }

                        else -> {}
                    }
                } else {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Spacer(modifier = Modifier.height(12.dp))
                        FilterSortScreen(
                            viewmodel = viewModel,
                            onFiltersApplied = { selectedCategories ->
                                viewModel.updateCategories(selectedCategories.map { it.id })
                            },
                            onSortSelected = { viewModel.updateSortBy(it.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OffersContent(
                            offerDetails = offerDetails,
                            onOfferClick = { offerId -> homeClick(HomeClick.OfferClick(offerId)) },
                            liketheoffer = {
                                offerId->viewModel.toogle_fav(offerId,true)
                            },
                            shareoffer = {
                                shareReferral(context,it)
                            }
                        )
                    }
                }
            }

            DisplayErrorToast(uiState = offerDetails, context = context)
            DisplayErrorToast(uiState = trendingSearches, context = context)
        }
    }
}

@Composable
fun SearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    showBack: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))


            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search for \"Reliance mart\"",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle.Default.copy(fontSize = 18.sp, color = Color.Black),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                }
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun FlowLayoutTrendingSearches(
    trendingSearches: List<String>,
    onTrendingSearchClick: (String) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(trendingSearches) { searchTerm ->
            TrendingPillbox(
                onClick = { onTrendingSearchClick(searchTerm) },
                text = searchTerm,
                modifier = Modifier.fillMaxWidth(),
                icon = R.drawable.arrow_growth
            )
        }
    }
}

@Composable
fun TrendingPillbox(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable { onClick() }
            // Remove fixed height to allow natural sizing
            .wrapContentHeight(),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Trending",
                tint = BtnColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color(0xFF374151),
                fontWeight = FontWeight.Medium,
                // Remove maxLines to allow text wrapping
                modifier = Modifier.weight(1f)
            )
        }
    }
}