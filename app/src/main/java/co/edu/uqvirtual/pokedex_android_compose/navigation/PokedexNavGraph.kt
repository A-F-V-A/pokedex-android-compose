package co.edu.uqvirtual.pokedex_android_compose.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.edu.uqvirtual.pokedex_android_compose.R
import co.edu.uqvirtual.pokedex_android_compose.feature.caughtpokemon.presentation.CaughtPokemonScreen
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemondetail.presentation.PokemonDetailScreen
import co.edu.uqvirtual.pokedex_android_compose.feature.pokemonlist.presentation.PokemonListScreen
import co.edu.uqvirtual.pokedex_android_compose.shared.ui.components.PokeballIcon

@Composable
fun PokedexAppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf(Routes.POKEMON_LIST, Routes.CAUGHT_LIST)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.POKEMON_LIST,
                        onClick = {
                            if (currentRoute != Routes.POKEMON_LIST) {
                                navController.navigate(Routes.POKEMON_LIST) {
                                    popUpTo(Routes.POKEMON_LIST) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(Icons.Filled.List, contentDescription = null) },
                        label = { Text(stringResource(id = R.string.screen_pokemon_list)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.CAUGHT_LIST,
                        onClick = {
                            if (currentRoute != Routes.CAUGHT_LIST) {
                                navController.navigate(Routes.CAUGHT_LIST) {
                                    popUpTo(Routes.POKEMON_LIST) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { PokeballIcon(modifier = Modifier.size(24.dp)) },
                        label = { Text(stringResource(id = R.string.screen_caught)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.POKEMON_LIST,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.POKEMON_LIST) {
                PokemonListScreen(
                    onPokemonClick = { id -> navController.navigate(Routes.pokemonDetail(id)) }
                )
            }
            composable(Routes.CAUGHT_LIST) {
                CaughtPokemonScreen(
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
}
