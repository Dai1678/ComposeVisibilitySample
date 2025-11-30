plugins {
    id("dev.dai.compose.visibility.kotlinMultiplatform")
    id("dev.dai.compose.visibility.composeMultiplatform")
    id("dev.dai.compose.visibility.feature")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain.image)
            implementation(projects.core.common)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.napier)
        }

        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
