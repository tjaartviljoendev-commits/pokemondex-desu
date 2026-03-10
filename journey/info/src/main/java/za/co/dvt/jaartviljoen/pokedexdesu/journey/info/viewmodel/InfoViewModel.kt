package za.co.dvt.jaartviljoen.pokedexdesu.journey.info.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.GetPokemonDetailUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.state.InfoUiState

class InfoViewModel(
    private val getPokemonDetail: GetPokemonDetailUseCase,
    private val pokemonId: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow<InfoUiState>(InfoUiState.Loading)
    val uiState: StateFlow<InfoUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun onRetry() {
        loadDetail()
    }

    private fun loadDetail() {
        _uiState.value = InfoUiState.Loading

        viewModelScope.launch {
            when (val result = getPokemonDetail.execute(pokemonId)) {
                is Result.Success -> _uiState.value = InfoUiState.Success(result.data)
                is Result.Error -> _uiState.value = InfoUiState.Error(
                    message = result.message ?: result.exception.localizedMessage.orEmpty()
                )
            }
        }
    }
}