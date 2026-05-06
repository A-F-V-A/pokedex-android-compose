package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.PokeballIcon
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.theme.PokedexRed

@Composable
fun CatchButton(
    isCaught: Boolean,
    isAnimating: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "catch-spin")
    val spinning by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isAnimating) spinning else 0f,
        label = "rotation"
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isCaught) Color.White else PokedexRed,
            contentColor = if (isCaught) PokedexRed else Color.White
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 24.dp,
            vertical = 12.dp
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PokeballIcon(modifier = Modifier
                .size(28.dp)
                .rotate(rotation))
            Text(
                text = stringResource(
                    id = if (isCaught) R.string.action_release else R.string.action_catch
                ),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CatchAnimationOverlay(visible: Boolean, modifier: Modifier = Modifier) {
    if (!visible) return
    val transition = rememberInfiniteTransition(label = "catch-overlay")
    val rotation by transition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble"
    )
    PokeballIcon(
        modifier = modifier
            .size(160.dp)
            .padding(8.dp)
            .rotate(rotation)
    )
}
