package com.firmino.fitio.view.theme.ui

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class Theme {
    DYNAMIC_NIGHT, DYNAMIC_LIGHT, CATPPUCCIN_NIGHT, CATPPUCCIN_LIGHT, MATERIAL_NIGHT, MATERIAL_LIGHT
}

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8caaee),
    onPrimary = Color(0xFF303446),
    primaryContainer = Color(0xFF414559),
    onPrimaryContainer = Color(0xFFa5adce),

    secondary = Color(0xFFbabbf1),
    onSecondary = Color(0xFFc6d0f5),
    secondaryContainer = Color(0xFF51576d),
    onSecondaryContainer = Color(0xFFb5bfe2),

    tertiary = Color(0xFFeebebe),
    onTertiary = Color(0xFFc6d0f5),
    tertiaryContainer = Color(0xFF626880),
    onTertiaryContainer = Color(0xFFc6d0f5),

    background = Color(0xFF292c3c),
    onBackground = Color(0xFFc6d0f5),

    surface = Color(0xFF303446),
    onSurface = Color(0xFFc6d0f5),
    surfaceVariant = Color(0xFF414559),
    onSurfaceVariant = Color(0xFFc6d0f5),
    surfaceTint = Color(0xFF303446),

    outline = Color(0xFF949cbb),
    outlineVariant = Color(0xFFc6d0f5),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1e66f5),
    onPrimary = Color(0xFFeff1f5),
    primaryContainer = Color(0xFFccd0da),
    onPrimaryContainer = Color(0xFF6c6f85),

    secondary = Color(0xFF7287fd),
    onSecondary = Color(0xFF4c4f69),
    secondaryContainer = Color(0xFFccd0da),
    onSecondaryContainer = Color(0xFF5c5f77),

    tertiary = Color(0xFFdd7878),
    onTertiary = Color(0xFF4c4f69),
    tertiaryContainer = Color(0xFFacb0be),
    onTertiaryContainer = Color(0xFF4c4f69),

    background = Color(0xFFe6e9ef),
    onBackground = Color(0xFF4c4f69),

    surface = Color(0xFFeff1f5),
    onSurface = Color(0xFF4c4f69),
    surfaceVariant = Color(0xFFccd0da),
    onSurfaceVariant = Color(0xFF4c4f69),
    surfaceTint = Color(0xFFeff1f5),

    outline = Color(0xFF7c7f93),
    outlineVariant = Color(0xFF4c4f69),

    )

@Composable
fun FitIOTheme(theme: Theme, content: @Composable () -> Unit) {
    val colorScheme = when {
        theme == Theme.DYNAMIC_NIGHT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }

        theme == Theme.DYNAMIC_LIGHT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }
        theme == Theme.CATPPUCCIN_NIGHT -> DarkColorScheme
        theme == Theme.MATERIAL_LIGHT -> lightColorScheme()
        theme == Theme.MATERIAL_NIGHT -> darkColorScheme()
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primaryContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = when(theme){
                Theme.DYNAMIC_NIGHT -> true
                Theme.DYNAMIC_LIGHT -> false
                Theme.CATPPUCCIN_NIGHT -> true
                Theme.CATPPUCCIN_LIGHT -> false
                Theme.MATERIAL_NIGHT -> true
                Theme.MATERIAL_LIGHT -> false
            }
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}