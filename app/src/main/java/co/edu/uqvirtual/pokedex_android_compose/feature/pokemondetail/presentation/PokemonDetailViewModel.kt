package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uqvirtual.pokedex_android_compose.core.network.ConnectivityObserver
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.usecase.GetPokemonDetailUseCase
import co.edu.uqvirtual.pokedex_android_compose.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val pokemonId: Int = checkNotNull(savedStateHandle[Routes.ARG_POKEMON_ID])

    private val _uiState = MutableStateFlow(PokemonDetailUiState())
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    init {
        observeConnectivity()
        observeDetail()
        refresh()
    }

    fun retry() {
        _uiState.update { it.copy(errorMessage = null, isLoading = true) }
        refresh()
    }

    private fun observeDetail() {
        viewModelScope.launch {
            getPokemonDetailUseCase(pokemonId).collect { detail ->
                _uiState.update {
                    it.copy(
                        detail = detail,
                        isLoading = if (detail != null) false else it.isLoading
                    )
                }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            runCatching { getPokemonDetailUseCase.refresh(pokemonId) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = if (it.detail == null) error.message else null
                        )
                    }
                }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.status.collect { status ->
                _uiState.update { it.copy(isOffline = status != ConnectivityObserver.Status.Available) }
            }
        }
    }
}
