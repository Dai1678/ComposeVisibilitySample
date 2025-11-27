package dev.dai.compose.visibility.sample.domain.repository

import dev.dai.compose.visibility.sample.domain.model.ImageItem

interface ImageRepository {
    suspend fun getImages(page: Int, limit: Int): Result<List<ImageItem>>
}
