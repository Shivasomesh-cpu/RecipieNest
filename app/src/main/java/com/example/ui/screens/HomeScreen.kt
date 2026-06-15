package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.entities.Category
import com.example.domain.entities.Meal
import com.example.domain.entities.isVegetarian
import com.example.domain.entities.isVegan
import com.example.domain.entities.isNonVeg
import com.example.domain.entities.courseType
import com.example.ui.viewmodel.MealViewModel
import com.example.ui.viewmodel.UiState
import com.example.ui.widgets.CategoryChip
import com.example.ui.widgets.MealCard
import com.example.ui.widgets.shimmer
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: MealViewModel,
    onMealClick: (Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    val mealOfTheDayState by viewModel.mealOfTheDayState.collectAsStateWithLifecycle()
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categoryMealsState by viewModel.categoryMealsState.collectAsStateWithLifecycle()

    val dietaryFilter by viewModel.selectedDietaryFilter.collectAsStateWithLifecycle()
    val courseFilter by viewModel.selectedCourseFilter.collectAsStateWithLifecycle()

    val greeting = remember { getGreetingMessage() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Greeting header with manual Theme Switch!
        item {
            val themeController = com.example.ui.theme.LocalThemeController.current
            val isDark = when (themeController.themeMode) {
                com.example.ui.theme.AppThemeMode.LIGHT -> false
                com.example.ui.theme.AppThemeMode.DARK -> true
                com.example.ui.theme.AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "RecipeNest",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Switch Component
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Switch(
                        checked = isDark,
                        onCheckedChange = { checked ->
                            themeController.setTheme(if (checked) com.example.ui.theme.AppThemeMode.DARK else com.example.ui.theme.AppThemeMode.LIGHT)
                        },
                        thumbContent = {
                            Text(
                                text = if (isDark) "🌙" else "☀️",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                    Text(
                        text = if (isDark) "Dark Mode" else "Light Mode",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // 2. Meal of the Day section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Meal of the Day",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { viewModel.loadHomeScreenData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                when (val state = mealOfTheDayState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmer()
                        )
                    }
                    is UiState.Success -> {
                        MealOfDayCard(meal = state.data, onClick = { onMealClick(state.data) })
                    }
                    is UiState.Error -> {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Couldn't fetch today's spotlight",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                TextButton(onClick = { viewModel.loadHomeScreenData() }) {
                                    Text("Retry", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }

        // 3. Categories list
        item {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "Browse Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )

                when (val state = categoriesState) {
                    is UiState.Loading -> {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(5) {
                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .shimmer()
                                )
                            }
                        }
                    }
                    is UiState.Success -> {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data) { category ->
                                CategoryChip(
                                    category = category,
                                    isSelected = selectedCategory == category.name,
                                    onSelected = { viewModel.selectCategory(category.name) }
                                )
                            }
                        }
                    }
                    is UiState.Error -> {
                        Text(
                            text = "Failed to load categories.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    else -> {}
                }
            }
        }

        // 4. Grid of category meals (same-screen grid section)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = selectedCategory?.let { "$it Selection" } ?: "Meals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                )

                // Dietary Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        "All" to "All 🍽️",
                        "Veg" to "Veg 🟢",
                        "Non-Veg" to "Non-Veg 🔴",
                        "Vegan" to "Vegan 🌱"
                    ).forEach { (value, label) ->
                        val isSelected = dietaryFilter == value
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectDietaryFilter(value) },
                            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                // Course Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        "All" to "All Courses 🥘",
                        "Starter" to "Starters 🍲",
                        "Main Course" to "Mains 🍛",
                        "Snacks" to "Snacks 🥪",
                        "Breakfast" to "Breakfast 🥞",
                        "Dessert" to "Dessert 🍰"
                    ).forEach { (value, label) ->
                        val isSelected = courseFilter == value
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCourseFilter(value) },
                            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }
        }

        when (val state = categoryMealsState) {
            is UiState.Loading -> {
                items(3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(180.dp)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmer()
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(180.dp)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmer()
                        )
                    }
                }
            }
            is UiState.Success -> {
                val allMealsInCat = state.data
                val filteredMeals = allMealsInCat.filter { meal ->
                    val matchesDiet = when (dietaryFilter) {
                        "Veg" -> meal.isVegetarian
                        "Non-Veg" -> meal.isNonVeg
                        "Vegan" -> meal.isVegan
                        else -> true
                    }
                    val matchesCourse = when (courseFilter) {
                        "Starter" -> meal.courseType == "Starter"
                        "Main Course" -> meal.courseType == "Main Course"
                        "Snacks" -> meal.courseType == "Snacks"
                        "Breakfast" -> meal.courseType == "Breakfast"
                        "Dessert" -> meal.courseType == "Dessert"
                        else -> true
                    }
                    matchesDiet && matchesCourse
                }
                
                val chunkedMeals = filteredMeals.chunked(2)
                if (chunkedMeals.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No recipes match your filter criteria", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    items(chunkedMeals) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            MealCard(
                                meal = pair[0],
                                onMealClick = onMealClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                            )
                            if (pair.size > 1) {
                                MealCard(
                                    meal = pair[1],
                                    onMealClick = onMealClick,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                )
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            is UiState.Error -> {
                item {
                    Text(
                        text = "Failed to load category meals: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {}
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MealOfDayCard(
    meal: Meal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = meal.thumbnail,
                contentDescription = meal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Rich gradient scrim for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                // Category Chip inside
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = meal.category.ifBlank { "Featured" },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (meal.area.isNotBlank()) {
                    Text(
                        text = "${meal.area} Cuisine",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

private fun getGreetingMessage(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Rise and shine! 🍳"
        in 12..16 -> "Hungry yet? 🍔"
        in 17..20 -> "Dinner's brewing! 🍷"
        else -> "Midnight snack? 🍕"
    }
}
