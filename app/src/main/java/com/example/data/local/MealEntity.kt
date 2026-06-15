package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.entities.Meal

@Entity(tableName = "favorite_meals")
data class MealEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbnail: String,
    val youtubeUrl: String,
    val ingredients: List<String>,
    val measures: List<String>
) {
    fun toMeal(): Meal {
        return Meal(
            id = id,
            name = name,
            category = category,
            area = area,
            instructions = instructions,
            thumbnail = thumbnail,
            youtubeUrl = youtubeUrl,
            ingredients = ingredients,
            measures = measures
        )
    }

    companion object {
        fun fromMeal(meal: Meal): MealEntity {
            return MealEntity(
                id = meal.id,
                name = meal.name,
                category = meal.category,
                area = meal.area,
                instructions = meal.instructions,
                thumbnail = meal.thumbnail,
                youtubeUrl = meal.youtubeUrl,
                ingredients = meal.ingredients,
                measures = meal.measures
            )
        }
    }
}
