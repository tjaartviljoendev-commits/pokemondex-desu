package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.SharedElementKeys
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.pokemonTypeColor

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonCard(
    pokemon: Pokemon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val border = if (isSelected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        null
    }

    val primaryTypeColor = pokemon.types.firstOrNull()?.let { pokemonTypeColor(it) }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        border = border,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
        ),
    ) {
        Box {
            // Subtle type-colored gradient on the left edge
            if (primaryTypeColor != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    primaryTypeColor.copy(alpha = 0.15f),
                                    Color.Transparent,
                                ),
                                startX = 0f,
                                endX = 300f,
                            )
                        )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = pokemon.name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        pokemon.types.forEach { type ->
                            TypeIcon(typeName = type, size = 22.dp)
                        }
                    }

                    if (pokemon.hp != null || pokemon.attack != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            pokemon.hp?.let { hp ->
                                StatLabel(label = "HP", value = hp)
                            }
                            pokemon.attack?.let { atk ->
                                StatLabel(label = "ATK", value = atk)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    if (pokemon.abilities.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            pokemon.abilities.take(2).forEach { ability ->
                                AbilityChip(abilityName = ability)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

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
                    size = 72.dp,
                )
            }
        }
    }
}

@Composable
private fun StatLabel(label: String, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PokemonCardPreview() {
    PokedexDesuTheme {
        PokemonCard(
            pokemon = Pokemon(
                id = 25,
                name = "pikachu",
                imageUrl = null,
                hp = 35,
                attack = 55,
                types = listOf("electric"),
                abilities = listOf("static", "lightning-rod"),
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PokemonCardMinimalPreview() {
    PokedexDesuTheme {
        PokemonCard(
            pokemon = Pokemon(id = 1, name = "bulbasaur", imageUrl = null),
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
