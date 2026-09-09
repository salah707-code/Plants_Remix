package com.example.plantencyclopedia.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK, AMOLED_BLACK
}

enum class ColorPalette {
    SAGE_HERBAL, DESERT_GOLD, NATURAL_TEAL, FOREST_EMERALD, ROYAL_LAVENDER, SUNSET_TERRACOTTA
}

enum class AppLayoutDirection {
    SYSTEM,     // يتكيف تلقائياً مع لغة النظام واتجاهه
    FORCE_RTL,  // اتجاه من اليمين لليسار (RTL - العربية)
    FORCE_LTR   // اتجاه من اليسار لليمين (LTR)
}

enum class AppFontSize(val scale: Float, val title: String) {
    SMALL(0.88f, "صغير"),
    MEDIUM(1.0f, "متوسط (افتراضي)"),
    LARGE(1.15f, "كبير"),
    EXTRA_LARGE(1.30f, "كبير جداً")
}

enum class AppFontColorStyle(val title: String) {
    DEFAULT("طبيعي متوازن"),
    HIGH_CONTRAST("تباين فائق"),
    WARM_SEPIA("بني ترابي مريح"),
    FOREST_HERB("أخضر نباتي هادئ")
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("al_yenboot_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_COLOR_PALETTE = "color_palette"
        private const val KEY_GRID_VIEW = "grid_view"
        private const val KEY_LAYOUT_DIRECTION = "layout_direction"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_FONT_COLOR_STYLE = "font_color_style"
    }

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _colorPalette = MutableStateFlow(loadColorPalette())
    val colorPalette: StateFlow<ColorPalette> = _colorPalette.asStateFlow()

    private val _isGridView = MutableStateFlow(prefs.getBoolean(KEY_GRID_VIEW, false))
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val _layoutDirection = MutableStateFlow(loadLayoutDirection())
    val layoutDirection: StateFlow<AppLayoutDirection> = _layoutDirection.asStateFlow()

    private val _fontSize = MutableStateFlow(loadFontSize())
    val fontSize: StateFlow<AppFontSize> = _fontSize.asStateFlow()

    private val _fontColorStyle = MutableStateFlow(loadFontColorStyle())
    val fontColorStyle: StateFlow<AppFontColorStyle> = _fontColorStyle.asStateFlow()

    private fun loadThemeMode(): AppThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        return try {
            AppThemeMode.valueOf(name)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    private fun loadColorPalette(): ColorPalette {
        val name = prefs.getString(KEY_COLOR_PALETTE, ColorPalette.SAGE_HERBAL.name) ?: ColorPalette.SAGE_HERBAL.name
        return try {
            ColorPalette.valueOf(name)
        } catch (_: Exception) {
            ColorPalette.SAGE_HERBAL
        }
    }

    private fun loadLayoutDirection(): AppLayoutDirection {
        val name = prefs.getString(KEY_LAYOUT_DIRECTION, AppLayoutDirection.SYSTEM.name) ?: AppLayoutDirection.SYSTEM.name
        return try {
            AppLayoutDirection.valueOf(name)
        } catch (_: Exception) {
            AppLayoutDirection.SYSTEM
        }
    }

    private fun loadFontSize(): AppFontSize {
        val name = prefs.getString(KEY_FONT_SIZE, AppFontSize.MEDIUM.name) ?: AppFontSize.MEDIUM.name
        return try {
            AppFontSize.valueOf(name)
        } catch (_: Exception) {
            AppFontSize.MEDIUM
        }
    }

    private fun loadFontColorStyle(): AppFontColorStyle {
        val name = prefs.getString(KEY_FONT_COLOR_STYLE, AppFontColorStyle.DEFAULT.name) ?: AppFontColorStyle.DEFAULT.name
        return try {
            AppFontColorStyle.valueOf(name)
        } catch (_: Exception) {
            AppFontColorStyle.DEFAULT
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setColorPalette(palette: ColorPalette) {
        prefs.edit().putString(KEY_COLOR_PALETTE, palette.name).apply()
        _colorPalette.value = palette
    }

    fun setLayoutDirection(direction: AppLayoutDirection) {
        prefs.edit().putString(KEY_LAYOUT_DIRECTION, direction.name).apply()
        _layoutDirection.value = direction
    }

    fun setFontSize(size: AppFontSize) {
        prefs.edit().putString(KEY_FONT_SIZE, size.name).apply()
        _fontSize.value = size
    }

    fun setFontColorStyle(style: AppFontColorStyle) {
        prefs.edit().putString(KEY_FONT_COLOR_STYLE, style.name).apply()
        _fontColorStyle.value = style
    }

    fun setGridView(isGrid: Boolean) {
        prefs.edit().putBoolean(KEY_GRID_VIEW, isGrid).apply()
        _isGridView.value = isGrid
    }

    fun getExportableSettingsJson(): String {
        return """
            {
                "themeMode": "${_themeMode.value.name}",
                "colorPalette": "${_colorPalette.value.name}",
                "layoutDirection": "${_layoutDirection.value.name}",
                "fontSize": "${_fontSize.value.name}",
                "fontColorStyle": "${_fontColorStyle.value.name}",
                "isGridView": ${_isGridView.value}
            }
        """.trimIndent()
    }

    fun importSettingsFromJson(json: String) {
        try {
            if (json.contains("AMOLED_BLACK")) setThemeMode(AppThemeMode.AMOLED_BLACK)
            else if (json.contains("LIGHT")) setThemeMode(AppThemeMode.LIGHT)
            else if (json.contains("DARK")) setThemeMode(AppThemeMode.DARK)
            else setThemeMode(AppThemeMode.SYSTEM)

            when {
                json.contains("FOREST_EMERALD") -> setColorPalette(ColorPalette.FOREST_EMERALD)
                json.contains("ROYAL_LAVENDER") -> setColorPalette(ColorPalette.ROYAL_LAVENDER)
                json.contains("SUNSET_TERRACOTTA") -> setColorPalette(ColorPalette.SUNSET_TERRACOTTA)
                json.contains("DESERT_GOLD") -> setColorPalette(ColorPalette.DESERT_GOLD)
                json.contains("NATURAL_TEAL") -> setColorPalette(ColorPalette.NATURAL_TEAL)
                else -> setColorPalette(ColorPalette.SAGE_HERBAL)
            }

            if (json.contains("FORCE_RTL")) setLayoutDirection(AppLayoutDirection.FORCE_RTL)
            else if (json.contains("FORCE_LTR")) setLayoutDirection(AppLayoutDirection.FORCE_LTR)
            else setLayoutDirection(AppLayoutDirection.SYSTEM)

            when {
                json.contains("EXTRA_LARGE") -> setFontSize(AppFontSize.EXTRA_LARGE)
                json.contains("LARGE") -> setFontSize(AppFontSize.LARGE)
                json.contains("SMALL") -> setFontSize(AppFontSize.SMALL)
                else -> setFontSize(AppFontSize.MEDIUM)
            }

            when {
                json.contains("HIGH_CONTRAST") -> setFontColorStyle(AppFontColorStyle.HIGH_CONTRAST)
                json.contains("WARM_SEPIA") -> setFontColorStyle(AppFontColorStyle.WARM_SEPIA)
                json.contains("FOREST_HERB") -> setFontColorStyle(AppFontColorStyle.FOREST_HERB)
                else -> setFontColorStyle(AppFontColorStyle.DEFAULT)
            }
        } catch (_: Exception) {}
    }
}
