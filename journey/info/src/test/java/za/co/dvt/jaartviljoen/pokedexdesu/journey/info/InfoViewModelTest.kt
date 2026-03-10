package za.co.dvt.jaartviljoen.pokedexdesu.journey.info

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonDetail
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.PokemonStat
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.GetPokemonDetailUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.state.InfoUiState
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.viewmodel.InfoViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class InfoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private lateinit var getPokemonDetail: GetPokemonDetailUseCase

    @Before
    fun setUp() {
        getPokemonDetail = mockk()
    }

    @Test
    fun `initial load emits loading state before coroutine executes`() = runTest {
        coEvery { getPokemonDetail.execute(POKEMON_ID) } returns Result.Success(testDetail())

        val viewModel = buildViewModel()

        assertTrue(viewModel.uiState.value is InfoUiState.Loading)
    }

    @Test
    fun `initial load transitions to success with correct pokemon detail`() = runTest {
        val detail = testDetail()
        coEvery { getPokemonDetail.execute(POKEMON_ID) } returns Result.Success(detail)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is InfoUiState.Success)
        assertEquals(detail, (state as InfoUiState.Success).pokemon)
    }

    @Test
    fun `initial load maps all detail fields correctly to success state`() = runTest {
        val detail = PokemonDetail(
            id = 25,
            name = "pikachu",
            imageUrl = "https://example.com/pikachu.png",
            height = 4,
            weight = 60,
            baseExperience = 112,
            stats = listOf(PokemonStat("speed", 90), PokemonStat("attack", 55)),
            abilities = listOf("static", "lightning-rod"),
            types = listOf("electric")
        )
        coEvery { getPokemonDetail.execute(POKEMON_ID) } returns Result.Success(detail)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        val success = viewModel.uiState.value as InfoUiState.Success
        with(success.pokemon) {
            assertEquals(25, id)
            assertEquals("pikachu", name)
            assertEquals("https://example.com/pikachu.png", imageUrl)
            assertEquals(4, height)
            assertEquals(60, weight)
            assertEquals(112, baseExperience)
            assertEquals(2, stats.size)
            assertEquals(90, stats.first { it.name == "speed" }.baseStat)
            assertEquals(listOf("static", "lightning-rod"), abilities)
            assertEquals(listOf("electric"), types)
        }
    }

    @Test
    fun `initial load emits error state when use case returns failure`() = runTest {
        coEvery { getPokemonDetail.execute(POKEMON_ID) } returns
            Result.Error(RuntimeException("not found"), "not found")

        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is InfoUiState.Error)
        assertEquals("not found", (state as InfoUiState.Error).message)
    }

    @Test
    fun `error state uses exception localizedMessage when result message is null`() = runTest {
        val exception = RuntimeException("exception message")
        coEvery { getPokemonDetail.execute(POKEMON_ID) } returns Result.Error(exception, message = null)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value as InfoUiState.Error
        assertEquals("exception message", state.message)
    }

    @Test
    fun `retry reloads detail and transitions to success`() = runTest {
        coEvery { getPokemonDetail.execute(POKEMON_ID) } returnsMany listOf(
            Result.Error(RuntimeException("timeout"), "timeout"),
            Result.Success(testDetail())
        )

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onRetry()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is InfoUiState.Success)
    }

    @Test
    fun `retry transitions through loading before settling on success`() = runTest {
        val detail = testDetail()
        coEvery { getPokemonDetail.execute(POKEMON_ID) } returnsMany listOf(
            Result.Error(RuntimeException("err"), "err"),
            Result.Success(detail)
        )

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            val errorState = awaitItem()
            assertTrue(errorState is InfoUiState.Error)

            viewModel.onRetry()
            assertTrue(awaitItem() is InfoUiState.Loading)

            advanceUntilIdle()
            assertTrue(awaitItem() is InfoUiState.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry calls use case with the same pokemon id`() = runTest {
        coEvery { getPokemonDetail.execute(POKEMON_ID) } returns Result.Success(testDetail())

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onRetry()
        advanceUntilIdle()

        coVerify(exactly = 2) { getPokemonDetail.execute(POKEMON_ID) }
    }

    private fun buildViewModel(): InfoViewModel =
        InfoViewModel(getPokemonDetail = getPokemonDetail, pokemonId = POKEMON_ID)

    private companion object {
        const val POKEMON_ID = 1

        fun testDetail(
            id: Int = POKEMON_ID,
            name: String = "bulbasaur",
            imageUrl: String? = "https://example.com/sprites/$id.png",
            height: Int = 7,
            weight: Int = 69,
            baseExperience: Int = 64,
            stats: List<PokemonStat> = listOf(PokemonStat("hp", 45), PokemonStat("attack", 49)),
            abilities: List<String> = listOf("overgrow", "chlorophyll"),
            types: List<String> = listOf("grass", "poison")
        ): PokemonDetail = PokemonDetail(
            id = id,
            name = name,
            imageUrl = imageUrl,
            height = height,
            weight = weight,
            baseExperience = baseExperience,
            stats = stats,
            abilities = abilities,
            types = types
        )
    }
}
