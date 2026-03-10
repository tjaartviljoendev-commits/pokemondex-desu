package za.co.dvt.jaartviljoen.pokedexdesu.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.navigation.HOME_ROUTE
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.navigation.homeScreen
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.navigation.createInfoRoute
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.navigation.infoScreen
import za.co.dvt.jaartviljoen.pokedexdesu.splash.SplashScreen

@Composable
fun PokedexLayout() {
    var showSplash by rememberSaveable { mutableStateOf(true) }

    AnimatedContent(
        targetState = showSplash,
        label = "splash_transition"
    ) { isSplash ->
        if (isSplash) {
            SplashScreen(onSplashComplete = { showSplash = false })
        } else {
            LayoutContent()
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun LayoutContent() {
    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = HOME_ROUTE,
        ) {
            homeScreen(
                onPokemonClick = { pokemonId ->
                    navController.navigate(createInfoRoute(pokemonId))
                },
                sharedTransitionScope = this@SharedTransitionLayout,
            )
            infoScreen(
                onBackClick = { navController.popBackStack() },
                sharedTransitionScope = this@SharedTransitionLayout,
            )
        }
    }
}
