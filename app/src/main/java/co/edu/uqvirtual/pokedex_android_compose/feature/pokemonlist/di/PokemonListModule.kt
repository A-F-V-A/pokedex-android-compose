package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.di

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.remote.PokemonApi
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.data.repository.PokemonRepositoryImpl
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.repository.PokemonRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PokemonListBindsModule {

    @Binds
    @Singleton
    abstract fun bindPokemonRepository(impl: PokemonRepositoryImpl): PokemonRepository
}

@Module
@InstallIn(SingletonComponent::class)
object PokemonListProvidesModule {

    @Provides
    @Singleton
    fun providePokemonApi(retrofit: Retrofit): PokemonApi = retrofit.create(PokemonApi::class.java)
}
