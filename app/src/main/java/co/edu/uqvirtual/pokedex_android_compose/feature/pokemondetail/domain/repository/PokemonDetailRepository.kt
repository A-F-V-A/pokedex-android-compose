package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.repository

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonDetail
import kotlinx.coroutines.flow.Flow

interface PokemonDetailRepository {
    fun observeDetail(id: Int): Flow<PokemonDetail?>
    suspend fun refresh(id: Int)
}
