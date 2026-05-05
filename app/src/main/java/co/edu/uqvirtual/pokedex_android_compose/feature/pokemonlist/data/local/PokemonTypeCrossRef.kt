package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "pokemon_type_cross_ref",
    primaryKeys = ["pokemonId", "typeName"],
    indices = [Index("typeName")]
)
data class PokemonTypeCrossRef(
    val pokemonId: Int,
    val typeName: String
)
