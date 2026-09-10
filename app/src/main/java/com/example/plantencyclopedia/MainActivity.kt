package com.example.plantencyclopedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.plantencyclopedia.ui.PlantViewModel
import com.example.plantencyclopedia.ui.components.AddPlantDialog
import com.example.plantencyclopedia.ui.components.EditPlantDialog
import com.example.plantencyclopedia.ui.screens.FamiliesScreen
import com.example.plantencyclopedia.ui.screens.FavoritesScreen
import com.example.plantencyclopedia.ui.screens.HomeScreen
import com.example.plantencyclopedia.ui.screens.LoginScreen
import com.example.plantencyclopedia.ui.screens.PlantDetailScreen
import com.example.plantencyclopedia.ui.screens.PlantsCatalogScreen
import com.example.plantencyclopedia.ui.screens.SecurityLockScreen
import com.example.plantencyclopedia.ui.screens.SettingsScreen
import com.example.plantencyclopedia.ui.screens.StatisticsScreen
import com.example.plantencyclopedia.ui.theme.CardBorder
import com.example.plantencyclopedia.ui.theme.CardSurface
import com.example.plantencyclopedia.ui.theme.PlantEncyclopediaTheme
import com.example.plantencyclopedia.ui.theme.SageGreen
import com.example.plantencyclopedia.ui.theme.SageGreenContainer
import com.example.plantencyclopedia.ui.theme.SageGreenDark
import com.example.plantencyclopedia.ui.theme.TextMuted

class MainActivity : ComponentActivity() {
    private val viewModel: PlantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
            val colorPalette by viewModel.colorPalette.collectAsStateWithLifecycle()
            val layoutDirectionPreference by viewModel.layoutDirectionPreference.collectAsStateWithLifecycle()

