package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ImmersiveDarkColorScheme = darkColorScheme(
    primary = AccentLavender,
    onPrimary = AccentDeepPurple,
    secondary = PurpleGrey80,
    background = ImmersiveBg,
    onBackground = ImmersiveTextMain,
    surface = ImmersiveHeader,
    onSurface = ImmersiveTextMain,
    outline = ImmersiveBorder,
    surfaceVariant = AccentDarkCard,
    onSurfaceVariant = ImmersiveTextSec
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Immerse theme for true cinematic experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ImmersiveDarkColorScheme,
        typography = Typography,
        content = content
    )
}
