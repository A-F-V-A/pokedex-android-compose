package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(habitats: List<HabitatEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrossRefs(refs: List<PokemonHabitatCrossRef>)

    @Query("SELECT * FROM habitat ORDER BY name ASC")
    fun observeAll(): Flow<List<HabitatEntity>>

    @Query("DELETE FROM pokemon_habitat_cross_ref WHERE habitatName = :habitatName")
    suspend fun clearCrossRefsForHabitat(habitatName: String)
}

@Dao
interface ColorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(colors: List<ColorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrossRefs(refs: List<PokemonColorCrossRef>)

    @Query("SELECT * FROM color ORDER BY name ASC")
    fun observeAll(): Flow<List<ColorEntity>>

    @Query("DELETE FROM pokemon_color_cross_ref WHERE colorName = :colorName")
    suspend fun clearCrossRefsForColor(colorName: String)
}
