package com.example.plantencyclopedia.data

import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {
    val allPlants: Flow<List<Plant>> = plantDao.getAllPlants()

    suspend fun ensureSeeded() {
        if (plantDao.getCount() == 0) {
            plantDao.insertAll(InitialPlantData.defaultPlants)
        }
    }

    suspend fun insertPlant(plant: Plant): Long {
        return plantDao.insert(plant)
    }

    suspend fun updatePlant(plant: Plant) {
        plantDao.update(plant)
    }

    suspend fun deletePlant(plant: Plant) {
        plantDao.delete(plant)
    }

    suspend fun toggleFavorite(id: Int) {
        plantDao.toggleFavorite(id)
    }
}
