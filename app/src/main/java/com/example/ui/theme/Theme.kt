package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = PureWhite,
    primaryContainer = SoftMint,
    onPrimaryContainer = DeepGreen,
    secondary = ForestGreenLight,
    onSecondary = PureWhite,
    secondaryContainer = PaleGreen,
    onSecondaryContainer = DarkNavy,
    tertiary = AccentBlue,
    onTertiary = PureWhite,
    background = WarmWhite,
    onBackground = DarkNavy,
    surface = PureWhite,
    onSurface = DarkNavy,
    surfaceVariant = PaleGreen,
    onSurfaceVariant = SecondaryText,
    outline = BorderColor,
    outlineVariant = BorderLight,
    error = AccentRed,
    onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreenLight,
    onPrimary = PureWhite,
    primaryContainer = DeepGreen,
    onPrimaryContainer = PaleGreen,
    secondary = SoftMint,
    onSecondary = DarkNavy,
    background = Color(0xFF101B17),
    onBackground = Color(0xFFF1F5F3),
    surface = Color(0xFF162520),
    onSurface = Color(0xFFF1F5F3),
    surfaceVariant = Color(0xFF1E332C),
    onSurfaceVariant = Color(0xFFB4C8BE),
    outline = Color(0xFF2C4A3E),
    error = AccentRed
)

@Composable
fun SaathTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
