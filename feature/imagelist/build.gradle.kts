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
        }
    }
}
