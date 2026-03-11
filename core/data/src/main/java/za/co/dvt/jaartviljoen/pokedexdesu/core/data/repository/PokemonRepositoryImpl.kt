package za.co.dvt.jaartviljoen.pokedexdesu.core.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.dao.PokemonDao
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.mapper.toDomain
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.mapper.toEntity
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.mapper.toSummary
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonDetail
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.repository.PokemonRepository
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.DispatcherProvider
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.api.ApiService

internal class PokemonRepositoryImpl(
    private val apiService: ApiService,
    private val dao: PokemonDao,
    private val dispatchers: DispatcherProvider
) : PokemonRepository {

    private val indexMutex = Mutex()
    private var indexLoaded = false

    override suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>> =
        withContext(dispatchers.io) {
            try {
                ensureFullIndexCached()

                val cached = dao.getPokemonList(limit, offset)
                if (cached.isNotEmpty() && cached.first().hp != null) {
                    return@withContext Result.Success(
                        cached.map {
                            it.toDomain()
                        })
                }

                val response = apiService.getPokemonList(limit = limit, offset = offset)
                val basicList = response.results.map { it.toDomain() }
                val enriched = fetchDetailsAndEnrich(basicList.map { it.id })
                val enrichedById = enriched.associateBy { it.id }
                val finalList = basicList.map { enrichedById[it.id] ?: it }
                dao.upsertPokemonList(finalList.map { it.toEntity() })
                Result.Success(finalList)
            } catch (e: Exception) {
                dao.getPokemonList(limit, offset)
                    .takeIf { it.isNotEmpty() }
                    ?.let {
                        Result.Success(
                            it.map { entity ->
                                entity.toDomain()
                            })
                    }
                    ?: Result.Error(exception = e, message = e.message)
            }
        }

    override suspend fun getPokemonDetail(id: Int): Result<PokemonDetail> =
        withContext(dispatchers.io) {
            try {
                val cached = dao.getPokemonDetail(id)
                if (cached != null) {
                    return@withContext Result.Success(cached.toDomain())
                }

                val response = apiService.getPokemonDetail(id)
                val detail = response.toDomain()
                dao.insertPokemonDetail(detail.toEntity())
                Result.Success(detail)
            } catch (e: Exception) {
                Result.Error(exception = e, message = e.message)
            }
        }

    override suspend fun refreshPokemonList(limit: Int, offset: Int): Result<List<Pokemon>> =
        withContext(dispatchers.io) {
            try {
                val response = apiService.getPokemonList(limit = limit, offset = offset)
                val basicList = response.results.map { it.toDomain() }
                val enriched = fetchDetailsAndEnrich(basicList.map { it.id })
                val enrichedById = enriched.associateBy { it.id }
                val finalList = basicList.map { enrichedById[it.id] ?: it }
                dao.upsertPokemonList(finalList.map { it.toEntity() })
                Result.Success(finalList)
            } catch (e: Exception) {
                Result.Error(exception = e, message = e.message)
            }
        }

    override suspend fun searchPokemon(query: String): Result<List<Pokemon>> =
        withContext(dispatchers.io) {
            try {
                ensureFullIndexCached()
                val results = dao.searchByName(query).map { it.toDomain() }
                val needsEnrichment = results.filter { it.hp == null }
                val enriched = fetchDetailsAndEnrich(needsEnrichment.map { it.id })
                if (enriched.isEmpty()) {
                    Result.Success(results)
                } else {
                    val enrichedById = enriched.associateBy { it.id }
                    Result.Success(results.map { enrichedById[it.id] ?: it })
                }
            } catch (e: Exception) {
                Result.Error(exception = e, message = e.message)
            }
        }

    private suspend fun ensureFullIndexCached() {
        if (indexLoaded) return

        indexMutex.withLock {
            if (indexLoaded) return

            val count = dao.getPokemonCount()
            if (count >= FULL_INDEX_LIMIT) {
                indexLoaded = true
                return
            }

            try {
                val response = apiService.getPokemonList(limit = FULL_INDEX_LIMIT, offset = 0)
                val allPokemon = response.results.map { it.toDomain() }
                dao.insertPokemonList(allPokemon.map { it.toEntity() })
                indexLoaded = true
            } catch (_: Exception) {
                // If the index fetch fails, search will still work on whatever is cached
            }
        }
    }

    private suspend fun fetchDetailsAndEnrich(ids: List<Int>): List<Pokemon> =
        coroutineScope {
            ids.map { id ->
                async {
                    try {
                        val detailResponse = apiService.getPokemonDetail(id)
                        val detail = detailResponse.toDomain()
                        dao.insertPokemonDetail(detail.toEntity())
                        detail.toSummary()
                    } catch (_: Exception) {
                        null
                    }
                }
            }.awaitAll().filterNotNull()
        }

    private companion object {
        const val FULL_INDEX_LIMIT = 100
    }
}
