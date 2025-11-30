package dev.dai.compose.visibility.sample.core.domain.image.repository

import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem

interface ImageRepository {
    suspend fun getImages(page: Int, limit: Int): Result<List<ImageItem>>
}
