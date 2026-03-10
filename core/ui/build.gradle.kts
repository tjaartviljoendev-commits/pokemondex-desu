plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "za.co.dvt.jaartviljoen.pokedexdesu.core.ui"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
            )
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:foundation"))
    implementation(project(":core:domain"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)

    // Coil
    implementation(libs.coil.compose)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
