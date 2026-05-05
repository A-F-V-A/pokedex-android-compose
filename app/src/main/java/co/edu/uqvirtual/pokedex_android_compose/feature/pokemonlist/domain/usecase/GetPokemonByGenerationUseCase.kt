package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPokemonByGenerationUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(generationName: String): Flow<List<Pokemon>> =
        repository.observePokemonByGeneration(generationName)

    suspend fun sync(generationName: String) = repository.syncPokemonForGeneration(generationName)
}
