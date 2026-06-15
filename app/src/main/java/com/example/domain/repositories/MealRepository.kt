package com.example.domain.repositories

import com.example.domain.entities.Category
import com.example.domain.entities.Meal
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    // Remote
    suspend fun getCategories(): List<Category>
    suspend fun getMealsByCategory(categoryName: String): List<Meal>
    suspend fun searchMeals(query: String): List<Meal>
    suspend fun getMealById(id: String): Meal
    suspend fun getRandomMeal(): Meal

    // Local DB
    fun getFavoriteMeals(): Flow<List<Meal>>
    suspend fun saveFavoriteMeal(meal: Meal)
    suspend fun removeFavoriteMealById(id: String)
    suspend fun isFavorite(id: String): Boolean
}
