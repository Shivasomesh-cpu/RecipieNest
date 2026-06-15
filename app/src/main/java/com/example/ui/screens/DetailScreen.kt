package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.entities.Meal
import com.example.domain.entities.isVegetarian
import com.example.domain.entities.isVegan
import com.example.domain.entities.difficulty
import com.example.domain.entities.estimatedNutrition
import com.example.domain.entities.courseType
import com.example.ui.viewmodel.MealViewModel
import com.example.ui.viewmodel.UiState
import com.example.ui.widgets.EmptyState
import com.example.ui.widgets.IngredientTile
import com.example.ui.widgets.shimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    mealId: String,
    viewModel: MealViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Trigger details fetch
    LaunchedEffect(mealId) {
        viewModel.loadMealDetail(mealId)
    }

    val detailStates by viewModel.mealDetailStates.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavoriteStream(mealId).collectAsState(initial = false)
    val state = detailStates[mealId] ?: UiState.Loading

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Recipe Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            if (state is UiState.Success) {
                FloatingActionButton(
                    onClick = { viewModel.toggleFavorite(state.data) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from Favourites" else "Add to Favourites",
                        tint = if (isFavorite) Color.White else Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state) {
                is UiState.Loading -> {
                    DetailSkeleton()
                }
                is UiState.Success -> {
                    val meal = state.data
                    val steps = remember(meal.instructions) { parseInstructions(meal.instructions) }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        // 1. Full-bleed Hero style Header image
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                            ) {
                                AsyncImage(
                                    model = meal.thumbnail,
                                    contentDescription = meal.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                
                                // Elegant gradient scrim
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Black.copy(alpha = 0.4f),
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.6f)
                                                )
                                            )
                                        )
                                )
                            }
                        }

                        // 2. Title and metadata chips
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = meal.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                
                                Spacer(modifier = Modifier.height(10.dp))

                                // Dietary & Difficulty Row with FSSAI dot
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isVegVal = meal.isVegetarian
                                    com.example.ui.widgets.DietaryIndicator(isVegetarian = isVegVal)
                                    
                                    val isVeganVal = meal.isVegan
                                    Text(
                                        text = if (isVeganVal) "Vegan 🌱" else if (isVegVal) "Vegetarian 🟢" else "Non-Vegetarian 🔴",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isVegVal) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), CircleShape)
                                    )
                                    
                                    val diffVal = meal.difficulty
                                    Text(
                                        text = "Difficulty: $diffVal",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when (diffVal) {
                                            "Easy" -> Color(0xFF2E7D32)
                                            "Hard" -> Color(0xFFC62828)
                                            else -> Color(0xFFE65100)
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Nutrition information card
                                val nutrition = meal.estimatedNutrition
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Estimated Nutrition Profile",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                        
                                        Spacer(modifier = Modifier.height(10.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("🔥", style = MaterialTheme.typography.titleMedium)
                                                Text("${nutrition.calories}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold)
                                                Text("Calories", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("💪", style = MaterialTheme.typography.titleMedium)
                                                Text("${nutrition.protein}g", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold)
                                                Text("Protein", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("🍞", style = MaterialTheme.typography.titleMedium)
                                                Text("${nutrition.carbs}g", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold)
                                                Text("Carbs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("🥑", style = MaterialTheme.typography.titleMedium)
                                                Text("${nutrition.fat}g", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.ExtraBold)
                                                Text("Fat", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Metadata chips row
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (meal.category.isNotBlank()) {
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(meal.category) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                            ),
                                            border = null
                                        )
                                    }
                                    
                                    if (meal.area.isNotBlank()) {
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(meal.area) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            ),
                                            border = null
                                        )
                                    }

                                    val courseVal = meal.courseType
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(courseVal) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        ),
                                        border = null
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // YouTube button
                                if (meal.youtubeUrl.isNotBlank()) {
                                    Button(
                                        onClick = { launchYouTube(context, meal.youtubeUrl) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF0000), // YouTube Red
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Watch on YouTube", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Gemini AI Kitchen Hub Section
                        item {
                            GeminiAiKitchenHubSection(meal = meal, viewModel = viewModel)
                        }

                        // 3. Ingredients Section Header
                        item {
                            Text(
                                    text = "Ingredients",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        // 4. Ingredients checkable list
                        val validIngredients = meal.ingredients.zip(meal.measures)
                        itemsIndexed(validIngredients) { _, pair ->
                            IngredientTile(
                                ingredient = pair.first,
                                measure = pair.second,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }

                        // 5. Instructions Section Header
                        item {
                            Text(
                                text = "Instructions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                            )
                        }

                        // 6. Detailed item steps
                        itemsIndexed(steps) { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Bottom Spacer
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
                is UiState.Error -> {
                    EmptyState(
                        icon = Icons.Default.Info,
                        title = "Recipe Unavailable",
                        subtitle = state.message,
                        actionText = "Back to Home",
                        onActionClick = onBackClick
                    )
                }
                else -> {
                    DetailSkeleton()
                }
            }
        }
    }
}

@Composable
fun DetailSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .shimmer()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmer()
                )
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmer()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmer()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            repeat(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmer()
                )
            }
        }
    }
}

fun parseInstructions(instructions: String): List<String> {
    return instructions
        .split(Regex("(\\r?\\n)+"))
        .map { it.trim() }
        .filter { it.isNotEmpty() && it.length > 5 && !it.startsWith("STEP", ignoreCase = true) }
        .map { step ->
            // Strip leading numbers or bullets from the instruction step if they exist
            step.replace(Regex("^\\s*([\\d.]+|-|•|\\*)\\s+"), "")
        }
}

fun launchYouTube(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not launch link. Browser/YouTube app may be missing.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun MarkdownLikeText(text: String, modifier: Modifier = Modifier) {
    if (text.isBlank()) return
    val paragraphs = text.split("\n")
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        paragraphs.forEach { paragraph ->
            if (paragraph.startsWith("###")) {
                Text(
                    text = paragraph.removePrefix("###").trim(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else if (paragraph.startsWith("##")) {
                Text(
                    text = paragraph.removePrefix("##").trim(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else if (paragraph.startsWith("-") || paragraph.startsWith("*") || paragraph.startsWith("•")) {
                val cleanParagraph = paragraph.replace(Regex("^[-*•]\\s*"), "")
                Row(modifier = Modifier.padding(start = 8.dp)) {
                    Text("• ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(
                        text = parseBoldText(cleanParagraph),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                    )
                }
            } else {
                Text(
                    text = parseBoldText(paragraph),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun parseBoldText(text: String): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        val parts = text.split("**")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                withStyle(style = androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(part)
                }
            } else {
                append(part)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiAiKitchenHubSection(
    meal: Meal,
    viewModel: MealViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Tweaks") }

    // State managers
    val tweakPref by viewModel.tweakPref.collectAsStateWithLifecycle()
    val tweakResult by viewModel.tweakResult.collectAsStateWithLifecycle()
    val isTweakLoading by viewModel.isTweakLoading.collectAsStateWithLifecycle()

    val subsResult by viewModel.subsResult.collectAsStateWithLifecycle()
    val isSubsLoading by viewModel.isSubsLoading.collectAsStateWithLifecycle()

    val nutritionResult by viewModel.nutritionResult.collectAsStateWithLifecycle()
    val isNutritionLoading by viewModel.isNutritionLoading.collectAsStateWithLifecycle()

    val chefQuestion by viewModel.chefQuestion.collectAsStateWithLifecycle()
    val chefResponse by viewModel.chefResponse.collectAsStateWithLifecycle()
    val isChefLoading by viewModel.isChefLoading.collectAsStateWithLifecycle()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Gemini AI",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Gemini Kitchen Assistant",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AI ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text(
                text = "Tweak, analyze, find substitutes, or chat with your virtual AI Sous Chef about this recipe.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.73f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ScrollableTabRow(
                selectedTabIndex = when (selectedTab) {
                    "Tweaks" -> 0
                    "Substitutes" -> 1
                    "Nutrition" -> 2
                    else -> 3
                },
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[when (selectedTab) {
                            "Tweaks" -> 0
                            "Substitutes" -> 1
                            "Nutrition" -> 2
                            else -> 3
                        }]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Tab(
                    selected = selectedTab == "Tweaks",
                    onClick = { selectedTab = "Tweaks" },
                    text = { Text("Tweak 🥦", style = MaterialTheme.typography.labelLarge) }
                )
                Tab(
                    selected = selectedTab == "Substitutes",
                    onClick = { selectedTab = "Substitutes" },
                    text = { Text("Substitutes 🔄", style = MaterialTheme.typography.labelLarge) }
                )
                Tab(
                    selected = selectedTab == "Nutrition",
                    onClick = { selectedTab = "Nutrition" },
                    text = { Text("Nutrition 🥗", style = MaterialTheme.typography.labelLarge) }
                )
                Tab(
                    selected = selectedTab == "Chef Bot",
                    onClick = { selectedTab = "Chef Bot" },
                    text = { Text("Chef Chat 💬", style = MaterialTheme.typography.labelLarge) }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                when (selectedTab) {
                    "Tweaks" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Adapt steps & ingredients for a custom dietary style (Low-Latency Flash Lite):",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val options = listOf("Vegetarian 🥦", "Vegan 🌱", "Gluten-Free 🌾", "Low-Carb 🥑", "High-Protein 🐟")
                                options.forEach { option ->
                                    val cleanOption = option.split(" ")[0]
                                    FilterChip(
                                        selected = tweakPref == cleanOption,
                                        onClick = {
                                            viewModel.getRecipeTweak(
                                                mealName = meal.name,
                                                dietaryPreference = cleanOption,
                                                originalInstructions = meal.instructions
                                            )
                                        },
                                        label = { Text(option) }
                                    )
                                }
                            }

                            if (isTweakLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            } else if (tweakResult.isNotBlank()) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    MarkdownLikeText(
                                        text = tweakResult,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = "Select a style above to adapt steps instantly.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                        }
                    }

                    "Substitutes" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Missing ingredients? Request replacements (Low-Latency Flash Lite):",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    viewModel.getIngredientSubstitutes(meal.ingredients)
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Find Substitutes 🔄")
                            }

                            if (isSubsLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            } else if (subsResult.isNotBlank()) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.22f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    MarkdownLikeText(
                                        text = subsResult,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    "Nutrition" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Analyze nutritional values, estimated calories, and health points (Flash AI):",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = {
                                    viewModel.getNutritionAnalysis(meal.name, meal.ingredients)
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("Generate Nutrition Profile 📊")
                            }

                            if (isNutritionLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            } else if (nutritionResult.isNotBlank()) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.12f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    MarkdownLikeText(
                                        text = nutritionResult,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    "Chef Bot" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Ask your companion chef any cooking question about preparation or equipment:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = chefQuestion,
                                onValueChange = { viewModel.updateChefQuestion(it) },
                                placeholder = { Text("Can I prepare this in an air fryer?") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (chefQuestion.isNotBlank()) {
                                                viewModel.askChefAssistant(
                                                    mealName = meal.name,
                                                    question = chefQuestion,
                                                    ingredients = meal.ingredients,
                                                    instructions = meal.instructions
                                                )
                                            }
                                        },
                                        enabled = chefQuestion.isNotBlank() && !isChefLoading
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = "Send"
                                        )
                                    }
                                }
                            )

                            if (isChefLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            } else if (chefResponse.isNotBlank()) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Chef Nest Bot:",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        MarkdownLikeText(text = chefResponse)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
