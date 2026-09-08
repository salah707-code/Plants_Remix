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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.plantencyclopedia.ui.screens.HomeScreen
import com.example.plantencyclopedia.ui.screens.PlantDetailScreen
import com.example.plantencyclopedia.ui.screens.PlantsCatalogScreen
import com.example.plantencyclopedia.ui.screens.SettingsScreen
import com.example.plantencyclopedia.ui.theme.BackgroundSage
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
            PlantEncyclopediaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    PlantEncyclopediaApp(viewModel = viewModel)
                }
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

    val snackbarHostState = remember { SnackbarHostState() }

    val navItems = listOf(
        NavItem("الرئيسية", Icons.Default.Home, "nav_home"),
        NavItem("النباتات", Icons.Default.LocalFlorist, "nav_plants"),
        NavItem("الفصائل", Icons.Default.Category, "nav_families"),
        NavItem("الإعدادات", Icons.Default.Settings, "nav_settings")
    )

    if (detailPlant != null) {
        PlantDetailScreen(
            plant = detailPlant!!,
            onBack = viewModel::closePlantDetail,
            onToggleFavorite = viewModel::toggleFavorite,
            onEditPlant = viewModel::onStartEditPlant
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundSage),
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
                        modifier = Modifier.navigationBarsPadding().height(68.dp)
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
                        onSeeAllClick = { viewModel.onTabSelected("النباتات") },
                        snackbarHostState = snackbarHostState
                    )

                    "النباتات" -> PlantsCatalogScreen(
                        plants = allPlants,
                        selectedPlant = selectedPlant,
                        onPlantSelect = viewModel::onPlantSelected,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onEditPlant = viewModel::onStartEditPlant,
                        onAddPlantClick = { viewModel.onShowAddDialog(true) }
                    )

                    "الفصائل" -> FamiliesScreen(
                        plants = allPlants,
                        onPlantSelect = viewModel::onPlantSelected,
                        onToggleFavorite = viewModel::toggleFavorite
                    )

                    "الإعدادات" -> SettingsScreen()
                }
            }
        }
    }

    if (editingPlant != null) {
        val plant = editingPlant!!
        EditPlantDialog(
            plant = plant,
            onDismiss = viewModel::onDismissEditDialog,
            onSave = { name, english, scientific, family, usage, chemicals, note ->
                viewModel.saveEditedPlant(plant, name, english, scientific, family, usage, chemicals, note)
            }
        )
    }

    if (showAddDialog) {
        AddPlantDialog(
            onDismiss = { viewModel.onShowAddDialog(false) },
            onAdd = { name, english, scientific, family, usage, chemicals, note, image ->
                viewModel.addNewPlant(name, english, scientific, family, usage, chemicals, note, image)
            }
        )
    }
}
