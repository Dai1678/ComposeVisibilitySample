package dev.dai.compose.visibility.sample.core.data.image.repository

import dev.dai.compose.visibility.sample.core.data.image.TestData
import dev.dai.compose.visibility.sample.core.data.image.fake.FakeRemoteDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImageRepositoryImplTest {
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var repository: ImageRepositoryImpl

    @BeforeTest
    fun setup() {
        fakeRemoteDataSource = FakeRemoteDataSource()
        repository = ImageRepositoryImpl(fakeRemoteDataSource)
    }

    @Test
    fun `getImages returns success when remote data source succeeds`() = runTest {
        fakeRemoteDataSource.setSuccessResponse(TestData.imageResponses)

        val result = repository.getImages(page = 1, limit = 30)

        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.first()?.id)
        assertEquals("Test Author 1", result.getOrNull()?.first()?.author)
    }

    @Test
    fun `getImages returns empty list when remote data source returns empty`() = runTest {
        fakeRemoteDataSource.setSuccessResponse(emptyList())

        val result = repository.getImages(page = 1, limit = 30)

        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }

    @Test
    fun `getImages returns failure when remote data source throws exception`() = runTest {
        fakeRemoteDataSource.setErrorResponse(Exception("Network error"))

        val result = repository.getImages(page = 1, limit = 30)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getImages maps ImageResponse to ImageItem correctly`() = runTest {
        fakeRemoteDataSource.setSuccessResponse(listOf(TestData.imageResponse1))

        val result = repository.getImages(page = 1, limit = 30)

        val imageItem = result.getOrNull()?.first()
        assertEquals("1", imageItem?.id)
        assertEquals("Test Author 1", imageItem?.author)
        assertEquals(5000, imageItem?.width)
        assertEquals(3333, imageItem?.height)
        assertEquals("https://picsum.photos/id/1/5000/3333", imageItem?.downloadUrl)
    }
}
