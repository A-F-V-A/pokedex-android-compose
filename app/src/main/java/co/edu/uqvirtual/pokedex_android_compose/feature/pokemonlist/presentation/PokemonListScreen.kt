package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation.components.FilterBar
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.ConnectivityBanner
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.ErrorView
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.LoadingIndicator
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.PokemonCard
import java.io.IOException

private fun Throwable.isLikelyNetworkError(): Boolean {
    if (this is IOException) return true
    val msg = message.orEmpty()
    return msg.contains("Unable to resolve host", ignoreCase = true) ||
        msg.contains("UnknownHostException", ignoreCase = true) ||
        msg.contains("No address associated", ignoreCase = true) ||
        msg.contains("timeout", ignoreCase = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    onPokemonClick: (Int) -> Unit,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredResults by viewModel.filteredResults.collectAsStateWithLifecycle()
    val pagingItems = viewModel.pagedPokemon.collectAsLazyPagingItems()

    // Considera offline si el OS reporta sin red O si la API esta fallando con
    // error de red (DNS, timeout, IO). Cubre el caso del emulador con DNS roto.
    val refreshError = (pagingItems.loadState.refresh as? LoadState.Error)?.error
    val appendError = (pagingItems.loadState.append as? LoadState.Error)?.error
    val networkProblem = listOfNotNull(refreshError, appendError).any { it.isLikelyNetworkError() }
    val effectiveOffline = uiState.isOffline || networkProblem

    // Auto-retry cuando vuelve la conexion y no hay datos
    LaunchedEffect(uiState.isOffline) {
        if (!uiState.isOffline && pagingItems.itemCount == 0 &&
            pagingItems.loadState.refresh is LoadState.Error) {
            pagingItems.retry()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.screen_pokemon_list)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ConnectivityBanner(isOffline = effectiveOffline)
            FilterBar(
                activeFilter = uiState.activeFilter,
                types = uiState.types,
                generations = uiState.generations,
                onTypeSelected = viewModel::selectTypeFilter,
                onGenerationSelected = viewModel::selectGenerationFilter,
                onClearFilters = viewModel::clearFilters
            )
            when (uiState.activeFilter) {
                ActiveFilter.None -> PagedPokemonGrid(
                    items = pagingItems,
                    onPokemonClick = onPokemonClick
                )
                else -> FilteredPokemonGrid(
                    pokemon = filteredResults,
                    isLoading = uiState.isLoadingFilter,
                    onPokemonClick = onPokemonClick
                )
            }
        }
    }
}

@Composable
private fun PagedPokemonGrid(
    items: LazyPagingItems<Pokemon>,
    onPokemonClick: (Int) -> Unit
) {
    val gridState = rememberLazyGridState()
    val refreshState = items.loadState.refresh
    val appendState = items.loadState.append

    when {
        refreshState is LoadState.Loading && items.itemCount == 0 -> LoadingIndicator()
        refreshState is LoadState.Error && items.itemCount == 0 -> {
            ErrorView(
                message = refreshState.error.localizedMessage
                    ?: stringResource(id = R.string.error_generic),
                onRetry = { items.retry() }
            )
        }
        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                count = items.itemCount,
                key = items.itemKey { it.id }
            ) { index ->
                val pokemon = items[index] ?: return@items
                PokemonCard(
                    id = pokemon.id,
                    name = pokemon.name,
                    imageUrl = pokemon.imageUrl,
                    types = pokemon.types,
                    onClick = { onPokemonClick(pokemon.id) }
                )
            }
            if (appendState is LoadState.Loading) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun FilteredPokemonGrid(
    pokemon: List<Pokemon>,
    isLoading: Boolean,
    onPokemonClick: (Int) -> Unit
) {
    if (isLoading && pokemon.isEmpty()) {
        LoadingIndicator()
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            count = pokemon.size,
            key = { pokemon[it].id }
        ) { index ->
            val item = pokemon[index]
            PokemonCard(
                id = item.id,
                name = item.name,
                imageUrl = item.imageUrl,
                types = item.types,
                onClick = { onPokemonClick(item.id) }
            )
        }
    }
}
