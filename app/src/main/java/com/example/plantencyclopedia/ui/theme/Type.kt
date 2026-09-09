package com.example.plantencyclopedia.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

// Configured typography optimized for Arabic script and bidirectional layouts
// Providing balanced line-heights and padding to prevent Arabic glyph clipping
private val defaultLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None
)

fun createPlantTypography(scale: Float = 1.0f, textColor: Color = Color.Unspecified): Typography {
    val s = scale.coerceIn(0.7f, 1.6f)
    return Typography(
        displayLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = (32 * s).sp,
            lineHeight = (42 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        displayMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = (28 * s).sp,
            lineHeight = (36 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        displaySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = (24 * s).sp,
            lineHeight = (32 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = (22 * s).sp,
            lineHeight = (30 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20 * s).sp,
            lineHeight = (28 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        headlineSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (18 * s).sp,
            lineHeight = (26 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = (18 * s).sp,
            lineHeight = (26 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (16 * s).sp,
            lineHeight = (24 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * s).sp,
            lineHeight = (20 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (16 * s).sp,
            lineHeight = (24 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (14 * s).sp,
            lineHeight = (22 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = (12 * s).sp,
            lineHeight = (18 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (14 * s).sp,
            lineHeight = (20 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (12 * s).sp,
            lineHeight = (16 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = (11 * s).sp,
            lineHeight = (14 * s).sp,
            lineHeightStyle = defaultLineHeightStyle,
            color = textColor
        )
    )
}

val PlantTypography = createPlantTypography(1.0f)

