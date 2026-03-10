package za.co.dvt.jaartviljoen.pokedexdesu.core.network.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.DetailResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.ListResponse

interface ApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: Int): DetailResponse
}
