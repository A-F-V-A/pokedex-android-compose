package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Generation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonType

sealed interface ActiveFilter {
    data object None : ActiveFilter
    data class Type(val name: String) : ActiveFilter
    data class GenerationFilter(val name: String) : ActiveFilter
}

data class PokemonListUiState(
    val isOffline: Boolean = false,
    val activeFilter: ActiveFilter = ActiveFilter.None,
    val types: List<PokemonType> = emptyList(),
    val generations: List<Generation> = emptyList(),
    val filteredResults: List<Pokemon> = emptyList(),
    val isLoadingFilter: Boolean = false,
    val errorMessage: String? = null
)
