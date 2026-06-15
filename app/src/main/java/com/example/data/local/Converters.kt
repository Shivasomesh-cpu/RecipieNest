package com.example.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString("||")
    }

    @TypeConverter
    fun toList(data: String?): List<String>? {
        if (data == null) return emptyList()
        return data.split("||").filter { it.isNotEmpty() }
    }
}
