package com.example.plantencyclopedia.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantencyclopedia.data.Plant
import com.example.plantencyclopedia.domain.ArabicTextNormalizer
import com.example.plantencyclopedia.ui.components.FilterChipsRow
import com.example.plantencyclopedia.ui.components.PlantDetailCard
import com.example.plantencyclopedia.ui.components.PlantRowItem
import com.example.plantencyclopedia.ui.components.SearchBar
import com.example.plantencyclopedia.ui.theme.*

private val catalogFilters = listOf("الكل", "المفضلة", "علاجية", "غذائية", "عطرية", "تجميلية")
private val sortOptions = listOf("الأحدث", "أبجدي", "الأكثر مشاهدة", "المفضلة أولاً")

@Composable
fun PlantsCatalogScreen(
    plants: List<Plant>,
    selectedPlant: Plant?,
    onPlantSelect: (Plant) -> Unit,
    onToggleFavorite: (Plant) -> Unit,
    onEditPlant: (Plant) -> Unit,
    onCopyPlant: (Plant) -> Unit = {},
    onDeletePlant: (Plant) -> Unit = {},
    onDeleteMultiplePlants: (List<Int>) -> Unit = {},
    onAddPlantClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }
    var selectedSort by remember { mutableStateOf("الأحدث") }
    var showSortMenu by remember { mutableStateOf(false) }

    // Multi-Select Mode
    var isMultiSelectMode by remember { mutableStateOf(false) }
    var selectedPlantIds by remember { mutableStateOf(setOf<Int>()) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    val filtered = remember(plants, query, selectedFilter, selectedSort) {
        var result = plants.filter { plant ->
            val matchesQuery = query.isBlank() ||
                    ArabicTextNormalizer.containsNormalized(plant.name, query) ||
                    ArabicTextNormalizer.containsNormalized(plant.english, query) ||
                    ArabicTextNormalizer.containsNormalized(plant.scientific, query) ||
                    ArabicTextNormalizer.containsNormalized(plant.family, query) ||
                    ArabicTextNormalizer.containsNormalized(plant.note, query) ||
                    plant.chemicals.any { ArabicTextNormalizer.containsNormalized(it, query) }

            val matchesFilter = when (selectedFilter) {
                "الكل" -> true
                "المفضلة" -> plant.isFavorite
                else -> plant.usage.contains(selectedFilter)
            }
            matchesQuery && matchesFilter
        }

        result = when (selectedSort) {
            "أبجدي" -> result.sortedBy { it.name }
            "الأكثر مشاهدة" -> result.sortedByDescending { it.viewCount }
            "المفضلة أولاً" -> result.sortedWith(compareByDescending<Plant> { it.isFavorite }.thenByDescending { it.id })
            else -> result.sortedByDescending { it.id }
        }

        result
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Multi-select toggle button
                        OutlinedButton(
                            onClick = {
                                isMultiSelectMode = !isMultiSelectMode
                                if (!isMultiSelectMode) {
                                    selectedPlantIds = emptySet()
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isMultiSelectMode) SageGreen else TextSecondary
                            )
                        ) {
                            Icon(
                                imageVector = if (isMultiSelectMode) Icons.Default.Close else Icons.Default.Checklist,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isMultiSelectMode) "إلغاء التحديد" else "تحديد",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
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
            }

            // Multi-Select Action Bar Banner
            if (isMultiSelectMode) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SageGreenContainer,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SageGreen.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "تم تحديد ${selectedPlantIds.size} من ${filtered.size}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SageGreenDark
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextButton(
                                    onClick = {
                                        selectedPlantIds = if (selectedPlantIds.size == filtered.size) {
                                            emptySet()
                                        } else {
                                            filtered.map { it.id }.toSet()
                                        }
                                    }
                                ) {
                                    Text(
                                        text = if (selectedPlantIds.size == filtered.size) "إلغاء الكل" else "تحديد الكل",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SageGreen
                                    )
                                }

                                if (selectedPlantIds.isNotEmpty()) {
                                    Button(
                                        onClick = { showBulkDeleteDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "حذف (${selectedPlantIds.size})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        modifier = Modifier.weight(1f)
                    )

                    // Sort Button & Dropdown
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardSurface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "ترتيب النتائج",
                                tint = SageGreen
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            sortOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = opt,
                                            fontWeight = if (selectedSort == opt) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedSort == opt) SageGreen else SageGreenDark
                                        )
                                    },
                                    onClick = {
                                        selectedSort = opt
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                FilterChipsRow(
                    filters = catalogFilters,
                    activeFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }

            if (!isMultiSelectMode && selectedPlant != null && filtered.any { it.id == selectedPlant.id }) {
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
                    onDelete = { onDeletePlant(plant) },
                    isMultiSelectMode = isMultiSelectMode,
                    isSelectedForBulk = selectedPlantIds.contains(plant.id),
                    onToggleBulkSelect = {
                        selectedPlantIds = if (selectedPlantIds.contains(plant.id)) {
                            selectedPlantIds - plant.id
                        } else {
                            selectedPlantIds + plant.id
                        }
                    }
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

        // Floating Action Button
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

        // Bulk Delete Confirmation Dialog
        if (showBulkDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showBulkDeleteDialog = false },
                title = {
                    Text(
                        text = "تأكيد حذف النباتات المحددة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = SageGreenDark
                    )
                },
                text = {
                    Text(
                        text = "هل أنت متأكد من رغبتك في حذف ${selectedPlantIds.size} نبات نهائياً من الموسوعة؟ لا يمكن التراجع عن هذا الإجراء.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteMultiplePlants(selectedPlantIds.toList())
                            selectedPlantIds = emptySet()
                            isMultiSelectMode = false
                            showBulkDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("نعم، حذف الكل", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBulkDeleteDialog = false }) {
                        Text("إلغاء", color = SageGreen)
                    }
                }
            )
        }
    }
}
