package dev.dai.compose.visibility.sample.domain.usecase

import dev.dai.compose.visibility.sample.TestData
import dev.dai.compose.visibility.sample.fake.FakeImageRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetImagesUseCaseTest {
    private lateinit var fakeRepository: FakeImageRepository
    private lateinit var useCase: GetImagesUseCase

    @BeforeTest
    fun setup() {
        fakeRepository = FakeImageRepository()
        useCase = GetImagesUseCase(fakeRepository)
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Given
        fakeRepository.setSuccessResult(TestData.imageItems)

        // When
        val result = useCase(page = 1, limit = 30)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        // Given
        fakeRepository.setErrorResult(Exception("Repository error"))

        // When
        val result = useCase(page = 1, limit = 30)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Repository error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke passes parameters to repository correctly`() = runTest {
        // Given
        fakeRepository.setSuccessResult(TestData.imageItems)

        // When
        val result = useCase(page = 2, limit = 50)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        // Given
        fakeRepository.setSuccessResult(emptyList())

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }
}
