package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "habitat")
data class HabitatEntity(
    @PrimaryKey val name: String,
    val url: String
)

@Entity(tableName = "color")
data class ColorEntity(
    @PrimaryKey val name: String,
    val url: String
)

@Entity(
    tableName = "pokemon_habitat_cross_ref",
    primaryKeys = ["pokemonId", "habitatName"],
    indices = [Index("habitatName")]
)
data class PokemonHabitatCrossRef(
    val pokemonId: Int,
    val habitatName: String
)

@Entity(
    tableName = "pokemon_color_cross_ref",
    primaryKeys = ["pokemonId", "colorName"],
    indices = [Index("colorName")]
)
data class PokemonColorCrossRef(
    val pokemonId: Int,
    val colorName: String
)
