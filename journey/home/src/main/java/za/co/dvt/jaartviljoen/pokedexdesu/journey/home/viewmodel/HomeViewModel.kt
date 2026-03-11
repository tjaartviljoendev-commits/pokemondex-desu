package za.co.dvt.jaartviljoen.pokedexdesu.journey.home.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.GetPokemonListUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.RefreshPokemonListUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.SearchPokemonUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.constant.HomeConstants
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.state.HomeUiState

@OptIn(FlowPreview::class)
class HomeViewModel(
    private val getPokemonList: GetPokemonListUseCase,
    private val refreshPokemonList: RefreshPokemonListUseCase,
    private val searchPokemon: SearchPokemonUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            selectedPokemonId = savedStateHandle.get<Int>(HomeConstants.SAVED_STATE_SELECTED_ID)
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentOffset = 0

    init {
        loadFirstPage()
        observeSearchQuery()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            if (query.isBlank()) {
                it.copy(searchQuery = query, searchResults = emptyList(), isSearching = false)
            } else {
                it.copy(searchQuery = query, isSearching = true)
            }
        }
    }

    fun onPokemonSelected(pokemon: Pokemon) {
        savedStateHandle[HomeConstants.SAVED_STATE_SELECTED_ID] = pokemon.id
        _uiState.update { it.copy(selectedPokemonId = pokemon.id) }
    }

    fun onLoadMore() {
        val state = _uiState.value
        if (state.isPaginating || state.isLoading || !state.canLoadMore || state.searchQuery.isNotBlank()) return

        _uiState.update { it.copy(isPaginating = true) }

        viewModelScope.launch {
            when (val result =
                getPokemonList(limit = HomeConstants.PAGE_SIZE, offset = currentOffset)) {
                is Result.Success -> {
                    val newList = result.data
                    currentOffset += newList.size
                    _uiState.update { current ->
                        current.copy(
                            pokemonList = current.pokemonList + newList,
                            isPaginating = false,
                            canLoadMore = newList.size == HomeConstants.PAGE_SIZE && currentOffset < HomeConstants.MAX_POKEMON
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isPaginating = false) }
                }
            }
        }
    }

    fun onRefresh() {
        _uiState.update { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            when (val result =
                refreshPokemonList(limit = HomeConstants.PAGE_SIZE, offset = 0)) {
                is Result.Success -> {
                    currentOffset = result.data.size
                    _uiState.update { state ->
                        state.copy(
                            pokemonList = result.data,
                            isRefreshing = false,
                            error = null,
                            canLoadMore = result.data.size == HomeConstants.PAGE_SIZE && currentOffset < HomeConstants.MAX_POKEMON
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isRefreshing = false,
                            error = result.message ?: result.exception.localizedMessage
                        )
                    }
                }
            }
        }
    }

    fun onRetry() {
        _uiState.update { it.copy(error = null) }
        loadFirstPage()
    }

    private fun observeSearchQuery() {
        _uiState
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(100L)
            .filter { it.isNotBlank() }
            .onEach { query -> executeSearch(query) }
            .launchIn(viewModelScope)
    }

    private suspend fun executeSearch(query: String) {
        when (val result = searchPokemon(query)) {
            is Result.Success -> {
                _uiState.update { state ->
                    if (state.searchQuery == query) {
                        state.copy(searchResults = result.data, isSearching = false)
                    } else {
                        state
                    }
                }
            }
            is Result.Error -> {
                _uiState.update { state ->
                    if (state.searchQuery == query) {
                        state.copy(searchResults = emptyList(), isSearching = false)
                    } else {
                        state
                    }
                }
            }
        }
    }

    private fun loadFirstPage() {
        currentOffset = 0
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = getPokemonList(limit = HomeConstants.PAGE_SIZE, offset = 0)) {
                is Result.Success -> {
                    currentOffset = result.data.size
                    _uiState.update { state ->
                        state.copy(
                            pokemonList = result.data,
                            isLoading = false,
                            canLoadMore = result.data.size == HomeConstants.PAGE_SIZE && currentOffset < HomeConstants.MAX_POKEMON
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = result.message ?: result.exception.localizedMessage
                        )
                    }
                }
            }
        }
    }
}