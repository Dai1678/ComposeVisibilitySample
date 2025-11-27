package dev.dai.compose.visibility.sample.domain.usecase

import dev.dai.compose.visibility.sample.domain.model.ImageItem
import dev.dai.compose.visibility.sample.domain.repository.ImageRepository

class GetImagesUseCase(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 30): Result<List<ImageItem>> {
        return repository.getImages(page, limit)
    }
}
