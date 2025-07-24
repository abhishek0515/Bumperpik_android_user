import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumperpick.bumperickUser.API.New_model.Category
import com.bumperpick.bumperickUser.Screens.Home.HomePageViewmodel
import com.bumperpick.bumperickUser.Screens.Home.UiState
import com.bumperpick.bumperickUser.ui.theme.BtnColor

data class FilterOption(
    val id: String,
    val label: String,
    val isSelected: Boolean = false
)

data class SortOption(
    val id: String,
    val label: String,
    val isSelected: Boolean = false
)

class FilterManager() {
    
    private val _distanceOptions = mutableStateListOf(
        FilterOption("1km", "Upto 1Km"),
        FilterOption("3km", "Upto 3Km"),
        FilterOption("5km", "Upto 5Km"),
        FilterOption("10km", "Upto 10Km"),
        FilterOption("beyond10km", "Beyond 10Km")
    )

    private val _categoryOptions =mutableStateListOf<FilterOption>()
    
    fun setCategories(categories: List<Category>) {
        _categoryOptions.add(FilterOption("all","All"))
      categories.forEach { _categoryOptions.add(FilterOption(id=it.id.toString(), label = it.name)) }
    }



    val distanceOptions: List<FilterOption> = _distanceOptions
    val categoryOptions: List<FilterOption> = _categoryOptions

    fun toggleDistanceFilter(filterId: String) {
        _distanceOptions.replaceAll { it.copy(isSelected = false) }
        val index = _distanceOptions.indexOfFirst { it.id == filterId }
        if (index != -1) {
            _distanceOptions[index] = _distanceOptions[index].copy(isSelected = true)
        }
    }

    fun toggleCategoryFilter(filterId: String) {
        if (filterId == "all") {
            val allSelected = _categoryOptions[0].isSelected
            _categoryOptions.replaceAll {
                if (it.id == "all") it.copy(isSelected = !allSelected)
                else it.copy(isSelected = !allSelected)
            }
        } else {
            val index = _categoryOptions.indexOfFirst { it.id == filterId }
            if (index != -1) {
                _categoryOptions[index] = _categoryOptions[index].copy(
                    isSelected = !_categoryOptions[index].isSelected
                )
                // Update "All" option based on other categories
                val allOthersSelected = _categoryOptions
                    .filter { it.id != "all" }
                    .all { it.isSelected }
                _categoryOptions[0] = _categoryOptions[0].copy(isSelected = allOthersSelected)
            }
        }
    }

    fun clearAllFilters() {
        _distanceOptions.replaceAll { it.copy(isSelected = false) }
        _categoryOptions.replaceAll { it.copy(isSelected = false) }
    }

    fun getActiveFilters(): List<FilterOption> {
        return (distanceOptions + categoryOptions).filter { it.isSelected }
    }

    fun removeFilter(filterId: String) {
        _distanceOptions.replaceAll {
            if (it.id == filterId) it.copy(isSelected = false) else it
        }
        _categoryOptions.replaceAll {
            if (it.id == filterId) {
                if (it.id == "all") {
                    _categoryOptions.replaceAll { opt -> opt.copy(isSelected = false) }
                }
                it.copy(isSelected = false)
            } else it
        }
    }
}

class SortManager {
    private val _sortOptions = mutableStateListOf(
        SortOption("1", "Latest First", true),
        SortOption("2", "Distance (Near to far)"),
        SortOption("3", "Distance (Far to near)"),
        SortOption("4", "Rating (High to low)")
    )

    val sortOptions: List<SortOption> = _sortOptions

    fun selectSort(sortId: String) {
        _sortOptions.replaceAll { it.copy(isSelected = it.id == sortId) }
    }

    fun getSelectedSort(): SortOption? {
        return sortOptions.find { it.isSelected }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filterManager: FilterManager,
    onDismiss: () -> Unit,
    onApplyFilters: (List<FilterOption>) -> Unit
) {
    var selectedTab by remember { mutableStateOf("distance") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(top = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF1A1A1A)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF666666)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Tab Navigation
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .background(Color(0xFFF8F8F8))
                    .padding(vertical = 8.dp)
            ) {
                TabItem(
                    text = "Distance",
                    isSelected = selectedTab == "distance",
                    onClick = { selectedTab = "distance" }
                )
                TabItem(
                    text = "Categories",
                    isSelected = selectedTab == "categories",
                    onClick = { selectedTab = "categories" }
                )
            }

