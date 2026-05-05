package co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.di

import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.data.repository.CaughtPokemonRepositoryImpl
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.repository.CaughtPokemonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CaughtPokemonModule {

    @Binds
    @Singleton
    abstract fun bindCaughtPokemonRepository(
        impl: CaughtPokemonRepositoryImpl
    ): CaughtPokemonRepository
}
