package co.edu.uqvirtual.pokedex_android_compose.shared.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PokeballIcon(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFFEE1515),
    secondaryColor: Color = Color.White,
    strokeColor: Color = Color(0xFF2D2D2D)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)
        val radius = (minOf(w, h) / 2f) - 2f

        // Mitad superior roja
        drawArc(
            color = primaryColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
        )

        // Mitad inferior blanca
        drawArc(
            color = secondaryColor,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
        )

        // Banda central
        drawLine(
            color = strokeColor,
            start = Offset(center.x - radius, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = radius * 0.18f
        )

        // Borde exterior
        drawCircle(
            color = strokeColor,
            radius = radius,
            center = center,
            style = Stroke(width = radius * 0.1f, pathEffect = PathEffect.cornerPathEffect(2f))
        )

        // Boton central (anillo)
        val buttonRadius = radius * 0.28f
        drawCircle(color = strokeColor, radius = buttonRadius, center = center)
        drawCircle(color = secondaryColor, radius = buttonRadius * 0.65f, center = center)
    }
}

@Composable
fun PokeballIconSmall(modifier: Modifier = Modifier) {
    PokeballIcon(modifier = modifier.then(Modifier))
}
