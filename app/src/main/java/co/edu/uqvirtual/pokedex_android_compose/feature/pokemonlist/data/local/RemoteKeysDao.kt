package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeysEntity>)

    @Query("SELECT * FROM remote_keys WHERE pokemonId = :id LIMIT 1")
    suspend fun getById(id: Int): RemoteKeysEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}
