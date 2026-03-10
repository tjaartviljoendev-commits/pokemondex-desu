package za.co.dvt.jaartviljoen.pokedexdesu

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import za.co.dvt.jaartviljoen.pokedexdesu.core.data.di.dataModule
import za.co.dvt.jaartviljoen.pokedexdesu.core.domain.di.domainModule
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.di.networkModule
import za.co.dvt.jaartviljoen.pokedexdesu.journey.home.di.homeModule
import za.co.dvt.jaartviljoen.pokedexdesu.journey.info.di.infoModule

class PokedexDesuApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PokedexDesuApp)
            modules(networkModule, dataModule, domainModule, homeModule, infoModule)
        }
    }
}
