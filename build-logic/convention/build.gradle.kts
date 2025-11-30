plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "dev.dai.compose.visibility.kotlinMultiplatform"
            implementationClass = "dev.dai.compose.visibility.sample.convention.KotlinMultiplatformConventionPlugin"
        }
        register("composeMultiplatform") {
            id = "dev.dai.compose.visibility.composeMultiplatform"
            implementationClass = "dev.dai.compose.visibility.sample.convention.ComposeMultiplatformConventionPlugin"
        }
        register("data") {
            id = "dev.dai.compose.visibility.data"
            implementationClass = "dev.dai.compose.visibility.sample.convention.DataConventionPlugin"
        }
        register("domain") {
            id = "dev.dai.compose.visibility.domain"
            implementationClass = "dev.dai.compose.visibility.sample.convention.DomainConventionPlugin"
        }
        register("feature") {
            id = "dev.dai.compose.visibility.feature"
            implementationClass = "dev.dai.compose.visibility.sample.convention.FeatureConventionPlugin"
        }
    }
}
