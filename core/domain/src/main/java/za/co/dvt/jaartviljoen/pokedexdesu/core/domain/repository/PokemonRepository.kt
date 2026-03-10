package za.co.dvt.jaartviljoen.pokedexdesu.core.domain.repository

import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonDetail
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>>
    suspend fun getPokemonDetail(id: Int): Result<PokemonDetail>
    suspend fun refreshPokemonList(limit: Int, offset: Int): Result<List<Pokemon>>
    suspend fun searchPokemon(query: String): Result<List<Pokemon>>
}
