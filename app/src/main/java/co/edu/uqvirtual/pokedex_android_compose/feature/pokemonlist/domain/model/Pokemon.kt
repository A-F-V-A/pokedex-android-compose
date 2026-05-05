package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String> = emptyList()
)
