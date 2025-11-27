package dev.dai.compose.visibility.sample.data.repository

import dev.dai.compose.visibility.sample.TestData
import dev.dai.compose.visibility.sample.fake.FakeRemoteDataSource
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
        // Given
        fakeRemoteDataSource.setSuccessResponse(TestData.imageResponses)

        // When
        val result = repository.getImages(page = 1, limit = 30)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
        assertEquals("1", result.getOrNull()?.first()?.id)
        assertEquals("Test Author 1", result.getOrNull()?.first()?.author)
    }

    @Test
    fun `getImages returns empty list when remote data source returns empty`() = runTest {
        // Given
        fakeRemoteDataSource.setSuccessResponse(emptyList())

        // When
        val result = repository.getImages(page = 1, limit = 30)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }

    @Test
    fun `getImages returns failure when remote data source throws exception`() = runTest {
        // Given
        fakeRemoteDataSource.setErrorResponse(Exception("Network error"))

        // When
        val result = repository.getImages(page = 1, limit = 30)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getImages maps ImageResponse to ImageItem correctly`() = runTest {
        // Given
        fakeRemoteDataSource.setSuccessResponse(listOf(TestData.imageResponse1))

        // When
        val result = repository.getImages(page = 1, limit = 30)

        // Then
        val imageItem = result.getOrNull()?.first()
        assertEquals("1", imageItem?.id)
        assertEquals("Test Author 1", imageItem?.author)
        assertEquals(5000, imageItem?.width)
        assertEquals(3333, imageItem?.height)
        assertEquals("https://picsum.photos/id/1/5000/3333", imageItem?.downloadUrl)
    }
}
