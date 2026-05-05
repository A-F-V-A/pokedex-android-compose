package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EvolutionChainDto(
    @Json(name = "id") val id: Int,
    @Json(name = "chain") val chain: ChainLinkDto
)

@JsonClass(generateAdapter = true)
data class ChainLinkDto(
    @Json(name = "species") val species: NamedDto,
    @Json(name = "evolution_details") val evolutionDetails: List<EvolutionDetailDto> = emptyList(),
    @Json(name = "evolves_to") val evolvesTo: List<ChainLinkDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EvolutionDetailDto(
    @Json(name = "min_level") val minLevel: Int?,
    @Json(name = "trigger") val trigger: NamedDto?,
    @Json(name = "item") val item: NamedDto?
)
