package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey val pokemonId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)
