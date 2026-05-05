package co.edu.uqvirtual.pokedex_android_compose.shared.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object TypeColors {
    val Normal = Color(0xFFA8A878)
    val Fire = Color(0xFFF08030)
    val Water = Color(0xFF6890F0)
    val Electric = Color(0xFFF8D030)
    val Grass = Color(0xFF78C850)
    val Ice = Color(0xFF98D8D8)
    val Fighting = Color(0xFFC03028)
    val Poison = Color(0xFFA040A0)
    val Ground = Color(0xFFE0C068)
    val Flying = Color(0xFFA890F0)
    val Psychic = Color(0xFFF85888)
    val Bug = Color(0xFFA8B820)
    val Rock = Color(0xFFB8A038)
    val Ghost = Color(0xFF705898)
    val Dragon = Color(0xFF7038F8)
    val Dark = Color(0xFF705848)
    val Steel = Color(0xFFB8B8D0)
    val Fairy = Color(0xFFEE99AC)
    val Unknown = Color(0xFF68A090)

    fun forType(name: String): Color = when (name.lowercase()) {
        "normal" -> Normal
        "fire" -> Fire
        "water" -> Water
        "electric" -> Electric
        "grass" -> Grass
        "ice" -> Ice
        "fighting" -> Fighting
        "poison" -> Poison
        "ground" -> Ground
        "flying" -> Flying
        "psychic" -> Psychic
        "bug" -> Bug
        "rock" -> Rock
        "ghost" -> Ghost
        "dragon" -> Dragon
        "dark" -> Dark
        "steel" -> Steel
        "fairy" -> Fairy
        else -> Unknown
    }

    fun gradientFor(types: List<String>): Brush {
        val colors = when {
            types.isEmpty() -> listOf(Unknown.copy(alpha = 0.85f), Unknown.copy(alpha = 0.4f))
            types.size == 1 -> {
                val base = forType(types[0])
                listOf(base.copy(alpha = 0.95f), base.copy(alpha = 0.55f))
            }
            else -> listOf(
                forType(types[0]).copy(alpha = 0.95f),
                forType(types[1]).copy(alpha = 0.75f)
            )
        }
        return Brush.verticalGradient(colors = colors)
    }
}
