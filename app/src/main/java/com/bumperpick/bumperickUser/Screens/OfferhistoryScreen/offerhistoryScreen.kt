package com.bumperpick.bumperpickvendor.Screens.OfferhistoryScreen

import DataStoreManager
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.API.New_model.DataXX
import com.bumperpick.bumperickUser.API.New_model.OfferHistoryModel
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.CartBottomSheet
import com.bumperpick.bumperickUser.Screens.Component.CartOfferView
import com.bumperpick.bumperickUser.Screens.Component.SearchCard
import com.bumperpick.bumperickUser.Screens.Home.HomePageViewmodel
import com.bumperpick.bumperickUser.Screens.Home.UiState
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.grey
import com.bumperpick.bumperickUser.ui.theme.satoshi_regular
import kotlinx.coroutines.flow.firstOrNull
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun offerhistoryScreen(  onBackClick: () -> Unit,
                         openOfferDetail:(String)->Unit,
                         viewmodel: HomePageViewmodel = koinViewModel()){


    val context = LocalContext.current

    val offerUiState by viewmodel.history_uiState.collectAsState()


    var search by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }


    var userId by remember { mutableStateOf("") }

    // Fetch user ID once
    LaunchedEffect(Unit) {
        userId = DataStoreManager(context).getUserId.firstOrNull()?:""
        viewmodel.getOffer_history()
    }






    Scaffold(
        containerColor = grey,
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top bar
                    Column(modifier = Modifier.background(Color.White)) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { onBackClick() }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Offer History",
                                color = Color.Black,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))

                        SearchCard(query = search, onQueryChange = { search = it }) {}

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    when(offerUiState){
                        UiState.Empty -> {
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
                                    "No offers found",
                                    modifier = Modifier.padding(horizontal = 0.dp).fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = BtnColor
                                )
                            }

                        }
                        is UiState.Error -> {

                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = (offerUiState as UiState.Error).message,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center),
                                    color = BtnColor
                                )}
                        }
                        UiState.Loading ->{
                            loading = true
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(
                                    color = BtnColor,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        is UiState.Success-> {
                            val list=(offerUiState as  UiState.Success<OfferHistoryModel>).data
                            val offerList = list.data
                            val filteredList = offerList.filter {
                                (  it.offer.title?:"").contains(search, ignoreCase = true)
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                                        Image(painter = painterResource(R.drawable.left), contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "${filteredList.size} OFFER AVAILED",
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
                                }
                                if(loading){
                                    item {
                                        Box(modifier = Modifier.fillMaxSize()){
                                            CircularProgressIndicator(color = BtnColor, modifier = Modifier.size(60.dp).align(Alignment.Center))
                                        }

                                    }
                                }
                                else {
                                    if(filteredList.isEmpty()) {
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
                                                    "No offers found",
                                                    modifier = Modifier.padding(horizontal = 0.dp).fillMaxWidth(),
                                                    textAlign = TextAlign.Center,
                                                    color = BtnColor
                                                )
                                            }
                                        }
                                    }
                                    else{
                                    items(filteredList) {

                                        val dataxx=DataXX(
                                            create_at = it.create_at,
                                            customer_id = it.customer_id,
                                            id = it.id,
                                            offer = it.offer,
                                            status = it.status,
                                            offer_id = it.offer_id

                                        )
                                        CartOfferView(
                                            showOpenQrBtn = false,
                                            offerModel = dataxx,
                                            openQr = { id ->

                                            },
                                            deleteCart = {

                                            },
                                            onCardClick = {
                                                openOfferDetail(it)
                                            }


                                        )
                                    }
                                    }
                                }
                            }
                        }
                    }


                }


            }
        }
    )
    



}