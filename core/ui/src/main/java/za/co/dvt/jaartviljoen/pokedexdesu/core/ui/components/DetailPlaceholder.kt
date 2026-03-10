package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.R
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme

@Composable
fun DetailPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PokeballIcon(
            size = 128.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            modifier = Modifier.alpha(0.6f),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.select_pokemon_prompt),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailPlaceholderPreview() {
    PokedexDesuTheme {
        DetailPlaceholder()
    }
}
