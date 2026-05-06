package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Generation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Habitat
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonColor
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonType

sealed interface ActiveFilter {
    data object None : ActiveFilter
    data class Type(val name: String) : ActiveFilter
    data class GenerationFilter(val name: String) : ActiveFilter
    data class HabitatFilter(val name: String) : ActiveFilter
    data class ColorFilter(val name: String) : ActiveFilter
    data class Search(val query: String) : ActiveFilter
}

data class PokemonListUiState(
    val isOffline: Boolean = false,
    val activeFilter: ActiveFilter = ActiveFilter.None,
    val searchQuery: String = "",
    val types: List<PokemonType> = emptyList(),
    val generations: List<Generation> = emptyList(),
    val habitats: List<Habitat> = emptyList(),
    val colors: List<PokemonColor> = emptyList(),
    val isLoadingFilter: Boolean = false,
    val errorMessage: String? = null
)
