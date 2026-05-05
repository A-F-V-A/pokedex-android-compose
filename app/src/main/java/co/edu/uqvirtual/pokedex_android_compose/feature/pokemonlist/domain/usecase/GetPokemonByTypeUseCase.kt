package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonByTypeUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(typeName: String): Flow<List<Pokemon>> =
        repository.observePokemonByType(typeName)

    suspend fun sync(typeName: String) = repository.syncPokemonForType(typeName)
}
