package com.bumperpick.bumperickUser.Screens.Home

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.HomeOfferView
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun offer_subcat( subcatId:String,subcatName:String,cat_id:String, onBackClick:()->Unit,homeclick:(HomeClick)->Unit, viewmodel: HomePageViewmodel= koinViewModel()){
    var sub_cat_searchQuery by remember { mutableStateOf("") }
    val context= LocalContext.current
    val offerDetails = viewmodel.offer_uiState.collectAsState().value
    LaunchedEffect(Unit) {
        viewmodel.updateCategories_and_sub_cat(listOf(cat_id),subcatId)

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
                //  .then(backgroundModifier)

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
            Column(modifier = Modifier.fillMaxWidth().then(backgroundModifier)) {
                Spacer(modifier = Modifier.height(48.dp))
                Row(  verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        modifier = Modifier.padding(start = 16.dp),
                        onClick = {
                        onBackClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = subcatName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sub_cat_searchQuery,
                    onValueChange = {sub_cat_searchQuery=it},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        if (sub_cat_searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { sub_cat_searchQuery=("") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    placeholder = { Text(text = "Search $subcatName offers", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Black,
                        focusedBorderColor = BtnColor,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
                Spacer(Modifier.height(36.dp))
            }


        }
        Spacer(modifier = Modifier.height(30.dp))
        LazyColumn {
            item{
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {

                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                        Image(painter = painterResource(R.drawable.left), contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "$subcatName offers".uppercase(),
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
                    val data = offerDetails.data.filter { !it.expire }
                    val filteredCategories = data.filter {
                        (it.title ?: "").contains(sub_cat_searchQuery, ignoreCase = true)
                    }
                    if (filteredCategories.isEmpty()) {
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
                                    "No offers available.",
                                    modifier = Modifier.padding(horizontal = 0.dp).fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = BtnColor
                                )
                            }
                        }
                    } else {
                        items(filteredCategories,) {
                            HomeOfferView(
                                offerModel = it,
                                offerClick = {
                                    homeclick(HomeClick.OfferClick(it,))
                                },
                                liketheoffer = {
                                    viewmodel.toogle_fav(it, true)
                                },
                                shareoffer = {
                                    shareReferral(context, it)
                                }

                            )
                        }
                    }
                }
                else -> {}
            }

        }






    }





}