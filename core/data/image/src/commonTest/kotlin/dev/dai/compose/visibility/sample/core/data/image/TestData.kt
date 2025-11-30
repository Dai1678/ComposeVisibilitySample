package dev.dai.compose.visibility.sample.core.data.image

import dev.dai.compose.visibility.sample.core.data.image.remote.model.ImageResponse

object TestData {
    val imageResponse1 = ImageResponse(
        id = "1",
        author = "Test Author 1",
        width = 5000,
        height = 3333,
        url = "https://picsum.photos/id/1/5000/3333",
        downloadUrl = "https://picsum.photos/id/1/5000/3333"
    )

    val imageResponse2 = ImageResponse(
        id = "2",
        author = "Test Author 2",
        width = 4000,
        height = 3000,
        url = "https://picsum.photos/id/2/4000/3000",
        downloadUrl = "https://picsum.photos/id/2/4000/3000"
    )

    val imageResponse3 = ImageResponse(
        id = "3",
        author = "Test Author 3",
        width = 3000,
        height = 2000,
        url = "https://picsum.photos/id/3/3000/2000",
        downloadUrl = "https://picsum.photos/id/3/3000/2000"
    )

    val imageResponses = listOf(imageResponse1, imageResponse2, imageResponse3)
}
