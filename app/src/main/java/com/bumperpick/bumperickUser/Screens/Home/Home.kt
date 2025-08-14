package com.bumperpick.bumperickUser.Screens.Home

import FilterSortScreen
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.DataXXXXXXXXXXXXX
import com.bumperpick.bumperickUser.API.New_model.Offer
import com.bumperpick.bumperickUser.Navigation.show_toast
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.AutoImageSlider
import com.bumperpick.bumperickUser.Screens.Component.CategoryItem
import com.bumperpick.bumperickUser.Screens.Component.ChipRowWithSelectiveIcons

import com.bumperpick.bumperickUser.Screens.Component.HomeOfferView
import com.bumperpick.bumperickUser.Screens.Component.LocationCard

import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.satoshi
import com.bumperpick.bumperpickvendor.API.Model.success_model
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

sealed class HomeClick(){
    data class OfferClick(val offerId:String):HomeClick()
    object CartClick:HomeClick()
    object LocationClick:HomeClick()
    object SearchClick:HomeClick()
    object FavClick: HomeClick()
    object NotifyClick: HomeClick()
    data class CategoryClick(val cat:Category):HomeClick()

}


sealed class gotoPage(){
    object Event:gotoPage()
    object Campaign:gotoPage()
}
@Composable
fun CampaignSlider(goto: (gotoPage) -> Unit) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    // Auto-scroll


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()

        ) { page ->
            when (page) {
                1 -> EventCard({ goto(gotoPage.Event) })
                0-> CampaignCard { goto(gotoPage.Campaign) }
            }
        }

        // Indicator
        Row(
            modifier = Modifier.padding(top = 6.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(2) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                        .background(
                            color = if (pagerState.currentPage == index)
                             BtnColor
                            else
                                Color.Gray,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

@Composable
fun Home(
    homeClick: (HomeClick) -> Unit,
    gotoEvent: () -> Unit,
    gotoCampaign: () -> Unit,
    viewModel: HomePageViewmodel = koinViewModel()
) {

    val offerDetails by viewModel.offer_uiState.collectAsState()
    val categoryDetails by viewModel.categories_uiState.collectAsState()
    val locationDetails by viewModel.getLocation.collectAsState()
    val context = LocalContext.current
    val fav_toogle by viewModel.fav_toogle_uiState.collectAsState()
    val fetch_banner by viewModel.banner.collectAsState()
    var hide_cat by remember { mutableStateOf(false) }
    // Define imageUrls here or pass them from a higher level if dynamic







    // Fetch data only once when the composable enters the composition
    LaunchedEffect(Unit) {
        viewModel.getOffers()
        viewModel.getCategory()
        viewModel.clearFilter()
        viewModel.fetchLocation()
        viewModel.fetchBanner()
    }

    LaunchedEffect(fav_toogle) {
        when(fav_toogle){
            UiState.Empty -> {}
            is UiState.Error -> {
                show_toast((fav_toogle as UiState.Error).message,context)
            }
            UiState.Loading -> {}
            is UiState.Success -> {
                show_toast((fav_toogle as UiState.Success<success_model>).data.message,context)
                viewModel.free_fav()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize() // Only one scrollable column
    ) {

        LocationCard(
            locationDetails=locationDetails,

            onCartClick = { homeClick(HomeClick.CartClick) },
            onLocationClick = { homeClick(HomeClick.LocationClick) },
            onFavClick = { homeClick(HomeClick.FavClick) },
            onNotificationClick = { homeClick(HomeClick.NotifyClick) },
            modifier = Modifier.animateContentSize(
                animationSpec = tween(durationMillis = 300),
            ),
        ) {
            // Search Card
            SearchCard(onSearchClick = { homeClick(HomeClick.SearchClick) })

            // Category List
            AnimatedVisibility(
                visible = hide_cat,
                enter = fadeIn(animationSpec = tween(300)) +
                        slideInVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) +
                        slideOutVertically(animationSpec = tween(300)),
                modifier = Modifier

            ) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    CategoryContent(
                        categoryDetails = categoryDetails,
                        onCategoryClick = { selectedCategory ->
                            homeClick(HomeClick.CategoryClick(selectedCategory))
                        }
                    )
                }
            }


        }
        val scrollState = rememberScrollState()
        val thirdElementPosition = with(LocalDensity.current) { (100.dp * 2).toPx() }
        LaunchedEffect(scrollState.value) {
            hide_cat = scrollState.value < thirdElementPosition
        }
        Column(
            modifier = Modifier.verticalScroll(scrollState))
        {
            Spacer(modifier = Modifier.height(4.dp))


           CampaignSlider { when(it){
               gotoPage.Event->gotoEvent()
               gotoPage.Campaign->gotoCampaign()
           } }
            Spacer(modifier = Modifier.height(4.dp))

            // Trending Offers Header
            TrendingOffersHeader()
            Spacer(modifier = Modifier.height(12.dp))

            // Image Slider
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
                border = BorderStroke(0.1.dp, Color.Black)
            ) {
                when(fetch_banner){
                    UiState.Empty ->{}
                    is UiState.Error -> {

                    }
                    UiState.Loading -> {
                            CircularProgressIndicator(color = BtnColor)
                    }
                    is UiState.Success<*> -> {
                        val banners=(fetch_banner as UiState.Success<List<DataXXXXXXXXXXXXX>>).data.map { it.banner }
                        AutoImageSlider(banners)
                    }
                }

            }
            Spacer(modifier = Modifier.height(8.dp))

            // Filter and Sort
            // Pass the actual category data when it's available
            FilterSortScreen(
                viewmodel = viewModel,
                onFiltersApplied = {
                    viewModel.updateFilters(it)




                },
                onSortSelected = { val id=it.id
                    viewModel.updateSortBy(id)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Offers Content
            OffersContent(
                offerDetails = offerDetails,
                onOfferClick = { offerId -> homeClick(HomeClick.OfferClick(offerId)) },
                liketheoffer = {offerId->
                    viewModel.toogle_fav(offerId,true)
                },
                shareoffer = {
                    Log.d("shareoffer",it)
                    shareReferral(context,it)

                }
            )

        }
        // Display Toast for errors
        DisplayErrorToast(uiState = categoryDetails, context = context)
        DisplayErrorToast(uiState = offerDetails, context = context)
    }

}

// --- Private Composable Functions for Modularity ---

@Composable
private fun SearchCard(onSearchClick: () -> Unit) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSearchClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                tint = Color.Black,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search for Reliance Mart",
                color = Color.Gray,
                fontFamily = satoshi,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CategoryContent(
    categoryDetails: UiState<List<Category>>,
    onCategoryClick: (Category) -> Unit
) {
    when (categoryDetails) {
        is UiState.Error -> {
            // Error handling for categories can be shown here, e.g., a Retry button
            Text("Failed to load categories: ${categoryDetails.message}", color = MaterialTheme.colorScheme.error)
        }
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BtnColor)
            }
        }
        is UiState.Success -> {
            if (categoryDetails.data.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(categoryDetails.data) { category ->
                        CategoryItem(category) { selectedCategory ->
                            onCategoryClick(selectedCategory)
                        }
                    }
                }
            } else {
                Text("No categories available.", modifier = Modifier.padding(horizontal = 16.dp), color = Color.White)
            }
        }
        is UiState.Empty -> {
            Text("No categories available.", modifier = Modifier.padding(horizontal = 16.dp), color = Color.White)
        }
    }
}

