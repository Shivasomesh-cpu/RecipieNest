package com.example.domain.entities

data class Meal(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbnail: String,
    val youtubeUrl: String,
    val ingredients: List<String>,
    val measures: List<String>
)
