package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.dto.ColorResponseDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.dto.GenerationResponseDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.dto.HabitatResponseDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.dto.PokemonListResponseDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.dto.TypeResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApi {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponseDto

    @GET("type")
    suspend fun getTypes(
        @Query("limit") limit: Int = 50
    ): PokemonListResponseDto

    @GET("type/{name}")
    suspend fun getPokemonByType(@Path("name") typeName: String): TypeResponseDto

    @GET("generation")
    suspend fun getGenerations(
        @Query("limit") limit: Int = 50
    ): PokemonListResponseDto

    @GET("generation/{name}")
    suspend fun getPokemonByGeneration(@Path("name") generationName: String): GenerationResponseDto

    @GET("pokemon-habitat")
    suspend fun getHabitats(
        @Query("limit") limit: Int = 50
    ): PokemonListResponseDto

    @GET("pokemon-habitat/{name}")
    suspend fun getPokemonByHabitat(@Path("name") habitatName: String): HabitatResponseDto

    @GET("pokemon-color")
    suspend fun getColors(
        @Query("limit") limit: Int = 50
    ): PokemonListResponseDto

    @GET("pokemon-color/{name}")
    suspend fun getPokemonByColor(@Path("name") colorName: String): ColorResponseDto
}
