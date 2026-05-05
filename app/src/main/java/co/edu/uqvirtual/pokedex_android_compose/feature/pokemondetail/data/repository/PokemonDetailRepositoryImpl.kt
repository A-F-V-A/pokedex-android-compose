package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.repository

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.local.PokemonDetailDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.mapper.toDomain
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.mapper.toEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.PokemonDetailApi
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.AbilityDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EncounterLocation
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.EvolutionChain
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonDetail
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.model.PokemonSpeciesInfo
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.repository.PokemonDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonDetailRepositoryImpl @Inject constructor(
    private val api: PokemonDetailApi,
    private val dao: PokemonDetailDao
) : PokemonDetailRepository {

    override fun observeDetail(id: Int): Flow<PokemonDetail?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun refresh(id: Int) {
        val dto = api.getPokemonDetail(id)
        dao.upsert(dto.toEntity(System.currentTimeMillis()))
    }

    override suspend fun getSpecies(id: Int): PokemonSpeciesInfo =
        api.getPokemonSpecies(id).toDomain()

    override suspend fun getEvolutionChain(url: String): EvolutionChain =
        api.getEvolutionChainByUrl(url).toDomain()

    override suspend fun getAbility(name: String): AbilityDetail =
        api.getAbility(name).toDomain()

    override suspend fun getEncounters(id: Int): List<EncounterLocation> =
        api.getPokemonEncounters(id).map { it.toDomain() }
}
