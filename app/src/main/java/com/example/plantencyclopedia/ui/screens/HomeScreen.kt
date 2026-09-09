package com.example.plantencyclopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantencyclopedia.data.Plant
import com.example.plantencyclopedia.ui.components.FilterChipsRow
import com.example.plantencyclopedia.ui.components.GreetingRow
import com.example.plantencyclopedia.ui.components.PlantDetailCard
import com.example.plantencyclopedia.ui.components.PlantRowItem
import com.example.plantencyclopedia.ui.components.SearchBar
import com.example.plantencyclopedia.ui.components.SummaryCard
import com.example.plantencyclopedia.ui.components.TopBar
import com.example.plantencyclopedia.ui.theme.BackgroundSage
import com.example.plantencyclopedia.ui.theme.CardBorder
import com.example.plantencyclopedia.ui.theme.CardSurface
import com.example.plantencyclopedia.ui.theme.SageGreen
import com.example.plantencyclopedia.ui.theme.SageGreenDark
import com.example.plantencyclopedia.ui.theme.TextMuted
import com.example.plantencyclopedia.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private val filterCategories = listOf("الكل", "علاجية", "غذائية", "عطرية", "تجميلية")

@Composable
fun HomeScreen(
    allPlants: List<Plant>,
    visiblePlants: List<Plant>,
    selectedPlant: Plant?,
    searchQuery: String,
    activeFilter: String,
    onSearchChange: (String) -> Unit,
    onFilterSelect: (String) -> Unit,
    onPlantSelect: (Plant) -> Unit,
    onToggleFavorite: (Plant) -> Unit,
    onEditPlant: (Plant) -> Unit,
    onCopyPlant: (Plant) -> Unit = {},
    onDeletePlant: (Plant) -> Unit = {},
    onSeeAllClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSage)
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TopBar(
                onNotificationClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("لا توجد إشعارات جديدة حالياً")
                    }
                }
            )
        }

        item {
            GreetingRow()
        }

        item {
            SummaryCard(totalPlants = allPlants.size)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "النباتات",
                    color = SageGreenDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "عرض الكل  ‹",
                    color = SageGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onSeeAllClick() }
                        .padding(4.dp)
                        .testTag("see_all_plants_button")
                )
            }
        }

        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchChange
            )
        }

        item {
            FilterChipsRow(
                filters = filterCategories,
                activeFilter = activeFilter,
                onFilterSelected = onFilterSelect
            )
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("plants_list_card"),
                color = CardSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (visiblePlants.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "لا توجد نتائج",
                                color = SageGreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "جرّب كلمة بحث أو تصفية أخرى.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        visiblePlants.forEach { plant ->
                            PlantRowItem(
                                plant = plant,
                                isSelected = selectedPlant?.id == plant.id,
                                onClick = { onPlantSelect(plant) },
                                onToggleFavorite = { onToggleFavorite(plant) },
                                onEdit = { onEditPlant(plant) },
                                onCopy = { onCopyPlant(plant) },
                                onDelete = { onDeletePlant(plant) }
                            )
                        }
                    }
                }
            }
        }

        if (selectedPlant != null) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                PlantDetailCard(
                    plant = selectedPlant,
                    onEditClick = { onEditPlant(selectedPlant) },
                    onToggleFavorite = { onToggleFavorite(selectedPlant) },
                    onViewDetailClick = { onPlantSelect(selectedPlant) },
                    onCopyClick = { onCopyPlant(selectedPlant) },
                    onDeleteClick = { onDeletePlant(selectedPlant) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
