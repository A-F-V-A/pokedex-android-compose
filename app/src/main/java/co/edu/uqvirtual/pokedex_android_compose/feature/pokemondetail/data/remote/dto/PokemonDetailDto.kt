package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "height") val height: Int,
    @Json(name = "weight") val weight: Int,
    @Json(name = "base_experience") val baseExperience: Int?,
    @Json(name = "stats") val stats: List<StatDto>,
    @Json(name = "types") val types: List<TypeSlotDto>,
    @Json(name = "abilities") val abilities: List<AbilitySlotDto>,
    @Json(name = "sprites") val sprites: SpritesDto
)

@JsonClass(generateAdapter = true)
data class StatDto(
    @Json(name = "base_stat") val baseStat: Int,
    @Json(name = "stat") val stat: NamedDto
)

@JsonClass(generateAdapter = true)
data class TypeSlotDto(
    @Json(name = "type") val type: NamedDto
)

@JsonClass(generateAdapter = true)
data class AbilitySlotDto(
    @Json(name = "ability") val ability: NamedDto
)

@JsonClass(generateAdapter = true)
data class NamedDto(
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String? = null
)

@JsonClass(generateAdapter = true)
data class SpritesDto(
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "other") val other: OtherSpritesDto?
)

@JsonClass(generateAdapter = true)
data class OtherSpritesDto(
    @Json(name = "official-artwork") val officialArtwork: OfficialArtworkDto?
)

@JsonClass(generateAdapter = true)
data class OfficialArtworkDto(
    @Json(name = "front_default") val frontDefault: String?
)
