package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.usecase

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.repository.PokemonDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonDetailUseCase @Inject constructor(
    private val repository: PokemonDetailRepository
) {
    operator fun invoke(id: Int): Flow<PokemonDetail?> = repository.observeDetail(id)

    suspend fun refresh(id: Int) = repository.refresh(id)
}
