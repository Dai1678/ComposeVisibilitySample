package dev.dai.compose.visibility.sample.feature.imagelist.di

import dev.dai.compose.visibility.sample.core.domain.image.usecase.GetImagesUseCase
import dev.dai.compose.visibility.sample.core.domain.image.usecase.LogImageVisibilityUseCase
import dev.dai.compose.visibility.sample.feature.imagelist.viewmodel.ImageListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val imageListFeatureModule = module {
    factoryOf(::GetImagesUseCase)
    factoryOf(::LogImageVisibilityUseCase)

    viewModelOf(::ImageListViewModel)
}
