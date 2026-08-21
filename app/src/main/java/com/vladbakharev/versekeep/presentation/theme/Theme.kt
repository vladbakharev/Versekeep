package com.vladbakharev.versekeep.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val VersekeepColors =
    lightColorScheme(
        primary = Color(0xFF67503A),
        secondary = Color(0xFF765B44),
        tertiary = Color(0xFF8C4554),
        background = Color(0xFFFFF8F1),
        surface = Color(0xFFFFF8F1),
        surfaceVariant = Color(0xFFF0E2D5),
    )

private val VersekeepShapes =
    Shapes(
        extraSmall = RoundedCornerShape(16.dp),
        small = RoundedCornerShape(16.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(16.dp),
    )

@Composable
fun VersekeepTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VersekeepColors,
        shapes = VersekeepShapes,
        typography = Typography(),
        content = content,
    )
}
