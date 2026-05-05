# 05 - Estructura de carpetas

```
co/edu/uqvirtual/pokedex_android_compose/
|-- PokedexApp.kt                       (@HiltAndroidApp)
|-- MainActivity.kt                     (@AndroidEntryPoint, host de Compose)
|-- navigation/
|   |-- PokedexNavGraph.kt              (NavHost que conecta los features)
|   `-- Routes.kt                        (constantes de rutas)
|
|-- core/                               INFRAESTRUCTURA TRANSVERSAL
|   |-- network/
|   |   |-- ConnectivityObserver.kt
|   |   |-- NetworkConnectivityObserver.kt
|   |   `-- di/NetworkModule.kt         (Retrofit, OkHttp, Moshi, Connectivity)
|   |-- database/
|   |   |-- PokedexDatabase.kt
|   |   `-- di/DatabaseModule.kt        (Room + DAOs como providers)
|   `-- common/
|       |-- Resource.kt                 (Loading/Success/Error)
|       `-- DispatcherProvider.kt
|
|-- shared/                             UI Y UTILIDADES REUTILIZABLES
|   `-- ui/
|       |-- theme/                      (Color, Type, Theme con Material3)
|       `-- components/
|           |-- ConnectivityBanner.kt
|           |-- LoadingIndicator.kt
|           |-- ErrorView.kt
|           `-- PokemonCard.kt
|
`-- feature/
    |-- pokemonlist/
    |   |-- data/
    |   |   |-- remote/
    |   |   |   |-- PokemonApi.kt
    |   |   |   `-- dto/                 (PokemonListResponseDto, TypeResponseDto, GenerationResponseDto)
    |   |   |-- local/                   (PokemonEntity, TypeEntity, GenerationEntity, cross refs, DAOs)
    |   |   |-- mapper/PokemonMappers.kt
    |   |   |-- paging/PokemonRemoteMediator.kt
    |   |   `-- repository/PokemonRepositoryImpl.kt
    |   |-- domain/
    |   |   |-- model/                   (Pokemon, PokemonType, Generation)
    |   |   |-- repository/PokemonRepository.kt
    |   |   `-- usecase/                 (Get*UseCase x5)
    |   |-- presentation/
    |   |   |-- PokemonListViewModel.kt
    |   |   |-- PokemonListScreen.kt
    |   |   |-- PokemonListUiState.kt
    |   |   `-- components/FilterBar.kt
    |   `-- di/PokemonListModule.kt
    `-- pokemondetail/
        |-- data/
        |   |-- remote/                  (PokemonDetailApi, dto)
        |   |-- local/                   (PokemonDetailEntity, PokemonDetailDao)
        |   |-- mapper/PokemonDetailMappers.kt
        |   `-- repository/PokemonDetailRepositoryImpl.kt
        |-- domain/
        |   |-- model/PokemonDetail.kt
        |   |-- repository/PokemonDetailRepository.kt
        |   `-- usecase/GetPokemonDetailUseCase.kt
        |-- presentation/
        |   |-- PokemonDetailViewModel.kt
        |   |-- PokemonDetailScreen.kt
        |   |-- PokemonDetailUiState.kt
        |   `-- components/StatBar.kt
        `-- di/PokemonDetailModule.kt
```

## Que vive en cada nivel

| Carpeta | Rol | Quien depende de ella |
|---|---|---|
| `core/` | Infraestructura compartida que no es propia de ningun feature | Todos los `feature/*` y `MainActivity` |
| `shared/` | Componentes Compose y theme que se reusan en mas de un feature | Cualquier `feature/*/presentation` |
| `feature/<x>/` | Una funcionalidad vertical completa (data + domain + presentation) | El `NavGraph` y entre si solo a traves de modelos de dominio |
| `navigation/` | Grafo Compose Navigation y constantes de ruta | Es el "punto de union" de la app |

## SOLID aplicado al feature `pokemonlist`

| Principio | Como se aplica | Archivo de referencia |
|---|---|---|
| **S - Single Responsibility** | El `PokemonListViewModel` solo mapea estado de dominio a UiState. Cada UseCase encapsula una unica operacion. El `PokemonRepositoryImpl` solo media entre API, DAOs y RemoteMediator. | `PokemonListViewModel.kt`, `GetPagedPokemonUseCase.kt` |
| **O - Open/Closed** | Para agregar un filtro nuevo (por habilidad, por color), se crea un nuevo UseCase y una nueva query DAO sin modificar lo existente. | Patron en `GetPokemonByTypeUseCase` |
| **L - Liskov** | `PokemonRepository` (interface) se puede reemplazar por una `FakePokemonRepository` en tests sin romper a los UseCases. | `domain/repository/PokemonRepository.kt` |
| **I - Interface Segregation** | Cinco UseCases pequenos (cada uno con `invoke(...)`) en lugar de un `PokemonInteractor` con cinco metodos. El ViewModel solo inyecta los que necesita. | Carpeta `domain/usecase/` |
| **D - Dependency Inversion** | El ViewModel depende de UseCases; los UseCases dependen del `PokemonRepository` (interface). Hilt resuelve la implementacion concreta via `@Binds` en `PokemonListBindsModule`. | `di/PokemonListModule.kt` |

## Por que `core` puede importar de `feature` (en este caso)

`PokedexDatabase` (en `core/database/`) importa entidades que viven en `feature/<x>/data/local/`. En una arquitectura multi-modulo estricta esto no se permitiria. Aca, en un solo modulo, se acepta como excepcion pragmatica porque Room exige una unica clase `@Database` que conozca todas las entidades. Si en el futuro el proyecto se modulariza, `PokedexDatabase` se moveria a un modulo `:database` separado que dependa de los modulos `:feature:*`.

## Regla de oro

- `domain/` **nunca** importa de Android, Retrofit ni Room.
- `presentation/` **nunca** importa de Retrofit ni Room (solo del dominio).
- `data/` puede importar lo que sea (Retrofit, Room, modelos de dominio).
