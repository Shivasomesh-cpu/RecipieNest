package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimaryDark,
    secondary = OrangeMediumDark,
    tertiary = AmberAccentDark,
    background = WarmBackgroundDark,
    surface = SoftSurfaceDark,
    onPrimary = WarmBackgroundDark,
    onSecondary = WarmBackgroundDark,
    onTertiary = WarmBackgroundDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    secondary = OrangeMedium,
    tertiary = AmberAccent,
    background = WarmBackground,
    surface = SoftSurface,
    onPrimary = SoftSurface,
    onSecondary = SoftSurface,
    onTertiary = SoftSurface,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

@Composable
fun RecipeNestTheme(
    darkTheme: Boolean = false, // computed inside based on local theme controller
    // Dynamic color is available on Android 12+, disabled by default for solid brand consistency
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    
    // Read from the manual theme controller provider, falling back to system theme
    val controller = LocalThemeController.current
    val isDark = when (controller.themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> systemDark
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
