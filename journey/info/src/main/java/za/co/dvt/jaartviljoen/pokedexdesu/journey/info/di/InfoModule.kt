package za.co.dvt.jaartviljoen.pokedexdesu.journey.info.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.viewmodel.InfoViewModel

val infoModule = module {
    viewModel { params -> InfoViewModel(get(), pokemonId = params.get()) }
}
