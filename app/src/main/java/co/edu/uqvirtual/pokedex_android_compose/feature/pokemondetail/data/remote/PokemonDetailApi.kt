package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.PokemonDetailDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonDetailApi {

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: Int): PokemonDetailDto
}
