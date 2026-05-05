package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonDetail

data class PokemonDetailUiState(
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val detail: PokemonDetail? = null,
    val errorMessage: String? = null
)
