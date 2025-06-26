package com.bumperpick.bumperickUser.Screens.Home

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.BottomNavigationBar
import com.bumperpick.bumperickUser.Screens.Component.LocationCard
import com.bumperpick.bumperickUser.Screens.Component.LocationPermissionScreen
import com.bumperpick.bumperickUser.Screens.Component.NavigationItem


@Composable
fun Homepage(onHomeClick: (HomeClick)->Unit, open_subID:(sub_cat_id:String,sub_cat_name:String,cat_id:String)->Unit,onAccountClick:(AccountClick)->Unit){

    HomeScreen(onHomeClick,onAccountClick,open_subID)

    val context = LocalContext.current
    val locationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )



if (locationPermission != PackageManager.PERMISSION_GRANTED) {
    LocationPermissionScreen(
        onPermissionGranted = {
            // Handle permission granted
            println("Location permission granted")
        },
        onPermissionDenied = {
            // Handle permission denied
            println("Location permission denied")
        }
    )
}

}

@Composable
fun HomeScreen(onHomeClick: (HomeClick)->Unit,onAccountClick: (AccountClick) -> Unit,open_subID:(sub_cat_id:String,sub_cat_name:String,cat_id:String)->Unit,) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()


            .background(Color.White)
    ) {



        val navItems = listOf(
            NavigationItem("Home", icon = Icons.Outlined.Home, contentDescription = "Home"),
            NavigationItem("Categories", icon = Icons.Default.List, contentDescription = "Create offers"),
            NavigationItem("Contest", painter = painterResource(R.drawable.contest), contentDescription = "Contest"),
            NavigationItem("Account", icon = Icons.Outlined.AccountCircle, contentDescription = "Account")
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

        ) {

                when(selectedTab){
                    0->{
                        home(homeclick = onHomeClick)
                    }
                    1->{
                        OfferScreen(homeclick=onHomeClick, open_subID = open_subID)
                    }
                    2->{
                        contest()
                    }
                    3->{
                        AccountScreen(accountclick = onAccountClick)
                    }

                }


        }




        BottomNavigationBar(
            items = navItems,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

    }
}





