package dev.dai.compose.visibility.sample.feature.imagelist

import dev.dai.compose.visibility.sample.core.domain.image.model.ImageItem

object TestData {
    val imageItem1 = ImageItem(
        id = "1",
        author = "Test Author 1",
        width = 5000,
        height = 3333,
        downloadUrl = "https://picsum.photos/id/1/5000/3333"
    )

    val imageItem2 = ImageItem(
        id = "2",
        author = "Test Author 2",
        width = 5000,
        height = 3333,
        downloadUrl = "https://picsum.photos/id/2/5000/3333"
    )

    val imageItem3 = ImageItem(
        id = "3",
        author = "Test Author 3",
        width = 5000,
        height = 3333,
        downloadUrl = "https://picsum.photos/id/3/5000/3333"
    )

    val imageItems = listOf(imageItem1, imageItem2, imageItem3)
}
