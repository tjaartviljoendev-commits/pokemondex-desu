package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.R

@Composable
fun SpinningPokeball(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val transition = rememberInfiniteTransition(label = "pokeball_spin")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pokeball_rotation",
    )

    Image(
        painter = painterResource(R.drawable.ic_pokeball_placeholder),
        contentDescription = null,
        modifier = modifier
            .size(size)
            .rotate(rotation)
            .alpha(0.5f),
    )
}

@Composable
fun PokemonAsyncImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    contentScale: ContentScale = ContentScale.Fit,
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(R.string.content_desc_pokemon_sprite),
        modifier = modifier.size(size),
        contentScale = contentScale,
        loading = {
            Box(contentAlignment = Alignment.Center) {
                SpinningPokeball(size = size * 0.65f)
            }
        },
        error = {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.ic_pokeball_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(size * 0.65f).alpha(0.4f),
                )
            }
        },
    )
}
