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
import com.bumperpick.bumperickUser.Screens.Component.LocationPermissionScreen


@Composable
fun Homepage(){
    HomeScreen()

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
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()


            .background(Color.White)
    ) {
        // Header
        LocationCard()

        // Main content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                // Empty box illustration
                //EmptyBoxIllustration()


            }
        }

        // Bottom navigation
        BottomNavigation(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}
@Composable
fun BottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.shadow(0.dp)
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = if (selectedTab == 0) Color(0xFF3B82F6) else Color.Gray
                )
            },
            label = {
                Text(
                    "Home",
                    color = if (selectedTab == 0) Color(0xFF3B82F6) else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == 0) FontWeight.Medium else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = if (selectedTab == 0) Color.Transparent else Color.Transparent
            ),
            modifier = if (selectedTab == 0) {
                Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.1f),
                            Color.White
                        )
                    )
                )
            } else Modifier
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    Icons.Default.List,
                    contentDescription = "Create offers",
                    tint = if (selectedTab == 1) Color(0xFF3B82F6) else Color.Gray
                )
            },
            label = {
                Text(
                    "Create offers",
                    color = if (selectedTab == 1) Color(0xFF3B82F6) else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == 1) FontWeight.Medium else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = if (selectedTab == 1) Color.Transparent else Color.Transparent
            ),
            modifier = if (selectedTab == 1) {
                Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.1f),
                            Color.White
                        )
                    )
                )
            } else Modifier
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(
                   painter = painterResource(R.drawable.icon),
                    contentDescription = "Contest",
                    tint = if (selectedTab == 2) Color(0xFF3B82F6) else Color.Gray
                )
            },
            label = {
                Text(
                    "Contest",
                    color = if (selectedTab == 2) Color(0xFF3B82F6) else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == 2) FontWeight.Medium else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = if (selectedTab == 2) Color.Transparent else Color.Transparent
            ),
            modifier = if (selectedTab == 2) {
                Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.1f),
                            Color.White
                        )
                    )
                )
            } else Modifier
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = "Account",
                    tint = if (selectedTab == 2) Color(0xFF3B82F6) else Color.Gray
                )
            },
            label = {
                Text(
                    "Account",
                    color = if (selectedTab == 2) Color(0xFF3B82F6) else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == 2) FontWeight.Medium else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = if (selectedTab == 2) Color.Transparent else Color.Transparent
            ),
            modifier = if (selectedTab == 2) {
                Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.1f),
                            Color.White
                        )
                    )
                )
            } else Modifier
        )
    }
}



@Composable
fun EmptyBoxIllustration() {
    // Using drawable image instead of canvas
    Box(
        modifier = Modifier.size(200.dp, 200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Replace this with your actual drawable
        Image(
            painter = painterResource(id = R.drawable._04_box),
            contentDescription = "Empty box",
            modifier = Modifier.fillMaxSize()
        )

    }
}

@Composable
fun LocationCard() {
    Card(
        modifier = Modifier

            .fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(topEnd = 0.dp,topStart = 0.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8B1538) // Burgundy/Maroon color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side with location icon and text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Location pin icon
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Text content
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Sector 48, Sohna road",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Gurugram, Sohna road",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

            }
        }
    }
}