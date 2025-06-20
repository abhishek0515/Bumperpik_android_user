package com.bumperpick.bumperickUser.Screens.Home

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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.API.New_model.DataXX
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.CartBottomSheet
import com.bumperpick.bumperickUser.Screens.Component.CartOfferView
import com.bumperpick.bumperickUser.Screens.Component.SearchCard
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.grey
import kotlinx.coroutines.flow.firstOrNull
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun Cart(onBackClick: () -> Unit) {

    val viewmodel: HomePageViewmodel = koinViewModel()
    val context = LocalContext.current
    val deleteState=viewmodel.delete_cart_uiState.collectAsState().value
    val cartState = viewmodel.cart_uiState.collectAsState().value

    val offerList = remember { mutableStateListOf<DataXX>() }
    var offerId by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var userId by remember { mutableStateOf("") }
    LaunchedEffect(deleteState) {
        when(deleteState){
            UiState.Empty -> {}
            is UiState.Error -> {
                loading=false
                Toast.makeText(context, deleteState.message, Toast.LENGTH_SHORT).show()

            }
            UiState.Loading -> {
                loading=true
            }
            is UiState.Success ->{
                loading=false
                Toast.makeText(context, deleteState.data, Toast.LENGTH_SHORT).show()
                viewmodel.resetaddtocart()
                viewmodel.getCart()


            }
        }
    }
    // Fetch user ID once
    LaunchedEffect(Unit) {
        userId = DataStoreManager(context).getUserId.firstOrNull()?:""
        viewmodel.getCart()
    }

    // React to cart state changes
    LaunchedEffect(cartState) {
        when (cartState) {
            is UiState.Success -> {
                offerList.clear()
                offerList.addAll(cartState.data.data)
            }
            is UiState.Error -> {
                Toast.makeText(context, cartState.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }


    val filteredList = offerList.filter {
        it.offer.title.contains(search, ignoreCase = true)
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
                                text = "Cart",
                                color = Color.Black,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))

                        SearchCard(query = search, onQueryChange = { search = it }) {}

                        Spacer(modifier = Modifier.height(16.dp))
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
                                    text = "${offerList.size} OFFER SAVED",
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
                            items(filteredList) {
                                CartOfferView(
                                    offerModel = it,
                                    openQr = { id ->
                                        Log.d("offerid",id)
                                        offerId = id
                                        showBottomSheet = true
                                    },
                                    deleteCart = {
                                        viewmodel.deleteCart(it)
                                    }


                                )
                            }
                        }
                    }
                }

                // QR Code Bottom Sheet
                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState,
                        containerColor = Color.White,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ) {
                        CartBottomSheet(jsonData ="""{"user_id":"$userId","offer_id":"$offerId"}""",offerId ) {
                           showBottomSheet=false
                        }
                    }
                }
            }
        }
    )
}
