package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GenerationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(generations: List<GenerationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrossRefs(refs: List<PokemonGenerationCrossRef>)

    @Query("SELECT * FROM generation ORDER BY name ASC")
    fun observeAll(): Flow<List<GenerationEntity>>

    @Query("DELETE FROM pokemon_generation_cross_ref WHERE generationName = :generationName")
    suspend fun clearCrossRefsForGeneration(generationName: String)
}
