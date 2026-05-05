package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.di

import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.remote.PokemonDetailApi
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.data.repository.PokemonDetailRepositoryImpl
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.repository.PokemonDetailRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PokemonDetailBindsModule {

    @Binds
    @Singleton
    abstract fun bindPokemonDetailRepository(
        impl: PokemonDetailRepositoryImpl
    ): PokemonDetailRepository
}

@Module
@InstallIn(SingletonComponent::class)
object PokemonDetailProvidesModule {

    @Provides
    @Singleton
    fun providePokemonDetailApi(retrofit: Retrofit): PokemonDetailApi =
        retrofit.create(PokemonDetailApi::class.java)
}
