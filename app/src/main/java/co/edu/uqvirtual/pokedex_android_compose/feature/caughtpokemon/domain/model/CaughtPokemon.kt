package co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.model

data class CaughtPokemon(
    val pokemonId: Int,
    val name: String,
    val imageUrl: String,
    val nickname: String?,
    val caughtAt: Long
)
