package dev.dai.compose.visibility.sample.data.model

import dev.dai.compose.visibility.sample.domain.model.ImageItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    @SerialName("download_url")
    val downloadUrl: String
) {
    fun toDomainModel() = ImageItem(
        id = id,
        author = author,
        width = width,
        height = height,
        downloadUrl = downloadUrl
    )
}
