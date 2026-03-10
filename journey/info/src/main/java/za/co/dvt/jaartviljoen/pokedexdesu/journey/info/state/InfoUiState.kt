package za.co.dvt.jaartviljoen.pokedexdesu.journey.info.state

import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonDetail

sealed interface InfoUiState {
    data object Loading : InfoUiState
    data class Success(val pokemon: PokemonDetail) : InfoUiState
    data class Error(val message: String) : InfoUiState
}