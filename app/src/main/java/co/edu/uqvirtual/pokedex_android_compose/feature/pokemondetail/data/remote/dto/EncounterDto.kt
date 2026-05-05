package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EncounterDto(
    @Json(name = "location_area") val locationArea: NamedDto,
    @Json(name = "version_details") val versionDetails: List<VersionDetailDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class VersionDetailDto(
    @Json(name = "max_chance") val maxChance: Int,
    @Json(name = "version") val version: NamedDto
)
