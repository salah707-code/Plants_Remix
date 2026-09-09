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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantencyclopedia.data.Plant
import com.example.plantencyclopedia.ui.components.FilterChipsRow
import com.example.plantencyclopedia.ui.components.PlantDetailCard
import com.example.plantencyclopedia.ui.components.PlantRowItem
import com.example.plantencyclopedia.ui.components.SearchBar
import com.example.plantencyclopedia.ui.theme.BackgroundSage
import com.example.plantencyclopedia.ui.theme.CardBorder
import com.example.plantencyclopedia.ui.theme.CardSurface
import com.example.plantencyclopedia.ui.theme.SageGreen
import com.example.plantencyclopedia.ui.theme.SageGreenContainer
import com.example.plantencyclopedia.ui.theme.SageGreenDark
import com.example.plantencyclopedia.ui.theme.TextMuted
import com.example.plantencyclopedia.ui.theme.TextSecondary

private val catalogFilters = listOf("الكل", "المفضلة", "علاجية", "غذائية", "عطرية", "تجميلية")

@Composable
fun PlantsCatalogScreen(
    plants: List<Plant>,
    selectedPlant: Plant?,
    onPlantSelect: (Plant) -> Unit,
    onToggleFavorite: (Plant) -> Unit,
    onEditPlant: (Plant) -> Unit,
    onCopyPlant: (Plant) -> Unit = {},
    onDeletePlant: (Plant) -> Unit = {},
    onAddPlantClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }

    val filtered = plants.filter { plant ->
        val matchesQuery = query.isBlank() ||
                plant.name.contains(query, ignoreCase = true) ||
                plant.english.contains(query, ignoreCase = true) ||
                plant.scientific.contains(query, ignoreCase = true) ||
                plant.family.contains(query, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "الكل" -> true
            "المفضلة" -> plant.isFavorite
            else -> plant.usage == selectedFilter
        }
        matchesQuery && matchesFilter
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSage)
            .testTag("catalog_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "فهرس النباتات",
                            color = SageGreenDark,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "تصفح جميع النباتات المصنفة والمسجلة في الدليل",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SageGreenContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${filtered.size} نبات",
                            color = SageGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it }
                )
            }

            item {
                FilterChipsRow(
                    filters = catalogFilters,
                    activeFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }

            if (selectedPlant != null && filtered.any { it.id == selectedPlant.id }) {
                item {
                    PlantDetailCard(
                        plant = selectedPlant,
                        onEditClick = { onEditPlant(selectedPlant) },
                        onToggleFavorite = { onToggleFavorite(selectedPlant) },
                        onViewDetailClick = { onPlantSelect(selectedPlant) },
                        onCopyClick = { onCopyPlant(selectedPlant) },
                        onDeleteClick = { onDeletePlant(selectedPlant) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(filtered, key = { it.id }) { plant ->
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

            if (filtered.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد نباتات مطابقة",
                            color = SageGreenDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "حاول البحث بكلمة أخرى أو تغيير تصنيف الفلترة.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        FloatingActionButton(
            onClick = onAddPlantClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 90.dp, start = 20.dp)
                .testTag("add_plant_fab"),
            containerColor = SageGreen,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة نبات"
                )
                Text(
                    text = "إضافة نبات",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