@Composable
private fun CampaignCard(gotoEvent: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BtnColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BtnColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 0.dp) // Apply padding here once
            .clickable { gotoEvent() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = R.drawable.speaker,
                    contentDescription = "Speaker Icon",
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Explore exciting campaign near you",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = "Forward",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp),
                tint = BtnColor
            )
        }
    }
}
@Composable
private fun EventCard(gotoEvent: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xff2F88FF).copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xff2F88FF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 0.dp)
            .clickable { gotoEvent() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = R.drawable.ticket_svgrepo_com,
                    contentDescription = "Speaker Icon",
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "Explore exciting events near you",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = "Forward",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp),
                tint = Color(0xff2F88FF)
            )
        }
    }
}

@Composable
private fun TrendingOffersHeader() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.left), // Ensure these drawables exist
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "TRENDING OFFERS",
                letterSpacing = 3.sp,
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(R.drawable.right), // Ensure these drawables exist
                contentDescription = null,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun OffersContent(
    offerDetails: UiState<List<Offer>>,
    onOfferClick: (String) -> Unit,
    liketheoffer:(String)->Unit={},
    shareoffer:(String)-> Unit={}
) {
    when (offerDetails) {
        is UiState.Error -> {
            // Error handling for offers, e.g., a message or a retry button
            Text("Failed to load offers: ${offerDetails.message}", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        }
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BtnColor)
            }
        }
        is UiState.Success -> {
            val data = offerDetails.data.filter { !it.expire }
            if (data.isNotEmpty()) {
                Column { // Offers are shown in a column, not lazy
                    data.forEach { offer ->
                        HomeOfferView(
                            offerModel = offer,
                            offerClick = { onOfferClick(offer.id.toString()) },
                           liketheoffer = liketheoffer,
                            shareoffer={
                                Log.d("shareoffer",it)
                                shareoffer(it)
                            }
                        )
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Image(painter = painterResource(R.drawable.artwork), contentDescription = null, modifier = Modifier.size(100.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("No trending offers available.", modifier = Modifier.padding(horizontal = 0.dp).fillMaxWidth(), textAlign = TextAlign.Center, color = BtnColor)
                }

            }
        }
        is UiState.Empty -> {
            Text("No trending offers available.", modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun DisplayErrorToast(uiState: UiState<*>, context: android.content.Context) {
    if (uiState is UiState.Error) {
        LaunchedEffect(uiState.message) {
            Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
        }
    }
}
