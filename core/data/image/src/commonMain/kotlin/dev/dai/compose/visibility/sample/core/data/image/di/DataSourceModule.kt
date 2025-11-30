package dev.dai.compose.visibility.sample.core.data.image.di

import dev.dai.compose.visibility.sample.core.data.image.remote.PicsumRemoteDataSource
import dev.dai.compose.visibility.sample.core.data.image.remote.RemoteDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    single { createHttpClient() }
    single<RemoteDataSource> { PicsumRemoteDataSource(get()) }
}
