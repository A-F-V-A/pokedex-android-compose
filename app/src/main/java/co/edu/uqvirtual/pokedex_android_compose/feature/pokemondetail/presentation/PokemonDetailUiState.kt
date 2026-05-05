package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.AbilityDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EncounterLocation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EvolutionChain
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonSpeciesInfo

data class PokemonDetailUiState(
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val isCaught: Boolean = false,
    val detail: PokemonDetail? = null,
    val species: PokemonSpeciesInfo? = null,
    val evolutionChain: EvolutionChain? = null,
    val abilities: List<AbilityDetail> = emptyList(),
    val encounters: List<EncounterLocation> = emptyList(),
    val isLoadingEvolution: Boolean = false,
    val errorMessage: String? = null,
    val showCatchAnimation: Boolean = false
)
