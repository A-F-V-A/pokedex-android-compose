# Documentacion - Pokedex Android Compose

Esta carpeta contiene toda la documentacion del proyecto. Cada archivo cubre un aspecto puntual y los diagramas estan escritos en Mermaid (se renderizan directo en GitHub) y DBML (se renderiza en https://dbdiagram.io).

## Indice

| # | Documento | Contenido |
|---|---|---|
| 01 | [Arquitectura](01-architecture.md) | Capas, flujo de dependencias, diagrama de componentes y de clases |
| 02 | [PokeAPI](02-pokeapi.md) | Por que se eligio, endpoints usados, ejemplos de request/response |
| 03 | [Base de datos (Room/SQLite)](03-database.md) | Esquema, tablas, indices, justificacion. Diagrama ER y DBML |
| 04 | [Flujo de datos](04-data-flow.md) | Diagramas de secuencia: online, offline, filtros, paginacion |
| 05 | [Estructura de carpetas](05-folder-structure.md) | Como esta organizado el codigo y por que (SOLID) |
| 06 | [Como ejecutar](06-setup.md) | Requisitos, pasos para abrir, correr en emulador |

## Diagramas

- [`diagrams/schema.dbml`](diagrams/schema.dbml) - esquema completo de la base de datos en formato DBML

## Como leer esta documentacion

1. Si es la primera vez que ves el proyecto: empieza por [01-architecture](01-architecture.md) para entender la forma general, despues [05-folder-structure](05-folder-structure.md) para saber donde esta cada cosa.
2. Si vas a agregar un endpoint o cambiar un mapper: leer [02-pokeapi](02-pokeapi.md).
3. Si vas a tocar el modelo de datos local: leer [03-database](03-database.md) y revisar el DBML.
4. Si quieres seguir un caso de uso end-to-end (por ejemplo "filtrar por tipo"): [04-data-flow](04-data-flow.md).
5. Si solo queres correr el proyecto: [06-setup](06-setup.md).
