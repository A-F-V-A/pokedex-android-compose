package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val stats: Stats,
    val types: List<String>,
    val abilities: List<String>
)

data class Stats(
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val specialAttack: Int,
    val specialDefense: Int,
    val speed: Int
)
