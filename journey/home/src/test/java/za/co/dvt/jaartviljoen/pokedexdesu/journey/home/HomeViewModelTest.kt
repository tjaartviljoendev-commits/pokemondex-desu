package za.co.dvt.jaartviljoen.pokedexdesu.journey.home

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.model.Pokemon
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.GetPokemonListUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.RefreshPokemonListUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.SearchPokemonUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.Result
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.constant.HomeConstants
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.viewmodel.HomeViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private lateinit var getPokemonList: GetPokemonListUseCase
    private lateinit var refreshPokemonList: RefreshPokemonListUseCase
    private lateinit var searchPokemon: SearchPokemonUseCase

    @Before
    fun setUp() {
        getPokemonList = mockk()
        refreshPokemonList = mockk()
        searchPokemon = mockk()
    }

    @Test
    fun `initial load emits loading state before coroutine executes`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(20))

        val viewModel = buildViewModel()

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial load transitions to success and populates pokemon list`() = runTest {
        val expected = createPokemonList(20)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns Result.Success(expected)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertFalse(isLoading)
            assertEquals(expected, pokemonList)
            assertNull(error)
        }
    }

    @Test
    fun `initial load with full page marks canLoadMore as true`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(PAGE_SIZE))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.canLoadMore)
    }

    @Test
    fun `initial load with partial page marks canLoadMore as false`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(5))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canLoadMore)
    }

    @Test
    fun `initial load error with empty list sets error state`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Error(RuntimeException("network error"), "network error")

        val viewModel = buildViewModel()
        advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertFalse(isLoading)
            assertEquals("network error", error)
            assertTrue(showErrorState)
        }
    }

    @Test
    fun `load more appends new items to existing list`() = runTest {
        val firstPage = createPokemonList(PAGE_SIZE)
        val secondPage = createPokemonList(PAGE_SIZE, startId = PAGE_SIZE + 1)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns Result.Success(firstPage)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = PAGE_SIZE) } returns Result.Success(secondPage)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLoadMore()
        advanceUntilIdle()

        assertEquals(firstPage + secondPage, viewModel.uiState.value.pokemonList)
    }

    @Test
    fun `load more does not trigger while already loading initial page`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(PAGE_SIZE))

        val viewModel = buildViewModel()
        viewModel.onLoadMore()
        advanceUntilIdle()

        coVerify(exactly = 1) { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) }
    }

    @Test
    fun `load more does not trigger while already paginating`() = runTest {
        val firstPage = createPokemonList(PAGE_SIZE)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns Result.Success(firstPage)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = PAGE_SIZE) } returns
            Result.Success(createPokemonList(PAGE_SIZE, startId = PAGE_SIZE + 1))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLoadMore()
        viewModel.onLoadMore()
        advanceUntilIdle()

        coVerify(exactly = 2) { getPokemonList.execute(any(), any()) }
    }

    @Test
    fun `load more sets canLoadMore to false when fewer than page size items returned`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(PAGE_SIZE))
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = PAGE_SIZE) } returns
            Result.Success(createPokemonList(3, startId = PAGE_SIZE + 1))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLoadMore()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canLoadMore)
    }

    @Test
    fun `load more does not trigger when canLoadMore is false`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(3))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLoadMore()
        advanceUntilIdle()

        coVerify(exactly = 1) { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) }
    }

    @Test
    fun `search query triggers use case and populates displayList with results`() = runTest {
        val searchResults = listOf(createPokemon(4, name = "charmander"))
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(3))
        coEvery { searchPokemon.execute("char") } returns Result.Success(searchResults)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("char")
        advanceTimeBy(200)
        advanceUntilIdle()

        assertEquals(searchResults, viewModel.uiState.value.displayList)
    }

    @Test
    fun `search with no results shows empty state`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(3))
        coEvery { searchPokemon.execute("zzznomatch") } returns Result.Success(emptyList())

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("zzznomatch")
        advanceTimeBy(200)
        advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertTrue(displayList.isEmpty())
            assertTrue(showEmptyState)
        }
    }

    @Test
    fun `clearing search query restores full pokemon list in displayList`() = runTest {
        val list = createPokemonList(5)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns Result.Success(list)
        coEvery { searchPokemon.execute("pokemon-1") } returns Result.Success(listOf(createPokemon(1)))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("pokemon-1")
        advanceTimeBy(200)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("")
        advanceUntilIdle()

        assertEquals(list, viewModel.uiState.value.displayList)
    }

    @Test
    fun `blank search query does not show empty state`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(5))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("")

        assertFalse(viewModel.uiState.value.showEmptyState)
    }

    @Test
    fun `refresh replaces existing list with fresh data`() = runTest {
        val initialList = createPokemonList(PAGE_SIZE)
        val refreshedList = createPokemonList(PAGE_SIZE, startId = 100)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns Result.Success(initialList)
        coEvery { refreshPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns Result.Success(refreshedList)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(refreshedList, pokemonList)
            assertFalse(isRefreshing)
            assertNull(error)
        }
    }

    @Test
    fun `refresh resets pagination offset so next load-more starts from correct position`() = runTest {
        val refreshedList = createPokemonList(PAGE_SIZE, startId = 100)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(PAGE_SIZE))
        coEvery { refreshPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(refreshedList)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = PAGE_SIZE) } returns
            Result.Success(createPokemonList(PAGE_SIZE, startId = PAGE_SIZE + 100))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        viewModel.onLoadMore()
        advanceUntilIdle()

        coVerify { getPokemonList.execute(limit = PAGE_SIZE, offset = PAGE_SIZE) }
    }

    @Test
    fun `refresh failure preserves existing list and sets error message`() = runTest {
        val existingList = createPokemonList(PAGE_SIZE)
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns Result.Success(existingList)
        coEvery { refreshPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Error(RuntimeException("server error"), "server error")

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(existingList, pokemonList)
            assertFalse(isRefreshing)
            assertEquals("server error", error)
            assertFalse(showErrorState)
        }
    }

    @Test
    fun `retry after error clears error and reloads first page`() = runTest {
        val errorResult = Result.Error(RuntimeException("timeout"), "timeout")
        val successResult = Result.Success(createPokemonList(PAGE_SIZE))
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returnsMany listOf(errorResult, successResult)

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onRetry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(PAGE_SIZE, state.pokemonList.size)
    }

    @Test
    fun `retry transitions through loading before settling on success`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returnsMany listOf(
            Result.Error(RuntimeException("err"), "err"),
            Result.Success(createPokemonList(PAGE_SIZE))
        )

        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            val errorState = awaitItem()
            assertTrue(errorState.error != null)

            viewModel.onRetry()
            val emissions = buildList {
                add(awaitItem())
                val next = kotlin.runCatching { expectMostRecentItem() }.getOrNull()
                if (next != null) add(next)
            }
            assertTrue(emissions.any { it.isLoading })

            advanceUntilIdle()
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertNull(successState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selecting a pokemon updates selectedPokemonId in state`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(PAGE_SIZE))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        val target = createPokemon(42)
        viewModel.onPokemonSelected(target)

        assertEquals(42, viewModel.uiState.value.selectedPokemonId)
    }

    @Test
    fun `selected pokemon id is restored from SavedStateHandle on creation`() = runTest {
        coEvery { getPokemonList.execute(limit = PAGE_SIZE, offset = 0) } returns
            Result.Success(createPokemonList(PAGE_SIZE))

        val savedState = SavedStateHandle(mapOf(HomeConstants.SAVED_STATE_SELECTED_ID to 7))
        val viewModel = buildViewModel(savedStateHandle = savedState)
        advanceUntilIdle()

        assertEquals(7, viewModel.uiState.value.selectedPokemonId)
    }

    private fun buildViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ): HomeViewModel = HomeViewModel(
        getPokemonList = getPokemonList,
        refreshPokemonList = refreshPokemonList,
        searchPokemon = searchPokemon,
        savedStateHandle = savedStateHandle
    )

    private companion object {
        const val PAGE_SIZE = 20

        fun createPokemon(id: Int, name: String = "pokemon-$id"): Pokemon =
            Pokemon(id = id, name = name, imageUrl = "https://example.com/sprites/$id.png")

        fun createPokemonList(count: Int, startId: Int = 1): List<Pokemon> =
            (startId until startId + count).map { createPokemon(it) }
    }
}
