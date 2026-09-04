package com.example.yra.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neumorphic(
    shape: Shape,
    lightShadowColor: Color,
    darkShadowColor: Color,
    elevation: Dp = 6.dp,
    isPressed: Boolean = false
) = this.drawBehind {
    val elevationPx = elevation.toPx()
    val outline = shape.createOutline(size, layoutDirection, this)

    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = android.graphics.Color.TRANSPARENT

        if (!isPressed) {
            // Sombra oscura (abajo-derecha)
            frameworkPaint.setShadowLayer(
                elevationPx * 1.5f,
                elevationPx,
                elevationPx,
                darkShadowColor.toArgb()
            )
            canvas.drawOutline(outline, paint)

            // Sombra clara (arriba-izquierda)
            frameworkPaint.setShadowLayer(
                elevationPx * 1.5f,
                -elevationPx,
                -elevationPx,
                lightShadowColor.toArgb()
            )
            canvas.drawOutline(outline, paint)
        } else {
            // Estado presionado: sombras más pequeñas e invertidas para efecto hundido
            val offset = elevationPx / 2
            val blur = elevationPx
            
            // Sombra oscura (arriba-izquierda simulando luz tapada)
            frameworkPaint.setShadowLayer(
                blur,
                -offset,
                -offset,
                darkShadowColor.toArgb()
            )
            canvas.drawOutline(outline, paint)

            // Sombra clara (abajo-derecha)
            frameworkPaint.setShadowLayer(
                blur,
                offset,
                offset,
                lightShadowColor.toArgb()
            )
            canvas.drawOutline(outline, paint)
        }
    }
}

/**
 * Modificador neumórfico que detecta automáticamente el tema activo 
 * leyendo el background del color scheme actual.
 */
fun Modifier.neumorphic(
    shape: Shape,
    elevation: Dp = 6.dp,
    isPressed: Boolean = false
) = this.composed {
    // Detectamos el tema activo comprobando si el color de fondo es el oscuro
    val isDarkTheme = MaterialTheme.colorScheme.background == DarkBackgroundStart
    
    val lightShadow = if (isDarkTheme) DarkShadowLight else LightShadowLight
    val darkShadow = if (isDarkTheme) DarkShadowDark else LightShadowDark

    neumorphic(
        shape = shape,
        lightShadowColor = lightShadow,
        darkShadowColor = darkShadow,
        elevation = elevation,
        isPressed = isPressed
    )
}
