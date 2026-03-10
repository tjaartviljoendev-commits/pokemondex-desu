package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme

@Composable
fun PokeballSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pokeball_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        PokeballIcon(
            size = size,
            modifier = Modifier.rotate(angle),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PokeballSpinnerPreview() {
    PokedexDesuTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            PokeballSpinner(size = 64.dp)
        }
    }
}
