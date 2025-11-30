package dev.dai.compose.visibility.sample.core.data.image.remote

import dev.dai.compose.visibility.sample.core.data.image.remote.model.ImageResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class PicsumRemoteDataSource(
    private val httpClient: HttpClient
) : RemoteDataSource {
    override suspend fun fetchImages(page: Int, limit: Int): List<ImageResponse> {
        return httpClient.get("https://picsum.photos/v2/list") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()
    }
}
