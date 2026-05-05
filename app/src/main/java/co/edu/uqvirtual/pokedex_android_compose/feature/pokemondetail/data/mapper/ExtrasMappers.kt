package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.mapper

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.AbilityDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.ChainLinkDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.EncounterDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.EvolutionChainDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.dto.PokemonSpeciesDto
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.AbilityDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EncounterLocation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EvolutionChain
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EvolutionStage
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonSpeciesInfo

private const val ARTWORK_TEMPLATE =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/%d.png"

private fun extractIdFromUrl(url: String): Int? =
    url.trimEnd('/').substringAfterLast('/').toIntOrNull()

fun PokemonSpeciesDto.toDomain(): PokemonSpeciesInfo {
    val flavor = flavorTextEntries
        .firstOrNull { it.language.name == "en" }
        ?.flavorText
        ?.replace("\n", " ")
        ?.replace("", " ")
        ?.trim()
        .orEmpty()
    return PokemonSpeciesInfo(
        id = id,
        color = color?.name,
        habitat = habitat?.name,
        isLegendary = isLegendary,
        isMythical = isMythical,
        captureRate = captureRate,
        flavorText = flavor,
        evolutionChainUrl = evolutionChain?.url
    )
}

fun EvolutionChainDto.toDomain(): EvolutionChain {
    val stages = mutableListOf<EvolutionStage>()
    flatten(chain, stages)
    return EvolutionChain(id = id, stages = stages)
}

private fun flatten(link: ChainLinkDto, into: MutableList<EvolutionStage>) {
    val id = extractIdFromUrl(link.species.url) ?: return
    val detail = link.evolutionDetails.firstOrNull()
    into.add(
        EvolutionStage(
            pokemonId = id,
            name = link.species.name,
            imageUrl = ARTWORK_TEMPLATE.format(id),
            minLevel = detail?.minLevel,
            trigger = detail?.trigger?.name,
            itemRequired = detail?.item?.name
        )
    )
    link.evolvesTo.forEach { flatten(it, into) }
}

fun AbilityDto.toDomain(): AbilityDetail {
    val englishEffect = effectEntries.firstOrNull { it.language.name == "en" }
    val englishFlavor = flavorTextEntries.firstOrNull { it.language.name == "en" }
    return AbilityDetail(
        name = name,
        shortEffect = englishEffect?.shortEffect?.trim().orEmpty(),
        flavorText = englishFlavor?.flavorText?.replace("\n", " ")?.trim().orEmpty()
    )
}

fun EncounterDto.toDomain(): EncounterLocation = EncounterLocation(
    name = locationArea.name.replace('-', ' '),
    maxChance = versionDetails.maxOfOrNull { it.maxChance } ?: 0,
    versions = versionDetails.map { it.version.name }
)
