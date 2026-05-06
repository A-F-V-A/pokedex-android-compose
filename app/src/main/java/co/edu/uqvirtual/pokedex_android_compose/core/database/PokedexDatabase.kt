package co.edu.uqvirtual.pokedex_android_compose.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.data.local.CaughtPokemonDao
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.data.local.CaughtPokemonEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.local.PokemonDetailDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.local.PokemonDetailEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.ColorDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.ColorEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.GenerationDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.GenerationEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.HabitatDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.HabitatEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonColorCrossRef
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonEntity
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonGenerationCrossRef
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonHabitatCrossRef
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
        HabitatEntity::class,
        ColorEntity::class,
        PokemonTypeCrossRef::class,
        PokemonGenerationCrossRef::class,
        PokemonHabitatCrossRef::class,
        PokemonColorCrossRef::class,
        RemoteKeysEntity::class,
        CaughtPokemonEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
    abstract fun typeDao(): TypeDao
    abstract fun generationDao(): GenerationDao
    abstract fun habitatDao(): HabitatDao
    abstract fun colorDao(): ColorDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun caughtPokemonDao(): CaughtPokemonDao

    companion object {
        const val DATABASE_NAME = "pokedex.db"
    }
}
