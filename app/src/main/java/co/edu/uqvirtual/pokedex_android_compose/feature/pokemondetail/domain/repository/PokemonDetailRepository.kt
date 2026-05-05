package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.repository

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.AbilityDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EncounterLocation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EvolutionChain
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonSpeciesInfo
import kotlinx.coroutines.flow.Flow

interface PokemonDetailRepository {
    fun observeDetail(id: Int): Flow<PokemonDetail?>
    suspend fun refresh(id: Int)
    suspend fun getSpecies(id: Int): PokemonSpeciesInfo
    suspend fun getEvolutionChain(url: String): EvolutionChain
    suspend fun getAbility(name: String): AbilityDetail
    suspend fun getEncounters(id: Int): List<EncounterLocation>
}
