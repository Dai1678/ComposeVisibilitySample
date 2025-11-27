package dev.dai.compose.visibility.sample.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VisibilityLogTest {
    @Test
    fun `toJson returns correct JSON format with String constructor`() {
        // Given
        val log = VisibilityLog(
            id = "test-id",
            position = 1,
            time = "1234567890.123456"
        )

        // When
        val json = log.toJson()

        // Then
        assertEquals("""{"id":"test-id","position":1,"time":1234567890.123456}""", json)
    }

    @Test
    fun `toJson returns correct JSON format with MicrosecondTimestamp constructor`() {
        // Given
        val timestamp = MicrosecondTimestamp(1234567890123456L)
        val log = VisibilityLog(
            id = "test-id",
            position = 1,
            timestamp = timestamp
        )

        // When
        val json = log.toJson()

        // Then
        assertEquals("""{"id":"test-id","position":1,"time":1234567890.123456}""", json)
    }

    @Test
    fun `MicrosecondTimestamp constructor formats time correctly`() {
        // Given
        val timestamp = MicrosecondTimestamp(1000000000000L)

        // When
        val log = VisibilityLog(
            id = "test-id",
            position = 0,
            timestamp = timestamp
        )

        // Then
        assertEquals("1000000.000000", log.time)
    }

    @Test
    fun `time field is formatted as expected in JSON output`() {
        // Given
        val log = VisibilityLog(
            id = "image-123",
            position = 5,
            time = "9999999999.999999"
        )

        // When
        val json = log.toJson()

        // Then
        assertTrue(json.contains("\"time\":9999999999.999999"))
    }
}
