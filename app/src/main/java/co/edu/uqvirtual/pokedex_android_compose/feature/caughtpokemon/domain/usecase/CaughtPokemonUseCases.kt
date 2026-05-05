package co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.usecase

import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.model.CaughtPokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.repository.CaughtPokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCaughtPokemonUseCase @Inject constructor(
    private val repository: CaughtPokemonRepository
) {
    operator fun invoke(): Flow<List<CaughtPokemon>> = repository.observeAll()
}

class GetCaughtCountUseCase @Inject constructor(
    private val repository: CaughtPokemonRepository
) {
    operator fun invoke(): Flow<Int> = repository.observeCount()
}

class IsPokemonCaughtUseCase @Inject constructor(
    private val repository: CaughtPokemonRepository
) {
    operator fun invoke(id: Int): Flow<Boolean> = repository.observeIsCaught(id)
}

class CatchPokemonUseCase @Inject constructor(
    private val repository: CaughtPokemonRepository
) {
    suspend operator fun invoke(
        pokemonId: Int,
        name: String,
        imageUrl: String,
        nickname: String? = null
    ) = repository.catch(pokemonId, name, imageUrl, nickname)
}

class ReleasePokemonUseCase @Inject constructor(
    private val repository: CaughtPokemonRepository
) {
    suspend operator fun invoke(pokemonId: Int) = repository.release(pokemonId)
}
