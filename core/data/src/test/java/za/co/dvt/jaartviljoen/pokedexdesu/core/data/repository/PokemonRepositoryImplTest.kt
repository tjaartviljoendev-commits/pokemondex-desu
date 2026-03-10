package za.co.dvt.jaartviljoen.pokedexdesu.core.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.dao.PokemonDao
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity.PokemonDetailEntity
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.entity.PokemonEntity
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.DispatcherProvider
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.api.ApiService
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.AbilityEntryResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.NamedApiResource
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.OfficialArtworkResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.OtherSpritesResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.DetailResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.ListItem
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.ListResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.SpritesResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.StatEntryResponse
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.dto.TypeEntryResponse

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var dao: PokemonDao
    private lateinit var repository: PokemonRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        dao = mockk(relaxUnitFun = true)

        stubIndexAlreadyLoaded()

        repository = PokemonRepositoryImpl(
            apiService = apiService,
            dao = dao,
            dispatchers = TestDispatcherProvider()
        )
    }

    @Test
    fun `getPokemonList returns cached data when cache has enriched entries`() = runTest {
        val entities = (1..3).map { enrichedEntity(it) }
        coEvery { dao.getPokemonList(limit = 20, offset = 0) } returns entities

        val result = repository.getPokemonList(limit = 20, offset = 0)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(3, data.size)
        assertEquals(1, data[0].id)
        assertEquals("pokemon-1", data[0].name)
    }

    @Test
    fun `getPokemonList does not call network when cache has enriched entries`() = runTest {
        coEvery { dao.getPokemonList(limit = 20, offset = 0) } returns (1..5).map { enrichedEntity(it) }

        repository.getPokemonList(limit = 20, offset = 0)

        coVerify(exactly = 0) { apiService.getPokemonList(any(), any()) }
    }

    // cache miss -> should hit network

    @Test
    fun `getPokemonList fetches from network when cache is empty`() = runTest {
        coEvery { dao.getPokemonList(limit = 20, offset = 0) } returns emptyList()
        stubNetworkListAndDetails(ids = listOf(1, 2))

        val result = repository.getPokemonList(limit = 20, offset = 0)

        assertTrue(result is Result.Success)
        assertEquals(2, (result as Result.Success).data.size)
    }

    @Test
    fun `getPokemonList fetches from network when cache is not enriched`() = runTest {
        coEvery { dao.getPokemonList(limit = 20, offset = 0) } returns (1..2).map { basicEntity(it) }
        stubNetworkListAndDetails(ids = listOf(1, 2))

        val result = repository.getPokemonList(limit = 20, offset = 0)

        assertTrue(result is Result.Success)
        assertEquals(2, (result as Result.Success).data.size)
    }

    @Test
    fun `getPokemonList upserts enriched data to Room after network fetch`() = runTest {
        coEvery { dao.getPokemonList(limit = 20, offset = 0) } returns emptyList()
        stubNetworkListAndDetails(ids = listOf(1, 2, 3))

        repository.getPokemonList(limit = 20, offset = 0)

        coVerify(exactly = 1) { dao.upsertPokemonList(any()) }
    }

    @Test
    fun `getPokemonList returns error when network fails and cache is empty`() = runTest {
        coEvery { dao.getPokemonList(limit = 20, offset = 0) } returns emptyList()
        coEvery { apiService.getPokemonList(limit = 20, offset = 0) } throws
            RuntimeException("connection refused")

        val result = repository.getPokemonList(limit = 20, offset = 0)

        assertTrue(result is Result.Error)
        assertEquals("connection refused", (result as Result.Error).message)
    }

    @Test
    fun `getPokemonList falls back to cache when network fails and cache is non-empty`() = runTest {
        coEvery { dao.getPokemonList(limit = 20, offset = 0) } returns (1..2).map { basicEntity(it) }
        coEvery { apiService.getPokemonList(limit = 20, offset = 0) } throws
            RuntimeException("timeout")

        val result = repository.getPokemonList(limit = 20, offset = 0)

        assertTrue(result is Result.Success)
        assertEquals(2, (result as Result.Success).data.size)
    }

    @Test
    fun `getPokemonList passes correct offset to DAO and API`() = runTest {
        coEvery { dao.getPokemonList(limit = 20, offset = 40) } returns emptyList()
        stubNetworkListAndDetails(ids = listOf(41), limit = 20, offset = 40)

        repository.getPokemonList(limit = 20, offset = 40)

        coVerify { dao.getPokemonList(limit = 20, offset = 40) }
        coVerify { apiService.getPokemonList(limit = 20, offset = 40) }
    }

    @Test
    fun `getPokemonDetail returns cached detail when available`() = runTest {
        val entity = sampleDetailEntity(id = 1)
        coEvery { dao.getPokemonDetail(id = 1) } returns entity

        val result = repository.getPokemonDetail(id = 1)

        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.id)
    }

    @Test
    fun `getPokemonDetail does not call network when cache is available`() = runTest {
        coEvery { dao.getPokemonDetail(id = 1) } returns sampleDetailEntity(id = 1)

        repository.getPokemonDetail(id = 1)

        coVerify(exactly = 0) { apiService.getPokemonDetail(any()) }
    }

    @Test
    fun `getPokemonDetail fetches from network when not cached`() = runTest {
        coEvery { dao.getPokemonDetail(id = 7) } returns null
        coEvery { apiService.getPokemonDetail(id = 7) } returns sampleDetailResponse(id = 7)

        val result = repository.getPokemonDetail(id = 7)

        assertTrue(result is Result.Success)
        assertEquals(7, (result as Result.Success).data.id)
    }

    @Test
    fun `getPokemonDetail caches network response after successful fetch`() = runTest {
        coEvery { dao.getPokemonDetail(id = 7) } returns null
        coEvery { apiService.getPokemonDetail(id = 7) } returns sampleDetailResponse(id = 7)

        repository.getPokemonDetail(id = 7)

        coVerify(exactly = 1) { dao.insertPokemonDetail(any()) }
    }

    @Test
    fun `getPokemonDetail returns error when network fails and no cache exists`() = runTest {
        coEvery { dao.getPokemonDetail(id = 999) } returns null
        coEvery { apiService.getPokemonDetail(id = 999) } throws RuntimeException("404 not found")

        val result = repository.getPokemonDetail(id = 999)

        assertTrue(result is Result.Error)
        assertEquals("404 not found", (result as Result.Error).message)
    }

    @Test
    fun `refreshPokemonList returns fresh data from network`() = runTest {
        stubNetworkListAndDetails(ids = listOf(1, 2, 3))

        val result = repository.refreshPokemonList(limit = 20, offset = 0)

        assertTrue(result is Result.Success)
        assertEquals(3, (result as Result.Success).data.size)
    }

    @Test
    fun `refreshPokemonList upserts fresh network response in Room`() = runTest {
        stubNetworkListAndDetails(ids = listOf(1, 2))

        repository.refreshPokemonList(limit = 20, offset = 0)

        coVerify(exactly = 1) { dao.upsertPokemonList(any()) }
    }

    @Test
    fun `refreshPokemonList returns error when network fails`() = runTest {
        coEvery { apiService.getPokemonList(limit = 20, offset = 0) } throws
            RuntimeException("no internet")

        val result = repository.refreshPokemonList(limit = 20, offset = 0)

        assertTrue(result is Result.Error)
        assertEquals("no internet", (result as Result.Error).message)
    }

    // Helpers

    private class TestDispatcherProvider : DispatcherProvider {
        private val dispatcher = UnconfinedTestDispatcher()
        override val main = dispatcher
        override val io = dispatcher
        override val default = dispatcher
    }

    private fun stubIndexAlreadyLoaded() {
        coEvery { dao.getPokemonCount() } returns 1302
    }

    private fun stubNetworkListAndDetails(
        ids: List<Int>,
        limit: Int = 20,
        offset: Int = 0,
    ) {
        coEvery { apiService.getPokemonList(limit = limit, offset = offset) } returns
            ListResponse(
                count = ids.size,
                next = null,
                previous = null,
                results = ids.map { id ->
                    ListItem(name = "pokemon-$id", url = "https://pokeapi.co/api/v2/pokemon/$id/")
                },
            )
        ids.forEach { id ->
            coEvery { apiService.getPokemonDetail(id = id) } returns sampleDetailResponse(id = id)
        }
    }

    private fun basicEntity(id: Int) = PokemonEntity(
        id = id,
        name = "pokemon-$id",
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
    )

    private fun enrichedEntity(id: Int) = PokemonEntity(
        id = id,
        name = "pokemon-$id",
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
        hp = 45,
        attack = 49,
        typesJson = """["grass"]""",
        abilitiesJson = """["overgrow"]""",
    )

    private fun sampleDetailEntity(id: Int) = PokemonDetailEntity(
        id = id,
        name = "pokemon-$id",
        imageUrl = "https://example.com/artwork/$id.png",
        height = 7,
        weight = 69,
        baseExperience = 64,
        statsJson = """[{"name":"hp","baseStat":45}]""",
        abilitiesJson = """["overgrow"]""",
        typesJson = """["grass"]""",
    )

    private fun sampleDetailResponse(id: Int) = DetailResponse(
        id = id,
        name = "pokemon-$id",
        height = 7,
        weight = 69,
        baseExperience = 64,
        sprites = SpritesResponse(
            frontDefault = "https://example.com/sprites/$id.png",
            other = OtherSpritesResponse(
                officialArtwork = OfficialArtworkResponse(
                    frontDefault = "https://example.com/artwork/$id.png",
                ),
            ),
        ),
        stats = listOf(
            StatEntryResponse(
                baseStat = 45,
                effort = 0,
                stat = NamedApiResource(name = "hp", url = "https://pokeapi.co/api/v2/stat/1/"),
            ),
            StatEntryResponse(
                baseStat = 49,
                effort = 0,
                stat = NamedApiResource(name = "attack", url = "https://pokeapi.co/api/v2/stat/2/"),
            ),
        ),
        abilities = listOf(
            AbilityEntryResponse(
                ability = NamedApiResource(name = "overgrow", url = "https://pokeapi.co/api/v2/ability/65/"),
                isHidden = false,
                slot = 1,
            ),
        ),
        types = listOf(
            TypeEntryResponse(
                slot = 1,
                type = NamedApiResource(name = "grass", url = "https://pokeapi.co/api/v2/type/12/"),
            ),
        ),
    )
}
