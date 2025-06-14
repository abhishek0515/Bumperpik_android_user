package com.bumperpick.bumperickUser.Screens.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.SearchCard
import com.bumperpick.bumperickUser.ui.theme.BtnColor

@Composable
fun ChooseLocation(onBackClick: () -> Unit){
    var search by remember { mutableStateOf("") }
    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.White) {
        Box(modifier = Modifier.padding(it).fillMaxSize()){
                    Column(modifier = Modifier.background(Color.White).fillMaxSize()) {
                        Spacer(modifier = Modifier.height(24.dp))
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
                                text = "Choose location",
                                color = Color.Black,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }


                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(modifier = Modifier.height(1.dp).fillMaxWidth())
                        Spacer(modifier = Modifier.height(9.dp))
                        SearchCard(query = search, onQueryChange = { search = it }, hint = "Search location (eg: Block B Malviya..)",) {

                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically){

                        Image(painter = painterResource(R.drawable.location), contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Use my current location",
                                color = BtnColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(modifier = Modifier.height(1.dp).fillMaxWidth().padding(horizontal = 24.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically){

                            Image(painter = painterResource(R.drawable.add), contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Add new address",
                                color = BtnColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(modifier = Modifier.height(1.dp).fillMaxWidth().padding(horizontal = 24.dp))
                        Spacer(modifier = Modifier.height(8.dp))

                    }
                }

            }




}


@Preview
@Composable
fun previewlocation(){
    ChooseLocation {  }
}