package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DoctorPrimary,
    onPrimary = Color(0xFF00202E),
    primaryContainer = Color(0xFF004D61),
    onPrimaryContainer = Color(0xFFBBE9FF),
    secondary = DoctorSecondary,
    onSecondary = Color(0xFF00382F),
    secondaryContainer = Color(0xFF005144),
    onSecondaryContainer = Color(0xFF73F8D4),
    tertiary = DoctorTertiary,
    background = DoctorBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = DoctorSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = DoctorSurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF283B63)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007799),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCBE6FF),
    onPrimaryContainer = Color(0xFF001E2B),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = Color(0xFF5E35B1),
    background = DoctorBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = DoctorSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = DoctorSurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFB0BEC5)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek dark cyber aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
