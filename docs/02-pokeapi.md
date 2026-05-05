# 02 - PokeAPI

## Por que esta API

Se eligio [PokeAPI v2](https://pokeapi.co/docs/v2) como fuente de datos. Razones:

- **Publica y gratuita**: no requiere API key, autenticacion ni cuenta.
- **Bien documentada**: cada endpoint esta descrito en https://pokeapi.co/docs/v2 con ejemplos de payload.
- **Volumen suficiente**: ~1300 Pokemon, ~20 tipos, ~9 generaciones. Es ideal para probar paginacion real.
- **Recursos relacionados**: la API expone tipos, generaciones, habilidades. Se aprovechan para los filtros.
- **Rate limit suave**: 100 requests/min sin auth, mas que suficiente para uso academico.
- **Recomendada para cache agresivo**: la propia documentacion sugiere cachear localmente, lo que justifica el uso de Room.
- **Imagenes oficiales**: las URLs de los sprites son estables y predecibles, ideales para `AsyncImage`.

URL base: `https://pokeapi.co/api/v2/` (definida como `BuildConfig.BASE_URL` en `app/build.gradle.kts`).

## Endpoints utilizados

La actividad pide minimo **4 endpoints**. Esta app usa **5 endpoints distintos** distribuidos por feature.

| # | Endpoint | Metodo | Donde se usa | Para que |
|---|---|---|---|---|
| 1 | `/pokemon?limit&offset` | GET | `PokemonApi.getPokemonList` | Lista paginada principal |
| 2 | `/pokemon/{id}` | GET | `PokemonDetailApi.getPokemonDetail` | Pantalla de detalle |
| 3 | `/type/{name}` | GET | `PokemonApi.getPokemonByType` | Filtro #1 (tipo) |
| 4 | `/generation/{name}` | GET | `PokemonApi.getPokemonByGeneration` | Filtro #2 (generacion) |
| 5 | `/type` | GET | `PokemonApi.getTypes` | Catalogo para el dropdown |
| 6 | `/generation` | GET | `PokemonApi.getGenerations` | Catalogo para el dropdown |

> Los puntos 5 y 6 son auxiliares: rellenan los dropdowns. Los filtros propiamente dichos (3 y 4) son los que satisfacen el requisito de "filtrar por dos campos diferentes via API".

## Detalle endpoint por endpoint

### 1) Lista paginada - `GET /pokemon?limit=20&offset=0`

```json
{
  "count": 1302,
  "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
  "previous": null,
  "results": [
    { "name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/" },
    { "name": "ivysaur",   "url": "https://pokeapi.co/api/v2/pokemon/2/" }
  ]
}
```

**DTO Kotlin**: `PokemonListResponseDto` y `NamedResourceDto` en `feature/pokemonlist/data/remote/dto/`.

El id se extrae con `extractIdFromUrl` desde la url (`.../pokemon/25/` -> `25`). La imagen se construye con la URL del official artwork: `https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/{id}.png`.

**Donde**: `PokemonRemoteMediator.load()` con `LoadType.REFRESH/APPEND` para alimentar Paging 3.

### 2) Detalle - `GET /pokemon/{id}` (ej: `/pokemon/25`)

```json
{
  "id": 25,
  "name": "pikachu",
  "height": 4,
  "weight": 60,
  "base_experience": 112,
  "types": [{ "type": { "name": "electric" } }],
  "stats": [
    { "base_stat": 35, "stat": { "name": "hp" } },
    { "base_stat": 55, "stat": { "name": "attack" } }
  ],
  "abilities": [{ "ability": { "name": "static" } }],
  "sprites": {
    "front_default": "https://.../25.png",
    "other": {
      "official-artwork": { "front_default": "https://.../25.png" }
    }
  }
}
```

**DTO Kotlin**: `PokemonDetailDto` (con sub-DTOs `StatDto`, `TypeSlotDto`, `AbilitySlotDto`, `SpritesDto`).

Se mapea a `PokemonDetailEntity` (Room) y luego a `PokemonDetail` (dominio). Los tipos y habilidades se almacenan como CSV (`typesCsv`, `abilitiesCsv`) para evitar tablas adicionales.

**Donde**: `PokemonDetailRepositoryImpl.refresh(id)`, llamado desde `PokemonDetailViewModel.init`.

### 3) Filtro por tipo - `GET /type/{name}` (ej: `/type/fire`)

```json
{
  "id": 10,
  "name": "fire",
  "pokemon": [
    { "pokemon": { "name": "charmander", "url": "https://.../pokemon/4/" } },
    { "pokemon": { "name": "charmeleon", "url": "https://.../pokemon/5/" } }
  ]
}
```

Devuelve **toda la lista** de Pokemon de ese tipo de una vez, sin paginar.

**DTO Kotlin**: `TypeResponseDto` con `TypeMemberDto`.

**Donde**: `PokemonRepositoryImpl.syncPokemonForType(typeName)` -> upsert en Room + cross refs `pokemon_type_cross_ref`. La UI luego observa `Flow<List<Pokemon>>` desde el DAO con un `INNER JOIN`.

### 4) Filtro por generacion - `GET /generation/{name}` (ej: `/generation/generation-i`)

```json
{
  "id": 1,
  "name": "generation-i",
  "pokemon_species": [
    { "name": "bulbasaur", "url": "https://.../pokemon-species/1/" },
    { "name": "ivysaur",   "url": "https://.../pokemon-species/2/" }
  ]
}
```

Devuelve los 151 Pokemon de Kanto (para generation-i).

**Importante**: `pokemon_species` apunta a `/pokemon-species/{id}` no a `/pokemon/{id}`. El id sigue siendo el mismo de Pokedex nacional, asi que se puede usar el mismo `extractIdFromUrl`.

**DTO Kotlin**: `GenerationResponseDto`.

**Donde**: `PokemonRepositoryImpl.syncPokemonForGeneration(generationName)`.

### 5) y 6) Catalogos `/type` y `/generation`

Ambos devuelven la misma estructura que la lista de Pokemon (`{count, next, previous, results}`). Se reusa `PokemonListResponseDto`.

Se cargan al iniciar `PokemonListViewModel` para poblar los dropdowns del `FilterBar`.

## Manejo de errores

- `IOException` -> sin red. El `RemoteMediator` retorna `MediatorResult.Error(e)`, Paging 3 lo expone como `LoadState.Error`. La UI muestra `ErrorView` solo si no hay datos cacheados; si hay cache, simplemente no agrega mas paginas.
- `HttpException` -> error HTTP (404, 500, etc). Mismo manejo.
- En filtros, los errores se atrapan con `runCatching` y se exponen via `errorMessage` en el UiState.

## Rate limit y politeness

PokeAPI sugiere cachear localmente y no martillar el servidor. La estrategia de la app:

- La lista paginada solo pega a la API cuando el usuario hace scroll (Paging 3 maneja el throttling).
- Los catalogos de tipos y generaciones se piden una unica vez por sesion.
- Los detalles se cachean en Room indefinidamente; un refresh manual los actualiza.
