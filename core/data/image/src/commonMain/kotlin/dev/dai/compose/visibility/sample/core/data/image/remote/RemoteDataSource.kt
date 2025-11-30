package dev.dai.compose.visibility.sample.core.data.image.remote

import dev.dai.compose.visibility.sample.core.data.image.remote.model.ImageResponse

interface RemoteDataSource {
    suspend fun fetchImages(page: Int, limit: Int): List<ImageResponse>
}
