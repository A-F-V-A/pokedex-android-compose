package co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.edu.uqvirtual.pokedex_android_compose.core.network.ConnectivityObserver
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.model.Pokemon
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetColorsUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetGenerationsUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetHabitatsUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetPagedPokemonUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetPokemonByColorUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetPokemonByGenerationUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetPokemonByHabitatUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetPokemonByTypeUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.GetTypesUseCase
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.domain.usecase.SearchPokemonByNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class PokemonListViewModel @Inject constructor(
    getPagedPokemonUseCase: GetPagedPokemonUseCase,
    private val getTypesUseCase: GetTypesUseCase,
    private val getGenerationsUseCase: GetGenerationsUseCase,
    private val getHabitatsUseCase: GetHabitatsUseCase,
    private val getColorsUseCase: GetColorsUseCase,
    private val getPokemonByTypeUseCase: GetPokemonByTypeUseCase,
    private val getPokemonByGenerationUseCase: GetPokemonByGenerationUseCase,
    private val getPokemonByHabitatUseCase: GetPokemonByHabitatUseCase,
    private val getPokemonByColorUseCase: GetPokemonByColorUseCase,
    private val searchPokemonByNameUseCase: SearchPokemonByNameUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val pagedPokemon: Flow<PagingData<Pokemon>> = getPagedPokemonUseCase()
        .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    private val activeFilter = MutableStateFlow<ActiveFilter>(ActiveFilter.None)

    val filteredResults: StateFlow<List<Pokemon>> = activeFilter
        .debounce { if (it is ActiveFilter.Search) 250L else 0L }
        .flatMapLatest { filter ->
            when (filter) {
                ActiveFilter.None -> flowOf(emptyList())
                is ActiveFilter.Type -> getPokemonByTypeUseCase(filter.name)
                is ActiveFilter.GenerationFilter -> getPokemonByGenerationUseCase(filter.name)
                is ActiveFilter.HabitatFilter -> getPokemonByHabitatUseCase(filter.name)
                is ActiveFilter.ColorFilter -> getPokemonByColorUseCase(filter.name)
                is ActiveFilter.Search -> searchPokemonByNameUseCase(filter.query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        observeConnectivity()
        observeCatalogs()
        loadCatalogs()
    }

    fun selectTypeFilter(name: String) = applyFilter(ActiveFilter.Type(name)) {
        runCatching { getPokemonByTypeUseCase.sync(name) }
    }

    fun selectGenerationFilter(name: String) = applyFilter(ActiveFilter.GenerationFilter(name)) {
        runCatching { getPokemonByGenerationUseCase.sync(name) }
    }

    fun selectHabitatFilter(name: String) = applyFilter(ActiveFilter.HabitatFilter(name)) {
        runCatching { getPokemonByHabitatUseCase.sync(name) }
    }

    fun selectColorFilter(name: String) = applyFilter(ActiveFilter.ColorFilter(name)) {
        runCatching { getPokemonByColorUseCase.sync(name) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, errorMessage = null) }
        if (query.isBlank()) {
            if (activeFilter.value is ActiveFilter.Search) clearFilters()
        } else {
            activeFilter.value = ActiveFilter.Search(query)
            _uiState.update { it.copy(activeFilter = ActiveFilter.Search(query), isLoadingFilter = false) }
        }
    }

    fun clearFilters() {
        activeFilter.value = ActiveFilter.None
        _uiState.update {
            it.copy(activeFilter = ActiveFilter.None, searchQuery = "", errorMessage = null)
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun applyFilter(filter: ActiveFilter, syncBlock: suspend () -> Result<Unit>) {
        activeFilter.value = filter
        _uiState.update { it.copy(activeFilter = filter, searchQuery = "", isLoadingFilter = true, errorMessage = null) }
        viewModelScope.launch {
            syncBlock().onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
            _uiState.update { it.copy(isLoadingFilter = false) }
        }
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
            getTypesUseCase().collect { types -> _uiState.update { it.copy(types = types) } }
        }
        viewModelScope.launch {
            getGenerationsUseCase().collect { gens -> _uiState.update { it.copy(generations = gens) } }
        }
        viewModelScope.launch {
            getHabitatsUseCase().collect { hs -> _uiState.update { it.copy(habitats = hs) } }
        }
        viewModelScope.launch {
            getColorsUseCase().collect { cs -> _uiState.update { it.copy(colors = cs) } }
        }
    }

    private fun loadCatalogs() {
        viewModelScope.launch { runCatching { getTypesUseCase.refresh() } }
        viewModelScope.launch { runCatching { getGenerationsUseCase.refresh() } }
        viewModelScope.launch { runCatching { getHabitatsUseCase.refresh() } }
        viewModelScope.launch { runCatching { getColorsUseCase.refresh() } }
    }
}
