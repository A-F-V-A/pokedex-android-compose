package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AbilityDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "effect_entries") val effectEntries: List<EffectEntryDto> = emptyList(),
    @Json(name = "flavor_text_entries") val flavorTextEntries: List<AbilityFlavorTextDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EffectEntryDto(
    @Json(name = "effect") val effect: String,
    @Json(name = "short_effect") val shortEffect: String,
    @Json(name = "language") val language: NamedDto
)

@JsonClass(generateAdapter = true)
data class AbilityFlavorTextDto(
    @Json(name = "flavor_text") val flavorText: String,
    @Json(name = "language") val language: NamedDto
)
