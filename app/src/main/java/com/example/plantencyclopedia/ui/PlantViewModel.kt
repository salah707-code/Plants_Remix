package com.example.plantencyclopedia.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantencyclopedia.data.InitialPlantData
import com.example.plantencyclopedia.data.Plant
import com.example.plantencyclopedia.data.PlantDatabase
import com.example.plantencyclopedia.data.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlantRepository

    val allPlants: StateFlow<List<Plant>>

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("الكل")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant.asStateFlow()

    private val _currentTab = MutableStateFlow("الرئيسية")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _editingPlant = MutableStateFlow<Plant?>(null)
    val editingPlant: StateFlow<Plant?> = _editingPlant.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    val filteredPlants: StateFlow<List<Plant>>

    init {
        val database = PlantDatabase.getDatabase(application, viewModelScope)
        repository = PlantRepository(database.plantDao())

        viewModelScope.launch {
            repository.ensureSeeded()
        }

        allPlants = repository.allPlants.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialPlantData.defaultPlants
        )

        filteredPlants = combine(allPlants, _searchQuery, _selectedFilter) { plants, query, filter ->
            plants.filter { plant ->
                val matchesText = query.isBlank() ||
                        plant.name.contains(query, ignoreCase = true) ||
                        plant.english.contains(query, ignoreCase = true) ||
                        plant.scientific.contains(query, ignoreCase = true) ||
                        plant.family.contains(query, ignoreCase = true)
                val matchesFilter = filter == "الكل" || plant.usage == filter
                matchesText && matchesFilter
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialPlantData.defaultPlants
        )

        // Select initial plant
        viewModelScope.launch {
            allPlants.collect { list ->
                if (_selectedPlant.value == null && list.isNotEmpty()) {
                    _selectedPlant.value = list.first()
                } else if (_selectedPlant.value != null) {
                    val updated = list.find { it.id == _selectedPlant.value?.id }
                    if (updated != null) {
                        _selectedPlant.value = updated
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }

    fun onPlantSelected(plant: Plant) {
        _selectedPlant.value = plant
    }

    fun onTabSelected(tab: String) {
        _currentTab.value = tab
    }

    fun onStartEditPlant(plant: Plant) {
        _editingPlant.value = plant
    }

    fun onDismissEditDialog() {
        _editingPlant.value = null
    }

    fun onShowAddDialog(show: Boolean) {
        _showAddDialog.value = show
    }

    fun toggleFavorite(plant: Plant) {
        viewModelScope.launch {
            repository.toggleFavorite(plant.id)
        }
    }

    fun saveEditedPlant(
        plant: Plant,
        name: String,
        english: String,
        scientific: String,
        family: String,
        usage: String,
        chemicals: List<String>,
        note: String
    ) {
        viewModelScope.launch {
            val updated = plant.copy(
                name = name,
                english = english,
                scientific = scientific,
                family = family,
                usage = usage,
                chemicals = chemicals,
                note = note
            )
            repository.updatePlant(updated)
            _selectedPlant.value = updated
            _editingPlant.value = null
        }
    }

    fun addNewPlant(
        name: String,
        english: String,
        scientific: String,
        family: String,
        usage: String,
        chemicals: List<String>,
        note: String,
        image: String
    ) {
        viewModelScope.launch {
            val newPlant = Plant(
                name = name.ifBlank { "نبات جديد" },
                english = english.ifBlank { "New Plant" },
                scientific = scientific.ifBlank { "Botanical species" },
                family = family.ifBlank { "الشفوية" },
                usage = usage.ifBlank { "علاجية" },
                chemicals = chemicals.ifEmpty { listOf("زيوت طيارة") },
                note = note.ifBlank { "تمت إضافة هذا النبات إلى الموسوعة النباتية." },
                image = image.ifBlank { "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=800&q=85" }
            )
            val newId = repository.insertPlant(newPlant)
            _selectedPlant.value = newPlant.copy(id = newId.toInt())
            _showAddDialog.value = false
        }
    }
}
