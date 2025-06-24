package com.bumperpick.bumperickUser.Screens.Home

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.AutoImageSlider
import com.bumperpick.bumperickUser.Screens.Component.CategoryItem
import com.bumperpick.bumperickUser.Screens.Component.ChipRowWithSelectiveIcons
import com.bumperpick.bumperickUser.Screens.Component.HomeOffer
import com.bumperpick.bumperickUser.Screens.Component.HomeOfferView
import com.bumperpick.bumperickUser.Screens.Component.LocationCard
import com.bumperpick.bumperickUser.Screens.Component.MarketingOption
import com.bumperpick.bumperickUser.Screens.Component.Media
import com.bumperpick.bumperickUser.Screens.Component.OfferValidation
import com.bumperpick.bumperickUser.Screens.Component.categorylist
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.satoshi
import org.koin.androidx.compose.koinViewModel

sealed class HomeClick(){
    data class OfferClick(val offerId:String):HomeClick()
    object CartClick:HomeClick()
    object LocationClick:HomeClick()
    object SearchClick:HomeClick()

}
@Composable
fun home(homeclick:(HomeClick)->Unit, viewmodel: HomePageViewmodel= koinViewModel()){
    val offerDetails = viewmodel.offer_uiState.collectAsState().value
    val categoryDetails = viewmodel.categories_uiState.collectAsState().value

    val context= LocalContext.current
    LaunchedEffect(Unit) {
        viewmodel.getOffers()
        viewmodel.getCategory()
    }
    Column( ) {

        LocationCard(onCartClick = {
            homeclick(HomeClick.CartClick)
        },
            onLocationClick = {
                homeclick(HomeClick.LocationClick)
            }) {
            // Default Search Card
            Card(
                border = BorderStroke(1.dp, Color.Black),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().clickable {
                    homeclick(HomeClick.SearchClick)
                }

            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
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

            Spacer(modifier = Modifier.height(24.dp))
            when(categoryDetails){
                is UiState.Error -> {
                    Toast.makeText(context, categoryDetails.message, Toast.LENGTH_SHORT).show()

                }
                is UiState.Loading -> {

                        CircularProgressIndicator(color = BtnColor)


                }
                is UiState.Success -> {
                    val categorylist = categoryDetails.data
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(categorylist) {
                            CategoryItem(it)
                        }
                    }
                }

                UiState.Empty -> {}
            }

        }

            Spacer(modifier = Modifier.height(30.dp))
        LazyColumn {
            item{
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = BtnColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BtnColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
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
                                modifier = Modifier.size(48.dp)
                            )

                            Text(
                                text = "Explore exciting events near you",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
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
                Spacer(modifier = Modifier.height(8.dp))
            }
            item{
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {

                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                        Image(painter = painterResource(R.drawable.left), contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "TRENDING OFFERS",
                            letterSpacing = 3.sp, // Use sp for text spacing, not dp
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp),

                            )
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(painter = painterResource(R.drawable.right), contentDescription = null, modifier = Modifier.size(12.dp))


                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

            }
            item {
                Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(horizontal = 8.dp), border = BorderStroke(0.1.dp,
                    Color.Black)
                )
                {
                    AutoImageSlider(imageurls)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item{
                ChipRowWithSelectiveIcons()
                Spacer(modifier = Modifier.height(8.dp))
            }

               when(offerDetails){
                   is UiState.Error -> {
                       Toast.makeText(context, offerDetails.message, Toast.LENGTH_SHORT).show()

                   }
                   is UiState.Loading -> {
                       item {
                           CircularProgressIndicator(color = BtnColor)
                       }

                   }
                   is UiState.Success -> {
                       val data=offerDetails.data.filter { !it.expire }
                       items(data) {
                           HomeOfferView(it) {
                               homeclick(HomeClick.OfferClick(it))
                           }
                       }
                   }
                   else -> {}
               }

            }

    }
}
val imageurls:ArrayList<String> = arrayListOf("https://bummper-tick.s3.amazonaws.com/offers/brand_logos/2wfejGHVZjSVcN3yBq7KcHC0lencBA0BOCdiiJQA.jpg",
    "https://bummper-tick.s3.amazonaws.com/offers/media/BAblAolhcnPnHl1pa9cYPur8vLHiUWM7AFQ1imOK.jpg",
    "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/LrxWjQUnzyjceN5N2GFrauatrrTneh3dHLSZvF5q.png",
    "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/Y4oZxIOW5exbkJGGW7dKd96FRmdlxhDGjLvvHAY2.png")


// Demo data for HomeOffer
val homeOfferDemoList = listOf(
    HomeOffer(
        offerId = "OFF001",
        Type = MarketingOption.EVENTS,
        offerValid = OfferValidation.Valid,
        Media_list = listOf(
            "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=800&h=600&fit=crop",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/2wfejGHVZjSVcN3yBq7KcHC0lencBA0BOCdiiJQA.jpg",
            "https://bummper-tick.s3.amazonaws.com/offers/media/BAblAolhcnPnHl1pa9cYPur8vLHiUWM7AFQ1imOK.jpg",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/LrxWjQUnzyjceN5N2GFrauatrrTneh3dHLSZvF5q.png",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/Y4oZxIOW5exbkJGGW7dKd96FRmdlxhDGjLvvHAY2.png",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/2wfejGHVZjSVcN3yBq7KcHC0lencBA0BOCdiiJQA.jpg",
            "https://bummper-tick.s3.amazonaws.com/offers/media/BAblAolhcnPnHl1pa9cYPur8vLHiUWM7AFQ1imOK.jpg",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/LrxWjQUnzyjceN5N2GFrauatrrTneh3dHLSZvF5q.png",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/Y4oZxIOW5exbkJGGW7dKd96FRmdlxhDGjLvvHAY2.png",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/2wfejGHVZjSVcN3yBq7KcHC0lencBA0BOCdiiJQA.jpg",
            "https://bummper-tick.s3.amazonaws.com/offers/media/BAblAolhcnPnHl1pa9cYPur8vLHiUWM7AFQ1imOK.jpg",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/LrxWjQUnzyjceN5N2GFrauatrrTneh3dHLSZvF5q.png",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/Y4oZxIOW5exbkJGGW7dKd96FRmdlxhDGjLvvHAY2.png",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/2wfejGHVZjSVcN3yBq7KcHC0lencBA0BOCdiiJQA.jpg",
            "https://bummper-tick.s3.amazonaws.com/offers/media/BAblAolhcnPnHl1pa9cYPur8vLHiUWM7AFQ1imOK.jpg",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/LrxWjQUnzyjceN5N2GFrauatrrTneh3dHLSZvF5q.png",
            "https://bummper-tick.s3.amazonaws.com/offers/brand_logos/Y4oZxIOW5exbkJGGW7dKd96FRmdlxhDGjLvvHAY2.png"
        ),
        discount = "25%",
        startDate = "2024-06-01",
        media = listOf(
            Media("MED001", "image", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=600&h=400&fit=crop", "Delicious pizza offer"),
            Media("MED002", "image", "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=600&h=400&fit=crop", "Pizza making process")
        ),
        approval = "approved",
        endDate = "2024-06-30",
        active = "true",
        offerTitle = "Summer Pizza Bonanza",
        brand_logo_url = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=200&h=200&fit=crop",
        offerTag = "HOT DEAL",
        offerDescription = "Get 25% off on all large pizzas. Perfect for summer parties and family gatherings!",
        termsAndCondition = "Valid on large pizzas only. Cannot be combined with other offers. Valid till June 30, 2024."
    ),

    HomeOffer(
        offerId = "OFF002",
        Type = MarketingOption.EVENTS,
        offerValid = OfferValidation.Valid,
        Media_list = listOf(
            "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1558769132-cb1aea458c5e?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=800&h=600&fit=crop"
        ),
        discount = "50%",
        startDate = "2024-06-05",
        media = listOf(
            Media("MED003", "image", "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=600&h=400&fit=crop", "Summer fashion collection"),
            Media("MED004", "image", "https://images.unsplash.com/photo-1558769132-cb1aea458c5e?w=600&h=400&fit=crop", "Latest trends showcase")
        ),
        approval = "approved",
        endDate = "2024-06-25",
        active = "true",
        offerTitle = "Summer Fashion Sale",
        brand_logo_url = "https://images.unsplash.com/photo-1596464716127-f2a82984de30?w=200&h=200&fit=crop",
        offerTag = "MEGA SALE",
        offerDescription = "Huge summer sale with up to 50% off on selected fashion items. Refresh your wardrobe with the latest trends!",
        termsAndCondition = "Valid on selected items only. Sale prices cannot be combined with other promotions. Limited stock available."
    ),

    HomeOffer(
        offerId = "OFF003",
        Type = MarketingOption.EVENTS,
        offerValid = OfferValidation.Valid,
        Media_list = listOf(
            "https://images.unsplash.com/photo-1468495244123-6c6c332eeece?w=800&h=600&fit=crop"
        ),
        discount = "30%",
        startDate = "2024-05-15",
        media = listOf(
            Media("MED005", "image", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&h=400&fit=crop", "Latest smartphone offers")
        ),
        approval = "approved",
        endDate = "2024-05-31",
        active = "false",
        offerTitle = "Electronics Flash Sale",
        brand_logo_url = "https://images.unsplash.com/photo-1523474253046-8cd2748b5fd2?w=200&h=200&fit=crop",
        offerTag = "EXPIRED",
        offerDescription = "Flash sale on electronics including smartphones, laptops, and accessories.",
        termsAndCondition = "Valid on selected electronics only. Limited time offer. While stocks last."
    ),

    HomeOffer(
        offerId = "OFF004",
        Type = MarketingOption.EVENTS,
        offerValid = OfferValidation.Valid,
        Media_list = listOf(
            "https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1506976785307-8732e854ad03?w=800&h=600&fit=crop"
        ),
        discount = "15%",
        startDate = "2024-06-10",
        media = listOf(
            Media("MED006", "image", "https://images.unsplash.com/photo-1542838132-92c53300491e?w=600&h=400&fit=crop", "Fresh grocery items"),
            Media("MED007", "image", "https://images.unsplash.com/photo-1506976785307-8732e854ad03?w=600&h=400&fit=crop", "Organic produce section")
        ),
        approval = "pending",
        endDate = "2024-06-20",
        active = "true",
        offerTitle = "Fresh Grocery Deals",
        brand_logo_url = "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=200&h=200&fit=crop",
        offerTag = "FRESH",
        offerDescription = "Save 15% on fresh groceries and organic produce. Farm-fresh vegetables and fruits delivered to your doorstep.",
        termsAndCondition = "Minimum order value Rs. 500. Free delivery on orders above Rs. 1000. Valid on fresh produce only."
    ),

    HomeOffer(
        offerId = "OFF005",
        Type = MarketingOption.EVENTS,
        offerValid = OfferValidation.Valid,
        Media_list = listOf(
            "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800&h=600&fit=crop",
            "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=800&h=600&fit=crop"
        ),
        discount = "40%",
        startDate = "2024-06-08",
        media = listOf(
            Media("MED008", "image", "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=600&h=400&fit=crop", "Delicious restaurant meals"),
            Media("MED009", "image", "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=600&h=400&fit=crop", "Chef preparing signature dishes")
        ),
        approval = "approved",
        endDate = "2024-06-22",
        active = "true",
        offerTitle = "Restaurant Week Special",
        brand_logo_url = "https://example.com/logos/swiggy_logo.png",
        offerTag = "LIMITED TIME",
        offerDescription = "Enjoy 40% off on orders from premium restaurants. Discover new cuisines and favorite dishes at unbeatable prices!",
        termsAndCondition = "Valid on orders above Rs. 300. Applicable on selected restaurants only. Cannot be combined with other discount codes."
    )
)