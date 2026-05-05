# 04 - Flujo de datos

Esta seccion muestra como viajan los datos para los cuatro escenarios principales: lista online, lista offline, filtro por tipo, paginacion con RemoteMediator.

## 1. Lista paginada (online)

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as PokemonListScreen
    participant VM as PokemonListViewModel
    participant UC as GetPagedPokemonUseCase
    participant R as PokemonRepositoryImpl
    participant Pager as Pager + RemoteMediator
    participant API as PokemonApi
    participant DB as PokedexDatabase

    U->>S: Abre la app
    S->>VM: hiltViewModel()
    VM->>UC: invoke()
    UC->>R: getPagedPokemon()
    R->>Pager: Pager(config, mediator, dao.pagingSource())
    Pager->>API: GET /pokemon?limit=20&offset=0
    API-->>Pager: 200 OK + 20 NamedResource
    Pager->>DB: upsertAll(pokemon) + insertAll(remoteKeys)
    DB-->>Pager: emit PagingSource invalidated
    Pager-->>R: Flow<PagingData<PokemonEntity>>
    R-->>UC: map -> Flow<PagingData<Pokemon>>
    UC-->>VM: cachedIn(viewModelScope)
    VM-->>S: pagingItems.collectAsLazyPagingItems()
    S-->>U: Renderiza grid con 20 cards
```

## 2. Lista offline

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as PokemonListScreen
    participant VM as PokemonListViewModel
    participant CONN as ConnectivityObserver
    participant Pager as Pager + RemoteMediator
    participant API as PokemonApi
    participant DB as PokedexDatabase

    U->>S: Abre la app sin red
    S->>VM: collectAsStateWithLifecycle()
    CONN-->>VM: status = Lost
    VM-->>S: uiState.isOffline = true
    S-->>U: Muestra ConnectivityBanner
    Pager->>API: GET /pokemon?limit=20&offset=0
    API--xPager: IOException
    Pager-->>S: LoadState.Error en append
    Pager->>DB: pagingSource() (datos cacheados previos)
    DB-->>Pager: 100 PokemonEntity de cache
    Pager-->>S: Renderiza grid con datos cacheados
    Note over S,U: La app sigue siendo navegable<br/>con la informacion guardada
```

## 3. Filtro por tipo

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as PokemonListScreen
    participant VM as PokemonListViewModel
    participant UC as GetPokemonByTypeUseCase
    participant R as PokemonRepositoryImpl
    participant API as PokemonApi
    participant DB as PokedexDatabase

    U->>S: Abre dropdown de tipo y selecciona "fire"
    S->>VM: selectTypeFilter("fire")
    VM->>VM: activeFilter = ActiveFilter.Type("fire")
    VM->>VM: uiState.isLoadingFilter = true
    par Sincronizacion
        VM->>UC: sync("fire")
        UC->>R: syncPokemonForType("fire")
        R->>API: GET /type/fire
        API-->>R: TypeResponseDto (~64 pokemon)
        R->>DB: upsertAll(pokemon) + clearCrossRefsForType + upsertCrossRefs
    and Observacion
        VM->>UC: invoke("fire")
        UC->>R: observePokemonByType("fire")
        R->>DB: pokemonDao.getByType("fire")<br/>(JOIN con cross ref)
        DB-->>R: Flow<List<PokemonEntity>>
        R-->>VM: Flow<List<Pokemon>>
        VM-->>S: filteredResults
    end
    S-->>U: Renderiza grid filtrado
```

## 4. Paginacion con RemoteMediator (APPEND)

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as PokemonListScreen
    participant LP as LazyPagingItems
    participant Pager as Pager
    participant Med as PokemonRemoteMediator
    participant API as PokemonApi
    participant DB as PokedexDatabase

    U->>S: Scroll hasta el final
    S->>LP: Pide siguiente item
    LP->>Pager: load(LoadType.APPEND)
    Pager->>Med: load(APPEND, state)
    Med->>DB: remoteKeysDao.getById(lastItem.id)
    DB-->>Med: nextKey = 4
    Med->>API: GET /pokemon?limit=20&offset=60
    API-->>Med: 20 nuevos NamedResource
    Med->>DB: withTransaction { upsertAll(pokemon) + insertAll(keys) }
    Med-->>Pager: MediatorResult.Success(endOfPagination=false)
    Pager-->>LP: nuevos items disponibles
    LP-->>S: 20 cards adicionales
    S-->>U: Grid extendido sin recarga
```

## 5. Detalle de un Pokemon

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as PokemonDetailScreen
    participant VM as PokemonDetailViewModel
    participant UC as GetPokemonDetailUseCase
    participant R as PokemonDetailRepositoryImpl
    participant API as PokemonDetailApi
    participant DB as PokedexDatabase

    U->>S: Toca una card
    S->>VM: hiltViewModel() con savedStateHandle["pokemonId"]
    par Observa cache
        VM->>UC: invoke(id)
        UC->>R: observeDetail(id)
        R->>DB: dao.observeById(id)
        DB-->>R: PokemonDetailEntity? (puede ser null)
        R-->>VM: Flow<PokemonDetail?>
    and Refresca desde la red
        VM->>UC: refresh(id)
        UC->>R: refresh(id)
        R->>API: GET /pokemon/{id}
        API-->>R: PokemonDetailDto
        R->>DB: dao.upsert(toEntity(now))
        DB-->>R: emite cambio en observeById
    end
    VM-->>S: uiState.detail = PokemonDetail(...)
    S-->>U: Renderiza imagen, stats, abilities
```

## Resumen visual

```mermaid
flowchart LR
    UI[UI - Compose] -->|observa| VM[ViewModel - StateFlow]
    VM -->|invoca| UC[UseCase]
    UC -->|interfaz| Repo[Repository]
    Repo -->|fuente unica| Room[(Room)]
    Repo -->|sync| API[Retrofit -> PokeAPI]
    API -.->|guarda| Room
    CONN[ConnectivityObserver] --> VM
```

> Las flechas continuas representan dependencias directas. Las punteadas representan flujo de datos asincrono que cierra el ciclo (la API escribe a Room y la UI lo recibe via Flow).
