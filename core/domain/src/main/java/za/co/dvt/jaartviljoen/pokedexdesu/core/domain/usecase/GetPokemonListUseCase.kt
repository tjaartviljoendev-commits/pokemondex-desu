package za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase

import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.repository.PokemonRepository
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result

class GetPokemonListUseCase(private val repository: PokemonRepository) {

    suspend fun execute(limit: Int, offset: Int): Result<List<Pokemon>> =
        repository.getPokemonList(limit = limit, offset = offset)
}
