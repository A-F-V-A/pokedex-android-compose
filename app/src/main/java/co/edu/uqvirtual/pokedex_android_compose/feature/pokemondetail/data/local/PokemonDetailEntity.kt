package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_detail")
data class PokemonDetailEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val specialAttack: Int,
    val specialDefense: Int,
    val speed: Int,
    val typesCsv: String,
    val abilitiesCsv: String,
    val lastUpdatedAt: Long
)
