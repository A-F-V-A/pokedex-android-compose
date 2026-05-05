package co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caught_pokemon")
data class CaughtPokemonEntity(
    @PrimaryKey val pokemonId: Int,
    val name: String,
    val imageUrl: String,
    val nickname: String?,
    val caughtAt: Long
)
