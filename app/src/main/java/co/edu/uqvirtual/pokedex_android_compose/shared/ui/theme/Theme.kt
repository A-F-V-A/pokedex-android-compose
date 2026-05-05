package co.edu.uqvirtual.pokedex_android_compose.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PokedexRed,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = PokedexBlue,
    tertiary = PokedexYellow,
    background = PokedexSurface,
    surface = PokedexSurface,
    onSurface = PokedexOnSurface
)

private val DarkColors = darkColorScheme(
    primary = PokedexRedDark,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = PokedexBlue,
    tertiary = PokedexYellow,
    background = PokedexSurfaceDark,
    surface = PokedexSurfaceDark,
    onSurface = PokedexOnSurfaceDark
)

@Composable
fun PokedexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = PokedexTypography,
        content = content
    )
}
