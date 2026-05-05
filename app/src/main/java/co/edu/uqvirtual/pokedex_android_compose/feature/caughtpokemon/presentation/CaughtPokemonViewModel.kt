package co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.model.CaughtPokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.usecase.GetCaughtPokemonUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.usecase.ReleasePokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaughtPokemonViewModel @Inject constructor(
    getCaughtPokemonUseCase: GetCaughtPokemonUseCase,
    private val releasePokemonUseCase: ReleasePokemonUseCase
) : ViewModel() {

    val caughtPokemon: StateFlow<List<CaughtPokemon>> = getCaughtPokemonUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun release(pokemonId: Int) {
        viewModelScope.launch { releasePokemonUseCase(pokemonId) }
    }
}
