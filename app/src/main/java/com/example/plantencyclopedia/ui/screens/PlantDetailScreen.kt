package com.example.plantencyclopedia.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantDetailScreen(
    plant: Plant,
    onBack: () -> Unit,
    onToggleFavorite: (Plant) -> Unit,
    onEditPlant: (Plant) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()
    var isImageZoomed by remember { mutableStateOf(false) }
    var showCopiedToast by remember { mutableStateOf(false) }

    val plantHabit = remember(plant.family, plant.name) {
        when {
            plant.family == "البروقية" || plant.name.contains("صبار") -> "نبات عصاري معمر دائم الخضرة"
            plant.family == "الوردية" -> "شجيرة زهرية خشبية معمرة"
            plant.family == "الشفوية" -> "عشبة عطرية معمرة دائمة الخضرة"
            plant.family == "النجمية" -> "عشبة حولية ذات قمم زهرية"
            else -> "نبات عشبي معمر عطري"
        }
    }

    val partsUsed = remember(plant.usage, plant.family, plant.name) {
        when {
            plant.name.contains("صبار") -> "الهلام الداخلي (الجل) والأوراق العصارية"
            plant.name.contains("ورد") || plant.name.contains("بابونج") -> "البتلات والقمم الزهرية الجافة أو الطازجة"
            plant.usage == "عطرية" -> "الأوراق الخضراء والزيوت العطرية المستخلصة"
            else -> "الأوراق الطازجة والمجففة والقمم المزهرة"
        }
    }

    val seasonAndClimate = remember(plant.family) {
        when (plant.family) {
            "البروقية" -> "بيئات جافة ودافئة، شمس ساطعة وتربة رملية جافة"
            "الوردية" -> "مناخ معتدل، شمس كاملة وتربة غنية بالمواد العضوية"
            "النجمية" -> "فصل الربيع وبداية الصيف، تربة خفيفة جيدة التهوية"
            else -> "مناخ البحر الأبيض المتوسط، شمس مباشرة وتربة جيدة التصريف"
        }
    }

    val pharmacologicalBenefits = remember(plant.chemicals, plant.usage) {
        when (plant.usage) {
            "علاجية" -> listOf(
                "مهدئ فعال لاضطرابات الجهاز الهضمي والقولون العصبي.",
                "يساعد على الاسترخاء وتقليل التوتر وتحسين جودة النوم.",
                "يمتلك خصائص مضادة للالتهابات والميكروبات بفضل الزيوت الطيارة.",
                "يدعم صحة الجهاز المناعي ومقاومة نزلات البرد الموسمية."
            )
            "غذائية" -> listOf(
                "يضفي نكهة عطرية غنية ويحفز إفراز الإنزيمات الهضمية.",
                "يحتوي على مضادات أكسدة طبيعية تحمي خلايا الجسم من الإجهاد التأكسدي.",
                "يستخدم كمنكه طبيعي ومطهر معوي في مختلف الأطباق التقليدية.",
                "يساعد على تحسين الشهية وتنشيط الدورة الدموية للجهاز الهضمي."
            )
            "عطرية" -> listOf(
                "زيوت طيارة ذات رائحة زكية تعمل على تصفية الذهن وتقليل الإجهاد.",
                "تستخدم في جلسات العلاج بالروائح (Aromatherapy) لتحفيز النشاط والتركيز.",
                "تطرد الحشرات طبيعياً وتنعش الأجواء المغلقة.",
                "تستخدم مستخلصاتها في العطور ومستحضرات الاستحمام الطبيعية."
            )
            "تجميلية" -> listOf(
                "ترطيب عميق للبشرة وتجديد خلايا الجلد المتضررة والمجهدة.",
                "تعزيز حيوية بصيلات الشعر وتقليل القشرة والجفاف.",
                "شد مسام البشرة ومنحها نضارة وإشراقة طبيعية وملمساً حريرياً.",
                "تقليل آثار الحروق السطحية والتهيجات الجلدية بفضل مضادات الأكسدة."
            )
            else -> listOf(
                "يحتوي على مواد كيميائية نباتية ذات تأثيرات حيوية إيجابية.",
                "استخدام تقليدي موثق عبر الأجيال في الطب الشعبي.",
                "غني بالمركبات العطرية ومضادات الأكسدة."
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSage)
            .testTag("plant_detail_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero Large Imagery Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(SageGreenLight)
                    .clickable { isImageZoomed = true }
                    .testTag("detail_hero_image_container")
            ) {
                AsyncImage(
                    model = plant.image,
                    contentDescription = plant.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("detail_hero_image"),
                    contentScale = ContentScale.Crop
                )

                // Top Gradient Scrim for action buttons legibility
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Bottom Gradient Scrim for badges legibility
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.65f)
                                )
                            )
                        )
                )

                // Top Bar with Actions inside the Hero
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Surface(
                        onClick = onBack,
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.45f),
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("detail_back_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Actions: Zoom, Share, Favorite, Edit
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Zoom Image Action
                        Surface(
                            onClick = { isImageZoomed = true },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("detail_zoom_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ZoomIn,
                                    contentDescription = "تكبير الصورة",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Share Action
                        Surface(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TITLE, plant.name)
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        """
                                            🌿 ${plant.name} (${plant.english})
                                            الاسم العلمي: ${plant.scientific}
                                            الفصيلة النباتية: ${plant.family}
                                            التصنيف والاستخدام: ${plant.usage}
                                            المواد الفعالة: ${plant.chemicals.joinToString("، ")}

                                            الوصف والخصائص:
                                            ${plant.note}

                                            — موسوعة النباتات
                                        """.trimIndent()
                                    )
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "مشاركة سجل ${plant.name}")
                                context.startActivity(shareIntent)
                            },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("detail_share_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "مشاركة",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Favorite Bookmark Toggle
                        Surface(
                            onClick = { onToggleFavorite(plant) },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            contentColor = if (plant.isFavorite) HerbGold else Color.White,
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("detail_favorite_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (plant.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "المفضلة",
                                    tint = if (plant.isFavorite) HerbGold else Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Edit Button
                        Surface(
                            onClick = { onEditPlant(plant) },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("detail_edit_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تعديل",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Badges at the bottom of the hero image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Plant Catalog ID
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "السجل رقم #${plant.id.toString().padStart(3, '0')}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Verified badge & Usage
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SageGreen.copy(alpha = 0.9f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = plant.usage,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
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
                                    modifier = Modifier.size(13.dp)
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
                }
            }

            // Body Content with Descriptive Information
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Nomenclature Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .testTag("detail_nomenclature_card"),
                    color = CardSurface,
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = plant.name,
                                    color = SageGreenDark,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    lineHeight = 32.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = plant.english,
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(HerbGoldContainer)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "فصيلة ${plant.family}",
                                    color = HerbGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(CardBorder)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFlorist,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "الاسم العلمي النباتي:",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = plant.scientific,
                                color = SageGreenDark,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Botanical Quick Specifications Grid (4 cards)
                Text(
                    text = "البطاقة التعريفية النباتية",
                    color = SageGreenDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpecMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Spa,
                        label = "الفصيلة النباتية",
                        value = plant.family
                    )
                    SpecMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        label = "تصنيف الاستخدام",
                        value = plant.usage
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpecMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Eco,
                        label = "طبيعة النمو",
                        value = plantHabit
                    )
                    SpecMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LocalFlorist,
                        label = "الأجزاء المستعملة",
                        value = partsUsed
                    )
                }

                // Active Chemical Compounds & Phytochemistry
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("detail_chemicals_card"),
                    color = CardSurface,
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ChemicalTagBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = ChemicalTagGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "المركبات الكيميائية والمواد الفعالة",
                                    color = SageGreenDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "العناصر الكيميائية المسؤولة عن الخصائص العلاجية والعطرية",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            plant.chemicals.forEach { chemical ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ChemicalTagBg)
                                        .border(1.dp, HerbGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "✦",
                                            color = ChemicalTagGold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = chemical,
                                            color = ChemicalTagGold,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Chemical details explanations
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SageGreenContainer.copy(alpha = 0.5f))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            plant.chemicals.forEach { chemical ->
                                val explanation = getChemicalExplanation(chemical)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "•",
                                        color = SageGreen,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Column {
                                        Text(
                                            text = chemical,
                                            color = SageGreenDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = explanation,
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Botanical Description & General Characteristics
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("detail_description_card"),
                    color = CardSurface,
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SageGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = SageGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "الوصف النباتي والملاحظات المورفولوجية",
                                color = SageGreenDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = plant.note,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(CardBorder)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Column {
                                Text(
                                    text = "الظروف البيئية ومناخ النمو المفضل:",
                                    color = SageGreenDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = seasonAndClimate,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                // Therapeutic Benefits & Uses Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    color = CardSurface,
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SageGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = SageGreenDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "الفوائد والخواص الوظيفية",
                                    color = SageGreenDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "التطبيقات والفوائد الموثقة في الطب والتغذية",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        pharmacologicalBenefits.forEach { benefit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(SageGreenLight)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = benefit,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Preparation and Usage Methods
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    color = CardSurface,
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "طرق التحضير والاستعمال الموصى بها",
                            color = SageGreenDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        UsageMethodRow(
                            stepNumber = "١",
                            title = "المنقوع المائي الساخن (شاي الأعشاب)",
                            instruction = "توضع ملعقة من الأوراق الجافة أو الطازجة في ماء مغلي، وتغطى لمدة ٧-١٠ دقائق لحفظ الزيوت الطيارة من التبخر قبل التصفية."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        UsageMethodRow(
                            stepNumber = "٢",
                            title = "الاستعمال الخارجي أو الموضعي",
                            instruction = "تستخدم المستخلصات المائية أو الزيوت المخففة ككمادات دافئة أو لتدليك المفاصل والعناية بمناطق البشرة والشعر."
                        )
                    }
                }

                // Safe Usage and Precautions Warning Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    color = HerbGoldContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, HerbGold.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = HerbGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "محاذير وتنبيهات الاستخدام الآمن",
                                color = HerbGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "يُنصح بالاعتدال في تناول النباتات الطبية وعدم الإفراط بالجرعات. يجب استشارة الطبيب المختص للحوامل والمرضعات وأصحاب الأمراض المزمنة قبل استخدام المستخلصات المركزة أو الزيوت العطرية النقية.",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                // Bottom Action Buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onEditPlant(plant) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("detail_bottom_edit_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SageGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تعديل بيانات وسجل النبات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val textToCopy = "${plant.name} - ${plant.scientific} (${plant.family})\n${plant.note}"
                            clipboardManager.setText(AnnotatedString(textToCopy))
                            showCopiedToast = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("detail_copy_summary_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SageGreen),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SageGreen
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (showCopiedToast) "تم نسخ ملخص النبات بنجاح ✓" else "نسخ ملخص السجل النباتي",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Full-screen Image Zoom Modal Dialog
    if (isImageZoomed) {
        Dialog(
            onDismissRequest = { isImageZoomed = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable { isImageZoomed = false }
                    .testTag("zoomed_image_dialog")
            ) {
                AsyncImage(
                    model = plant.image,
                    contentDescription = plant.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )

                // Top Header in Dialog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = plant.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = plant.scientific,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }

                    IconButton(
                        onClick = { isImageZoomed = false },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecMetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp)),
        color = CardSurface,
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SageGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
            Text(
                text = value,
                color = SageGreenDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UsageMethodRow(
    stepNumber: String,
    title: String,
    instruction: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SageGreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = SageGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = SageGreenDark,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = instruction,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 17.sp
            )
        }
    }
}

private fun getChemicalExplanation(chemical: String): String {
    return when {
        chemical.contains("روزمارينيك") -> "مضاد أكسدة فائق القوة، يحمي الخلايا العصبية ويخفف التفاعلات الالتهابية."
        chemical.contains("ثيمول") -> "مركب فينولي مطهر قوي ومضاد طبيعي للجراثيم والفطريات والميكروبات."
        chemical.contains("كارفاكرول") -> "مضاد ميكروبي فعال يعزز مناعة الجهاز التنفسي والهضمي."
        chemical.contains("منثول") -> "يمنح إحساساً بالبرودة والانتعاش، ويسكن تشنجات العضلات والمجاري التنفسية."
        chemical.contains("منثون") -> "زيت عطري منشط للدورة الدموية ومساعد على التهدئة وطرد الغازات."
        chemical.contains("كامازولين") -> "مادة زرقاء طبيعية قوية مضادة للالتهابات والتحسس والتقلصات المعوية."
        chemical.contains("بيسابولول") -> "ملطف فائق للجلد والأغشية المخاطية، يسرع التئام الجروح والتهيج."
        chemical.contains("لينالول") -> "كحول تربيني عطري يساعد على تقليل القلق وتحسين النوم وتهدئة الأعصاب."
        chemical.contains("خلات الليناليل") -> "مركب إستر مهدئ يمنح الاسترخاء العضلي ويخفف الصداع العصبي."
        chemical.contains("سيترونيلول") -> "معطر نباتي طبيعي يطرد الحشرات ويوفر حماية مضادة للأكسدة للبشرة."
        chemical.contains("جيرانيول") -> "مركب عطري طبيعي ذو فاعلية في حماية الأنسجة وتجديد النضارة."
        chemical.contains("سينول") -> "يساعد على إذابة البلغم وتوسيع القصبات الهوائية وتحفيز اليقظة."
        chemical.contains("كافور") -> "منشط موضعي للدورة الدموية ومخفف لآلام العضلات والمفاصل."
        chemical.contains("ألوين") -> "مركب أنثراكينون منقي وملطف للأنسجة ومرطب عميق."
        chemical.contains("سكاريد") -> "سكريات معقدة تحتفظ بالرطوبة وتدعم الجهاز المناعي وترميم الجلد."
        chemical.contains("فيتامين") -> "فيتامين أساسي يحارب الجذور الحرة ويغذي خلايا البشرة والشعر."
        chemical.contains("ثوجون") -> "مركب عطري طبيعي يمنح الرائحة النفاذة ويساعد على تنبيه الذاكرة باعتدال."
        else -> "مركب نباتي نشط حيوياً يسهم في الخواص الفسيولوجية والطبية للنبات."
    }
}
