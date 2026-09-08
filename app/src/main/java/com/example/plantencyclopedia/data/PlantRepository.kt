package com.example.plantencyclopedia.data

import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {
    val allPlants: Flow<List<Plant>> = plantDao.getAllPlants()
    val favoritePlants: Flow<List<Plant>> = plantDao.getFavoritePlants()

    suspend fun ensureSeeded() {
        if (plantDao.getCount() == 0) {
            plantDao.insertAll(InitialPlantData.defaultPlants)
        }
    }

    suspend fun getPlantById(id: Int): Plant? {
        return plantDao.getPlantById(id)
    }

    suspend fun insertPlant(plant: Plant): Long {
        return plantDao.insert(plant)
    }

    suspend fun insertAll(plants: List<Plant>) {
        plantDao.insertAll(plants)
    }

    suspend fun updatePlant(plant: Plant) {
        val updated = plant.copy(updatedAt = System.currentTimeMillis())
        plantDao.update(updated)
    }

    suspend fun deletePlant(plant: Plant) {
        plantDao.delete(plant)
    }

    suspend fun deletePlantById(id: Int) {
        plantDao.deleteById(id)
    }

    suspend fun deleteAll() {
        plantDao.deleteAll()
    }

    suspend fun copyPlant(source: Plant): Long {
        val copy = source.copy(
            id = 0,
            name = "${source.name} (نسخة)",
            isFavorite = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            viewCount = 0
        )
        return plantDao.insert(copy)
    }

    suspend fun toggleFavorite(id: Int) {
        plantDao.toggleFavorite(id)
    }

    suspend fun recordView(plantId: Int) {
        plantDao.incrementViewCount(plantId)
        plantDao.insertHistory(PlantHistory(plantId = plantId))
    }

    fun getMostViewedPlants(limit: Int = 5): Flow<List<Plant>> {
        return plantDao.getMostViewedPlants(limit)
    }

    fun getRecentHistory(limit: Int = 20): Flow<List<PlantHistory>> {
        return plantDao.getRecentHistory(limit)
    }

    suspend fun clearHistory() {
        plantDao.clearHistory()
    }
}

