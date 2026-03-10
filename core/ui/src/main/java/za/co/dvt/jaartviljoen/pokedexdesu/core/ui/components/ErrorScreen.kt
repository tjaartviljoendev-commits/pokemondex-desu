package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.R
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PokeballIcon(
            size = 72.dp,
            color = MaterialTheme.colorScheme.error,
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        OutlinedButton(onClick = onRetry) {
            Text(
                text = stringResource(R.string.error_retry),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    PokedexDesuTheme {
        ErrorScreen(
            message = "Unable to reach the server. Check your connection and try again.",
            onRetry = {},
        )
    }
}
