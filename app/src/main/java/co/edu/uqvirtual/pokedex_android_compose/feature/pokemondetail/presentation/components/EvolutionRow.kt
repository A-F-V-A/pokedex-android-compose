package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EvolutionStage

@Composable
fun EvolutionRow(
    stages: List<EvolutionStage>,
    onStageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (stages.size < 2) {
        Text(
            text = stringResource(id = R.string.no_evolution),
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier.padding(8.dp)
        )
        return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, stage ->
            EvolutionStageItem(
                stage = stage,
                index = index,
                onClick = { onStageClick(stage.pokemonId) }
            )
            if (index < stages.lastIndex) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    val nextDetail = stages[index + 1]
                    nextDetail.minLevel?.let { lvl ->
                        Text(
                            text = stringResource(id = R.string.evolution_level, lvl),
                            style = MaterialTheme.typography.labelSmall
                        )
                    } ?: nextDetail.itemRequired?.let { item ->
                        Text(
                            text = stringResource(id = R.string.evolution_item, item),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EvolutionStageItem(
    stage: EvolutionStage,
    index: Int,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(stage.pokemonId) {
        kotlinx.coroutines.delay(index * 180L)
        visible = true
    }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.4f,
        animationSpec = tween(durationMillis = 360, easing = LinearEasing),
        label = "scale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it / 2 }),
        exit = fadeOut() + slideOutHorizontally()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .scale(scale)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(stage.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Text(
                text = stage.name.replaceFirstChar { it.titlecase() },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
