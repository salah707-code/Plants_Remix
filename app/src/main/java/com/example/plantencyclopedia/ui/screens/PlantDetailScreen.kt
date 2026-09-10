package com.example.plantencyclopedia.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.plantencyclopedia.images.ImageStorageManager
import com.example.plantencyclopedia.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlantDetailScreen(
    plant: Plant,
    allPlants: List<Plant> = emptyList(),
    onBack: () -> Unit,
    onToggleFavorite: (Plant) -> Unit,
    onEditPlant: (Plant) -> Unit,
    onCopyPlant: (Plant) -> Unit = {},
    onDeletePlant: (Plant) -> Unit = {},
    onAddImage: (Plant, String) -> Unit = { _, _ -> },
    onRemoveImage: (Plant, String) -> Unit = { _, _ -> },
    onSetPrimaryImage: (Plant, String) -> Unit = { _, _ -> },
    onSelectRelatedPlant: (Plant) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    var activeImageIndex by remember(plant.id, plant.images) { mutableIntStateOf(0) }
    var isImageZoomed by remember { mutableStateOf(false) }
    var showCopiedToast by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAddImageSourceDialog by remember { mutableStateOf(false) }

    // Gemini AI state
    var geminiExpanded by remember { mutableStateOf(false) }
    var geminiQuery by remember { mutableStateOf("") }
    var geminiResponse by remember { mutableStateOf<String?>(null) }
    var isGeminiLoading by remember { mutableStateOf(false) }
    var geminiStatusMessage by remember { mutableStateOf<String?>(null) }

    val allImageList = remember(plant.images, plant.image) {
        val list = plant.getAllImagesList()
        if (list.isEmpty() && plant.image.isNotBlank()) listOf(plant.image) else list
    }

    val currentDisplayImage = remember(activeImageIndex, allImageList) {
        if (allImageList.isNotEmpty() && activeImageIndex in allImageList.indices) {
            allImageList[activeImageIndex]
        } else {
            plant.image
        }
    }

    // Photo picker for adding images from gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val localPath = ImageStorageManager.saveImageFromUri(context, uri)
                    onAddImage(plant, localPath)
                } catch (_: Exception) {}
            }
        }
    }

    // Camera launcher for taking photo
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            coroutineScope.launch {
                try {
                    val localPath = ImageStorageManager.saveBitmap(context, bitmap)
                    onAddImage(plant, localPath)
                } catch (_: Exception) {}
            }
        }
    }

    val relatedPlants = remember(plant.id, plant.chemicals, plant.family, allPlants) {
        val targetChems = plant.chemicals.map { it.trim().lowercase() }.toSet()
        allPlants.filter { it.id != plant.id && (it.family == plant.family || it.chemicals.any { c -> targetChems.contains(c.trim().lowercase()) }) }.take(5)
    }


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
                    model = currentDisplayImage,
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
                            .size(42.dp)
                            .testTag("detail_back_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Actions: Zoom, Copy Plant, Share, Favorite, Edit, Delete
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Zoom Image Action
                        Surface(
                            onClick = { isImageZoomed = true },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("detail_zoom_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ZoomIn,
                                    contentDescription = "تكبير الصورة",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Duplicate/Copy Plant Action
                        Surface(
                            onClick = { onCopyPlant(plant) },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("detail_copy_plant_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Difference,
                                    contentDescription = "نسخ النبات",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
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
                                .size(38.dp)
                                .testTag("detail_share_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "مشاركة",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
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
                                .size(38.dp)
                                .testTag("detail_favorite_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (plant.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "المفضلة",
                                    tint = if (plant.isFavorite) HerbGold else Color.White,
                                    modifier = Modifier.size(18.dp)
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
                                .size(38.dp)
                                .testTag("detail_edit_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تعديل",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Delete Button
                        Surface(
                            onClick = { showDeleteConfirmDialog = true },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("detail_delete_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
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
                // Multi-Image Gallery Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    color = CardSurface,
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Collections,
                                    contentDescription = null,
                                    tint = SageGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "معرض صور النبات (${allImageList.size})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SageGreenDark
                                )
                            }

                            // Add Image button
                            Button(
                                onClick = { showAddImageSourceDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SageGreen)
                            ) {
                                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إضافة صورة", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Thumbnails row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            allImageList.forEachIndexed { idx, imgUrl ->
                                val isSelected = idx == activeImageIndex
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) SageGreen else CardBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { activeImageIndex = idx }
                                ) {
                                    AsyncImage(
                                        model = imgUrl,
                                        contentDescription = "صورة $idx",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    if (idx == 0) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .padding(vertical = 1.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("رئيسية", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Actions for selected thumbnail
                        if (allImageList.isNotEmpty() && activeImageIndex in allImageList.indices) {
                            val selectedImg = allImageList[activeImageIndex]
                            val isPrimary = activeImageIndex == 0
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!isPrimary) {
                                    TextButton(
                                        onClick = {
                                            onSetPrimaryImage(plant, selectedImg)
                                            activeImageIndex = 0
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = HerbGold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("تعيين كصورة رئيسية", fontSize = 11.sp, color = HerbGold)
                                    }
                                }

                                if (allImageList.size > 1) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(
                                        onClick = {
                                            onRemoveImage(plant, selectedImg)
                                            activeImageIndex = 0
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("حذف الصورة", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

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
                                text = if (plant.precautions.isNotBlank()) plant.precautions else "يُنصح بالاعتدال في تناول النباتات الطبية وعدم الإفراط بالجرعات. يجب استشارة الطبيب المختص للحوامل والمرضعات وأصحاب الأمراض المزمنة قبل استخدام المستخلصات المركزة أو الزيوت العطرية النقية.",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                // Botanical Ecology & Habitat Card
                if (plant.habitat.isNotBlank() || plant.partsUsed.isNotBlank() || plant.growthForm.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp)),
                        color = CardSurface,
                        border = BorderStroke(1.dp, CardBorder),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "الخصائص النباتية والموطن الطبيعي",
                                color = SageGreenDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (plant.habitat.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = SageGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "الموطن: ${plant.habitat}", fontSize = 12.sp, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            if (plant.partsUsed.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Spa, contentDescription = null, tint = SageGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "الأجزاء المستعملة: ${plant.partsUsed}", fontSize = 12.sp, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            if (plant.growthForm.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Nature, contentDescription = null, tint = SageGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "طبيعة النمو: ${plant.growthForm}", fontSize = 12.sp, color = TextPrimary)
                                }
                            }
                        }
                    }
                }

                // Related Plants Card
                if (relatedPlants.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp)),
                        color = CardSurface,
                        border = BorderStroke(1.dp, CardBorder),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.DeviceHub, contentDescription = null, tint = SageGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "نباتات ذات صلة (نفس الفصيلة أو المواد الفعالة)",
                                    color = SageGreenDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                relatedPlants.forEach { related ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectRelatedPlant(related) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = SageGreenContainer.copy(alpha = 0.5f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(text = related.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SageGreenDark)
                                                Text(text = "${related.family} • ${related.usage}", fontSize = 10.sp, color = TextSecondary)
                                            }
                                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = SageGreen, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Gemini AI Botanical Assistant Card (Optional AI Exploration)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("gemini_botanical_card"),
                    color = CardSurface,
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "المستشار النباتي الذكي (Gemini)",
                                        color = SageGreenDark,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "ميزة مساعدة اختيارية للاستفسار والتوثيق النباتي",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = { geminiExpanded = !geminiExpanded }
                            ) {
                                Icon(
                                    imageVector = if (geminiExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = SageGreen
                                )
                            }
                        }

                        // Medical Caution Notice (Mandatory Play Policy & Medical Safety Rule)
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = HerbGoldContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, HerbGold.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = HerbGold,
                                    modifier = Modifier.size(16.dp).padding(top = 1.dp)
                                )
                                Text(
                                    text = "تنبيه هام: استشارات الذكاء الاصطناعي هي ميزة بحثية وتثقيفية فقط، ولا تغني مطلقاً عن الاستشارة الطبية أو الصيدلانية المعتمدة. المحتوى غير مؤكد سريرياً.",
                                    fontSize = 10.5.sp,
                                    color = TextPrimary,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        AnimatedVisibility(visible = geminiExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                OutlinedTextField(
                                    value = geminiQuery,
                                    onValueChange = { geminiQuery = it },
                                    placeholder = { Text("اكتب سؤالك حول النبتة، فوائدها أو محاذيرها...", fontSize = 12.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("gemini_query_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = CardSurface,
                                        unfocusedContainerColor = CardSurface,
                                        focusedIndicatorColor = SageGreen,
                                        unfocusedIndicatorColor = CardBorder
                                    ),
                                    maxLines = 3
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        isGeminiLoading = true
                                        geminiStatusMessage = null
                                        coroutineScope.launch {
                                            val queryText = if (geminiQuery.isBlank()) "ما هي أهم الفوائد والتحذيرات الطبية المثبتة لهذه النبتة؟" else geminiQuery
                                            val result = com.example.plantencyclopedia.network.GeminiBotanicalService.consultBotanicalAssistant(
                                                plant = plant,
                                                userQuestion = queryText
                                            )
                                            isGeminiLoading = false
                                            when (result) {
                                                is com.example.plantencyclopedia.network.GeminiResult.Success -> {
                                                    geminiResponse = result.response
                                                    geminiStatusMessage = null
                                                }
                                                is com.example.plantencyclopedia.network.GeminiResult.Offline -> {
                                                    geminiStatusMessage = "أنت حالياً في وضع عدم الاتصال. يتم تصفح موسوعة النباتات بكامل سجلاتها بنجاح أوفلاين."
                                                }
                                                is com.example.plantencyclopedia.network.GeminiResult.Error -> {
                                                    geminiStatusMessage = result.message
                                                }
                                            }
                                        }
                                    },
                                    enabled = !isGeminiLoading,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isGeminiLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("جارٍ تحليل البيانات النباتية...", fontSize = 12.sp)
                                    } else {
                                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("طلب استشارة المستشار النباتي", fontSize = 12.sp)
                                    }
                                }

                                if (geminiStatusMessage != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = geminiStatusMessage ?: "",
                                        fontSize = 11.5.sp,
                                        color = TextSecondary,
                                        lineHeight = 17.sp
                                    )
                                }

                                if (geminiResponse != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        color = SageGreenContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, SageGreen.copy(alpha = 0.3f))
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "إفادة المستشار النباتي:",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = SageGreenDark
                                                )
                                                TextButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(geminiResponse ?: ""))
                                                    }
                                                ) {
                                                    Text("نسخ الرد", fontSize = 11.sp, color = SageGreen)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = geminiResponse ?: "",
                                                fontSize = 12.sp,
                                                color = TextPrimary,
                                                lineHeight = 19.sp
                                            )
                                        }
                                    }
                                }
                            }
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

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text("تأكيد حذف النبات", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("هل أنت متأكد من حذف نبات \"${plant.name}\" من الموسوعة؟ لا يمكن التراجع عن هذا الإجراء.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeletePlant(plant)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف نهائي")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Add Image Source Dialog (Camera vs Gallery)
    if (showAddImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showAddImageSourceDialog = false },
            title = {
                Text("إضافة صورة للنبات", fontWeight = FontWeight.Bold, color = SageGreenDark)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "اختر مصدر إضافة صورة جديدة إلى معرض هذا النبات:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Button(
                        onClick = {
                            showAddImageSourceDialog = false
                            cameraLauncher.launch(null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("التقاط صورة بالكاميرا")
                    }

                    OutlinedButton(
                        onClick = {
                            showAddImageSourceDialog = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SageGreen)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("اختيار من معرض الصور")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddImageSourceDialog = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            }
        )
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
                    model = currentDisplayImage,
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
