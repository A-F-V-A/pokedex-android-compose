package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(detail: PokemonDetailEntity)

    @Query("SELECT * FROM pokemon_detail WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<PokemonDetailEntity?>

    @Query("SELECT * FROM pokemon_detail WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PokemonDetailEntity?
}
