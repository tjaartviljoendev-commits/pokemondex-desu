package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.pokemonTypeColor
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.pokemonTypeTextColor

@Composable
fun TypeChip(
    typeName: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(pokemonTypeColor(typeName))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = typeName.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            color = pokemonTypeTextColor(typeName),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
private fun TypeChipPreview() {
    PokedexDesuTheme {
        FlowRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TypeChip(typeName = "fire")
            TypeChip(typeName = "flying")
            TypeChip(typeName = "water")
            TypeChip(typeName = "electric")
            TypeChip(typeName = "grass")
            TypeChip(typeName = "dragon")
        }
    }
}
