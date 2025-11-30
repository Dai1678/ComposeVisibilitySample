package dev.dai.compose.visibility.sample.di

import dev.dai.compose.visibility.sample.core.data.image.di.dataSourceModule
import dev.dai.compose.visibility.sample.core.data.image.di.repositoryModule
import dev.dai.compose.visibility.sample.feature.imagelist.di.imageListFeatureModule
import org.koin.dsl.module

val appModule = module {
    includes(
        dataSourceModule,
        repositoryModule,
        imageListFeatureModule
    )
}
