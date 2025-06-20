package com.bumperpick.bumperickUser.Screens.Home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.API.New_model.sub_categories
import com.bumperpick.bumperickUser.R
import com.bumperpick.bumperickUser.Screens.Component.LocationCard
import com.bumperpick.bumperickUser.ui.theme.BtnColor
import org.koin.androidx.compose.koinViewModel

// ImprovedOfferScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferScreen(viewModel: CategoryViewModel = koinViewModel()) {
    var showSubCategories by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedCategoryName by remember { mutableStateOf("") }

    val categoriesState = viewModel.categoriesUiState.collectAsState().value
    val subCategoriesState = viewModel.subCategoriesUiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    LaunchedEffect(selectedCategoryId) {
        selectedCategoryId?.let { id ->
            viewModel.fetchSubCategories(id)
        }
    }

    BackHandler(enabled = showSubCategories) {
        showSubCategories = false
        selectedCategoryId = null
        selectedCategoryName = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        if (showSubCategories) {
            TopAppBar(
                title = {
                    Text(
                        text = selectedCategoryName,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            showSubCategories = false
                            selectedCategoryId = null
                            selectedCategoryName = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }

        if (!showSubCategories) {
            LocationCard()
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!showSubCategories) {
            CategoriesContent(
                categoriesState = categoriesState,
                onCategoryClick = { category ->
                    showSubCategories = true
                    selectedCategoryId = category.id
                    selectedCategoryName = category.name
                }
            )
        } else {
            SubCategoriesContent(
                subCategoriesState = subCategoriesState,
                onSubCategoryClick = { subCategory ->
                    // Handle subcategory click
                }
            )
        }
    }
}

@Composable
private fun CategoriesContent(
    categoriesState: UiState<List<Category>>,
    onCategoryClick: (Category) -> Unit
) {
    Column {
        // Header
        SectionHeader(
            title = "CATEGORIES",
            subtitle = "Choose your category"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories List
        when (categoriesState) {
            UiState.Empty -> {
                EmptyState(
                    message = "No categories available",
                    icon = Icons.Outlined.Close
                )
            }

            is UiState.Error -> {
                ErrorState(
                    message = categoriesState.message,
                    onRetry = { /* retry logic */ }
                )
            }

            UiState.Loading -> {
                LoadingState()
            }

            is UiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = categoriesState.data,
                        key = { it.id }
                    ) { category ->
                        CategoryItem(
                            category = category,
                            onClick = { onCategoryClick(category) }
                        )
                    }

                    // Add bottom padding
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SubCategoriesContent(
    subCategoriesState: UiState<List<sub_categories>>,
    onSubCategoryClick: (sub_categories) -> Unit
) {
    Column {
        // Header
        SectionHeader(
            title = "SUB CATEGORIES",
            subtitle = "Select a subcategory"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // SubCategories List
        when (subCategoriesState) {
            UiState.Empty -> {
                EmptyState(
                    message = "No subcategories available",
                    icon = Icons.Outlined.Close
                )
            }

            is UiState.Error -> {
                ErrorState(
                    message = subCategoriesState.message,
                    onRetry = { /* retry logic */ }
                )
            }

            UiState.Loading -> {
                LoadingState()
            }

            is UiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = subCategoriesState.data,
                        key = { it.id }
                    ) { subCategory ->
                        SubCategoryItem(
                            subCategory = subCategory,
                            onClick = { onSubCategoryClick(subCategory) }
                        )
                    }

                    // Add bottom padding
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF666666),
                letterSpacing = 2.sp
            )

            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )
        }

        subtitle?.let {
            Text(
                text = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                fontSize = 12.sp,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryItem(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE8E8E8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Image
            Card(
                modifier = Modifier.size(58.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                AsyncImage(
                    model = category.image_url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,

                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Category Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C2C2C),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )


            }

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF999999)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubCategoryItem(
    subCategory: sub_categories,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 4.dp
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, Color(0xFFE8E8E8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon (optional)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Color(0xFF6B0221).copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = subCategory.name.take(1).uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B0221)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Subcategory Name
            Text(
                text = subCategory.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C2C2C),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF999999)
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF6B0221),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = Color(0xFFE57373)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Oops! Something went wrong",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2C2C2C),
            textAlign = TextAlign.Center
        )

        Text(
            text = message,
            fontSize = 14.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6B0221)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Retry",
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyState(
    message: String,
    icon: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = Color(0xFFBDBDBD)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}