package co.edu.uqvirtual.pokedex_android_compose.navigation

object Routes {
    const val POKEMON_LIST = "pokemon_list"
    const val CAUGHT_LIST = "caught_list"
    const val POKEMON_DETAIL = "pokemon_detail/{pokemonId}"

    fun pokemonDetail(id: Int) = "pokemon_detail/$id"

    const val ARG_POKEMON_ID = "pokemonId"
}
