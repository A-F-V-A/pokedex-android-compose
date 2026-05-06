# 02 - PokeAPI

## Por que esta API

Se eligio [PokeAPI v2](https://pokeapi.co/docs/v2) como fuente de datos. Razones:

- **Publica y gratuita**: no requiere API key, autenticacion ni cuenta.
- **Bien documentada**: cada endpoint esta descrito en https://pokeapi.co/docs/v2 con ejemplos de payload.
- **Volumen suficiente**: ~1300 Pokemon, ~20 tipos, ~9 generaciones. Es ideal para probar paginacion real.
- **Recursos relacionados**: la API expone tipos, generaciones, habilidades, evoluciones, encuentros. Perfectos para enriquecer la UI.
- **Rate limit suave**: 100 requests/min sin auth, mas que suficiente para uso academico.
- **Recomendada para cache agresivo**: la propia documentacion sugiere cachear localmente, lo que justifica el uso de Room.
- **Imagenes oficiales**: las URLs de los sprites son estables y predecibles, ideales para `AsyncImage`.

URL base: `https://pokeapi.co/api/v2/` (definida como `BuildConfig.BASE_URL` en `app/build.gradle.kts`).

## Endpoints utilizados

La actividad pide minimo **4 endpoints**. Esta app consume **10 endpoints distintos**.

| # | Endpoint | Metodo | Donde se usa | Para que |
|---|---|---|---|---|
| 1 | `/pokemon?limit&offset` | GET | `PokemonApi.getPokemonList` | Lista paginada principal |
| 2 | `/pokemon/{id}` | GET | `PokemonDetailApi.getPokemonDetail` | Pantalla de detalle |
| 3 | `/type/{name}` | GET | `PokemonApi.getPokemonByType` | Filtro por tipo |
| 4 | `/generation/{name}` | GET | `PokemonApi.getPokemonByGeneration` | Filtro por generacion |
| 5 | `/type` | GET | `PokemonApi.getTypes` | Catalogo para dropdown |
| 6 | `/generation` | GET | `PokemonApi.getGenerations` | Catalogo para dropdown |
| 7 | `/pokemon-species/{id}` | GET | `PokemonDetailApi.getPokemonSpecies` | Color, habitat, capture rate, flavor text, link al evolution chain |
| 8 | `/evolution-chain/{id}` | GET | `PokemonDetailApi.getEvolutionChainByUrl` | Cadena de evoluciones con minLevel y triggers |
| 9 | `/ability/{name}` | GET | `PokemonDetailApi.getAbility` | Efecto y descripcion de cada habilidad |
| 10 | `/pokemon/{id}/encounters` | GET | `PokemonDetailApi.getPokemonEncounters` | Donde se encuentra el Pokemon y probabilidad |

## Detalle endpoint por endpoint

### 1) Lista paginada - `GET /pokemon?limit=20&offset=0`

```json
{
  "count": 1302,
  "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
  "previous": null,
  "results": [
    { "name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/" }
  ]
}
```

**DTO**: `PokemonListResponseDto`. **Donde**: `PokemonRemoteMediator.load()` con `LoadType.REFRESH/APPEND` para Paging 3.

### 2) Detalle - `GET /pokemon/{id}`

```json
{
  "id": 25, "name": "pikachu", "height": 4, "weight": 60, "base_experience": 112,
  "types": [{ "type": { "name": "electric" } }],
  "stats": [{ "base_stat": 35, "stat": { "name": "hp" } }],
  "abilities": [{ "ability": { "name": "static" } }],
  "sprites": { "other": { "official-artwork": { "front_default": "https://.../25.png" } } }
}
```

**DTO**: `PokemonDetailDto`. **Donde**: `PokemonDetailRepositoryImpl.refresh(id)`.

### 3) Filtro por tipo - `GET /type/{name}`

```json
{ "id": 10, "name": "fire", "pokemon": [{ "pokemon": { "name": "charmander", "url": "..." } }] }
```

**DTO**: `TypeResponseDto`. **Donde**: `PokemonRepositoryImpl.syncPokemonForType(typeName)`.

### 4) Filtro por generacion - `GET /generation/{name}`

```json
{ "id": 1, "name": "generation-i", "pokemon_species": [{ "name": "bulbasaur", "url": "..." }] }
```

**DTO**: `GenerationResponseDto`. **Donde**: `PokemonRepositoryImpl.syncPokemonForGeneration(generationName)`.

### 5) y 6) Catalogos `/type` y `/generation`

Devuelven la misma estructura que la lista de Pokemon. Se cargan al iniciar `PokemonListViewModel` para poblar los dropdowns.

### 7) Especie - `GET /pokemon-species/{id}`

```json
{
  "id": 25, "name": "pikachu",
  "color": { "name": "yellow" },
  "habitat": { "name": "forest" },
  "is_legendary": false, "is_mythical": false,
  "capture_rate": 190,
  "flavor_text_entries": [{ "flavor_text": "When several...", "language": { "name": "en" } }],
  "evolution_chain": { "url": "https://pokeapi.co/api/v2/evolution-chain/10/" }
}
```

**DTO**: `PokemonSpeciesDto`. **Mapeo**: filtra `flavor_text_entries` por idioma `en` y limpia los caracteres de control. Devuelve `PokemonSpeciesInfo` con badges de legendario/mitico, habitat y capture_rate. La URL de la cadena evolutiva se pasa al endpoint 8.

**Donde**: `PokemonDetailViewModel.loadSpeciesAndEvolution()` muestra el flavor en la tarjeta "Pokedex" del detalle, badges en `SpeciesBadgesRow` y dispara la carga de la cadena evolutiva.

### 8) Cadena evolutiva - `GET /evolution-chain/{id}` (URL completa)

```json
{
  "id": 10,
  "chain": {
    "species": { "name": "pichu", "url": "https://pokeapi.co/api/v2/pokemon-species/172/" },
    "evolves_to": [{
      "species": { "name": "pikachu", "url": "..." },
      "evolution_details": [{ "min_level": null, "trigger": { "name": "level-up" } }],
      "evolves_to": [{ "species": { "name": "raichu", "url": "..." }, "evolution_details": [...] }]
    }]
  }
}
```

**DTO**: `EvolutionChainDto` + `ChainLinkDto` recursivo. **Mapeo**: aplana el arbol a una `List<EvolutionStage>` con `pokemonId`, `name`, `imageUrl`, `minLevel` y `itemRequired`. Aprovecha la URL del species para extraer el id y construir la imagen oficial.

**Donde**: `EvolutionRow` lo renderiza con animacion staggered (cada stage entra con delay y scale tween).

### 9) Habilidad - `GET /ability/{name}`

```json
{
  "id": 9, "name": "static",
  "effect_entries": [{ "short_effect": "Has a 30% chance...", "language": { "name": "en" } }],
  "flavor_text_entries": [{ "flavor_text": "Body is charged...", "language": { "name": "en" } }]
}
```

**DTO**: `AbilityDto`. **Mapeo**: `AbilityDetail(name, shortEffect, flavorText)`.

**Donde**: `PokemonDetailViewModel.loadAbilities` resuelve cada habilidad del Pokemon en paralelo con `runCatching` (no bloquea si una falla). El detalle pinta `AbilityCard` con el nombre y el short effect debajo.

### 10) Encuentros - `GET /pokemon/{id}/encounters`

```json
[
  {
    "location_area": { "name": "kanto-route-2-south-towards-viridian-city" },
    "version_details": [{ "max_chance": 25, "version": { "name": "red" } }]
  }
]
```

Devuelve un **array directo**. **DTO**: `EncounterDto`. **Mapeo**: `EncounterLocation(name, maxChance, versions)` agrega y se queda con la mejor probabilidad por ubicacion.

**Donde**: en la pantalla de detalle, dentro de la `SectionCard` "Donde encontrarlo", muestra hasta 8 ubicaciones con la chance maxima.

## Manejo de errores

- `IOException` (sin red) y `HttpException` (4xx/5xx): atrapados con `runCatching` y exposicion via `errorMessage` del UiState. Los datos cacheados en Room siguen mostrandose.
- En el detalle, los endpoints opcionales (species, evolution, ability, encounters) fallan silenciosamente: el detalle principal sigue funcionando.

## Rate limit y politeness

- Lista paginada: solo pega cuando el usuario hace scroll (Paging 3 throttling).
- Catalogos: una sola vez por sesion.
- Detalles: cacheados en Room indefinidamente, refresh manual.
- Endpoints auxiliares (species/evolution/ability/encounters): no se cachean en Room para mantener el modelo simple, pero se podrian cachear con poca infraestructura adicional.
