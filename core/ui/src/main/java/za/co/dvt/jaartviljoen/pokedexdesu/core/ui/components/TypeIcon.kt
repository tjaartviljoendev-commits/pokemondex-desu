package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.pokemonTypeColor

// Short labels that fit in a small circle — most are just the first 3 chars
private val typeLabels = mapOf(
    "fire" to "FIR",
    "water" to "WTR",
    "grass" to "GRS",
    "electric" to "ELC",
    "ice" to "ICE",
    "fighting" to "FGT",
    "poison" to "PSN",
    "ground" to "GND",
    "flying" to "FLY",
    "psychic" to "PSY",
    "bug" to "BUG",
    "rock" to "RCK",
    "ghost" to "GHO",
    "dragon" to "DRG",
    "dark" to "DRK",
    "steel" to "STL",
    "fairy" to "FRY",
    "normal" to "NRM",
)

@Composable
fun TypeIcon(
    typeName: String,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
) {
    val color = pokemonTypeColor(typeName)
    val label = typeLabels[typeName.lowercase()] ?: typeName.take(3).uppercase()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = (size.value * 0.3f).sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TypeIconPreview() {
    PokedexDesuTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            TypeIcon(typeName = "fire")
            TypeIcon(typeName = "water")
            TypeIcon(typeName = "grass")
            TypeIcon(typeName = "electric")
            TypeIcon(typeName = "ice")
            TypeIcon(typeName = "dragon")
            TypeIcon(typeName = "ghost")
            TypeIcon(typeName = "fairy")
        }
    }
}
