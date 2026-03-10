package za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase

import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.repository.PokemonRepository
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result

class RefreshPokemonListUseCase(private val repository: PokemonRepository) {

    suspend fun execute(limit: Int, offset: Int): Result<List<Pokemon>> =
        repository.refreshPokemonList(limit = limit, offset = offset)
}
