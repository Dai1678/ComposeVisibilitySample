package dev.dai.compose.visibility.sample.di

import dev.dai.compose.visibility.sample.data.remote.PicsumRemoteDataSource
import dev.dai.compose.visibility.sample.data.remote.RemoteDataSource
import dev.dai.compose.visibility.sample.data.repository.ImageRepositoryImpl
import dev.dai.compose.visibility.sample.domain.repository.ImageRepository
import dev.dai.compose.visibility.sample.domain.usecase.GetImagesUseCase
import dev.dai.compose.visibility.sample.domain.usecase.LogImageVisibilityUseCase
import dev.dai.compose.visibility.sample.ui.viewmodel.ImageListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Data Layer
    single { createHttpClient() }
    single<RemoteDataSource> { PicsumRemoteDataSource(get()) }
    single<ImageRepository> { ImageRepositoryImpl(get()) }

    // Domain Layer
    factory { GetImagesUseCase(get()) }
    factory { LogImageVisibilityUseCase() }

    // UI Layer
    viewModel { ImageListViewModel(get(), get()) }
}
