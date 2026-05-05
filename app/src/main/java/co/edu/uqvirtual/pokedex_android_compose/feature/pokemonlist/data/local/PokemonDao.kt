package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(pokemon: List<PokemonEntity>)

    @Query("SELECT * FROM pokemon ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PokemonEntity?

    @Query(
        """
        SELECT p.* FROM pokemon p
        INNER JOIN pokemon_type_cross_ref ptc ON ptc.pokemonId = p.id
        WHERE ptc.typeName = :typeName
        ORDER BY p.id ASC
        """
    )
    fun getByType(typeName: String): Flow<List<PokemonEntity>>

    @Query(
        """
        SELECT p.* FROM pokemon p
        INNER JOIN pokemon_generation_cross_ref pgc ON pgc.pokemonId = p.id
        WHERE pgc.generationName = :generationName
        ORDER BY p.id ASC
        """
    )
    fun getByGeneration(generationName: String): Flow<List<PokemonEntity>>

    @Query("DELETE FROM pokemon")
    suspend fun clearAll()
}
