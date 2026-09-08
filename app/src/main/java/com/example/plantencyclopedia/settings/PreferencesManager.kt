package com.example.plantencyclopedia.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class ColorPalette {
    SAGE_HERBAL, DESERT_GOLD, NATURAL_TEAL
}

enum class AppLayoutDirection {
    SYSTEM,     // يتكيف تلقائياً مع لغة النظام واتجاهه
    FORCE_RTL,  // اتجاه من اليمين لليسار (RTL - العربية)
    FORCE_LTR   // اتجاه من اليسار لليمين (LTR)
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("al_yenboot_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_COLOR_PALETTE = "color_palette"
        private const val KEY_GRID_VIEW = "grid_view"
        private const val KEY_LAYOUT_DIRECTION = "layout_direction"
    }

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _colorPalette = MutableStateFlow(loadColorPalette())
    val colorPalette: StateFlow<ColorPalette> = _colorPalette.asStateFlow()

    private val _isGridView = MutableStateFlow(prefs.getBoolean(KEY_GRID_VIEW, false))
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val _layoutDirection = MutableStateFlow(loadLayoutDirection())
    val layoutDirection: StateFlow<AppLayoutDirection> = _layoutDirection.asStateFlow()

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
                "isGridView": ${_isGridView.value}
            }
        """.trimIndent()
    }

    fun importSettingsFromJson(json: String) {
        try {
            if (json.contains("LIGHT")) setThemeMode(AppThemeMode.LIGHT)
            else if (json.contains("DARK")) setThemeMode(AppThemeMode.DARK)
            else setThemeMode(AppThemeMode.SYSTEM)

            if (json.contains("DESERT_GOLD")) setColorPalette(ColorPalette.DESERT_GOLD)
            else if (json.contains("NATURAL_TEAL")) setColorPalette(ColorPalette.NATURAL_TEAL)
            else setColorPalette(ColorPalette.SAGE_HERBAL)

            if (json.contains("FORCE_RTL")) setLayoutDirection(AppLayoutDirection.FORCE_RTL)
            else if (json.contains("FORCE_LTR")) setLayoutDirection(AppLayoutDirection.FORCE_LTR)
            else setLayoutDirection(AppLayoutDirection.SYSTEM)
        } catch (_: Exception) {}
    }
}
