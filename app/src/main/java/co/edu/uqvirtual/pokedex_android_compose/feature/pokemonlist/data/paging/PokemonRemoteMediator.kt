package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import co.edu.uqvirtual.pokedex_android_compose.core.database.PokedexDatabase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.RemoteKeysDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.RemoteKeysEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.mapper.toPokemonEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.PokemonApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val api: PokemonApi,
    private val database: PokedexDatabase,
    private val pokemonDao: PokemonDao,
    private val remoteKeysDao: RemoteKeysDao
) : RemoteMediator<Int, PokemonEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                val key = remoteKeysDao.getById(lastItem.id)
                key?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
            val offset = (page - 1) * PAGE_SIZE
            val response = api.getPokemonList(limit = PAGE_SIZE, offset = offset)
            val now = System.currentTimeMillis()
            val entities = response.results.mapNotNull { it.toPokemonEntity(page, now) }
            val endOfPagination = response.next == null || entities.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pokemonDao.clearAll()
                    remoteKeysDao.clearAll()
                }
                pokemonDao.upsertAll(entities)
                val keys = entities.map {
                    RemoteKeysEntity(
                        pokemonId = it.id,
                        prevKey = if (page == STARTING_PAGE) null else page - 1,
                        nextKey = if (endOfPagination) null else page + 1
                    )
                }
                remoteKeysDao.insertAll(keys)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private companion object {
        const val STARTING_PAGE = 1
        const val PAGE_SIZE = 20
    }
}
