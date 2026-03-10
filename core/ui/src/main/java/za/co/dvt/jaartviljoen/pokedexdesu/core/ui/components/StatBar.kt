package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme

private val statDisplayNames = mapOf(
    "hp" to "HP",
    "attack" to "Attack",
    "defense" to "Defense",
    "special-attack" to "Sp. Atk",
    "special-defense" to "Sp. Def",
    "speed" to "Speed",
)

@Composable
fun StatBar(
    statName: String,
    statValue: Int,
    modifier: Modifier = Modifier,
    maxValue: Int = 255,
) {
    var targetReached by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { targetReached = true }

    val fraction by animateFloatAsState(
        targetValue = if (targetReached) statValue.coerceIn(0, maxValue).toFloat() / maxValue else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "stat_bar_anim",
    )

    val barColor = when {
        statValue < 50 -> Color(0xFFE74C3C)
        statValue < 100 -> Color(0xFFF39C12)
        else -> Color(0xFF27AE60)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = statDisplayNames[statName.lowercase()] ?: statName.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(80.dp),
        )

        Text(
            text = statValue.toString(),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .padding(start = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outlineVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatBarPreview() {
    PokedexDesuTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatBar(statName = "HP", statValue = 45)
            StatBar(statName = "Attack", statValue = 80)
            StatBar(statName = "Defense", statValue = 120)
            StatBar(statName = "Sp. Atk", statValue = 150)
        }
    }
}
