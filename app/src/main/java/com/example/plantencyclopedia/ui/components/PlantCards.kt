package com.example.plantencyclopedia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.plantencyclopedia.data.Plant
import com.example.plantencyclopedia.ui.theme.BackgroundSage
import com.example.plantencyclopedia.ui.theme.CardBorder
import com.example.plantencyclopedia.ui.theme.CardSurface
import com.example.plantencyclopedia.ui.theme.ChemicalTagBg
import com.example.plantencyclopedia.ui.theme.ChemicalTagGold
import com.example.plantencyclopedia.ui.theme.HerbGold
import com.example.plantencyclopedia.ui.theme.HerbGoldContainer
import com.example.plantencyclopedia.ui.theme.SageGreen
import com.example.plantencyclopedia.ui.theme.SageGreenContainer
import com.example.plantencyclopedia.ui.theme.SageGreenDark
import com.example.plantencyclopedia.ui.theme.SageGreenLight
import com.example.plantencyclopedia.ui.theme.TextMuted
import com.example.plantencyclopedia.ui.theme.TextPrimary
import com.example.plantencyclopedia.ui.theme.TextSecondary
import com.example.plantencyclopedia.ui.theme.VerifiedBadgeBg
import com.example.plantencyclopedia.ui.theme.VerifiedBadgeText

@Composable
fun TopBar(
    onNotificationClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SageGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✦",
                    color = SageGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column {
                Text(
                    text = "موسوعة النباتات",
                    color = SageGreenDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                )
                Text(
                    text = "مكتبة المعرفة النباتية",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .clickable { onFavoritesClick() }
                    .testTag("top_favorites_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "المفضلة",
                    tint = SageGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .clickable { onNotificationClick() }
                    .testTag("bell_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "الإشعارات",
                    tint = SageGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun GreetingRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "قاعدة البيانات النباتية",
                color = SageGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "مرحباً بك في موسوعتك",
                color = SageGreenDark,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "تصفّح السجلات النباتية بسهولة واطّلع على خواصها وموادها الفعالة.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SageGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "س",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SummaryCard(
    totalPlants: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SageGreen)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "إجمالي النباتات الموثّقة",
                    color = Color(0xFFD7E6D1),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$totalPlants",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "سجل نباتي",
                        color = Color(0xFFC9DBBF),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "↑ ١٢٪ سجلات إضافية هذا الشهر",
                    color = Color(0xFFC8DFBF),
                    fontSize = 10.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x33DCEBD4)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌿",
                    fontSize = 32.sp
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "ابحث عن نبات، الاسم العلمي، الفصيلة...",
                color = TextMuted,
                fontSize = 13.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "بحث",
                tint = SageGreen,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .testTag("search_input"),
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CardSurface,
            unfocusedContainerColor = CardSurface,
            disabledContainerColor = CardSurface,
            focusedIndicatorColor = SageGreen,
            unfocusedIndicatorColor = CardBorder
        ),
        singleLine = true
    )
}

