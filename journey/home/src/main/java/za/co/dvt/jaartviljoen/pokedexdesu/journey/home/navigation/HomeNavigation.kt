package za.co.dvt.jaartviljoen.pokedexdesu.journey.home.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.HomeScreen

const val HOME_ROUTE = "home"

fun NavGraphBuilder.homeScreen(
    onPokemonClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
) {
    composable(route = HOME_ROUTE) {
        HomeScreen(
            onPokemonClick = onPokemonClick,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable,
        )
    }
}
