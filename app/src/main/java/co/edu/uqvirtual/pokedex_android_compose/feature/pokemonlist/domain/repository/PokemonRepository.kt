package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.repository

import androidx.paging.PagingData
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Generation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Habitat
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonColor
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonType
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPagedPokemon(): Flow<PagingData<Pokemon>>

    fun observeTypes(): Flow<List<PokemonType>>
    fun observeGenerations(): Flow<List<Generation>>
    fun observeHabitats(): Flow<List<Habitat>>
    fun observeColors(): Flow<List<PokemonColor>>

    fun observePokemonByType(typeName: String): Flow<List<Pokemon>>
    fun observePokemonByGeneration(generationName: String): Flow<List<Pokemon>>
    fun observePokemonByHabitat(habitatName: String): Flow<List<Pokemon>>
    fun observePokemonByColor(colorName: String): Flow<List<Pokemon>>

    fun searchPokemonByName(query: String): Flow<List<Pokemon>>

    suspend fun refreshTypes()
    suspend fun refreshGenerations()
    suspend fun refreshHabitats()
    suspend fun refreshColors()

    suspend fun syncPokemonForType(typeName: String)
    suspend fun syncPokemonForGeneration(generationName: String)
    suspend fun syncPokemonForHabitat(habitatName: String)
    suspend fun syncPokemonForColor(colorName: String)
}
