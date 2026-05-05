package co.edu.uqvirtual.pokedex_android_compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.PokemonDetailScreen
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation.PokemonListScreen

@Composable
fun PokedexNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.POKEMON_LIST
    ) {
        composable(Routes.POKEMON_LIST) {
            PokemonListScreen(
                onPokemonClick = { id -> navController.navigate(Routes.pokemonDetail(id)) }
            )
        }
        composable(
            route = Routes.POKEMON_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_POKEMON_ID) { type = NavType.IntType })
        ) {
            PokemonDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
