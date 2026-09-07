package com.example.plantencyclopedia.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val english: String,
    val scientific: String,
    val family: String,
    val usage: String,
    val chemicals: List<String>,
    val note: String,
    val image: String,
    val isFavorite: Boolean = false
)

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString("||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("||").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
