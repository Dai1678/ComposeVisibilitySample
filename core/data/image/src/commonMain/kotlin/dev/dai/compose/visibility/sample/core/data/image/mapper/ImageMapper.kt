package dev.dai.compose.visibility.sample.core.data.image.mapper

import dev.dai.compose.visibility.sample.core.data.image.remote.model.ImageResponse
import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem

fun ImageResponse.toDomainModel(): ImageItem =
    ImageItem(
        id = id,
        author = author,
        width = width,
        height = height,
        downloadUrl = downloadUrl
    )

fun List<ImageResponse>.toDomainModel(): List<ImageItem> =
    map { it.toDomainModel() }
