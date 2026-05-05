package co.edu.uqvirtual.pokedex_android_compose.core.database.di

import android.content.Context
import androidx.room.Room
import co.edu.uqvirtual.pokedex_android_compose.core.database.PokedexDatabase
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.data.local.CaughtPokemonDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.local.PokemonDetailDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.GenerationDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.PokemonDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.RemoteKeysDao
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.local.TypeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePokedexDatabase(@ApplicationContext context: Context): PokedexDatabase =
        Room.databaseBuilder(
            context,
            PokedexDatabase::class.java,
            PokedexDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun providePokemonDao(db: PokedexDatabase): PokemonDao = db.pokemonDao()

    @Provides
    fun providePokemonDetailDao(db: PokedexDatabase): PokemonDetailDao = db.pokemonDetailDao()

    @Provides
    fun provideTypeDao(db: PokedexDatabase): TypeDao = db.typeDao()

    @Provides
    fun provideGenerationDao(db: PokedexDatabase): GenerationDao = db.generationDao()

    @Provides
    fun provideRemoteKeysDao(db: PokedexDatabase): RemoteKeysDao = db.remoteKeysDao()

    @Provides
    fun provideCaughtPokemonDao(db: PokedexDatabase): CaughtPokemonDao = db.caughtPokemonDao()
}
