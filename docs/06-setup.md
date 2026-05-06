# 06 - Como ejecutar el proyecto

## Requisitos

- **Android Studio** Ladybug (2024.2.1) o mas reciente.
- **JDK 17** (Android Studio lo trae embebido).
- **Android SDK 35** instalado (compileSdk = targetSdk = 35).
- **Emulador o dispositivo** con minSdk 28 (Android 9 o superior).
- Conexion a internet en el primer arranque para poblar el cache.

## Pasos

1. **Clonar el repo**:
   ```bash
   git clone git@github.com:A-F-V-A/pokedex-android-compose.git
   cd pokedex-android-compose
   ```

2. **Abrir en Android Studio** desde el directorio raiz. Aceptar el sync inicial de Gradle.

3. **Esperar a que descargue dependencias**. La primera vez baja:
   - AGP 8.7.3
   - Gradle 8.9
   - Compose BOM 2024.10
   - Hilt, Retrofit, Room, Paging 3, Coil, etc.

4. **Compilar** (opcional pero recomendado para verificar):
   ```bash
   ./gradlew assembleDebug
   ```

5. **Ejecutar** desde Android Studio (boton Run con el emulador seleccionado) o por linea de comandos:
   ```bash
   ./gradlew installDebug
   ```

## Verificacion del flujo completo

Una vez instalado:

1. Abrir la app -> debe mostrar 20 Pokemon en cards con gradiente.
2. Hacer scroll hacia abajo -> debe cargar mas paginas automaticamente (Paging 3).
3. Tocar el dropdown "Tipo" -> elegir `fire` -> el grid se filtra a Pokemon de fuego, las cards usan el color del tipo.
4. Tocar "Limpiar filtros" -> vuelve a la lista paginada.
5. Tocar el dropdown "Generacion" -> elegir `generation-i` -> aparecen los 151 Pokemon de Kanto.
6. Tocar una card -> abre el detalle con hero gradiente, stats, evolucion animada, abilities con descripcion y ubicaciones donde encontrarlo.
7. Tocar el boton "Atrapar" (Pokeball roja) -> animacion de captura, el Pokemon se guarda en SQLite.
8. Bottom nav -> "Mis atrapados" -> ver la lista de capturados con contador en la AppBar.
9. Tocar "Liberar" en una card de atrapados -> dialog de confirmacion -> el Pokemon vuelve a la naturaleza.
10. **Modo offline**: activar modo avion, cerrar y reabrir la app. Debe seguir mostrando todo lo cacheado, los atrapados, y aparecer el banner rojo "Sin conexion".

## Logs

Las llamadas HTTP se imprimen en Logcat con `OkHttpHttpLoggingInterceptor` (nivel `BODY` en debug). Filtrar por:

```
tag:OkHttp
```

Para ver lo que esta cacheando Room, abrir el `Database Inspector` desde Android Studio (View -> Tool Windows -> App Inspection -> Database Inspector). Se ven en vivo las tablas `pokemon`, `remote_keys`, etc.

## Estructura de archivos relevantes

- Configuracion de Gradle: `build.gradle.kts`, `app/build.gradle.kts`, `gradle/libs.versions.toml`.
- Manifest con permisos: `app/src/main/AndroidManifest.xml`.
- Punto de entrada: `MainActivity.kt` y `PokedexApp.kt`.

## Problemas comunes

- **"Hilt cannot find provider"** -> revisar que el feature tenga su modulo en `feature/<x>/di/` y este anotado con `@InstallIn(SingletonComponent::class)`.
- **"unresolved reference BuildConfig"** -> rebuild del proyecto. La constante `BASE_URL` se genera al compilar gracias a `buildConfigField`.
- **La lista no carga**: verificar que el dispositivo tenga conexion. La primera carga necesita la API; despues, el cache permite seguir funcionando offline.
