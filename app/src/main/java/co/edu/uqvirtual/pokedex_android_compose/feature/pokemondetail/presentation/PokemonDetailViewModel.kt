package co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uqvirtual.pokedex_android_compose.core.network.ConnectivityObserver
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.usecase.CatchPokemonUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.usecase.IsPokemonCaughtUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.domain.usecase.ReleasePokemonUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.usecase.GetAbilityDetailUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.usecase.GetEvolutionChainUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.usecase.GetPokemonDetailUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.usecase.GetPokemonEncountersUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.domain.usecase.GetPokemonSpeciesUseCase
import co.edu.uqvirtual.pokedex_android_compose.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val getPokemonSpeciesUseCase: GetPokemonSpeciesUseCase,
    private val getEvolutionChainUseCase: GetEvolutionChainUseCase,
    private val getAbilityDetailUseCase: GetAbilityDetailUseCase,
    private val getPokemonEncountersUseCase: GetPokemonEncountersUseCase,
    private val isPokemonCaughtUseCase: IsPokemonCaughtUseCase,
    private val catchPokemonUseCase: CatchPokemonUseCase,
    private val releasePokemonUseCase: ReleasePokemonUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val pokemonId: Int = checkNotNull(savedStateHandle[Routes.ARG_POKEMON_ID])

    private val _uiState = MutableStateFlow(PokemonDetailUiState())
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    init {
        observeConnectivity()
        observeDetail()
        observeCaughtState()
        refreshAll()
    }

    fun retry() {
        _uiState.update { it.copy(errorMessage = null, isLoading = true) }
        refreshAll()
    }

    fun toggleCatch() {
        val state = _uiState.value
        val detail = state.detail ?: return
        viewModelScope.launch {
            if (state.isCaught) {
                releasePokemonUseCase(pokemonId)
            } else {
                catchPokemonUseCase(
                    pokemonId = pokemonId,
                    name = detail.name,
                    imageUrl = detail.imageUrl
                )
                _uiState.update { it.copy(showCatchAnimation = true) }
                delay(1_400)
                _uiState.update { it.copy(showCatchAnimation = false) }
            }
        }
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
                if (detail != null && _uiState.value.abilities.isEmpty()) {
                    loadAbilities(detail.abilities)
                }
            }
        }
    }

    private fun observeCaughtState() {
        viewModelScope.launch {
            isPokemonCaughtUseCase(pokemonId).collect { caught ->
                _uiState.update { it.copy(isCaught = caught) }
            }
        }
    }

    private fun refreshAll() {
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
                .onSuccess { _uiState.update { it.copy(isLoading = false) } }
        }
        loadSpeciesAndEvolution()
        loadEncounters()
    }

    private fun loadSpeciesAndEvolution() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEvolution = true) }
            runCatching { getPokemonSpeciesUseCase(pokemonId) }
                .onSuccess { species ->
                    _uiState.update { it.copy(species = species) }
                    val url = species.evolutionChainUrl
                    if (url != null) {
                        runCatching { getEvolutionChainUseCase(url) }
                            .onSuccess { chain ->
                                _uiState.update { it.copy(evolutionChain = chain) }
                            }
                    }
                }
            _uiState.update { it.copy(isLoadingEvolution = false) }
        }
    }

    private fun loadAbilities(names: List<String>) {
        viewModelScope.launch {
            val resolved = names.mapNotNull { name ->
                runCatching { getAbilityDetailUseCase(name) }.getOrNull()
            }
            _uiState.update { it.copy(abilities = resolved) }
        }
    }

    private fun loadEncounters() {
        viewModelScope.launch {
            runCatching { getPokemonEncountersUseCase(pokemonId) }
                .onSuccess { encounters ->
                    _uiState.update { it.copy(encounters = encounters) }
                }
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.status.collect { status ->
                _uiState.update {
                    it.copy(isOffline = status != ConnectivityObserver.Status.Available)
                }
            }
        }
    }
}
