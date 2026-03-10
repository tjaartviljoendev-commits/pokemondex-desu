package za.co.dvt.jaartviljoen.pokedexdesu.journey.home.state

import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon

data class HomeUiState(
    val pokemonList: List<Pokemon> = emptyList(),
    val searchResults: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPaginating: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedPokemonId: Int? = null,
    val canLoadMore: Boolean = true,
) {
    val displayList: List<Pokemon>
        get() = if (searchQuery.isBlank()) pokemonList else searchResults

    val suggestions: List<String>
        get() = if (searchQuery.length >= 2) {
            searchResults.take(3).map { it.name.replaceFirstChar { c -> c.uppercase() } }
        } else {
            emptyList()
        }

    val showEmptyState: Boolean
        get() = !isLoading && !isSearching && error == null && displayList.isEmpty() && searchQuery.isNotBlank()

    val showErrorState: Boolean
        get() = !isLoading && error != null && pokemonList.isEmpty()
}