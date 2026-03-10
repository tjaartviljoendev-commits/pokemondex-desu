package za.co.dvt.jaartviljoen.pokedexdesu.journey.info.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.InfoScreen

const val INFO_ROUTE = "info/{pokemonId}"
private const val ARG_POKEMON_ID = "pokemonId"

fun createInfoRoute(pokemonId: Int): String = "info/$pokemonId"

fun NavGraphBuilder.infoScreen(
    onBackClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
) {
    composable(
        route = INFO_ROUTE,
        arguments = listOf(
            navArgument(ARG_POKEMON_ID) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val pokemonId = backStackEntry.arguments?.getInt(ARG_POKEMON_ID) ?: return@composable
        InfoScreen(
            pokemonId = pokemonId,
            onBackClick = onBackClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable,
        )
    }
}
