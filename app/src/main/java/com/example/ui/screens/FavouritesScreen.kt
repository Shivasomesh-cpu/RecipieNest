package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.entities.Meal
import com.example.ui.viewmodel.MealViewModel
import com.example.ui.widgets.EmptyState
import com.example.ui.widgets.MealCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavouritesScreen(
    viewModel: MealViewModel,
    onMealClick: (Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.favoriteMeals.collectAsStateWithLifecycle()
    var selectedMealToDelete by remember { mutableStateOf<Meal?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Surface
        Surface(
            tonalElevation = 6.dp,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "My Favourites",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Your personalized cookbook list",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (favorites.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.FavoriteBorder,
                    title = "Your cookbook is empty",
                    subtitle = "Explore recipes on the home page or search and save them by tapping the heart icon!"
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(favorites, key = { it.id }) { meal ->
                        MealCard(
                            meal = meal,
                            onMealClick = onMealClick,
                            onMealLongClick = { selectedMealToDelete = it }
                        )
                    }
                }
            }
            
            // Delete Dialog Confirmation
            selectedMealToDelete?.let { meal ->
                AlertDialog(
                    onDismissRequest = { selectedMealToDelete = null },
                    icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    title = { Text("Remove from Favourites?") },
                    text = { Text("Are you sure you want to remove '${meal.name}' from your saved recipes?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.removeFavoriteById(meal.id)
                                selectedMealToDelete = null
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Remove", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedMealToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
