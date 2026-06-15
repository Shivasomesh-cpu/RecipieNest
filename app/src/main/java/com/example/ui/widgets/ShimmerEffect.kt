package com.example.ui.widgets

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize

fun Modifier.shimmer(): Modifier = composed {
    var size by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    // Animate the offset of the gradient
    val startOffsetX by transition.animateFloat(
        initialValue = -1.5f * size.width,
        targetValue = 1.5f * size.width,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffsetX"
    )

    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val baseColor = if (isDark) Color(0xFF2B211C) else Color(0xFFECEAE6)
    val highlightColor = if (isDark) Color(0xFF3B2E28) else Color(0xFFFAF9F6)

    val brush = remember(startOffsetX, size, isDark) {
        if (size.width == 0f) {
            Brush.linearGradient(colors = listOf(baseColor, baseColor))
        } else {
            Brush.linearGradient(
                colors = listOf(baseColor, highlightColor, baseColor),
                start = Offset(startOffsetX, 0f),
                end = Offset(startOffsetX + size.width, size.height)
            )
        }
    }

    this.onSizeChanged { size = it.toSize() }
        .background(brush)
}
