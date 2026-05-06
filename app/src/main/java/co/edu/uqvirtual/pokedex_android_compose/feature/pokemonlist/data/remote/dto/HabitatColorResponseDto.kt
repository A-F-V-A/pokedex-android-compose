package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HabitatResponseDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "pokemon_species") val pokemonSpecies: List<NamedResourceDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ColorResponseDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "pokemon_species") val pokemonSpecies: List<NamedResourceDto> = emptyList()
)
