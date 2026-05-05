package co.edu.uqvirtual.pokedex_android_compose.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.local.PokemonDetailDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.local.PokemonDetailEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.GenerationDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.GenerationEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonGenerationCrossRef
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonTypeCrossRef
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.RemoteKeysDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.RemoteKeysEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.TypeDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.TypeEntity

@Database(
    entities = [
        PokemonEntity::class,
        PokemonDetailEntity::class,
        TypeEntity::class,
        GenerationEntity::class,
        PokemonTypeCrossRef::class,
        PokemonGenerationCrossRef::class,
        RemoteKeysEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
    abstract fun typeDao(): TypeDao
    abstract fun generationDao(): GenerationDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "pokedex.db"
    }
}
