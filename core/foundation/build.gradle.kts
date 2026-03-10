plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "za.co.dvt.jaartviljoen.pokedexdesu.core.foundation"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
