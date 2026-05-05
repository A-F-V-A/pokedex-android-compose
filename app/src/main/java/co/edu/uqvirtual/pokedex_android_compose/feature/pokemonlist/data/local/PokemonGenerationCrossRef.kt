package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "pokemon_generation_cross_ref",
    primaryKeys = ["pokemonId", "generationName"],
    indices = [Index("generationName")]
)
data class PokemonGenerationCrossRef(
    val pokemonId: Int,
    val generationName: String
)
