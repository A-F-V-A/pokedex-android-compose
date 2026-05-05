package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.repository

import androidx.paging.PagingData
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Generation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonType
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPagedPokemon(): Flow<PagingData<Pokemon>>
    fun observeTypes(): Flow<List<PokemonType>>
    fun observeGenerations(): Flow<List<Generation>>
    fun observePokemonByType(typeName: String): Flow<List<Pokemon>>
    fun observePokemonByGeneration(generationName: String): Flow<List<Pokemon>>
    suspend fun refreshTypes()
    suspend fun refreshGenerations()
    suspend fun syncPokemonForType(typeName: String)
    suspend fun syncPokemonForGeneration(generationName: String)
}
