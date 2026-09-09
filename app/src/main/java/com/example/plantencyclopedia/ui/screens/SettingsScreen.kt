package com.example.plantencyclopedia.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantencyclopedia.settings.AppFontColorStyle
import com.example.plantencyclopedia.settings.AppFontSize
import com.example.plantencyclopedia.settings.AppLayoutDirection
import com.example.plantencyclopedia.settings.AppThemeMode
import com.example.plantencyclopedia.settings.ColorPalette
import com.example.plantencyclopedia.ui.PlantViewModel
import com.example.plantencyclopedia.ui.components.ExcelImportDialog
import com.example.plantencyclopedia.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: PlantViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.preferencesManager.themeMode.collectAsState()
    val colorPalette by viewModel.preferencesManager.colorPalette.collectAsState()
    val fontSize by viewModel.preferencesManager.fontSize.collectAsState()
    val fontColorStyle by viewModel.preferencesManager.fontColorStyle.collectAsState()
    val layoutDirectionPref by viewModel.layoutDirectionPreference.collectAsState()
    val isSecurityEnabled = viewModel.securityManager.isSecurityEnabled()
    val importPreview by viewModel.importPreview.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinConfirm by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var showPasteExcelDialog by remember { mutableStateOf(false) }
    var pastedExcelText by remember { mutableStateOf("") }

    // ActivityResult Launchers for Backup & Excel
    val exportExcelLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.exportToExcel(uri) {}
        }
    }

    val importExcelLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.previewExcelImport(uri)
        }
    }

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.createFullBackup(uri) {}
        }
    }

    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.restoreFullBackup(uri) {}
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "الإعدادات",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "الإعدادات والنسخ الاحتياطي",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "الينبوت V2 — تحكم كامل في المظهر والبيانات والأمان",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 1: Appearance & Theme
        item {
            SettingsCategoryHeader(title = "المظهر والألوان وحجم الخط")
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "وضع الإضاءة والثيم:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ThemeOptionChip(
                            label = "تلقائي",
                            selected = themeMode == AppThemeMode.SYSTEM,
                            icon = Icons.Default.BrightnessAuto,
                            onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "فاتح",
                            selected = themeMode == AppThemeMode.LIGHT,
                            icon = Icons.Default.LightMode,
                            onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "داكن",
                            selected = themeMode == AppThemeMode.DARK,
                            icon = Icons.Default.DarkMode,
                            onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "AMOLED",
                            selected = themeMode == AppThemeMode.AMOLED_BLACK,
                            icon = Icons.Default.Brightness2,
                            onClick = { viewModel.setThemeMode(AppThemeMode.AMOLED_BLACK) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "نظام ألوان الموسوعة (6 باقات ألوان):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Palette Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaletteOptionChip(
                            label = "عشبي ينبوت",
                            color = SageGreen,
                            selected = colorPalette == ColorPalette.SAGE_HERBAL,
                            onClick = { viewModel.setColorPalette(ColorPalette.SAGE_HERBAL) },
                            modifier = Modifier.weight(1f)
                        )
                        PaletteOptionChip(
                            label = "ذهبي صحراوي",
                            color = DesertGold,
                            selected = colorPalette == ColorPalette.DESERT_GOLD,
                            onClick = { viewModel.setColorPalette(ColorPalette.DESERT_GOLD) },
                            modifier = Modifier.weight(1f)
                        )
                        PaletteOptionChip(
                            label = "أزرق مائي",
                            color = NaturalTeal,
                            selected = colorPalette == ColorPalette.NATURAL_TEAL,
                            onClick = { viewModel.setColorPalette(ColorPalette.NATURAL_TEAL) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Palette Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaletteOptionChip(
                            label = "زمرد الغابة",
                            color = ForestEmerald,
                            selected = colorPalette == ColorPalette.FOREST_EMERALD,
                            onClick = { viewModel.setColorPalette(ColorPalette.FOREST_EMERALD) },
                            modifier = Modifier.weight(1f)
                        )
                        PaletteOptionChip(
                            label = "لافندر ملكي",
                            color = RoyalLavender,
                            selected = colorPalette == ColorPalette.ROYAL_LAVENDER,
                            onClick = { viewModel.setColorPalette(ColorPalette.ROYAL_LAVENDER) },
                            modifier = Modifier.weight(1f)
                        )
                        PaletteOptionChip(
                            label = "طين الغروب",
                            color = SunsetTerracotta,
                            selected = colorPalette == ColorPalette.SUNSET_TERRACOTTA,
                            onClick = { viewModel.setColorPalette(ColorPalette.SUNSET_TERRACOTTA) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Font Size Selection
                    Text(
                        text = "اختيار حجم الخط:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ThemeOptionChip(
                            label = "صغير",
                            selected = fontSize == AppFontSize.SMALL,
                            icon = Icons.Default.FormatSize,
                            onClick = { viewModel.setFontSize(AppFontSize.SMALL) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "متوسط",
                            selected = fontSize == AppFontSize.MEDIUM,
                            icon = Icons.Default.FormatSize,
                            onClick = { viewModel.setFontSize(AppFontSize.MEDIUM) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "كبير",
                            selected = fontSize == AppFontSize.LARGE,
                            icon = Icons.Default.FormatSize,
                            onClick = { viewModel.setFontSize(AppFontSize.LARGE) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "كبير جداً",
                            selected = fontSize == AppFontSize.EXTRA_LARGE,
                            icon = Icons.Default.FormatSize,
                            onClick = { viewModel.setFontSize(AppFontSize.EXTRA_LARGE) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Font Color Style Selection
                    Text(
                        text = "اختيار لون ونمط نصوص الخط:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaletteOptionChip(
                            label = "متوازن",
                            color = Color(0xFF1B2A1E),
                            selected = fontColorStyle == AppFontColorStyle.DEFAULT,
                            onClick = { viewModel.setFontColorStyle(AppFontColorStyle.DEFAULT) },
                            modifier = Modifier.weight(1f)
                        )
                        PaletteOptionChip(
                            label = "تباين فائق",
                            color = Color.Black,
                            selected = fontColorStyle == AppFontColorStyle.HIGH_CONTRAST,
                            onClick = { viewModel.setFontColorStyle(AppFontColorStyle.HIGH_CONTRAST) },
                            modifier = Modifier.weight(1f)
                        )
                        PaletteOptionChip(
                            label = "ترابي دافئ",
                            color = Color(0xFF3E2723),
                            selected = fontColorStyle == AppFontColorStyle.WARM_SEPIA,
                            onClick = { viewModel.setFontColorStyle(AppFontColorStyle.WARM_SEPIA) },
                            modifier = Modifier.weight(1f)
                        )
                        PaletteOptionChip(
                            label = "أخضر نباتي",
                            color = Color(0xFF1B5E20),
                            selected = fontColorStyle == AppFontColorStyle.FOREST_HERB,
                            onClick = { viewModel.setFontColorStyle(AppFontColorStyle.FOREST_HERB) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Typography Preview Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "معاينة الخط الحيّة:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "نبات الينبوت (Prosopis farcta) — عشبة برية طبية غنية بالفلافونويدات ومضادات الأكسدة.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "اتجاه وتنسيق الواجهة واللغة:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "يتكيف التطبيق تلقائياً مع لغة النظام (RTL للغة العربية والعبرية)، أو يمكنك تثبيت الاتجاه",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOptionChip(
                            label = "تلقائي (النظام)",
                            selected = layoutDirectionPref == AppLayoutDirection.SYSTEM,
                            icon = Icons.Default.Language,
                            onClick = { viewModel.setLayoutDirection(AppLayoutDirection.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "العربية (RTL)",
                            selected = layoutDirectionPref == AppLayoutDirection.FORCE_RTL,
                            icon = Icons.Default.FormatTextdirectionRToL,
                            onClick = { viewModel.setLayoutDirection(AppLayoutDirection.FORCE_RTL) },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOptionChip(
                            label = "English (LTR)",
                            selected = layoutDirectionPref == AppLayoutDirection.FORCE_LTR,
                            icon = Icons.Default.FormatTextdirectionLToR,
                            onClick = { viewModel.setLayoutDirection(AppLayoutDirection.FORCE_LTR) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section 2: Security & App Lock
        item {
            SettingsCategoryHeader(title = "الأمان وقفل التطبيق")
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "حماية التطبيق برمز PIN",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isSecurityEnabled) "القفل مفعل حالياً ومحمي بتشفير SHA-256" else "حماية بيانات الموسوعة من الوصول غير المصرح به",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isSecurityEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    showPinDialog = true
                                } else {
                                    viewModel.disableAppPin()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    if (isSecurityEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showPinDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Password, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تغيير PIN", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.lockApp() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("قفل الآن", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section 3: Full Backup & Restore
        item {
            SettingsCategoryHeader(title = "النسخ الاحتياطي الشامل (ZIP)")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "يشمل قاعدة البيانات الكاملة، جميع الصور المحلية المخزنة، والإعدادات.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionRowItem(
                        title = "إنشاء نسخة احتياطية كاملة",
                        subtitle = "تصدير ملف ZIP يحتوي على النباتات والصور والإعدادات",
                        icon = Icons.Default.CloudUpload,
                        buttonLabel = "تصدير النسخة",
                        onClick = {
                            val fileName = "al_yenboot_backup_${System.currentTimeMillis()}.zip"
                            createBackupLauncher.launch(fileName)
                        }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    ActionRowItem(
                        title = "استعادة نسخة احتياطية كاملة",
                        subtitle = "استرجاع النباتات والصور من ملف ZIP سابق بأمان",
                        icon = Icons.Default.CloudDownload,
                        buttonLabel = "استعادة النسخة",
                        onClick = {
                            restoreBackupLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*"))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section 4: Excel Export & Import
        item {
            SettingsCategoryHeader(title = "تصدير واستيراد جداول Excel")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "متوافق 100% مع Microsoft Excel باللغة العربية بترميز UTF-8 ومطابقة الأعمدة.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionRowItem(
                        title = "تصدير إلى ملف Excel",
                        subtitle = "حفظ جميع النباتات في جدول إلكتروني بالأسماء والتصنيفات",
                        icon = Icons.Default.TableChart,
                        buttonLabel = "تصدير Excel",
                        onClick = {
                            val fileName = "plants_encyclopedia_${System.currentTimeMillis()}.csv"
                            exportExcelLauncher.launch(fileName)
                        }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    ActionRowItem(
                        title = "استيراد من ملف Excel (.xlsx أو .csv)",
                        subtitle = "معاينة الملف ومطابقة الأعمدة والتحقق قبل الاستيراد",
                        icon = Icons.Default.FileDownload,
                        buttonLabel = "استيراد ملف",
                        onClick = {
                            importExcelLauncher.launch(
                                arrayOf(
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                    "text/csv",
                                    "text/comma-separated-values",
                                    "*/*"
                                )
                            )
                        }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    ActionRowItem(
                        title = "استيراد من نص إكسل منسوخ (لصق مباشر)",
                        subtitle = "لصق خلايا أو أسطر منسوخة مباشرة من برنامج Excel",
                        icon = Icons.Default.ContentPaste,
                        buttonLabel = "لصق ومعاينة",
                        onClick = {
                            pastedExcelText = ""
                            showPasteExcelDialog = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // App Info Footer
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الينبوت — Plants Remix V2",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "موسوعة نباتية ذكية تعمل محلياً بالكامل (Offline-First)\nقاعدة بيانات Room + SQLite مشفرة ومؤمنة",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }

    // Set Password / PIN Dialog
    if (showPinDialog) {
        var isPasswordVisible by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                pinInput = ""
                pinConfirm = ""
                pinError = null
            },
            title = {
                Text("تعيين كلمة المرور / رمز PIN", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("أدخل كلمة مرور أو رمز PIN (4 أرقام أو حروف على الأقل) لحماية التطبيق:", fontSize = 13.sp)
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("كلمة المرور الجديدة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pinConfirm,
                        onValueChange = { pinConfirm = it },
                        label = { Text("تأكيد كلمة المرور") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (pinError != null) {
                        Text(text = pinError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length < 4) {
                            pinError = "يجب ألا تقل كلمة المرور عن 4 خانات"
                        } else if (pinInput != pinConfirm) {
                            pinError = "كلمتا المرور غير متطابقتين"
                        } else {
                            viewModel.setAppPin(pinInput)
                            showPinDialog = false
                            pinInput = ""
                            pinConfirm = ""
                            pinError = null
                        }
                    }
                ) {
                    Text("حفظ وتفعيل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Direct Excel Paste Dialog
    if (showPasteExcelDialog) {
        AlertDialog(
            onDismissRequest = {
                showPasteExcelDialog = false
                pastedExcelText = ""
            },
            title = {
                Text("استيراد من نص إكسل منسوخ", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "الصق الأسطر والخلايا المنسوخة من جدول Excel (مفصولة بفواصل أو فواصل جدولية Tabs):",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = pastedExcelText,
                        onValueChange = { pastedExcelText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("الاسم بالعربية,الاسم العلمي,الفصيلة,الاستخدام\nينبوت,Prosopis farcta,البقولية,علاجي") },
                        maxLines = 8
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val text = pastedExcelText.trim()
                        if (text.isNotEmpty()) {
                            showPasteExcelDialog = false
                            viewModel.previewExcelTextImport(text)
                            pastedExcelText = ""
                        }
                    },
                    enabled = pastedExcelText.isNotBlank()
                ) {
                    Text("معاينة البيانات")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPasteExcelDialog = false
                    pastedExcelText = ""
                }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (importPreview != null) {
        ExcelImportDialog(
            preview = importPreview!!,
            onDismiss = viewModel::dismissImportPreview,
            onConfirmImport = viewModel::executeExcelImport
        )
    }
}

@Composable
private fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ThemeOptionChip(
    label: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PaletteOptionChip(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionRowItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    buttonLabel: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(buttonLabel, fontSize = 11.sp)
        }
    }
}
