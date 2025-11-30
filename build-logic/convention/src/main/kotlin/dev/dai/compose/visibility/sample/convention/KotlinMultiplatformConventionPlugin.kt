package dev.dai.compose.visibility.sample.convention

import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.library")

        extensions.configure<KotlinMultiplatformExtension> {
            androidTarget {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }

            listOf(
                iosArm64(),
                iosSimulatorArm64()
            ).configureFramework()

            jvmToolchain(11)
            applyDefaultHierarchyTemplate()
        }

        extensions.configure<LibraryExtension> {
            compileSdk = libs.versionInt("android-compileSdk")
            defaultConfig {
                minSdk = libs.versionInt("android-minSdk")
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
            namespace = defaultNamespace()
        }
    }
}

private fun List<KotlinNativeTarget>.configureFramework() = forEach { target ->
    target.binaries.framework {
        baseName = target.project.name
        isStatic = true
    }
}

private fun Project.defaultNamespace(): String {
    val suffix = path.removePrefix(":").replace(':', '.')
    return buildString {
        append("dev.dai.compose.visibility.sample")
        if (suffix.isNotEmpty()) {
            append('.')
            append(suffix)
        }
    }
}
