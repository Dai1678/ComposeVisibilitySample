package dev.dai.compose.visibility.sample.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                named("commonMain") {
                    dependencies {
                        implementation(libs.library("androidx-lifecycle-viewmodelCompose"))
                        implementation(libs.library("androidx-lifecycle-runtimeCompose"))
                        implementation(libs.library("coil-compose"))
                        implementation(libs.library("coil-network-ktor"))
                        implementation(libs.library("koin-core"))
                        implementation(libs.library("koin-compose"))
                        implementation(libs.library("koin-compose-viewmodel"))
                        implementation(libs.library("napier"))
                    }
                }
                named("androidMain") {
                    dependencies {
                        implementation(libs.library("androidx-activity-compose"))
                    }
                }
                named("androidUnitTest") {
                    dependencies {
                        implementation(libs.library("junit"))
                        implementation(libs.library("kotlinx-coroutines-test"))
                    }
                }
            }
        }
    }
}
