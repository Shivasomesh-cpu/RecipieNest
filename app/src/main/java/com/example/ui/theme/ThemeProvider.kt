package com.example.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

enum class AppThemeMode {
    LIGHT, DARK, SYSTEM
}

class ThemeController(context: Context, initialMode: AppThemeMode = AppThemeMode.SYSTEM) {
    private val prefs = context.getSharedPreferences("recipe_nest_prefs", Context.MODE_PRIVATE)
    
    var themeMode by mutableStateOf(getStoredThemeMode())
        private set

    fun setTheme(mode: AppThemeMode) {
        themeMode = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    private fun getStoredThemeMode(): AppThemeMode {
        val stored = prefs.getString("theme_mode", AppThemeMode.SYSTEM.name)
        return try {
            AppThemeMode.valueOf(stored ?: AppThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }
}

val LocalThemeController = compositionLocalOf<ThemeController> {
    error("No ThemeController provided")
}

@Composable
fun ThemeProvider(context: Context, content: @Composable () -> Unit) {
    val themeController = remember(context) { ThemeController(context) }
    CompositionLocalProvider(LocalThemeController provides themeController) {
        content()
    }
}
