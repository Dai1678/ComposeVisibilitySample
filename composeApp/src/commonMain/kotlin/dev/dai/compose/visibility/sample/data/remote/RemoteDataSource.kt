package dev.dai.compose.visibility.sample.data.remote

import dev.dai.compose.visibility.sample.data.model.ImageResponse

interface RemoteDataSource {
    suspend fun fetchImages(page: Int, limit: Int): List<ImageResponse>
}
