package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Generation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGenerationsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<List<Generation>> = repository.observeGenerations()

    suspend fun refresh() = repository.refreshGenerations()
}
