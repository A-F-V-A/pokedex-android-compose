package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.AbilityDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.EncounterDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.EvolutionChainDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.PokemonDetailDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.PokemonSpeciesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface PokemonDetailApi {

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: Int): PokemonDetailDto

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(@Path("id") id: Int): PokemonSpeciesDto

    @GET
    suspend fun getEvolutionChainByUrl(@Url url: String): EvolutionChainDto

    @GET("ability/{name}")
    suspend fun getAbility(@Path("name") name: String): AbilityDto

    @GET("pokemon/{id}/encounters")
    suspend fun getPokemonEncounters(@Path("id") id: Int): List<EncounterDto>
}
