package za.co.dvt.jaartviljoen.pokedexdesu.core.data.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.local.db.PokemonDatabase
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.repository.PokemonRepositoryImpl
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.repository.PokemonRepository
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.DefaultDispatcherProvider
import za.co.dvt.jaartviljoen.pokedexdesu.core.foundation.DispatcherProvider

private const val DATABASE_NAME = "pokedex_database"

val dataModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            PokemonDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration(false).build()
    }

    single { get<PokemonDatabase>().pokemonDao() }

    single<DispatcherProvider> { DefaultDispatcherProvider() }

    single<PokemonRepository> {
        PokemonRepositoryImpl(
            apiService = get(),
            dao = get(),
            dispatchers = get()
        )
    }
}
