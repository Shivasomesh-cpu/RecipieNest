package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM favorite_meals")
    fun getAllFavorites(): Flow<List<MealEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE id = :id LIMIT 1)")
    suspend fun isFavorite(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(meal: MealEntity)

    @Delete
    suspend fun deleteFavorite(meal: MealEntity)

    @Query("DELETE FROM favorite_meals WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)
}
