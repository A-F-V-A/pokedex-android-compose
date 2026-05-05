package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonType
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTypesUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<List<PokemonType>> = repository.observeTypes()

    suspend fun refresh() = repository.refreshTypes()
}
