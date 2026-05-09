package com.aibrowser.app.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Color palette - Arc Browser inspired with AI vibes
object AITheme {
    // Primary colors
    val Purple80 = Color(0xFFB794F4)
    val PurpleGrey80 = Color(0xFFCCC2DC)
    val Pink80 = Color(0xFFEFB8C8)
    
    val Purple40 = Color(0xFF6650a4)
    val PurpleGrey40 = Color(0xFF625b71)
    val Pink40 = Color(0xFF7D5260)
    
    // Dark theme colors
    val DarkBackground = Color(0xFF0D0D0F)
    val DarkSurface = Color(0xFF1A1A1F)
    val DarkSurfaceVariant = Color(0xFF252530)
    val DarkCard = Color(0xFF2A2A35)
    
    // Light theme colors
    val LightBackground = Color(0xFFF8F9FA)
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurfaceVariant = Color(0xFFF0F1F3)
    val LightCard = Color(0xFFFFFFFF)
    
    // Accent colors
    val AccentBlue = Color(0xFF3B82F6)
    val AccentPurple = Color(0xFF8B5CF6)
    val AccentCyan = Color(0xFF06B6D4)
    val AccentGreen = Color(0xFF10B981)
    val AccentOrange = Color(0xFFF59E0B)
    val AccentRed = Color(0xFFEF4444)
    
    // Gradient colors
    val GradientStart = Color(0xFF667EEA)
    val GradientEnd = Color(0xFF764BA2)
    
    // Text colors
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA1A1AA)
    val TextTertiary = Color(0xFF71717A)
    
    val TextPrimaryDark = Color(0xFF18181B)
    val TextSecondaryDark = Color(0xFF52525B)
    
    // Border colors
    val BorderLight = Color(0xFFE4E4E7)
    val BorderDark = Color(0xFF3F3F46)
}

private val DarkColorScheme = darkColorScheme(
    primary = AITheme.AccentPurple,
    onPrimary = Color.White,
    primaryContainer = AITheme.DarkSurfaceVariant,
    onPrimaryContainer = AITheme.Purple80,
    secondary = AITheme.AccentCyan,
    onSecondary = Color.White,
    secondaryContainer = AITheme.DarkSurfaceVariant,
    onSecondaryContainer = AITheme.AccentCyan,
    tertiary = AITheme.AccentBlue,
    onTertiary = Color.White,
    tertiaryContainer = AITheme.DarkSurfaceVariant,
    onTertiaryContainer = AITheme.AccentBlue,
    error = AITheme.AccentRed,
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFCA5A5),
    background = AITheme.DarkBackground,
    onBackground = AITheme.TextPrimary,
    surface = AITheme.DarkSurface,
    onSurface = AITheme.TextPrimary,
    surfaceVariant = AITheme.DarkSurfaceVariant,
    onSurfaceVariant = AITheme.TextSecondary,
    outline = AITheme.BorderDark,
    outlineVariant = AITheme.DarkSurfaceVariant,
    inverseSurface = Color.White,
    inverseOnSurface = AITheme.DarkBackground,
    inversePrimary = AITheme.Purple40
)

private val LightColorScheme = lightColorScheme(
    primary = AITheme.AccentPurple,
    onPrimary = Color.White,
    primaryContainer = AITheme.Purple80,
    onPrimaryContainer = AITheme.Purple40,
    secondary = AITheme.AccentCyan,
    onSecondary = Color.White,
    secondaryContainer = AITheme.LightSurfaceVariant,
    onSecondaryContainer = AITheme.AccentCyan,
    tertiary = AITheme.AccentBlue,
    onTertiary = Color.White,
    tertiaryContainer = AITheme.LightSurfaceVariant,
    onTertiaryContainer = AITheme.AccentBlue,
    error = AITheme.AccentRed,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = AITheme.AccentRed,
    background = AITheme.LightBackground,
    onBackground = AITheme.TextPrimaryDark,
    surface = AITheme.LightSurface,
    onSurface = AITheme.TextPrimaryDark,
    surfaceVariant = AITheme.LightSurfaceVariant,
    onSurfaceVariant = AITheme.TextSecondaryDark,
    outline = AITheme.BorderLight,
    outlineVariant = AITheme.LightSurfaceVariant,
    inverseSurface = Color(0xFF18181B),
    inverseOnSurface = Color.White,
    inversePrimary = AITheme.Purple80
)

