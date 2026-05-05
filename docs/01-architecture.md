# 01 - Arquitectura

La aplicacion esta organizada en **capas limpias** (Clean Architecture) con **organizacion vertical por feature**. La regla principal: las dependencias apuntan **hacia adentro** (UI -> ViewModel -> UseCase -> Repository interface), nunca al reves.

## Capas

| Capa | Responsabilidad | Conoce a... | Usa... |
|---|---|---|---|
| **Presentation** | Composables y ViewModels | Domain | Compose, StateFlow, Hilt |
| **Domain** | Use cases y modelos puros | Solo a si misma | Coroutines |
| **Data** | Implementaciones de repositorio, API, DAOs, mappers | Domain | Retrofit, Room, Paging 3 |
| **Core** | Infraestructura compartida | - | Hilt, OkHttp, ConnectivityManager |
| **Shared** | UI reutilizable y theme | Domain (solo modelos) | Compose |

## Diagrama de componentes

```mermaid
flowchart TB
    subgraph UI["Presentation (Compose)"]
        S1[PokemonListScreen]
        S2[PokemonDetailScreen]
        VM1[PokemonListViewModel]
        VM2[PokemonDetailViewModel]
    end

    subgraph DOM["Domain"]
        UC1[GetPagedPokemonUseCase]
        UC2[GetPokemonByTypeUseCase]
        UC3[GetPokemonByGenerationUseCase]
        UC4[GetTypesUseCase]
        UC5[GetGenerationsUseCase]
        UC6[GetPokemonDetailUseCase]
        IRepo1[PokemonRepository iface]
        IRepo2[PokemonDetailRepository iface]
    end

    subgraph DATA["Data"]
        Repo1[PokemonRepositoryImpl]
        Repo2[PokemonDetailRepositoryImpl]
        Med[PokemonRemoteMediator]
        API1[PokemonApi - Retrofit]
        API2[PokemonDetailApi - Retrofit]
        DAO1[PokemonDao]
        DAO2[PokemonDetailDao]
        DAO3[TypeDao]
        DAO4[GenerationDao]
        DAO5[RemoteKeysDao]
    end

    subgraph CORE["Core"]
        DB[(PokedexDatabase - Room)]
        NET[Retrofit + OkHttp]
        CONN[ConnectivityObserver]
    end

    S1 --> VM1
    S2 --> VM2
    VM1 --> UC1 & UC2 & UC3 & UC4 & UC5 & CONN
    VM2 --> UC6 & CONN
    UC1 --> IRepo1
    UC2 --> IRepo1
    UC3 --> IRepo1
    UC4 --> IRepo1
    UC5 --> IRepo1
    UC6 --> IRepo2
    IRepo1 -.binds.-> Repo1
    IRepo2 -.binds.-> Repo2
    Repo1 --> API1 & DAO1 & DAO3 & DAO4 & DAO5 & Med
    Repo2 --> API2 & DAO2
    Med --> API1 & DAO1 & DAO5
    API1 --> NET
    API2 --> NET
    DAO1 --> DB
    DAO2 --> DB
    DAO3 --> DB
    DAO4 --> DB
    DAO5 --> DB
```

## Diagrama de clases (feature pokemonlist)

```mermaid
classDiagram
    class PokemonRepository {
        <<interface>>
        +getPagedPokemon() Flow~PagingData~Pokemon~~
        +observeTypes() Flow~List~PokemonType~~
        +observeGenerations() Flow~List~Generation~~
        +observePokemonByType(name) Flow~List~Pokemon~~
        +observePokemonByGeneration(name) Flow~List~Pokemon~~
        +refreshTypes()
        +refreshGenerations()
        +syncPokemonForType(name)
        +syncPokemonForGeneration(name)
    }

    class PokemonRepositoryImpl {
        -api: PokemonApi
        -database: PokedexDatabase
        -pokemonDao: PokemonDao
        -typeDao: TypeDao
        -generationDao: GenerationDao
        -remoteKeysDao: RemoteKeysDao
    }

    class GetPagedPokemonUseCase {
        +invoke() Flow~PagingData~Pokemon~~
    }

    class GetPokemonByTypeUseCase {
        +invoke(name) Flow~List~Pokemon~~
        +sync(name)
    }

    class PokemonListViewModel {
        +pagedPokemon: Flow~PagingData~Pokemon~~
        +uiState: StateFlow~PokemonListUiState~
        +filteredResults: StateFlow~List~Pokemon~~
        +selectTypeFilter(name)
        +selectGenerationFilter(name)
        +clearFilters()
    }

    PokemonRepository <|.. PokemonRepositoryImpl
    GetPagedPokemonUseCase --> PokemonRepository
    GetPokemonByTypeUseCase --> PokemonRepository
    PokemonListViewModel --> GetPagedPokemonUseCase
    PokemonListViewModel --> GetPokemonByTypeUseCase
```

## Single Source of Truth

La UI **siempre** observa Room. Cuando llega un dato nuevo:

1. Se hace la llamada HTTP via Retrofit.
2. El DTO se mapea a Entity.
3. Se inserta en Room (upsert).
4. Room emite el cambio via Flow.
5. El ViewModel recibe el nuevo valor y la UI se recompone.

Esto garantiza que **offline funcione gratis**: si la API falla, Room sigue emitiendo el ultimo estado conocido.

## Inyeccion de dependencias

Hilt conecta todo:

- `core/network/di/NetworkModule` - Retrofit, OkHttp, Moshi, ConnectivityObserver (singletons).
- `core/database/di/DatabaseModule` - Room database y todos los DAOs.
- `feature/<x>/di/<X>Module` - API especifica del feature y `@Binds` del repositorio (interfaz -> implementacion).

Los ViewModels se obtienen con `hiltViewModel()` en los Composables.
