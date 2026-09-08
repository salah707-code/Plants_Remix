package com.example.plantencyclopedia.ui.theme

import android.app.Activity
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.example.plantencyclopedia.settings.AppLayoutDirection
import com.example.plantencyclopedia.settings.AppThemeMode
import com.example.plantencyclopedia.settings.ColorPalette
import java.util.Locale

@Composable
fun PlantEncyclopediaTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    colorPalette: ColorPalette = ColorPalette.SAGE_HERBAL,
    layoutDirectionPreference: AppLayoutDirection = AppLayoutDirection.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val (primaryColor, primaryDark, primaryLight, primaryContainer) = when (colorPalette) {
        ColorPalette.SAGE_HERBAL -> listOf(SageGreen, SageGreenDark, SageGreenLight, SageGreenContainer)
        ColorPalette.DESERT_GOLD -> listOf(DesertGold, DesertGoldDark, DesertGoldLight, DesertGoldContainer)
        ColorPalette.NATURAL_TEAL -> listOf(NaturalTeal, NaturalTealDark, NaturalTealLight, NaturalTealContainer)
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = primaryLight,
            onPrimary = primaryDark,
            primaryContainer = primaryDark,
            onPrimaryContainer = primaryLight,
            secondary = HerbGold,
            onSecondary = DarkSurface,
            secondaryContainer = DarkSurfaceVariant,
            onSecondaryContainer = HerbGold,
            background = DarkBackground,
            onBackground = DarkTextPrimary,
            surface = DarkSurface,
            onSurface = DarkTextPrimary,
            surfaceVariant = DarkSurfaceVariant,
            onSurfaceVariant = DarkTextSecondary,
            outline = DarkBorder
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = CardSurface,
            primaryContainer = primaryContainer,
            onPrimaryContainer = primaryDark,
            secondary = HerbGold,
            onSecondary = CardSurface,
            secondaryContainer = HerbGoldContainer,
            onSecondaryContainer = HerbGold,
            background = BackgroundSage,
            onBackground = TextPrimary,
            surface = CardSurface,
            onSurface = TextPrimary,
            surfaceVariant = primaryLight,
            onSurfaceVariant = TextSecondary,
            outline = CardBorder
        )
    }

    // Dynamic resolution of system language and RTL layout direction
    val configuration = LocalConfiguration.current
    val currentLocale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0] ?: Locale.getDefault()
    } else {
        @Suppress("DEPRECATION")
        configuration.locale ?: Locale.getDefault()
    }

    // Detect if current system locale is RTL or Arabic
    val isSystemRtl = TextUtils.getLayoutDirectionFromLocale(currentLocale) == View.LAYOUT_DIRECTION_RTL ||
            currentLocale.language.equals("ar", ignoreCase = true) ||
            configuration.layoutDirection == android.util.LayoutDirection.RTL

    val resolvedLayoutDirection = when (layoutDirectionPreference) {
        AppLayoutDirection.SYSTEM -> if (isSystemRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        AppLayoutDirection.FORCE_RTL -> LayoutDirection.Rtl
        AppLayoutDirection.FORCE_LTR -> LayoutDirection.Ltr
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = (if (isDark) DarkBackground else BackgroundSage).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides resolvedLayoutDirection) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PlantTypography,
            content = content
        )
    }
}


