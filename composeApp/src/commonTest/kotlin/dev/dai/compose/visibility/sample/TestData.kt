package dev.dai.compose.visibility.sample

import dev.dai.compose.visibility.sample.data.model.ImageResponse
import dev.dai.compose.visibility.sample.domain.model.ImageItem

object TestData {
    val imageResponse1 = ImageResponse(
        id = "1",
        author = "Test Author 1",
        width = 5000,
        height = 3333,
        url = "https://unsplash.com/photos/test1",
        downloadUrl = "https://picsum.photos/id/1/5000/3333"
    )

    val imageResponse2 = ImageResponse(
        id = "2",
        author = "Test Author 2",
        width = 5000,
        height = 3333,
        url = "https://unsplash.com/photos/test2",
        downloadUrl = "https://picsum.photos/id/2/5000/3333"
    )

    val imageResponse3 = ImageResponse(
        id = "3",
        author = "Test Author 3",
        width = 5000,
        height = 3333,
        url = "https://unsplash.com/photos/test3",
        downloadUrl = "https://picsum.photos/id/3/5000/3333"
    )

    val imageResponses = listOf(imageResponse1, imageResponse2, imageResponse3)

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
