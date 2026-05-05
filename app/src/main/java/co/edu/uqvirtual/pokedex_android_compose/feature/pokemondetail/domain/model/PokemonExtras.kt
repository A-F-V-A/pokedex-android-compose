package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model

data class PokemonSpeciesInfo(
    val id: Int,
    val color: String?,
    val habitat: String?,
    val isLegendary: Boolean,
    val isMythical: Boolean,
    val captureRate: Int,
    val flavorText: String,
    val evolutionChainUrl: String?
)

data class EvolutionStage(
    val pokemonId: Int,
    val name: String,
    val imageUrl: String,
    val minLevel: Int?,
    val trigger: String?,
    val itemRequired: String?
)

data class EvolutionChain(
    val id: Int,
    val stages: List<EvolutionStage>
)

data class AbilityDetail(
    val name: String,
    val shortEffect: String,
    val flavorText: String
)

data class EncounterLocation(
    val name: String,
    val maxChance: Int,
    val versions: List<String>
)
