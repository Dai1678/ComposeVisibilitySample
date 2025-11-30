plugins {
    id("dev.dai.compose.visibility.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Shared utilities can declare dependencies here when needed.
        }
    }
}
