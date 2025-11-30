package dev.dai.compose.visibility.sample.core.data.image.di

import dev.dai.compose.visibility.sample.core.data.image.repository.ImageRepositoryImpl
import dev.dai.compose.visibility.sample.core.domain.image.repository.ImageRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ImageRepository> { ImageRepositoryImpl(get()) }
}
