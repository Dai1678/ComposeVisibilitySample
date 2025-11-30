package dev.dai.compose.visibility.sample.core.data.image.fake

import dev.dai.compose.visibility.sample.core.data.image.remote.RemoteDataSource
import dev.dai.compose.visibility.sample.core.data.image.remote.model.ImageResponse

class FakeRemoteDataSource : RemoteDataSource {
    var shouldThrowError = false
    var errorToThrow: Exception? = null
    var imageResponses: List<ImageResponse> = emptyList()

    override suspend fun fetchImages(page: Int, limit: Int): List<ImageResponse> {
        if (shouldThrowError) {
            throw errorToThrow ?: Exception("Network error")
        }
        return imageResponses
    }

    fun setSuccessResponse(responses: List<ImageResponse>) {
        imageResponses = responses
        shouldThrowError = false
    }

    fun setErrorResponse(error: Exception) {
        errorToThrow = error
        shouldThrowError = true
    }
}
