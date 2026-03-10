package za.co.dvt.jaartviljoen.pokedexdesu.journey.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.R
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.EmptyState
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.ErrorScreen
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.PokemonCard
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.PokemonSearchBar
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.ShimmerCard
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.TypeLegendDialog
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.constant.HomeConstants
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.state.HomeUiState
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    onPokemonClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showLegend by remember { mutableStateOf(false) }

    if (showLegend) {
        TypeLegendDialog(onDismiss = { showLegend = false })
    }

    val reachedEnd by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - HomeConstants.PAGINATION_THRESHOLD
        }
    }

    LaunchedEffect(reachedEnd) {
        if (reachedEnd && !state.isLoading && state.searchQuery.isBlank()) {
            viewModel.onLoadMore()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_pokeball_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(32.dp),
                    )
                },
                actions = {
                    IconButton(onClick = { showLegend = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.content_desc_type_legend),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        when {
            state.showErrorState -> {
                ErrorScreen(
                    message = state.error.orEmpty(),
                    onRetry = viewModel::onRetry,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = viewModel::onRefresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    HomeContent(
                        state = state,
                        listState = listState,
                        isLandscape = isLandscape,
                        onSearchQueryChanged = viewModel::onSearchQueryChanged,
                        onPokemonClick = { pokemon ->
                            if (isLandscape) viewModel.onPokemonSelected(pokemon)
                            onPokemonClick(pokemon.id)
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun HomeContent(
    state: HomeUiState,
    listState: LazyListState,
    isLandscape: Boolean,
    onSearchQueryChanged: (String) -> Unit,
    onPokemonClick: (za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(key = "search") {
            PokemonSearchBar(
                query = state.searchQuery,
                onQueryChange = onSearchQueryChanged,
                suggestions = state.suggestions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .animateItem(),
            )
        }

        if (state.isLoading) {
            items(count = HomeConstants.SHIMMER_COUNT, key = { "shimmer_$it" }) {
                ShimmerCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(
                            fadeOutSpec = tween(durationMillis = HomeConstants.ANIMATION_DURATION_MS),
                        ),
                )
            }
        } else if (state.showEmptyState) {
            item(key = "empty") {
                EmptyState(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .animateItem(),
                )
            }
        } else {
            items(
                items = state.displayList,
                key = { it.id }
            ) { pokemon ->
                PokemonCard(
                    pokemon = pokemon,
                    onClick = { onPokemonClick(pokemon) },
                    isSelected = isLandscape && pokemon.id == state.selectedPokemonId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(
                            fadeInSpec = tween(durationMillis = HomeConstants.ANIMATION_DURATION_MS),
                        ),
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            }

            if (state.isPaginating) {
                items(count = HomeConstants.PAGINATION_SHIMMER_COUNT, key = { "paginate_shimmer_$it" }) {
                    ShimmerCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                    )
                }
            }
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private val previewPokemonList = listOf(
    Pokemon(
        id = 25,
        name = "pikachu",
        imageUrl = null,
        hp = 35,
        attack = 55,
        types = listOf("electric"),
        abilities = listOf("static", "lightning-rod"),
    ),
    Pokemon(
        id = 6,
        name = "charizard",
        imageUrl = null,
        hp = 78,
        attack = 84,
        types = listOf("fire", "flying"),
        abilities = listOf("blaze", "solar-power"),
    ),
    Pokemon(
        id = 9,
        name = "blastoise",
        imageUrl = null,
        hp = 79,
        attack = 83,
        types = listOf("water"),
        abilities = listOf("torrent", "rain-dish"),
    ),
    Pokemon(
        id = 94,
        name = "gengar",
        imageUrl = null,
        hp = 60,
        attack = 65,
        types = listOf("ghost", "poison"),
        abilities = listOf("cursed-body"),
    ),
    Pokemon(
        id = 448,
        name = "lucario",
        imageUrl = null,
        hp = 70,
        attack = 110,
        types = listOf("fighting", "steel"),
        abilities = listOf("steadfast", "inner-focus"),
    ),
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeContentPreview() {
    PokedexDesuTheme {
        HomeContent(
            state = HomeUiState(pokemonList = previewPokemonList),
            listState = LazyListState(),
            isLandscape = false,
            onSearchQueryChanged = {},
            onPokemonClick = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeContentLoadingPreview() {
    PokedexDesuTheme {
        HomeContent(
            state = HomeUiState(isLoading = true),
            listState = LazyListState(),
            isLandscape = false,
            onSearchQueryChanged = {},
            onPokemonClick = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeContentEmptyPreview() {
    PokedexDesuTheme {
        HomeContent(
            state = HomeUiState(searchQuery = "xyznotfound"),
            listState = LazyListState(),
            isLandscape = false,
            onSearchQueryChanged = {},
            onPokemonClick = {},
        )
    }
}
