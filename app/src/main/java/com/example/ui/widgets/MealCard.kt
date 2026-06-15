package com.example.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.domain.entities.Meal
import com.example.domain.entities.isVegetarian
import com.example.domain.entities.difficulty
import com.example.domain.entities.estimatedNutrition

@Composable
fun DietaryIndicator(isVegetarian: Boolean, modifier: Modifier = Modifier) {
    val borderColor = if (isVegetarian) Color(0xFF388E3C) else Color(0xFFD32F2F)
    val dotColor = if (isVegetarian) Color(0xFF388E3C) else Color(0xFFD32F2F)
    Box(
        modifier = modifier
            .size(16.dp)
            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(2.dp))
            .border(1.2.dp, borderColor, RoundedCornerShape(2.dp))
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(dotColor, CircleShape)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealCard(
    meal: Meal,
    onMealClick: (Meal) -> Unit,
    modifier: Modifier = Modifier,
    onMealLongClick: ((Meal) -> Unit)? = null
) {
    val clickModifier = if (onMealLongClick != null) {
        Modifier.combinedClickable(
            onClick = { onMealClick(meal) },
            onLongClick = { onMealLongClick(meal) }
        )
    } else {
        Modifier.clickable { onMealClick(meal) }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .then(clickModifier)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = meal.thumbnail,
                contentDescription = meal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Rich gradient overlay for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.2f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top overlay metadata badges row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = meal.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (meal.difficulty) {
                                "Easy" -> Color(0xFF81C784)
                                "Hard" -> Color(0xFFE57373)
                                else -> Color(0xFFFFB74D)
                            }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 5.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🔥 ${meal.estimatedNutrition.calories}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFCC80)
                        )
                    }
                }
                
                DietaryIndicator(isVegetarian = meal.isVegetarian)
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
