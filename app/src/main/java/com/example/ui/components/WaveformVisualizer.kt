package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicGoldGlow

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    progress: Float = 0f,
    modifier: Modifier = Modifier,
    barCount: Int = 36
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformAnimation")
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = (width / barCount) * 0.55f
        val gap = (width - (barWidth * barCount)) / (barCount - 1)

        val gradient = Brush.verticalGradient(
            colors = listOf(CinematicGoldGlow, CinematicGold, Color(0xFFB45309))
        )
        val mutedBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF3B4459), Color(0xFF232A3B))
        )

        for (i in 0 until barCount) {
            val barX = i * (barWidth + gap)
            val normalizedIndex = i.toFloat() / barCount

            // Height curve
            val baseScale = kotlin.math.sin(normalizedIndex * 3.14159f).coerceAtLeast(0.18f)
            val animatedFactor = if (isPlaying) {
                val wave = kotlin.math.sin(normalizedIndex * 9f + animPhase) * 0.35f +
                        kotlin.math.cos(normalizedIndex * 15f - animPhase * 1.5f) * 0.25f
                (baseScale + wave).coerceIn(0.15f, 1.0f)
            } else {
                baseScale * 0.45f
            }

            val barHeight = (height * animatedFactor).coerceIn(4f, height)
            val barY = (height - barHeight) / 2f

            // Past vs future progress coloring
            val isPassed = (i.toFloat() / barCount) <= progress
            val brush = if (isPlaying || isPassed) gradient else mutedBrush

            drawRoundRect(
                brush = brush,
                topLeft = Offset(barX, barY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
