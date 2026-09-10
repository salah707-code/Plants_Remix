package com.example.plantencyclopedia.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY id ASC")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoritePlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: Int): Plant?

    @Query("SELECT COUNT(*) FROM plants")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Plant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: Plant): Long

    @Update
    suspend fun update(plant: Plant)

    @Delete
    suspend fun delete(plant: Plant)

    @Query("DELETE FROM plants WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM plants WHERE id IN (:ids)")
    suspend fun deleteMultiple(ids: List<Int>)

    @Query("SELECT * FROM plants WHERE id IN (:ids)")
    suspend fun getPlantsByIds(ids: List<Int>): List<Plant>

    @Query("SELECT * FROM plants WHERE LOWER(TRIM(scientific)) = LOWER(TRIM(:scientific)) LIMIT 1")
    suspend fun findByScientific(scientific: String): Plant?

    @Query("SELECT * FROM plants WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name)) LIMIT 1")
    suspend fun findByName(name: String): Plant?

    @Query("DELETE FROM plants")
    suspend fun deleteAll()

    @Query("UPDATE plants SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int)

    @Query("UPDATE plants SET viewCount = viewCount + 1 WHERE id = :id")
    suspend fun incrementViewCount(id: Int)

    @Query("SELECT * FROM plants ORDER BY viewCount DESC LIMIT :limit")
    fun getMostViewedPlants(limit: Int = 5): Flow<List<Plant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: PlantHistory)

    @Query("SELECT * FROM plant_history ORDER BY viewedAt DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 20): Flow<List<PlantHistory>>

    @Query("DELETE FROM plant_history")
    suspend fun clearHistory()
}

