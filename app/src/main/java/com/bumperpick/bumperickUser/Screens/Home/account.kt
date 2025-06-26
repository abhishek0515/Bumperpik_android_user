package com.bumperpick.bumperickUser.Screens.Home

import android.content.Context
import android.content.Intent
import android.widget.ToggleButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.ButtonView
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import com.bumperpick.bumperickUser.ui.theme.grey
import com.bumperpick.bumperickUser.ui.theme.satoshi_regular
import org.koin.androidx.compose.koinViewModel
fun shareReferral(context: Context) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Try this APP")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}

@Composable
fun ReferralSettingsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.left),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ACCOUNT SETTINGS",
                    letterSpacing = 4.sp,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    painter = painterResource(R.drawable.right),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Offer History Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Handle click */ }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.clock),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Offer history",
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
            }

            Divider(
                color = Color.LightGray,
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Notification Setting Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.bell_01),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Notification Setting",
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                ToggleButton()
            }

            Divider(
                color = Color.LightGray,
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Send Instant Reminder Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Handle click */ }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Favourite ",
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ToggleButton() {
    var isChecked by remember { mutableStateOf(false) }

    Switch(
        checked = isChecked,
        onCheckedChange = { isChecked = it },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = BtnColor,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color.Gray
        )
    )
}

sealed class AccountClick(){
    object Logout:AccountClick()
    object EditAccount:AccountClick()
}
@Composable
fun AccountScreen(accountclick:(AccountClick)->Unit,viewmodel: AccountViewmodel= koinViewModel()){
    val profile =viewmodel.profileState.collectAsState().value
    LaunchedEffect(Unit) {
        viewmodel.getProfile()
    }

    val context= LocalContext.current

    val isLogout by viewmodel.isLogout.collectAsState()

    if (isLogout) {
     accountclick(AccountClick.Logout)
    }



    Scaffold(containerColor = grey) {paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState())) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Account",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = satoshi_regular,
                        color = Color.Black
                    )
                }
            }
            when(profile){
                UiState.Empty ->{}
                is UiState.Error ->{
                    Card(
                        modifier = Modifier.padding(16.dp).height(100.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                    ){
                        Text(text = profile.message, fontSize = 16.sp, color = BtnColor, modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally))
                    }
                }
                UiState.Loading ->
                {
                    Box(
                        modifier = Modifier.padding(16.dp).height(100.dp).fillMaxWidth().background(Color.White),
                    ){
                        CircularProgressIndicator( color = BtnColor, modifier = Modifier.size(36.dp).align(Alignment.Center))
                    }
                }
                is UiState.Success-> {
                    val profile=profile.data
                    Card(
                        modifier = Modifier.padding(16.dp).height(100.dp),
                        colors = CardDefaults.cardColors(containerColor = BtnColor)
                    ) {
                        Box(modifier = Modifier.padding(12.dp).fillMaxWidth(),){
                            Row(modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight()){
                                Card(shape = CircleShape, modifier = Modifier.size(60.dp).align(Alignment.CenterVertically)) {
                                    AsyncImage(model = profile.data.image_url?:"", contentScale = ContentScale.FillBounds, contentDescription = null)
                                }
                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                                    Text(text = profile.data.name?:"User", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = profile.data.phone_number?:"", fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.White)


                                }
                            }
                            Box( modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)){


                                Icon(painter = painterResource(R.drawable.pencil_svgrepo_com), contentDescription = null, tint = Color.White, modifier = Modifier.size(27.dp)
                                    .clickable {accountclick(AccountClick.EditAccount)  },)
                            }
                        }
                    }
                }
            }




            ReferralSettingsCard()


            Column (modifier = Modifier.fillMaxWidth().background(Color.White)){
                Spacer(modifier = Modifier.height(16.dp))
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                    Image(painter = painterResource(R.drawable.left), contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "REFERRAL",
                        letterSpacing = 4.sp, // Use sp for text spacing, not dp
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp),

                        )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(painter = painterResource(R.drawable.right), contentDescription = null, modifier = Modifier.size(12.dp))


                }
                Spacer(modifier = Modifier.height(18.dp))
                Image(painter = painterResource( R.drawable.refer), contentDescription = null, modifier = Modifier.align(Alignment.CenterHorizontally).size(100.dp))
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Refer this app to your friends and family",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,)
                Spacer(modifier = Modifier.height(24.dp))
                ButtonView(text = "Refer now", color = BtnColor, textColor = Color.White) {
                    shareReferral(context)

                }

            }
            Spacer(modifier = Modifier.height(18.dp))
            Box (modifier = Modifier.fillMaxWidth().clickable {
                viewmodel.logout()

            }.background(Color.White), ){
                Row (modifier = Modifier.padding(12.dp).align(Alignment.CenterStart)){
                    Image(imageVector = Icons.Outlined.ExitToApp, contentDescription = null, modifier = Modifier.size(30.dp),)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Sign out", color = Color.Black, fontSize = 16.sp,)
                }

                Image(imageVector = Icons.Outlined.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(24.dp).align(
                    Alignment.CenterEnd),)

            }






        }

    }
}