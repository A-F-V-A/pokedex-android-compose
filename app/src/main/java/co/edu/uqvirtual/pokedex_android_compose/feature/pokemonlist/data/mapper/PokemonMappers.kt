package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.mapper

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.GenerationEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.TypeEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.dto.NamedResourceDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Generation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonType

private const val ARTWORK_TEMPLATE =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png"

fun extractIdFromUrl(url: String): Int? {
    val trimmed = url.trimEnd('/')
    val lastSegment = trimmed.substringAfterLast('/')
    return lastSegment.toIntOrNull()
}

fun officialArtworkUrl(id: Int): String = ARTWORK_TEMPLATE.format(id)

fun NamedResourceDto.toPokemonEntity(page: Int, now: Long): PokemonEntity? {
    val id = extractIdFromUrl(url) ?: return null
    return PokemonEntity(
        id = id,
        name = name,
        imageUrl = officialArtworkUrl(id),
        page = page,
        lastUpdatedAt = now
    )
}

fun PokemonEntity.toDomain(): Pokemon = Pokemon(
    id = id,
    name = name,
    imageUrl = imageUrl
)

fun NamedResourceDto.toTypeEntity(): TypeEntity = TypeEntity(name = name, url = url)

fun NamedResourceDto.toGenerationEntity(): GenerationEntity = GenerationEntity(name = name, url = url)

fun TypeEntity.toDomain(): PokemonType = PokemonType(name = name)

fun GenerationEntity.toDomain(): Generation = Generation(name = name)
