package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.PokeballIcon
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

@Composable
fun PokemonListScreen(
    onPokemonClick: (Int) -> Unit,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredResults by viewModel.filteredResults.collectAsStateWithLifecycle()
    val pagingItems = viewModel.pagedPokemon.collectAsLazyPagingItems()

    val refreshError = (pagingItems.loadState.refresh as? LoadState.Error)?.error
    val appendError = (pagingItems.loadState.append as? LoadState.Error)?.error
    val networkProblem = listOfNotNull(refreshError, appendError).any { it.isLikelyNetworkError() }
    val effectiveOffline = uiState.isOffline || networkProblem

    LaunchedEffect(uiState.isOffline) {
        if (!uiState.isOffline && pagingItems.itemCount == 0 &&
            pagingItems.loadState.refresh is LoadState.Error
        ) {
            pagingItems.retry()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ConnectivityBanner(isOffline = effectiveOffline)
        CompactHeader()
        FilterBar(
            activeFilter = uiState.activeFilter,
            searchQuery = uiState.searchQuery,
            types = uiState.types,
            generations = uiState.generations,
            habitats = uiState.habitats,
            colors = uiState.colors,
            onSearchQueryChange = viewModel::setSearchQuery,
            onTypeSelected = viewModel::selectTypeFilter,
            onGenerationSelected = viewModel::selectGenerationFilter,
            onHabitatSelected = viewModel::selectHabitatFilter,
            onColorSelected = viewModel::selectColorFilter,
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

@Composable
private fun CompactHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PokeballIcon(modifier = Modifier.size(28.dp))
        Text(
            text = stringResource(id = R.string.screen_pokemon_list),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp)
        )
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
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
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
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
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
