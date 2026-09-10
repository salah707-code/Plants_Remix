package com.example.plantencyclopedia.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(
    tableName = "plants",
    indices = [
        Index(value = ["scientific"]),
        Index(value = ["family"]),
        Index(value = ["isFavorite"]),
        Index(value = ["name"])
    ]
)
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
    val images: List<String> = emptyList(),
    val habitat: String = "",
    val partsUsed: String = "",
    val preparation: String = "",
    val precautions: String = "",
    val growthForm: String = "",
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val viewCount: Int = 0
) {
    fun getAllImagesList(): List<String> {
        val list = mutableListOf<String>()
        if (image.isNotBlank()) list.add(image)
        images.forEach { if (it.isNotBlank() && !list.contains(it)) list.add(it) }
        return list
    }

    fun getPrimaryImage(): String {
        return images.firstOrNull { it.isNotBlank() } ?: image
    }
}

@Entity(tableName = "plant_history")
data class PlantHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val plantId: Int,
    val viewedAt: Long = System.currentTimeMillis()
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

