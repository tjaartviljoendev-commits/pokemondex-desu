package za.co.dvt.jaartviljoen.pokedexdesu.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.R
import za.co.dvt.jaartviljoen.pokedexdesu.core.ui.theme.PokedexDesuTheme

@Composable
fun PokemonSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    suggestions: List<String> = emptyList(),
    onSuggestionSelected: (String) -> Unit = onQueryChange,
) {
    val focusManager = LocalFocusManager.current
    var hasFocus by remember { mutableStateOf(false) }
    val showSuggestions = hasFocus && suggestions.isNotEmpty() && query.isNotEmpty()

    Box(modifier = modifier.zIndex(if (showSuggestions) 1f else 0f)) {
        Column {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { hasFocus = it.isFocused },
                placeholder = {
                    Text(text = stringResource(R.string.search_placeholder))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.content_desc_search),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.content_desc_back),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
            )

            AnimatedVisibility(
                visible = showSuggestions,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 8.dp, end = 8.dp),
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        suggestions.forEachIndexed { index, suggestion ->
                            val annotatedText = buildHighlightedText(
                                suggestion = suggestion,
                                query = query,
                                highlightColor = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = annotatedText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSuggestionSelected(suggestion.lowercase())
                                        focusManager.clearFocus()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                            )
                            if (index < suggestions.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun buildHighlightedText(
    suggestion: String,
    query: String,
    highlightColor: Color,
) = buildAnnotatedString {
    val matchIndex = suggestion.lowercase().indexOf(query.lowercase())
    if (matchIndex >= 0) {
        append(suggestion.substring(0, matchIndex))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = highlightColor)) {
            append(suggestion.substring(matchIndex, matchIndex + query.length))
        }
        append(suggestion.substring(matchIndex + query.length))
    } else {
        append(suggestion)
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarEmptyPreview() {
    PokedexDesuTheme {
        PokemonSearchBar(query = "", onQueryChange = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchBarWithSuggestionsPreview() {
    PokedexDesuTheme {
        PokemonSearchBar(
            query = "saur",
            onQueryChange = {},
            suggestions = listOf("Bulbasaur", "Ivysaur", "Venusaur"),
        )
    }
}
