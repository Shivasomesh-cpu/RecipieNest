package com.example.data.repository

import com.example.data.local.MealDao
import com.example.data.local.MealEntity
import com.example.data.remote.TheMealDbApi
import com.example.domain.entities.Category
import com.example.domain.entities.Meal
import com.example.domain.repositories.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealRepositoryImpl(
    private val api: TheMealDbApi,
    private val dao: MealDao
) : MealRepository {

    override suspend fun getCategories(): List<Category> {
        val remote = try {
            val response = api.getCategories()
            response.categories?.map { it.toCategory() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        val hasIndian = remote.any { it.name.equals("Indian", ignoreCase = true) }
        return if (!hasIndian) {
            val indianCategory = Category(
                id = "indian_cat_id_999",
                name = "Indian",
                thumbnail = "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?auto=format&fit=crop&q=80&w=200",
                description = "Flavorful and golden spiced authentic dishes from India."
            )
            listOf(indianCategory) + remote
        } else {
            remote
        }
    }

    override suspend fun getMealsByCategory(categoryName: String): List<Meal> {
        val remote = try {
            val response = api.filterByCategory(categoryName)
            response.meals?.map { it.toMeal() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        
        val localIndian = when (categoryName.lowercase()) {
            "indian" -> IndianMealData.meals
            "vegetarian" -> IndianMealData.meals.filter { !it.category.equals("Chicken", ignoreCase = true) }
            "chicken" -> IndianMealData.meals.filter { it.category.equals("Chicken", ignoreCase = true) }
            "dessert" -> IndianMealData.meals.filter { f -> f.category.equals("Dessert", ignoreCase = true) }
            else -> emptyList()
        }
        
        return (localIndian + remote).distinctBy { it.id }
    }

    override suspend fun searchMeals(query: String): List<Meal> {
        if (query.isBlank()) return emptyList()
        val remote = try {
            val response = api.searchMeals(query)
            response.meals?.map { it.toMeal() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        val localIndian = IndianMealData.meals.filter { meal ->
            meal.name.contains(query, ignoreCase = true) ||
            meal.category.contains(query, ignoreCase = true) ||
            meal.area.contains(query, ignoreCase = true) ||
            meal.ingredients.any { it.contains(query, ignoreCase = true) }
        }
        return (localIndian + remote).distinctBy { it.id }
    }

    override suspend fun getMealById(id: String): Meal {
        if (id.startsWith("indian_")) {
            return IndianMealData.meals.find { it.id == id }
                ?: throw NoSuchElementException("No Indian recipe found for ID $id")
        }
        val response = api.lookupMealById(id)
        val remoteMeal = response.meals?.firstOrNull() ?: throw NoSuchElementException("No meal found for ID $id")
        return remoteMeal.toMeal()
    }

    override suspend fun getRandomMeal(): Meal {
        val showIndianRandomly = (1..10).random() <= 4
        if (showIndianRandomly) {
            return IndianMealData.meals.random()
        }
        return try {
            val response = api.getRandomMeal()
            val remoteMeal = response.meals?.firstOrNull() ?: throw NoSuchElementException("No random meal returned")
            remoteMeal.toMeal()
        } catch (e: Exception) {
            IndianMealData.meals.random()
        }
    }

    override fun getFavoriteMeals(): Flow<List<Meal>> {
        return dao.getAllFavorites().map { entities ->
            entities.map { it.toMeal() }
        }
    }

    override suspend fun saveFavoriteMeal(meal: Meal) {
        val entity = MealEntity.fromMeal(meal)
        dao.insertFavorite(entity)
    }

    override suspend fun removeFavoriteMealById(id: String) {
        dao.deleteFavoriteById(id)
    }

    override suspend fun isFavorite(id: String): Boolean {
        return dao.isFavorite(id)
    }
}
