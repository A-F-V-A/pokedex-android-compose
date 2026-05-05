package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val page: Int,
    val lastUpdatedAt: Long
)
