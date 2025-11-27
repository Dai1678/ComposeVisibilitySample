package dev.dai.compose.visibility.sample.data.repository

import dev.dai.compose.visibility.sample.data.remote.RemoteDataSource
import dev.dai.compose.visibility.sample.domain.model.ImageItem
import dev.dai.compose.visibility.sample.domain.repository.ImageRepository

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
