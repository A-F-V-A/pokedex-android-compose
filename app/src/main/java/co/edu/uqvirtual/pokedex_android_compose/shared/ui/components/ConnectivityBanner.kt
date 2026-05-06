package co.edu.uqvirtual.pokedex_android_compose.shared.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.uqvirtual.pokedex_android_compose.R

private val OnlineGreen = Color(0xFF2E7D32)
private val OfflineRed = Color(0xFFC62828)

@Composable
fun ConnectivityBanner(
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    val targetColor = if (isOffline) OfflineRed else OnlineGreen
    val backgroundColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 350),
        label = "bg-color"
    )

    // Punto que pulsa cuando esta en linea (asi se nota que esta vivo)
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val text = stringResource(
        id = if (isOffline) R.string.connectivity_offline else R.string.connectivity_online
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        StatusDot(
            color = Color.White,
            alphaPulse = if (isOffline) 1f else pulseAlpha
        )
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun StatusDot(color: Color, alphaPulse: Float) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(10.dp)
            .alpha(alphaPulse)
            .clip(CircleShape)
            .background(color)
    )
}
