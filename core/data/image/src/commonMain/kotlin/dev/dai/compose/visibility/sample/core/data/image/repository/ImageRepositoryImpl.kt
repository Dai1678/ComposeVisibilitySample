package dev.dai.compose.visibility.sample.core.data.image.repository

import dev.dai.compose.visibility.sample.core.data.image.mapper.toDomainModel
import dev.dai.compose.visibility.sample.core.data.image.remote.RemoteDataSource
import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem
import dev.dai.compose.visibility.sample.core.domain.image.repository.ImageRepository

class ImageRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : ImageRepository {
    override suspend fun getImages(page: Int, limit: Int): Result<List<ImageItem>> =
        try {
            val response = remoteDataSource.fetchImages(page, limit)
            Result.success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
}