@Composable
fun AIBrowserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

val Typography = Typography(
    displayLarge = TypographyTokens.DisplayLarge,
    displayMedium = TypographyTokens.DisplayMedium,
    displaySmall = TypographyTokens.DisplaySmall,
    headlineLarge = TypographyTokens.HeadlineLarge,
    headlineMedium = TypographyTokens.HeadlineMedium,
    headlineSmall = TypographyTokens.HeadlineSmall,
    titleLarge = TypographyTokens.TitleLarge,
    titleMedium = TypographyTokens.TitleMedium,
    titleSmall = TypographyTokens.TitleSmall,
    bodyLarge = TypographyTokens.BodyLarge,
    bodyMedium = TypographyTokens.BodyMedium,
    bodySmall = TypographyTokens.BodySmall,
    labelLarge = TypographyTokens.LabelLarge,
    labelMedium = TypographyTokens.LabelMedium,
    labelSmall = TypographyTokens.LabelSmall
)

private object TypographyTokens {
    val DisplayLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Normal,
        fontSize = androidx.compose.ui.unit.TextUnit(57f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(64f, androidx.compose.ui.unit.TextUnitType.Sp),
        letterSpacing = androidx.compose.ui.unit.TextUnit(-0.25f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val DisplayMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Normal,
        fontSize = androidx.compose.ui.unit.TextUnit(45f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(52f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val DisplaySmall = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Normal,
        fontSize = androidx.compose.ui.unit.TextUnit(36f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(44f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val HeadlineLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.SemiBold,
        fontSize = androidx.compose.ui.unit.TextUnit(32f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(40f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val HeadlineMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.SemiBold,
        fontSize = androidx.compose.ui.unit.TextUnit(28f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(36f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val HeadlineSmall = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.SemiBold,
        fontSize = androidx.compose.ui.unit.TextUnit(24f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(32f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val TitleLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Medium,
        fontSize = androidx.compose.ui.unit.TextUnit(22f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(28f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val TitleMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Medium,
        fontSize = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(24f, androidx.compose.ui.unit.TextUnitType.Sp),
        letterSpacing = androidx.compose.ui.unit.TextUnit(0.15f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val TitleSmall = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Medium,
        fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp),
        letterSpacing = androidx.compose.ui.unit.TextUnit(0.1f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val BodyLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Normal,
        fontSize = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(24f, androidx.compose.ui.unit.TextUnitType.Sp),
        letterSpacing = androidx.compose.ui.unit.TextUnit(0.5f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val BodyMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Normal,
        fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp),
        letterSpacing = androidx.compose.ui.unit.TextUnit(0.25f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val BodySmall = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Normal,
        fontSize = androidx.compose.ui.unit.TextUnit(12f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp),
        letterSpacing = androidx.compose.ui.unit.TextUnit(0.4f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val LabelLarge = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Medium,
        fontSize = androidx.compose.ui.unit.TextUnit(14f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp),
        letterSpacing = androidx.compose.ui.unit.TextUnit(0.1f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val LabelMedium = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Medium,
        fontSize = androidx.compose.ui.unit.TextUnit(12f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp),
        letterSpacing = androidx.compose.ui.unit.TextUnit(0.5f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
    
    val LabelSmall = androidx.compose.ui.text.TextStyle(
        fontWeight = androidx.compose.ui.text.font.weight.Medium,
        fontSize = androidx.compose.ui.unit.TextUnit(11f, androidx.compose.ui.unit.TextUnitType.Sp),
        lineHeight = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp),
        letterSpacing = androidx.compose.ui.unit.TextUnit(0.5f, androidx.compose.ui.unit.TextUnitType.Sp)
    )
}
