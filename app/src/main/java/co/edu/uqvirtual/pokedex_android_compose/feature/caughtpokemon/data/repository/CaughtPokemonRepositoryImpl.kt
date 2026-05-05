package co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.data.repository

import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.data.local.CaughtPokemonDao
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.data.local.CaughtPokemonEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.model.CaughtPokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.repository.CaughtPokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaughtPokemonRepositoryImpl @Inject constructor(
    private val dao: CaughtPokemonDao
) : CaughtPokemonRepository {

    override fun observeAll(): Flow<List<CaughtPokemon>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeIsCaught(id: Int): Flow<Boolean> = dao.observeIsCaught(id)

    override fun observeCount(): Flow<Int> = dao.observeCount()

    override suspend fun catch(pokemonId: Int, name: String, imageUrl: String, nickname: String?) {
        dao.upsert(
            CaughtPokemonEntity(
                pokemonId = pokemonId,
                name = name,
                imageUrl = imageUrl,
                nickname = nickname,
                caughtAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun release(pokemonId: Int) = dao.release(pokemonId)

    private fun CaughtPokemonEntity.toDomain() = CaughtPokemon(
        pokemonId = pokemonId,
        name = name,
        imageUrl = imageUrl,
        nickname = nickname,
        caughtAt = caughtAt
    )
}
