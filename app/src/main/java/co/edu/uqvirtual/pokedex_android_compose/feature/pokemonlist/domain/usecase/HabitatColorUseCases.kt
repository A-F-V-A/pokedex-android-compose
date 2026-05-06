package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Habitat
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonColor
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitatsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<List<Habitat>> = repository.observeHabitats()
    suspend fun refresh() = repository.refreshHabitats()
}

class GetColorsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(): Flow<List<PokemonColor>> = repository.observeColors()
    suspend fun refresh() = repository.refreshColors()
}

class GetPokemonByHabitatUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(habitatName: String): Flow<List<Pokemon>> =
        repository.observePokemonByHabitat(habitatName)

    suspend fun sync(habitatName: String) = repository.syncPokemonForHabitat(habitatName)
}

class GetPokemonByColorUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(colorName: String): Flow<List<Pokemon>> =
        repository.observePokemonByColor(colorName)

    suspend fun sync(colorName: String) = repository.syncPokemonForColor(colorName)
}

class SearchPokemonByNameUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    operator fun invoke(query: String): Flow<List<Pokemon>> =
        repository.searchPokemonByName(query)
}
