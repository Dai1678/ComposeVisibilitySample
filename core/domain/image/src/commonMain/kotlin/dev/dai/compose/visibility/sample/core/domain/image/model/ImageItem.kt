package dev.dai.compose.visibility.sample.core.domain.image.model

data class ImageItem(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val downloadUrl: String
)
