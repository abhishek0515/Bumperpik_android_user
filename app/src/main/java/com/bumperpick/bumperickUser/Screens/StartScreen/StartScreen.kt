package com.bumperpick.bumperickUser.Screens.StartScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.ButtonView
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.BumperickTheme
import com.bumperpick.bumperickUser.ui.theme.satoshi
import com.bumperpick.bumperickUser.ui.theme.satoshi_bold
import java.time.format.TextStyle

@Composable
fun StartScreen(gotoLogin:()->Unit){


        Column {
            Surface(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxWidth(),
                color = Color.White,
                tonalElevation = 4.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(R.drawable.union),
                        contentDescription = "Background 1",

                        modifier = Modifier.fillMaxSize()
                    )

                    Column {

                        Box(modifier = Modifier.padding(bottom = 15.dp), contentAlignment = Alignment.TopEnd) {
                            Image(
                                painter = painterResource(R.drawable.frame10486),
                                contentDescription = "Foreground 1",
                                modifier = Modifier.fillMaxSize().padding(vertical = 0.dp),
                                contentScale = ContentScale.Fit

                            )


                        }

                    }

                    Box(modifier = Modifier.padding(top = 30.dp)){
                    Image(
                        painter = painterResource(R.drawable.sale)
                        , contentDescription = "Foreground 1",
                        modifier = Modifier
                            .size(200.dp)

                    )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(0.4f)

                    .fillMaxWidth()
                    .background(Color.White),
            ) {

                Column(
                    modifier = Modifier.align(Alignment.Center).padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Your gateway to stunning offers!",
                        color = Color.Black,
                        style = androidx.compose.ui.text.TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            lineHeight = 30.sp // ← Increased line spacing
                        ),

                        fontFamily = satoshi_bold
                    )
                    Spacer(Modifier.height(10.dp))

                    Text( text = "Find the best offers and events around you.\nGrab it,don't miss it!",
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 24.sp // ← Increased line spacing
                        ),

                        fontFamily = satoshi)
                    Spacer(modifier = Modifier.height(100.dp))

                }

                    ButtonView(
                        text = "Get Started",
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        onClick = gotoLogin

                    )



            }

        }




}

