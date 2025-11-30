package dev.dai.compose.visibility.sample.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.versionInt(alias: String): Int =
    findVersion(alias)
        .orElseThrow { IllegalStateException("Version catalog entry '$alias' not found.") }
        .requiredVersion
        .toInt()

internal fun VersionCatalog.library(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias)
        .orElseThrow { IllegalStateException("Library catalog entry '$alias' not found.") }
