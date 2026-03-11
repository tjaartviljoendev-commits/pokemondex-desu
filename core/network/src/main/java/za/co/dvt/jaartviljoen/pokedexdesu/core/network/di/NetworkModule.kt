package za.co.dvt.jaartviljoen.pokedexdesu.core.network.di

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.BuildConfig
import za.co.dvt.jaartviljoen.pokedexdesu.core.network.api.ApiService
import java.util.concurrent.TimeUnit

// In a production application, BASE_URL should be sourced from a BuildConfig field
// configured per build variant (e.g. debug vs release) in the module's build.gradle.kts:
//
//   buildTypes {
//       debug { buildConfigField("String", "BASE_URL", "\"https://staging-api.example.com/\"") }
//       release { buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"") }
//   }
//
// Then referenced as: BuildConfig.BASE_URL
internal object NetworkConfig {
    const val BASE_URL = "https://pokeapi.co/api/v2/"
    const val TIMEOUT_SECONDS = 30L
}

val networkModule = module {

    single {
        Moshi.Builder().build()
    }

    single {
        OkHttpClient.Builder()
            .connectTimeout(NetworkConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    single<ApiService> {
        get<Retrofit>().create(ApiService::class.java)
    }
}
