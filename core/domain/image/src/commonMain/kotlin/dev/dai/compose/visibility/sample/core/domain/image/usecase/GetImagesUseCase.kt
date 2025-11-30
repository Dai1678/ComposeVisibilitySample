package dev.dai.compose.visibility.sample.core.domain.image.usecase

import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem
import dev.dai.compose.visibility.sample.core.domain.image.repository.ImageRepository

class GetImagesUseCase(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 30): Result<List<ImageItem>> {
        return repository.getImages(page, limit)
    }
}
