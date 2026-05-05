package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.usecase

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.AbilityDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EncounterLocation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EvolutionChain
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonSpeciesInfo
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.repository.PokemonDetailRepository
import javax.inject.Inject

class GetPokemonSpeciesUseCase @Inject constructor(
    private val repository: PokemonDetailRepository
) {
    suspend operator fun invoke(id: Int): PokemonSpeciesInfo = repository.getSpecies(id)
}

class GetEvolutionChainUseCase @Inject constructor(
    private val repository: PokemonDetailRepository
) {
    suspend operator fun invoke(url: String): EvolutionChain = repository.getEvolutionChain(url)
}

class GetAbilityDetailUseCase @Inject constructor(
    private val repository: PokemonDetailRepository
) {
    suspend operator fun invoke(name: String): AbilityDetail = repository.getAbility(name)
}

class GetPokemonEncountersUseCase @Inject constructor(
    private val repository: PokemonDetailRepository
) {
    suspend operator fun invoke(id: Int): List<EncounterLocation> = repository.getEncounters(id)
}
