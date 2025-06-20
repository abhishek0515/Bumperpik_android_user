package com.bumperpick.bumperickUser.Screens.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.ui.theme.BtnColor

@Composable
fun contest(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {


            Text("Contest", fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "COMING SOON", fontSize = 18.sp, color = BtnColor, fontWeight = FontWeight.Bold)
        }

    }

}