package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generation")
data class GenerationEntity(
    @PrimaryKey val name: String,
    val url: String
)
