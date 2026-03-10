package za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase

import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonDetail
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.repository.PokemonRepository
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result

class GetPokemonDetailUseCase(private val repository: PokemonRepository) {

    suspend fun execute(id: Int): Result<PokemonDetail> =
        repository.getPokemonDetail(id)
}
