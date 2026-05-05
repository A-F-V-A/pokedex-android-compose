package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonSpeciesDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "color") val color: NamedDto?,
    @Json(name = "habitat") val habitat: NamedDto?,
    @Json(name = "is_legendary") val isLegendary: Boolean = false,
    @Json(name = "is_mythical") val isMythical: Boolean = false,
    @Json(name = "capture_rate") val captureRate: Int = 0,
    @Json(name = "flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryDto> = emptyList(),
    @Json(name = "evolution_chain") val evolutionChain: EvolutionChainRefDto?
)

@JsonClass(generateAdapter = true)
data class FlavorTextEntryDto(
    @Json(name = "flavor_text") val flavorText: String,
    @Json(name = "language") val language: NamedDto
)

@JsonClass(generateAdapter = true)
data class EvolutionChainRefDto(
    @Json(name = "url") val url: String
)
