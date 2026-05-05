package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.edu.uqvirtual.pokedex_android_compose.core.database.PokedexDatabase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.GenerationDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonGenerationCrossRef
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonTypeCrossRef
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.RemoteKeysDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.TypeDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.mapper.extractIdFromUrl
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.mapper.officialArtworkUrl
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.mapper.toDomain
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.mapper.toGenerationEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.mapper.toTypeEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.paging.PokemonRemoteMediator
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.PokemonApi
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Generation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.PokemonType
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class PokemonRepositoryImpl @Inject constructor(
    private val api: PokemonApi,
    private val database: PokedexDatabase,
    private val pokemonDao: PokemonDao,
    private val typeDao: TypeDao,
    private val generationDao: GenerationDao,
    private val remoteKeysDao: RemoteKeysDao
) : PokemonRepository {

    override fun getPagedPokemon(): Flow<PagingData<Pokemon>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = 5, enablePlaceholders = false),
        remoteMediator = PokemonRemoteMediator(api, database, pokemonDao, remoteKeysDao),
        pagingSourceFactory = { pokemonDao.pagingSource() }
    ).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override fun observeTypes(): Flow<List<PokemonType>> =
        typeDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeGenerations(): Flow<List<Generation>> =
        generationDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observePokemonByType(typeName: String): Flow<List<Pokemon>> =
        pokemonDao.getByType(typeName).map { list ->
            list.map { it.toDomain().copy(types = listOf(typeName)) }
        }

    override fun observePokemonByGeneration(generationName: String): Flow<List<Pokemon>> =
        pokemonDao.getByGeneration(generationName).map { list -> list.map { it.toDomain() } }

    override suspend fun refreshTypes() {
        val response = api.getTypes()
        typeDao.upsertAll(response.results.map { it.toTypeEntity() })
    }

    override suspend fun refreshGenerations() {
        val response = api.getGenerations()
        generationDao.upsertAll(response.results.map { it.toGenerationEntity() })
    }

    override suspend fun syncPokemonForType(typeName: String) {
        val response = api.getPokemonByType(typeName)
        val now = System.currentTimeMillis()
        val members = response.pokemon.mapNotNull { member ->
            val id = extractIdFromUrl(member.pokemon.url) ?: return@mapNotNull null
            id to member.pokemon.name
        }
        if (members.isEmpty()) return

        val pokemon = members.map { (id, name) ->
            co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonEntity(
                id = id,
                name = name,
                imageUrl = officialArtworkUrl(id),
                page = 0,
                lastUpdatedAt = now
            )
        }
        val crossRefs = members.map { (id, _) -> PokemonTypeCrossRef(id, typeName) }

        pokemonDao.upsertAll(pokemon)
        typeDao.clearCrossRefsForType(typeName)
        typeDao.upsertCrossRefs(crossRefs)
    }

    override suspend fun syncPokemonForGeneration(generationName: String) {
        val response = api.getPokemonByGeneration(generationName)
        val now = System.currentTimeMillis()
        val members = response.pokemonSpecies.mapNotNull { species ->
            val id = extractIdFromUrl(species.url) ?: return@mapNotNull null
            id to species.name
        }
        if (members.isEmpty()) return

        val pokemon = members.map { (id, name) ->
            co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonEntity(
                id = id,
                name = name,
                imageUrl = officialArtworkUrl(id),
                page = 0,
                lastUpdatedAt = now
            )
        }
        val crossRefs = members.map { (id, _) -> PokemonGenerationCrossRef(id, generationName) }

        pokemonDao.upsertAll(pokemon)
        generationDao.clearCrossRefsForGeneration(generationName)
        generationDao.upsertCrossRefs(crossRefs)
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