@Composable
fun FilterChipsRow(
    filters: List<String>,
    activeFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = activeFilter == filter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) SageGreen else SageGreenContainer)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 9.dp)
                    .testTag("filter_chip_$filter"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter,
                    color = if (isSelected) Color.White else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun PlantRowItem(
    plant: Plant,
    isSelected: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onCopy: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    isMultiSelectMode: Boolean = false,
    isSelectedForBulk: Boolean = false,
    onToggleBulkSelect: (() -> Unit)? = null
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable {
                if (isMultiSelectMode) {
                    onToggleBulkSelect?.invoke()
                } else {
                    onClick()
                }
            }
            .testTag("plant_row_${plant.id}"),
        color = if (isSelectedForBulk) SageGreenContainer.copy(alpha = 0.7f) else if (isSelected) SageGreenLight.copy(alpha = 0.5f) else CardSurface,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelectedForBulk) 1.5.dp else 1.dp,
            if (isSelectedForBulk) SageGreen else if (isSelected) SageGreen else CardBorder
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isMultiSelectMode) {
                androidx.compose.material3.Checkbox(
                    checked = isSelectedForBulk,
                    onCheckedChange = { onToggleBulkSelect?.invoke() },
                    colors = androidx.compose.material3.CheckboxDefaults.colors(
                        checkedColor = SageGreen,
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.padding(end = 6.dp)
                )
            }

            AsyncImage(
                model = plant.image,
                contentDescription = plant.name,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SageGreenLight),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = plant.name,
                    color = SageGreenDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = plant.scientific,
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(HerbGoldContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = plant.family,
                            color = HerbGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(text = "•", color = TextMuted, fontSize = 10.sp)

                    Text(
                        text = plant.usage,
                        color = SageGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.testTag("fav_button_${plant.id}")
            ) {
                Icon(
                    imageVector = if (plant.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "حفظ في المفضلة",
                    tint = if (plant.isFavorite) SageGreen else TextMuted
                )
            }

            // Quick Actions Menu (Edit, Copy, Delete)
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.testTag("menu_button_${plant.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "خيارات النبتة",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    if (onEdit != null) {
                        DropdownMenuItem(
                            text = { Text("تعديل النبتة", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = SageGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                    }

                    if (onCopy != null) {
                        DropdownMenuItem(
                            text = { Text("نسخ السجل (تكرار)", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Difference,
                                    contentDescription = null,
                                    tint = SageGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onCopy()
                            }
                        )
                    }

                    if (onDelete != null) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "حذف النبتة",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "حذف النبتة",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("هل أنت متأكد من رغبتك في حذف سجل \"${plant.name}\" نهائياً من الموسوعة؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("نعم، حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantDetailCard(
    plant: Plant,
    onEditClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onViewDetailClick: (() -> Unit)? = null,
    onCopyClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .testTag("plant_detail_card"),
        color = CardSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(SageGreenLight)
                    .then(
                        if (onViewDetailClick != null) {
                            Modifier.clickable { onViewDetailClick() }
                        } else Modifier
                    )
            ) {
                AsyncImage(
                    model = plant.image,
                    contentDescription = plant.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VerifiedBadgeBg)
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = VerifiedBadgeText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "سجل موثّق",
                            color = VerifiedBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = plant.name,
                            color = SageGreenDark,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = plant.english,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SageGreenContainer)
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "ID · ${plant.id.toString().padStart(3, '0')}",
                                color = SageGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(36.dp).testTag("detail_fav_button")
                        ) {
                            Icon(
                                imageVector = if (plant.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "حفظ في المفضلة",
                                tint = if (plant.isFavorite) SageGreen else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = plant.scientific,
                    color = SageGreenDark,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "الفصيلة النباتية",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = plant.family,
                            color = SageGreenDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "نوع الاستخدام",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = plant.usage,
                            color = SageGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "المواد الكيميائية الفعالة",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    plant.chemicals.forEach { chemical ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ChemicalTagBg)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = chemical,
                                color = ChemicalTagGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ملاحظات الخصائص والوصف",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = plant.note,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (onViewDetailClick != null) {
                    Button(
                        onClick = onViewDetailClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_detail_screen_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SageGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "عرض كامل التفاصيل والصور المكبرة  ‹",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onEditClick,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("edit_plant_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SageGreenContainer,
                            contentColor = SageGreen
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تعديل",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (onCopyClick != null) {
                        OutlinedButton(
                            onClick = onCopyClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("copy_plant_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Difference,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "نسخ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (onDeleteClick != null) {
                        OutlinedButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("delete_plant_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "حذف",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog && onDeleteClick != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "حذف النبتة",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("هل أنت متأكد من رغبتك في حذف سجل \"${plant.name}\" نهائياً من الموسوعة؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("نعم، حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
