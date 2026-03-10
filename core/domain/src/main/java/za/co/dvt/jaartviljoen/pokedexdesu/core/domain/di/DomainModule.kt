package za.co.dvt.jaartviljoen.pokedexdesu.core.domain.di

import org.koin.dsl.module
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.GetPokemonDetailUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.GetPokemonListUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.RefreshPokemonListUseCase
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.usecase.SearchPokemonUseCase

val domainModule = module {
    factory { GetPokemonListUseCase(get()) }
    factory { GetPokemonDetailUseCase(get()) }
    factory { RefreshPokemonListUseCase(get()) }
    factory { SearchPokemonUseCase(get()) }
}
