package com.example.data.remote

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json
import com.example.domain.entities.Category

@JsonClass(generateAdapter = true)
data class CategoryListResponse(
    @Json(name = "categories") val categories: List<CategoryRemoteModel>?
)

@JsonClass(generateAdapter = true)
data class CategoryRemoteModel(
    @Json(name = "idCategory") val idCategory: String,
    @Json(name = "strCategory") val strCategory: String,
    @Json(name = "strCategoryThumb") val strCategoryThumb: String?,
    @Json(name = "strCategoryDescription") val strCategoryDescription: String?
) {
    fun toCategory(): Category {
        return Category(
            id = idCategory,
            name = strCategory,
            thumbnail = strCategoryThumb ?: "",
            description = strCategoryDescription ?: ""
        )
    }
}
