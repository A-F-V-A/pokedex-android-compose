package co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CaughtPokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CaughtPokemonEntity)

    @Query("DELETE FROM caught_pokemon WHERE pokemonId = :id")
    suspend fun release(id: Int)

    @Query("SELECT * FROM caught_pokemon ORDER BY caughtAt DESC")
    fun observeAll(): Flow<List<CaughtPokemonEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM caught_pokemon WHERE pokemonId = :id)")
    fun observeIsCaught(id: Int): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM caught_pokemon")
    fun observeCount(): Flow<Int>
}
