package com.example.plantencyclopedia.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PlantColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = CardSurface,
    primaryContainer = SageGreenContainer,
    onPrimaryContainer = SageGreenDark,
    secondary = HerbGold,
    onSecondary = CardSurface,
    secondaryContainer = HerbGoldContainer,
    onSecondaryContainer = HerbGold,
    background = BackgroundSage,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SageGreenLight,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun PlantEncyclopediaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = PlantColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundSage.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
