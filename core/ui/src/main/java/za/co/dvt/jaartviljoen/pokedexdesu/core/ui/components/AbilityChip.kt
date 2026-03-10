package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme

/**
 * A small outlined chip displaying a Pokemon ability name.
 * Styled as a subtle outline to differentiate from the solid [TypeChip].
 */
@Composable
fun AbilityChip(
    abilityName: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Text(
        text = abilityName.replace("-", " ").replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = shape,
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun AbilityChipPreview() {
    PokedexDesuTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AbilityChip(abilityName = "static")
            AbilityChip(abilityName = "lightning-rod")
            AbilityChip(abilityName = "overgrow")
        }
    }
}
