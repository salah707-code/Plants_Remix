package com.example.plantencyclopedia.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantencyclopedia.backup.BackupManager
import com.example.plantencyclopedia.backup.BackupRestoreSummary
import com.example.plantencyclopedia.data.InitialPlantData
import com.example.plantencyclopedia.data.Plant
import com.example.plantencyclopedia.data.PlantDatabase
import com.example.plantencyclopedia.data.PlantRepository
import com.example.plantencyclopedia.domain.ArabicTextNormalizer
import com.example.plantencyclopedia.domain.PlantAnalyticsUseCase
import com.example.plantencyclopedia.domain.PlantEncyclopediaStats
import com.example.plantencyclopedia.images.ImageStorageManager
import com.example.plantencyclopedia.importexport.ExcelImportExportManager
import com.example.plantencyclopedia.importexport.ImportMode
import com.example.plantencyclopedia.importexport.ImportPreviewResult
import com.example.plantencyclopedia.security.SecurityManager
import com.example.plantencyclopedia.settings.AppFontColorStyle
import com.example.plantencyclopedia.settings.AppFontSize
import com.example.plantencyclopedia.settings.AppLayoutDirection
import com.example.plantencyclopedia.settings.AppThemeMode
import com.example.plantencyclopedia.settings.ColorPalette
import com.example.plantencyclopedia.settings.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlantRepository
    val securityManager = SecurityManager(application)
    val preferencesManager = PreferencesManager(application)

    val appTheme: StateFlow<AppThemeMode> = preferencesManager.themeMode
    val colorPalette: StateFlow<ColorPalette> = preferencesManager.colorPalette
    val layoutDirectionPreference: StateFlow<AppLayoutDirection> = preferencesManager.layoutDirection
    val fontSize: StateFlow<AppFontSize> = preferencesManager.fontSize
    val fontColorStyle: StateFlow<AppFontColorStyle> = preferencesManager.fontColorStyle

    fun setLayoutDirection(direction: AppLayoutDirection) {
        preferencesManager.setLayoutDirection(direction)
    }

    fun setFontSize(size: AppFontSize) {
        preferencesManager.setFontSize(size)
    }

    fun setFontColorStyle(style: AppFontColorStyle) {
        preferencesManager.setFontColorStyle(style)
    }

    val allPlants: StateFlow<List<Plant>>

    // App Lock & Security
    private val _isAppLocked = MutableStateFlow(securityManager.isSecurityEnabled())
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("الكل")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _selectedFamilyFilter = MutableStateFlow<String?>(null)
    val selectedFamilyFilter: StateFlow<String?> = _selectedFamilyFilter.asStateFlow()

    private val _sortBy = MutableStateFlow("الأحدث") // الأحدث, أبجدي, الأكثر مشاهدة
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant.asStateFlow()

    private val _detailPlant = MutableStateFlow<Plant?>(null)
    val detailPlant: StateFlow<Plant?> = _detailPlant.asStateFlow()

    private val _currentTab = MutableStateFlow("الرئيسية")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _editingPlant = MutableStateFlow<Plant?>(null)
    val editingPlant: StateFlow<Plant?> = _editingPlant.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    // Import / Export states
    private val _importPreview = MutableStateFlow<ImportPreviewResult?>(null)
    val importPreview: StateFlow<ImportPreviewResult?> = _importPreview.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    val filteredPlants: StateFlow<List<Plant>>
    val statistics: StateFlow<PlantEncyclopediaStats>

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

        filteredPlants = combine(
            allPlants,
            _searchQuery,
            _selectedFilter,
            _selectedFamilyFilter,
            _sortBy
        ) { plants, query, filter, familyFilter, sortOption ->
            var list = plants.filter { plant ->
                val q = query.trim()
                val matchesText = q.isBlank() ||
                        ArabicTextNormalizer.containsNormalized(plant.name, q) ||
                        ArabicTextNormalizer.containsNormalized(plant.english, q) ||
                        ArabicTextNormalizer.containsNormalized(plant.scientific, q) ||
                        ArabicTextNormalizer.containsNormalized(plant.family, q) ||
                        plant.chemicals.any { ArabicTextNormalizer.containsNormalized(it, q) } ||
                        ArabicTextNormalizer.containsNormalized(plant.note, q) ||
                        plant.tags.any { ArabicTextNormalizer.containsNormalized(it, q) }

                val matchesUsage = when (filter) {
                    "الكل" -> true
                    "المفضلة" -> plant.isFavorite
                    else -> plant.usage.contains(filter)
                }

                val matchesFamily = familyFilter == null || plant.family == familyFilter

                matchesText && matchesUsage && matchesFamily
            }

            list = when (sortOption) {
                "أبجدي" -> list.sortedBy { it.name }
                "الأكثر مشاهدة" -> list.sortedByDescending { it.viewCount }
                else -> list.sortedByDescending { it.id }
            }

            list
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InitialPlantData.defaultPlants
        )

        statistics = combine(allPlants) { plantsArr ->
            PlantAnalyticsUseCase.calculateStats(plantsArr.first())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlantAnalyticsUseCase.calculateStats(InitialPlantData.defaultPlants)
        )

        // Select initial plant and sync detail plant
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
                if (_detailPlant.value != null) {
                    val updatedDetail = list.find { it.id == _detailPlant.value?.id }
                    if (updatedDetail != null) {
                        _detailPlant.value = updatedDetail
                    }
                }
            }
        }
    }

    // Security Unlock & Login
    fun unlockWithPin(pin: String): Boolean {
        val valid = securityManager.verifyPin(pin)
        if (valid) {
            _isAppLocked.value = false
        }
        return valid
    }

    fun unlockWithPassword(password: String): Boolean {
        val valid = securityManager.verifyPin(password)
        if (valid) {
            _isAppLocked.value = false
        }
        return valid
    }

    fun enterAsGuest() {
        _isAppLocked.value = false
    }

    fun unlockBiometric() {
        _isAppLocked.value = false
    }

    fun lockApp() {
        _isAppLocked.value = true
    }

    fun setAppPin(pin: String) {
        securityManager.setPin(pin)
        _actionMessage.value = "تم تعيين كلمة المرور بنجاح"
    }

    fun disableAppPin() {
        securityManager.disableSecurity()
        _isAppLocked.value = false
        _actionMessage.value = "تم تعطيل قفل التطبيق وكلمة المرور"
    }

    // Theme & Settings
    fun setThemeMode(mode: AppThemeMode) {
        preferencesManager.setThemeMode(mode)
    }

    fun setColorPalette(palette: ColorPalette) {
        preferencesManager.setColorPalette(palette)
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }

    fun onFamilyFilterSelected(family: String?) {
        _selectedFamilyFilter.value = family
    }

    fun onSortByChanged(sort: String) {
        _sortBy.value = sort
    }

    fun onPlantSelected(plant: Plant) {
        _selectedPlant.value = plant
        _detailPlant.value = plant
        viewModelScope.launch {
            repository.recordView(plant.id)
        }
    }

    fun openPlantDetail(plant: Plant) {
        onPlantSelected(plant)
    }

    fun closePlantDetail() {
        _detailPlant.value = null
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
            if (_detailPlant.value?.id == plant.id) {
                _detailPlant.value = _detailPlant.value?.copy(isFavorite = !plant.isFavorite)
            }
        }
    }

    fun copyPlant(plant: Plant) {
        viewModelScope.launch {
            val newId = repository.copyPlant(plant)
            _actionMessage.value = "تم تكرار نبات ${plant.name} بنجاح"
            val copied = repository.getPlantById(newId.toInt())
            if (copied != null) {
                _selectedPlant.value = copied
                _detailPlant.value = copied
            }
        }
    }

    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            repository.deletePlant(plant)
            if (_detailPlant.value?.id == plant.id) {
                _detailPlant.value = null
            }
            if (_selectedPlant.value?.id == plant.id) {
                _selectedPlant.value = allPlants.value.firstOrNull { it.id != plant.id }
            }
            _actionMessage.value = "تم حذف نبات ${plant.name} بنجاح"
        }
    }

    fun deleteMultiplePlants(ids: List<Int>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.deleteMultiple(ids)
            if (ids.contains(_detailPlant.value?.id)) {
                _detailPlant.value = null
            }
            if (ids.contains(_selectedPlant.value?.id)) {
                _selectedPlant.value = allPlants.value.firstOrNull { !ids.contains(it.id) }
            }
            _actionMessage.value = "تم حذف ${ids.size} نباتات بنجاح"
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
        note: String,
        images: List<String> = plant.images,
        habitat: String = plant.habitat,
        partsUsed: String = plant.partsUsed,
        preparation: String = plant.preparation,
        precautions: String = plant.precautions,
        growthForm: String = plant.growthForm
    ) {
        viewModelScope.launch {
            val primaryImg = images.firstOrNull { it.isNotBlank() } ?: plant.image
            val updated = plant.copy(
                name = name,
                english = english,
                scientific = scientific,
                family = family,
                usage = usage,
                chemicals = chemicals,
                note = note,
                image = primaryImg,
                images = images,
                habitat = habitat,
                partsUsed = partsUsed,
                preparation = preparation,
                precautions = precautions,
                growthForm = growthForm,
                updatedAt = System.currentTimeMillis()
            )
            repository.updatePlant(updated)
            _selectedPlant.value = updated
            if (_detailPlant.value?.id == updated.id) {
                _detailPlant.value = updated
            }
            _editingPlant.value = null
            _actionMessage.value = "تم حفظ تعديلات ${updated.name}"
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
        image: String,
        images: List<String> = emptyList(),
        habitat: String = "",
        partsUsed: String = "",
        preparation: String = "",
        precautions: String = "",
        growthForm: String = ""
    ) {
        viewModelScope.launch {
            val fullImages = if (images.isNotEmpty()) images else if (image.isNotBlank()) listOf(image) else emptyList()
            val primaryImg = fullImages.firstOrNull() ?: image.ifBlank { "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=800&q=85" }

            val newPlant = Plant(
                name = name.ifBlank { "نبات جديد" },
                english = english.ifBlank { "New Plant" },
                scientific = scientific.ifBlank { "Botanical species" },
                family = family.ifBlank { "الشفوية" },
                usage = usage.ifBlank { "علاجية" },
                chemicals = chemicals.ifEmpty { listOf("زيوت طيارة") },
                note = note.ifBlank { "تمت إضافة هذا النبات إلى الموسوعة النباتية." },
                image = primaryImg,
                images = fullImages,
                habitat = habitat,
                partsUsed = partsUsed,
                preparation = preparation,
                precautions = precautions,
                growthForm = growthForm,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val newId = repository.insertPlant(newPlant)
            val inserted = newPlant.copy(id = newId.toInt())
            _selectedPlant.value = inserted
            _showAddDialog.value = false
            _actionMessage.value = "تمت إضافة نبات ${inserted.name} إلى الموسوعة"
        }
    }

    // Multi-Image additions
    fun addImageToPlant(plant: Plant, imagePathOrUri: String) {
        val currentImages = plant.getAllImagesList().toMutableList()
        if (!currentImages.contains(imagePathOrUri)) {
            currentImages.add(imagePathOrUri)
            saveEditedPlant(
                plant = plant,
                name = plant.name,
                english = plant.english,
                scientific = plant.scientific,
                family = plant.family,
                usage = plant.usage,
                chemicals = plant.chemicals,
                note = plant.note,
                images = currentImages
            )
        }
    }

    fun removeImageFromPlant(plant: Plant, imagePath: String) {
        val currentImages = plant.getAllImagesList().toMutableList()
        currentImages.remove(imagePath)
        val newPrimary = currentImages.firstOrNull() ?: ""
        val updated = plant.copy(
            images = currentImages,
            image = newPrimary
        )
        viewModelScope.launch {
            repository.updatePlant(updated)
            _selectedPlant.value = updated
            if (_detailPlant.value?.id == updated.id) {
                _detailPlant.value = updated
            }
            ImageStorageManager.deleteImageFile(imagePath)
        }
    }

    fun setPrimaryImage(plant: Plant, imagePath: String) {
        val currentImages = plant.getAllImagesList().toMutableList()
        currentImages.remove(imagePath)
        currentImages.add(0, imagePath) // move to top
        val updated = plant.copy(
            images = currentImages,
            image = imagePath
        )
        viewModelScope.launch {
            repository.updatePlant(updated)
            _selectedPlant.value = updated
            if (_detailPlant.value?.id == updated.id) {
                _detailPlant.value = updated
            }
        }
    }

    // Excel Export & Import
    fun exportToExcel(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = ExcelImportExportManager.exportToExcelCsv(
                context = getApplication(),
                destinationUri = uri,
                plants = allPlants.value
            )
            if (success) {
                _actionMessage.value = "تم تصدير ملف Excel بنجاح"
            }
            onResult(success)
        }
    }

    fun previewExcelImport(uri: Uri) {
        viewModelScope.launch {
            val preview = ExcelImportExportManager.previewExcelCsv(
                context = getApplication(),
                sourceUri = uri,
                existingPlants = allPlants.value
            )
            _importPreview.value = preview
        }
    }

    fun previewExcelTextImport(text: String) {
        viewModelScope.launch {
            val preview = ExcelImportExportManager.previewPastedText(
                text = text,
                existingPlants = allPlants.value
            )
            _importPreview.value = preview
        }
    }

    fun dismissImportPreview() {
        _importPreview.value = null
    }

    fun executeExcelImport(mode: ImportMode) {
        val preview = _importPreview.value ?: return
        viewModelScope.launch {
            val currentList = allPlants.value
            val existingByName = currentList.associateBy { it.name.trim().lowercase() }

            val plantsToSave = mutableListOf<Plant>()
            for (item in preview.validPlants) {
                val existing = existingByName[item.name.trim().lowercase()]
                when (mode) {
                    ImportMode.ADD_NEW -> {
                        plantsToSave.add(item.copy(id = 0))
                    }
                    ImportMode.SKIP_DUPLICATES -> {
                        if (existing == null) {
                            plantsToSave.add(item.copy(id = 0))
                        }
                    }
                    ImportMode.UPDATE_EXISTING -> {
                        if (existing != null) {
                            plantsToSave.add(item.copy(id = existing.id, isFavorite = existing.isFavorite))
                        } else {
                            plantsToSave.add(item.copy(id = 0))
                        }
                    }
                }
            }

            repository.insertAll(plantsToSave)
            _actionMessage.value = "تم استيراد ${plantsToSave.size} نبات بنجاح"
            _importPreview.value = null
        }
    }

    // Full Backup & Restore
    fun createFullBackup(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = BackupManager.createFullBackup(
                context = getApplication(),
                destinationUri = uri,
                plants = allPlants.value,
                preferencesManager = preferencesManager
            )
            if (success) {
                _actionMessage.value = "تم إنشاء النسخة الاحتياطية الكاملة بنجاح"
            }
            onResult(success)
        }
    }

    fun restoreFullBackup(uri: Uri, onResult: (BackupRestoreSummary?) -> Unit) {
        viewModelScope.launch {
            val result = BackupManager.restoreFullBackup(
                context = getApplication(),
                sourceUri = uri,
                preferencesManager = preferencesManager
            )
            if (result != null) {
                val (plants, summary) = result
                repository.deleteAll()
                repository.insertAll(plants)
                _actionMessage.value = "تم استرجاع ${summary.plantCount} نبتة و ${summary.imagesCount} صورة بنجاح"
                onResult(summary)
            } else {
                _actionMessage.value = "فشلت استعادة النسخة الاحتياطية. تأكد من صحة الملف."
                onResult(null)
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}

