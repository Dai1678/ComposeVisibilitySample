package dev.dai.compose.visibility.sample.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class DomainConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.apply {
                named("commonMain") {
                    dependencies {
                        implementation(libs.library("kotlinx-coroutines-core"))
                        implementation(libs.library("kotlinx-datetime"))
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
