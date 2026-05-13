package com.example.profletterai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Indigo600,
    onPrimary = Slate50,
    primaryContainer = Indigo100,
    onPrimaryContainer = Indigo800,
    secondary = Indigo500,
    onSecondary = Slate50,
    background = Slate50,
    onBackground = Slate900,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200,
    outlineVariant = Slate200,
    error = Red500,
    errorContainer = Red50,
    onError = androidx.compose.ui.graphics.Color.White,
    onErrorContainer = Red700
)


private val DarkColorScheme = darkColorScheme(
    primary = Indigo400,
    onPrimary = Slate900,
    primaryContainer = Indigo700,
    onPrimaryContainer = Indigo100,
    secondary = Indigo400,
    onSecondary = Slate900,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate100,
    surfaceVariant = Slate700,
    onSurfaceVariant = Slate300,
    outline = Slate600,
    outlineVariant = Slate700,
    error = Red500
)

@Composable
fun ProfletterAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),

    forceLight: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        forceLight -> LightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
