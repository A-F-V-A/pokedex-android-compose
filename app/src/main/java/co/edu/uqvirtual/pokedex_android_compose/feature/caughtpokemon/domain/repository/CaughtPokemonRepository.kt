package co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.repository

import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.model.CaughtPokemon
import kotlinx.coroutines.flow.Flow

interface CaughtPokemonRepository {
    fun observeAll(): Flow<List<CaughtPokemon>>
    fun observeIsCaught(id: Int): Flow<Boolean>
    fun observeCount(): Flow<Int>
    suspend fun catch(pokemonId: Int, name: String, imageUrl: String, nickname: String? = null)
    suspend fun release(pokemonId: Int)
}
