package com.vladbakharev.versekeep.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.vladbakharev.versekeep.R

val VersekeepGray = Color(0xFF494D4F)

private val Literata = FontFamily(Font(R.font.literata))

private val DefaultTypography = Typography()

private val VersekeepTypography =
    with(DefaultTypography) {
        Typography(
            displayLarge = displayLarge.copy(fontFamily = Literata),
            displayMedium = displayMedium.copy(fontFamily = Literata),
            displaySmall = displaySmall.copy(fontFamily = Literata),
            headlineLarge = headlineLarge.copy(fontFamily = Literata),
            headlineMedium = headlineMedium.copy(fontFamily = Literata),
            headlineSmall = headlineSmall.copy(fontFamily = Literata),
            titleLarge = titleLarge.copy(fontFamily = Literata),
            titleMedium = titleMedium.copy(fontFamily = Literata),
            titleSmall = titleSmall.copy(fontFamily = Literata),
            bodyLarge = bodyLarge.copy(fontFamily = Literata),
            bodyMedium = bodyMedium.copy(fontFamily = Literata),
            bodySmall = bodySmall.copy(fontFamily = Literata),
            labelLarge = labelLarge.copy(fontFamily = Literata),
            labelMedium = labelMedium.copy(fontFamily = Literata),
            labelSmall = labelSmall.copy(fontFamily = Literata),
        )
    }

private val VersekeepColors =
    lightColorScheme(
        primary = Color.Black,
        onPrimary = Color.White,
        primaryContainer = Color.Black,
        onPrimaryContainer = Color.White,
        inversePrimary = Color.White,
        secondary = Color.Black,
        onSecondary = Color.White,
        secondaryContainer = Color.Black,
        onSecondaryContainer = Color.White,
        tertiary = Color.Black,
        onTertiary = Color.White,
        tertiaryContainer = Color.Black,
        onTertiaryContainer = Color.White,
        background = Color.White,
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color.Black,
        surfaceVariant = Color.White,
        onSurfaceVariant = Color.Black,
        surfaceTint = Color.Black,
        inverseSurface = Color.Black,
        inverseOnSurface = Color.White,
        error = Color.Black,
        onError = Color.White,
        errorContainer = Color.Black,
        onErrorContainer = Color.White,
        outline = Color.Black,
        outlineVariant = Color.Black,
        scrim = Color.Black,
        surfaceBright = Color.White,
        surfaceDim = Color.White,
        surfaceContainer = Color.White,
        surfaceContainerHigh = Color.White,
        surfaceContainerHighest = Color.White,
        surfaceContainerLow = Color.White,
        surfaceContainerLowest = Color.White,
    )

private val VersekeepShapes =
    Shapes(
        extraSmall = RoundedCornerShape(32.dp),
        small = RoundedCornerShape(32.dp),
        medium = RoundedCornerShape(32.dp),
        large = RoundedCornerShape(32.dp),
        extraLarge = RoundedCornerShape(32.dp),
    )

@Composable
fun VersekeepTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VersekeepColors,
        shapes = VersekeepShapes,
        typography = VersekeepTypography,
        content = content,
    )
}
