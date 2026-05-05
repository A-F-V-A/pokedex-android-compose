package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TypeResponseDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "pokemon") val pokemon: List<TypeMemberDto>
)

@JsonClass(generateAdapter = true)
data class TypeMemberDto(
    @Json(name = "pokemon") val pokemon: NamedResourceDto
)
