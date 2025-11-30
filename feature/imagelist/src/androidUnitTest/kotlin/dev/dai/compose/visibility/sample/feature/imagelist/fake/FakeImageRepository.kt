package dev.dai.compose.visibility.sample.feature.imagelist.fake

import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem
import dev.dai.compose.visibility.sample.core.domain.image.repository.ImageRepository

class FakeImageRepository : ImageRepository {
    var result: Result<List<ImageItem>> = Result.success(emptyList())

    override suspend fun getImages(page: Int, limit: Int): Result<List<ImageItem>> {
        return result
    }

    fun setSuccessResult(images: List<ImageItem>) {
        result = Result.success(images)
    }

    fun setErrorResult(error: Throwable) {
        result = Result.failure(error)
    }
}
