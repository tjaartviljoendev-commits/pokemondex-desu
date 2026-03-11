package za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase

import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.repository.PokemonRepository
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result

class SearchPokemonUseCase(private val repository: PokemonRepository) {

    suspend operator fun invoke(query: String): Result<List<Pokemon>> =
        repository.searchPokemon(query = query)
}
