package dev.dai.compose.visibility.sample.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class DataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                named("commonMain") {
                    dependencies {
                        implementation(libs.library("ktor-client-core"))
                        implementation(libs.library("ktor-client-content-negotiation"))
                        implementation(libs.library("ktor-serialization-kotlinx-json"))
                        implementation(libs.library("ktor-client-logging"))
                        implementation(libs.library("koin-core"))
                        implementation(libs.library("napier"))
                    }
                }
                named("androidMain") {
                    dependencies {
                        implementation(libs.library("ktor-client-okhttp"))
                    }
                }
                named("iosMain") {
                    dependencies {
                        implementation(libs.library("ktor-client-darwin"))
                    }
                }
                named("commonTest") {
                    dependencies {
                        implementation(libs.library("kotlin-test"))
                        implementation(libs.library("kotlinx-coroutines-test"))
                    }
                }
            }
        }
    }
}
