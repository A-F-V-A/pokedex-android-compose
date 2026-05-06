package co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.model.CaughtPokemon
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.ConnectivityBanner
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.PokeballIcon
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.theme.PokedexRed
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.theme.PokedexRedDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaughtPokemonScreen(
    onPokemonClick: (Int) -> Unit,
    viewModel: CaughtPokemonViewModel = hiltViewModel()
) {
    val caught by viewModel.caughtPokemon.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    var pendingRelease by remember { mutableStateOf<CaughtPokemon?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PokeballIcon(modifier = Modifier.size(28.dp))
                        Text(
                            text = "  " + stringResource(id = R.string.screen_caught) +
                                "  -  " + stringResource(id = R.string.caught_count, caught.size),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PokedexRed,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            ConnectivityBanner(isOffline = isOffline)
            Box(modifier = Modifier.fillMaxSize()) {
            if (caught.isEmpty()) {
                EmptyState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(caught, key = { it.pokemonId }) { item ->
                        CaughtCard(
                            caught = item,
                            onClick = { onPokemonClick(item.pokemonId) },
                            onRelease = { pendingRelease = item }
                        )
                    }
                }
            }
            }
        }
    }

    AnimatedVisibility(
        visible = pendingRelease != null,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        val target = pendingRelease
        if (target != null) {
            AlertDialog(
                onDismissRequest = { pendingRelease = null },
                title = { Text(text = stringResource(id = R.string.caught_release_confirm, target.name.replaceFirstChar { it.titlecase() })) },
                text = { Text(text = stringResource(id = R.string.caught_release_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.release(target.pokemonId)
                        pendingRelease = null
                    }) {
                        Text(text = stringResource(id = R.string.action_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingRelease = null }) {
                        Text(text = stringResource(id = R.string.action_cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun CaughtCard(
    caught: CaughtPokemon,
    onClick: () -> Unit,
    onRelease: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(listOf(PokedexRed, PokedexRedDark))
                )
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#%03d".format(caught.pokemonId),
                        color = Color.White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    PokeballIcon(modifier = Modifier.size(22.dp))
                }
                Text(
                    text = caught.name.replaceFirstChar { it.titlecase() },
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(caught.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
                TextButton(onClick = onRelease, modifier = Modifier.align(Alignment.End)) {
                    Text(text = stringResource(id = R.string.action_release), color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PokeballIcon(modifier = Modifier.size(120.dp))
        Text(
            text = stringResource(id = R.string.caught_empty_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.caught_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
