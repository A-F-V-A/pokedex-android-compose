package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.components.StatBar
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.ConnectivityBanner
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.ErrorView
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    onBack: () -> Unit,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detail = uiState.detail

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(detail?.name?.replaceFirstChar { it.titlecase() } ?: "")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ConnectivityBanner(isOffline = uiState.isOffline)
            when {
                uiState.isLoading && detail == null -> LoadingIndicator()
                detail == null && uiState.errorMessage != null -> ErrorView(
                    message = uiState.errorMessage
                        ?: stringResource(id = R.string.error_generic),
                    onRetry = viewModel::retry
                )
                detail != null -> PokemonDetailContent(detail = detail)
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(detail: PokemonDetail) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            AsyncImage(
                model = detail.imageUrl,
                contentDescription = stringResource(id = R.string.cd_pokemon_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Fit
            )
        }
        item {
            Text(
                text = "#%03d %s".format(detail.id, detail.name.replaceFirstChar { it.titlecase() }),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                detail.types.forEach { type ->
                    AssistChip(onClick = {}, label = { Text(type.replaceFirstChar { it.titlecase() }) })
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn(label = stringResource(id = R.string.detail_height), value = "${detail.height / 10.0} m")
                StatColumn(label = stringResource(id = R.string.detail_weight), value = "${detail.weight / 10.0} kg")
                StatColumn(label = stringResource(id = R.string.detail_base_xp), value = detail.baseExperience.toString())
            }
        }
        item {
            Text(
                text = stringResource(id = R.string.detail_stats),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item { StatBar(label = "HP", value = detail.stats.hp) }
        item { StatBar(label = "Attack", value = detail.stats.attack) }
        item { StatBar(label = "Defense", value = detail.stats.defense) }
        item { StatBar(label = "Sp. Attack", value = detail.stats.specialAttack) }
        item { StatBar(label = "Sp. Defense", value = detail.stats.specialDefense) }
        item { StatBar(label = "Speed", value = detail.stats.speed) }
        item {
            Text(
                text = stringResource(id = R.string.detail_abilities),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(detail.abilities) { ability ->
            Text(
                text = "- ${ability.replaceFirstChar { it.titlecase() }}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