            // Filter Options
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "SELECT BY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    if (selectedTab == "distance") {
                        items(filterManager.distanceOptions) { option ->
                            FilterOptionItem(
                                option = option,
                                onToggle = { filterManager.toggleDistanceFilter(option.id) },
                                useCheckbox = false
                            )
                        }
                    } else {
                        items(filterManager.categoryOptions) { option ->
                            FilterOptionItem(
                                option = option,
                                onToggle = { filterManager.toggleCategoryFilter(option.id) },
                                useCheckbox = true
                            )
                        }
                    }
                }
            }
        }

        // Buttons Section (Full Width)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { filterManager.clearAllFilters()
                    onApplyFilters(emptyList())
                          onDismiss()},
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF666666),
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Clear All",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = {
                    onApplyFilters(filterManager.getActiveFilters())
                    onDismiss()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BtnColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Apply Filters",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFFF8F8F8),
        label = "TabBackground"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) BtnColor else Color(0xFF333333),
        label = "TabText"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = textColor
        )
    }
}

@Composable
private fun FilterOptionItem(
    option: FilterOption,
    onToggle: () -> Unit,
    useCheckbox: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (useCheckbox) {
            Checkbox(
                checked = option.isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = BtnColor,
                    uncheckedColor = Color(0xFF666666),
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.size(24.dp)
            )
        } else {
            RadioButton(
                selected = option.isSelected,
                onClick = { onToggle() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = BtnColor,
                    unselectedColor = Color(0xFF666666)
                ),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = option.label,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            color = Color(0xFF333333)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    sortManager: SortManager,
    onDismiss: () -> Unit,
    onSortSelected: (SortOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF666666))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(sortManager.sortOptions) { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            sortManager.selectSort(option.id)
                            onSortSelected(option)
                            onDismiss()
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        color = Color(0xFF333333),
                        modifier = Modifier.weight(1f)
                    )

                    RadioButton(
                        selected = option.isSelected,
                        onClick = {
                            sortManager.selectSort(option.id)
                            onSortSelected(option)
                            onDismiss()
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = BtnColor,
                            unselectedColor = Color(0xFF666666)
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortScreen(
    viewmodel: HomePageViewmodel,
  
    onFiltersApplied: (List<FilterOption>) -> Unit = {},
    onSortSelected: (SortOption) -> Unit = {}
) {
    val filterManager = remember { FilterManager() }
    val category by viewmodel.categories_uiState.collectAsState()
    LaunchedEffect(category) {
        when(category){
            UiState.Empty -> {}
            is UiState.Error -> {

            }
            UiState.Loading -> {}
            is UiState.Success -> {
                var categories=(category as UiState.Success<List<Category>>).data

                filterManager.setCategories(categories)
            }
        }
    }
    
    
    

    val sortManager = remember { SortManager() }

    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }

    val filterBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val sortBottomSheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier

            .padding(horizontal = 16.dp, vertical = 2.dp)

    )
    {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                OutlinedButton(
                    onClick = { showFilterSheet = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, BtnColor),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {


                    Text("Filters", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            item {
                OutlinedButton(
                    onClick = { showSortSheet = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, BtnColor),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text("Sort By", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            items(filterManager.getActiveFilters()) { filter ->
                SelectedFilterChip(
                    label = filter.label,
                    onRemove = {
                        filterManager.removeFilter(filter.id)
                        onFiltersApplied(filterManager.getActiveFilters())
                    }
                )
            }

            sortManager.getSelectedSort()?.let { sort ->
                if (sort.id != "1") {
                    item {
                        SelectedFilterChip(
                            label = sort.label,
                            onRemove = {
                                sortManager.selectSort("1")
                                onSortSelected(sortManager.getSelectedSort()!!)
                            }
                        )
                    }
                }
            }
        }

    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = filterBottomSheetState,
            containerColor = Color.White,
            contentColor = Color.Black,
            dragHandle = null
        ) {
            FilterBottomSheet(
                filterManager = filterManager,
                onDismiss = { showFilterSheet = false },
                onApplyFilters = { filters ->
                    onFiltersApplied(filters)
                    showFilterSheet = false
                }
            )
        }
    }

    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            sheetState = sortBottomSheetState,
            containerColor = Color.White,
            contentColor = Color.Black,
            dragHandle = null
        ) {
            SortBottomSheet(
                sortManager = sortManager,
                onDismiss = { showSortSheet = false },
                onSortSelected = { sort ->
                    onSortSelected(sort)
                    showSortSheet = false
                }
            )
        }
    }
}

@Composable
private fun SelectedFilterChip(
    label: String,
    onRemove: () -> Unit
) {
    OutlinedButton(
        onClick = { },
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = BtnColor
        ),
        border = BorderStroke(1.dp, BtnColor),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "Remove",
            modifier = Modifier
                .size(16.dp)
                .clickable { onRemove() },
            tint = (BtnColor)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
            color = (BtnColor)
        )
    }
}