            PlantEncyclopediaTheme(
                themeMode = appTheme,
                colorPalette = colorPalette,
                layoutDirectionPreference = layoutDirectionPreference
            ) {
                PlantEncyclopediaApp(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun PlantEncyclopediaApp(viewModel: PlantViewModel) {
    val allPlants by viewModel.allPlants.collectAsStateWithLifecycle()
    val visiblePlants by viewModel.filteredPlants.collectAsStateWithLifecycle()
    val selectedPlant by viewModel.selectedPlant.collectAsStateWithLifecycle()
    val detailPlant by viewModel.detailPlant.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val editingPlant by viewModel.editingPlant.collectAsStateWithLifecycle()
    val showAddDialog by viewModel.showAddDialog.collectAsStateWithLifecycle()
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()
    val statistics by viewModel.statistics.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearActionMessage()
        }
    }

    if (isAppLocked) {
        LoginScreen(
            securityManager = viewModel.securityManager,
            onUnlockWithPassword = viewModel::unlockWithPassword,
            onUnlockBiometric = viewModel::unlockBiometric,
            onEnterAsGuest = viewModel::enterAsGuest,
            onSetPassword = viewModel::setAppPin
        )
        return
    }

    val navItems = listOf(
        NavItem("الرئيسية", Icons.Default.Home, "nav_home"),
        NavItem("النباتات", Icons.Default.LocalFlorist, "nav_plants"),
        NavItem("الفصائل", Icons.Default.Category, "nav_families"),
        NavItem("الإحصائيات", Icons.Default.BarChart, "nav_stats"),
        NavItem("الإعدادات", Icons.Default.Settings, "nav_settings")
    )

    if (detailPlant != null) {
        PlantDetailScreen(
            plant = detailPlant!!,
            allPlants = allPlants,
            onBack = viewModel::closePlantDetail,
            onToggleFavorite = viewModel::toggleFavorite,
            onEditPlant = viewModel::onStartEditPlant,
            onCopyPlant = viewModel::copyPlant,
            onDeletePlant = viewModel::deletePlant,
            onAddImage = viewModel::addImageToPlant,
            onRemoveImage = viewModel::removeImageFromPlant,
            onSetPrimaryImage = viewModel::setPrimaryImage,
            onSelectRelatedPlant = viewModel::onPlantSelected
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bottom_nav_bar"),
                    color = CardSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                ) {
                    NavigationBar(
                        containerColor = CardSurface,
                        contentColor = SageGreenDark,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .height(68.dp)
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentTab == item.title
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.onTabSelected(item.title) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SageGreen,
                                    selectedTextColor = SageGreen,
                                    indicatorColor = SageGreenContainer,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    "الرئيسية" -> HomeScreen(
                        allPlants = allPlants,
                        visiblePlants = visiblePlants,
                        selectedPlant = selectedPlant,
                        searchQuery = searchQuery,
                        activeFilter = selectedFilter,
                        onSearchChange = viewModel::onSearchQueryChanged,
                        onFilterSelect = viewModel::onFilterSelected,
                        onPlantSelect = viewModel::onPlantSelected,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onEditPlant = viewModel::onStartEditPlant,
                        onCopyPlant = viewModel::copyPlant,
                        onDeletePlant = viewModel::deletePlant,
                        onSeeAllClick = { viewModel.onTabSelected("النباتات") },
                        snackbarHostState = snackbarHostState
                    )

                    "النباتات" -> PlantsCatalogScreen(
                        plants = allPlants,
                        selectedPlant = selectedPlant,
                        onPlantSelect = viewModel::onPlantSelected,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onEditPlant = viewModel::onStartEditPlant,
                        onCopyPlant = viewModel::copyPlant,
                        onDeletePlant = viewModel::deletePlant,
                        onDeleteMultiplePlants = viewModel::deleteMultiplePlants,
                        onAddPlantClick = { viewModel.onShowAddDialog(true) }
                    )

                    "المفضلة" -> FavoritesScreen(
                        favoritePlants = allPlants.filter { it.isFavorite },
                        selectedPlant = selectedPlant,
                        onPlantSelect = viewModel::onPlantSelected,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onEditPlant = viewModel::onStartEditPlant,
                        onCopyPlant = viewModel::copyPlant,
                        onDeletePlant = viewModel::deletePlant,
                        onExploreCatalogClick = { viewModel.onTabSelected("النباتات") }
                    )

                    "الفصائل" -> FamiliesScreen(
                        plants = allPlants,
                        onPlantSelect = viewModel::onPlantSelected,
                        onToggleFavorite = viewModel::toggleFavorite
                    )

                    "الإحصائيات" -> StatisticsScreen(
                        stats = statistics,
                        onPlantSelect = viewModel::onPlantSelected
                    )

                    "الإعدادات" -> SettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (editingPlant != null) {
        val plant = editingPlant!!
        EditPlantDialog(
            plant = plant,
            onDismiss = viewModel::onDismissEditDialog,
            onSave = { name, english, scientific, family, usage, chemicals, note, habitat, partsUsed, preparation, precautions, growthForm ->
                viewModel.saveEditedPlant(
                    plant = plant,
                    name = name,
                    english = english,
                    scientific = scientific,
                    family = family,
                    usage = usage,
                    chemicals = chemicals,
                    note = note,
                    habitat = habitat,
                    partsUsed = partsUsed,
                    preparation = preparation,
                    precautions = precautions,
                    growthForm = growthForm
                )
            }
        )
    }

    if (showAddDialog) {
        AddPlantDialog(
            onDismiss = { viewModel.onShowAddDialog(false) },
            onAdd = { name, english, scientific, family, usage, chemicals, note, image, habitat, partsUsed, preparation, precautions, growthForm ->
                viewModel.addNewPlant(
                    name = name,
                    english = english,
                    scientific = scientific,
                    family = family,
                    usage = usage,
                    chemicals = chemicals,
                    note = note,
                    image = image,
                    habitat = habitat,
                    partsUsed = partsUsed,
                    preparation = preparation,
                    precautions = precautions,
                    growthForm = growthForm
                )
            }
        )
    }
}

