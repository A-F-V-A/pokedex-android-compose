package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(types: List<TypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrossRefs(refs: List<PokemonTypeCrossRef>)

    @Query("SELECT * FROM type ORDER BY name ASC")
    fun observeAll(): Flow<List<TypeEntity>>

    @Query("DELETE FROM pokemon_type_cross_ref WHERE typeName = :typeName")
    suspend fun clearCrossRefsForType(typeName: String)
}
