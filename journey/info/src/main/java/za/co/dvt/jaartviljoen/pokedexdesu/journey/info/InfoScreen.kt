package za.co.dvt.jaartviljoen.pokedexdesu.journey.info

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonDetail
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonStat
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.R
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.SharedElementKeys
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.ErrorScreen
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.PokeballSpinner
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.PokemonAsyncImage
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.StatBar
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components.TypeChip
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.constant.InfoConstants
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.state.InfoUiState
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.viewmodel.InfoViewModel

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    pokemonId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val viewModel: InfoViewModel = koinViewModel(
        parameters = { parametersOf(pokemonId) }
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val title = when (val currentState = state) {
        is InfoUiState.Success -> currentState.pokemon.name.capitalize(Locale.current)
        else -> ""
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.content_desc_back)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val currentState = state) {
                is InfoUiState.Loading -> {
                    PokeballSpinner(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is InfoUiState.Error -> {
                    ErrorScreen(
                        message = currentState.message,
                        onRetry = viewModel::onRetry,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is InfoUiState.Success -> {
                    DetailContent(
                        pokemon = currentState.pokemon,
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
private fun DetailContent(
    pokemon: PokemonDetail,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val imageModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
            with(sharedTransitionScope) {
                Modifier.sharedElement(
                    rememberSharedContentState(key = SharedElementKeys.pokemonImage(pokemon.id)),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            }
        } else {
            Modifier
        }

        PokemonAsyncImage(
            imageUrl = pokemon.imageUrl,
            modifier = imageModifier,
            size = InfoConstants.POKEMON_IMAGE_SIZE,
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = pokemon.name.capitalize(Locale.current),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            pokemon.types.forEach { type ->
                TypeChip(typeName = type)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats
        DetailSectionCard {
            SectionHeader(text = stringResource(R.string.stats_section))
            Spacer(modifier = Modifier.height(12.dp))
            pokemon.stats.forEach { stat ->
                StatBar(
                    statName = stat.name,
                    statValue = stat.baseStat,
                    maxValue = InfoConstants.MAX_STAT_VALUE,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Abilities
        DetailSectionCard {
            SectionHeader(text = stringResource(R.string.abilities_section))
            Spacer(modifier = Modifier.height(8.dp))
            pokemon.abilities.forEachIndexed { index, ability ->
                Text(
                    text = ability.replaceFirstChar { it.uppercase() }.replace("-", " "),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                if (index < pokemon.abilities.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Physical measurements
        DetailSectionCard {
            SectionHeader(text = stringResource(R.string.physical_section))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                PhysicalMeasurement(
                    label = stringResource(R.string.height_label),
                    value = pokemon.height
                )
                PhysicalMeasurement(
                    label = stringResource(R.string.weight_label),
                    value = pokemon.weight
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailSectionCard(
    content: @Composable () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PhysicalMeasurement(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

private val previewPokemonDetail = PokemonDetail(
    id = 6,
    name = "charizard",
    imageUrl = null,
    height = 17,
    weight = 905,
    baseExperience = 267,
    stats = listOf(
        PokemonStat(name = "HP", baseStat = 78),
        PokemonStat(name = "Attack", baseStat = 84),
        PokemonStat(name = "Defense", baseStat = 78),
        PokemonStat(name = "Sp. Atk", baseStat = 109),
        PokemonStat(name = "Sp. Def", baseStat = 85),
        PokemonStat(name = "Speed", baseStat = 100),
    ),
    abilities = listOf("blaze", "solar-power"),
    types = listOf("fire", "flying"),
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailContentPreview() {
    PokedexDesuTheme {
        DetailContent(pokemon = previewPokemonDetail)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InfoScreenLoadingPreview() {
    PokedexDesuTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            PokeballSpinner()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InfoScreenErrorPreview() {
    PokedexDesuTheme {
        ErrorScreen(
            message = "Failed to load Pokemon details",
            onRetry = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
