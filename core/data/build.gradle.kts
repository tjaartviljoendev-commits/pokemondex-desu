plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "za.co.dvt.jaartviljoen.pokedexdesu.core.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core:foundation"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))

    // Koin
    implementation(libs.koin.android)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Moshi
    implementation(libs.moshi)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
