package com.example.yra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun CircularDurationPicker(
    modifier: Modifier = Modifier,
    maxMinutes: Int = 120,
    currentMinutes: Int,
    onMinutesChanged: (Int) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .padding(16.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val angleRad = atan2(offset.y - center.y, offset.x - center.x)
                            var angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
                            angleDeg += 90f
                            if (angleDeg < 0) angleDeg += 360f
                            val newMinutes = ((angleDeg / 360f) * maxMinutes).roundToInt().coerceIn(0, maxMinutes)
                            onMinutesChanged(newMinutes)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val pos = change.position
                            val angleRad = atan2(pos.y - center.y, pos.x - center.x)
                            var angleDeg = Math.toDegrees(angleRad.toDouble()).toFloat()
                            angleDeg += 90f // shift so 0 is at the top
                            if (angleDeg < 0) angleDeg += 360f
                            
                            val newMinutes = ((angleDeg / 360f) * maxMinutes).roundToInt().coerceIn(0, maxMinutes)
                            onMinutesChanged(newMinutes)
                        }
                    )
                }
        ) {
            val strokeWidth = 16.dp.toPx()
            val radius = size.minDimension / 2f - strokeWidth / 2f
            
            // Draw background track
            drawCircle(
                color = trackColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            // Draw progress arc
            val sweepAngle = (currentMinutes.toFloat() / maxMinutes) * 360f
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        Text(
            text = "$currentMinutes min",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
