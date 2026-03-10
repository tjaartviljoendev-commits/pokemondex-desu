package za.co.dvt.jaartviljoen.pokedexdesu.journey.home.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.viewmodel.HomeViewModel

val homeModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
}
