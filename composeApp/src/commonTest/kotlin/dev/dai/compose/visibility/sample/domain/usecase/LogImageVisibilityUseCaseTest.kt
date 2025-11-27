package dev.dai.compose.visibility.sample.domain.usecase

import kotlin.test.Test
import kotlin.test.assertNotNull

class LogImageVisibilityUseCaseTest {
    private val useCase = LogImageVisibilityUseCase()

    @Test
    fun `invoke executes without throwing exception`() {
        // Given
        val imageId = "test-id"
        val position = 1

        // When & Then - should not throw
        useCase(imageId, position)
    }

    @Test
    fun `invoke handles various image IDs`() {
        // Given & When & Then - should not throw
        useCase("", 1)
        useCase("123", 1)
        useCase("very-long-image-id-string-12345", 1)
    }

    @Test
    fun `invoke handles various positions`() {
        // Given & When & Then - should not throw
        useCase("test-id", 0)
        useCase("test-id", 1)
        useCase("test-id", 100)
        useCase("test-id", Int.MAX_VALUE)
    }

    @Test
    fun `useCase instance is created successfully`() {
        // Then
        assertNotNull(useCase)
    }
}
