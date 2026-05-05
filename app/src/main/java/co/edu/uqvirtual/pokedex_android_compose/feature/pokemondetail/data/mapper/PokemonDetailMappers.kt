package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.mapper

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.local.PokemonDetailEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.PokemonDetailDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.Stats

private fun PokemonDetailDto.statValue(name: String): Int =
    stats.firstOrNull { it.stat.name == name }?.baseStat ?: 0

private fun PokemonDetailDto.bestImageUrl(): String =
    sprites.other?.officialArtwork?.frontDefault
        ?: sprites.frontDefault
        ?: "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

fun PokemonDetailDto.toEntity(now: Long): PokemonDetailEntity = PokemonDetailEntity(
    id = id,
    name = name,
    imageUrl = bestImageUrl(),
    height = height,
    weight = weight,
    baseExperience = baseExperience ?: 0,
    hp = statValue("hp"),
    attack = statValue("attack"),
    defense = statValue("defense"),
    specialAttack = statValue("special-attack"),
    specialDefense = statValue("special-defense"),
    speed = statValue("speed"),
    typesCsv = types.joinToString(",") { it.type.name },
    abilitiesCsv = abilities.joinToString(",") { it.ability.name },
    lastUpdatedAt = now
)

fun PokemonDetailEntity.toDomain(): PokemonDetail = PokemonDetail(
    id = id,
    name = name,
    imageUrl = imageUrl,
    height = height,
    weight = weight,
    baseExperience = baseExperience,
    stats = Stats(
        hp = hp,
        attack = attack,
        defense = defense,
        specialAttack = specialAttack,
        specialDefense = specialDefense,
        speed = speed
    ),
    types = if (typesCsv.isBlank()) emptyList() else typesCsv.split(","),
    abilities = if (abilitiesCsv.isBlank()) emptyList() else abilitiesCsv.split(",")
)
