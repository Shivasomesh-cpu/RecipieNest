package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.remote.TheMealDbApi
import com.example.data.repository.MealRepositoryImpl
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.FavouritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.RecipeNestTheme
import com.example.ui.viewmodel.MealViewModel
import com.example.ui.viewmodel.MealViewModelFactory

import coil.Coil
import coil.ImageLoader
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Configure Coil to send a proper User-Agent header globally
        val imageLoader = ImageLoader.Builder(applicationContext)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val originalRequest = chain.request()
                        val requestWithUserAgent = originalRequest.newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                            .build()
                        chain.proceed(requestWithUserAgent)
                    }
                    .build()
            }
            .crossfade(true)
            .build()
        Coil.setImageLoader(imageLoader)
        
        // Setup simple manual constructor injection
        val api = TheMealDbApi.create()
        val database = AppDatabase.getInstance(applicationContext)
        val dao = database.mealDao()
        val repository = MealRepositoryImpl(api, dao)
        val viewModelFactory = MealViewModelFactory(repository)

        setContent {
            com.example.ui.theme.ThemeProvider(context = applicationContext) {
                RecipeNestTheme {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                // Obtain our ViewModel
                val mealViewModel: MealViewModel = viewModel(factory = viewModelFactory)

                // Define bottom navigation screens
                val tabs = listOf(
                    NavigationTab("home", "Explore", Icons.Filled.Home, Icons.Outlined.Home),
                    NavigationTab("search", "Search", Icons.Filled.Search, Icons.Outlined.Search),
                    NavigationTab("favourites", "Favourites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
                )

                // Determine whether to show the BottomNavigationBar based on the active route
                val showBottomBar = currentRoute in listOf("home", "search", "favourites")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                tabs.forEach { tab ->
                                    val isSelected = currentRoute == tab.route
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            navController.navigate(tab.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = { Text(text = tab.label) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = mealViewModel,
                                onMealClick = { meal ->
                                    navController.navigate("detail/${meal.id}")
                                }
                            )
                        }
                        
                        composable("search") {
                            SearchScreen(
                                viewModel = mealViewModel,
                                onMealClick = { meal ->
                                    navController.navigate("detail/${meal.id}")
                                }
                            )
                        }
                        
                        composable("favourites") {
                            FavouritesScreen(
                                viewModel = mealViewModel,
                                onMealClick = { meal ->
                                    navController.navigate("detail/${meal.id}")
                                }
                            )
                        }
                        
                        composable(
                            route = "detail/{mealId}",
                            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
                            DetailScreen(
                                mealId = mealId,
                                viewModel = mealViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    }
}

data class NavigationTab(
    val route: String,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
