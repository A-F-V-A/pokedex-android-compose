package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.edu.uqvirtual.pokedex_android_compose.core.network.ConnectivityObserver
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetGenerationsUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetPagedPokemonUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetPokemonByGenerationUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetPokemonByTypeUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetTypesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PokemonListViewModel @Inject constructor(
    getPagedPokemonUseCase: GetPagedPokemonUseCase,
    private val getTypesUseCase: GetTypesUseCase,
    private val getGenerationsUseCase: GetGenerationsUseCase,
    private val getPokemonByTypeUseCase: GetPokemonByTypeUseCase,
    private val getPokemonByGenerationUseCase: GetPokemonByGenerationUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val pagedPokemon: Flow<PagingData<Pokemon>> = getPagedPokemonUseCase()
        .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    private val activeFilter = MutableStateFlow<ActiveFilter>(ActiveFilter.None)

    val filteredResults: StateFlow<List<Pokemon>> = activeFilter
        .flatMapLatest { filter ->
            when (filter) {
                ActiveFilter.None -> flowOf(emptyList())
                is ActiveFilter.Type -> getPokemonByTypeUseCase(filter.name)
                is ActiveFilter.GenerationFilter -> getPokemonByGenerationUseCase(filter.name)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        observeConnectivity()
        observeCatalogs()
        loadCatalogs()
    }

    fun selectTypeFilter(typeName: String) {
        activeFilter.value = ActiveFilter.Type(typeName)
        _uiState.update { it.copy(activeFilter = ActiveFilter.Type(typeName), isLoadingFilter = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getPokemonByTypeUseCase.sync(typeName) }
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
            _uiState.update { it.copy(isLoadingFilter = false) }
        }
    }

    fun selectGenerationFilter(generationName: String) {
        activeFilter.value = ActiveFilter.GenerationFilter(generationName)
        _uiState.update { it.copy(activeFilter = ActiveFilter.GenerationFilter(generationName), isLoadingFilter = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { getPokemonByGenerationUseCase.sync(generationName) }
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
            _uiState.update { it.copy(isLoadingFilter = false) }
        }
    }

    fun clearFilters() {
        activeFilter.value = ActiveFilter.None
        _uiState.update { it.copy(activeFilter = ActiveFilter.None, errorMessage = null) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.status
                .map { it != ConnectivityObserver.Status.Available }
                .catch { /* ignore */ }
                .collect { offline ->
                    _uiState.update { it.copy(isOffline = offline) }
                }
        }
    }

    private fun observeCatalogs() {
        viewModelScope.launch {
            getTypesUseCase().collect { types ->
                _uiState.update { it.copy(types = types) }
            }
        }
        viewModelScope.launch {
            getGenerationsUseCase().collect { generations ->
                _uiState.update { it.copy(generations = generations) }
            }
        }
    }

    private fun loadCatalogs() {
        viewModelScope.launch {
            runCatching { getTypesUseCase.refresh() }
        }
        viewModelScope.launch {
            runCatching { getGenerationsUseCase.refresh() }
        }
    }
}

