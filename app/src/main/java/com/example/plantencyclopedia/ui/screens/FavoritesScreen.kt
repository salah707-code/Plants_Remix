package com.example.plantencyclopedia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantencyclopedia.data.Plant
import com.example.plantencyclopedia.domain.ArabicTextNormalizer
import com.example.plantencyclopedia.ui.components.FilterChipsRow
import com.example.plantencyclopedia.ui.components.PlantRowItem
import com.example.plantencyclopedia.ui.components.SearchBar
import com.example.plantencyclopedia.ui.theme.*

private val favoriteCategories = listOf("الكل", "علاجية", "غذائية", "عطرية", "تجميلية")

@Composable
fun FavoritesScreen(
    favoritePlants: List<Plant>,
    selectedPlant: Plant?,
    onPlantSelect: (Plant) -> Unit,
    onToggleFavorite: (Plant) -> Unit,
    onEditPlant: (Plant) -> Unit,
    onCopyPlant: (Plant) -> Unit,
    onDeletePlant: (Plant) -> Unit,
    onExploreCatalogClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }

    val filteredFavorites = remember(favoritePlants, searchQuery, selectedCategory) {
        favoritePlants.filter { plant ->
            val matchesQuery = searchQuery.isBlank() ||
                    ArabicTextNormalizer.containsNormalized(plant.name, searchQuery) ||
                    ArabicTextNormalizer.containsNormalized(plant.scientific, searchQuery) ||
                    ArabicTextNormalizer.containsNormalized(plant.english, searchQuery) ||
                    ArabicTextNormalizer.containsNormalized(plant.family, searchQuery) ||
                    plant.chemicals.any { ArabicTextNormalizer.containsNormalized(it, searchQuery) }

            val matchesCategory = selectedCategory == "الكل" || plant.usage.contains(selectedCategory)

            matchesQuery && matchesCategory
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSage)
            .testTag("favorites_screen")
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "النباتات المفضلة",
                                color = SageGreenDark,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "نباتاتك المحفوظة للوصول السريع ومراجعة الخصائص",
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
                            text = "${favoritePlants.size} محفوظة",
                            color = SageGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (favoritePlants.isNotEmpty()) {
                item {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it }
                    )
                }

                item {
                    FilterChipsRow(
                        filters = favoriteCategories,
                        activeFilter = selectedCategory,
                        onFilterSelected = { selectedCategory = it }
                    )
                }

                items(filteredFavorites, key = { it.id }) { plant ->
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

                if (filteredFavorites.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            color = CardSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "لا توجد نتائج مطابقة في المفضلة",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SageGreenDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "جرّب البحث بكلمة أخرى أو تغيير التصفية.",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp)
                            .testTag("empty_favorites_card"),
                        color = CardSurface,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(36.dp))
                                    .background(SageGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    tint = SageGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "قائمة المفضلة فارغة حالياً",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = SageGreenDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "اضغط على أيقونة الإشارة المرجعية بجانب أي نبتة في الفهرس أو الصفحة الرئيسية لحفظها هنا لسهولة الوصول إليها في أي وقت.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}
