package dev.dai.compose.visibility.sample.core.domain.image.model

import kotlin.test.Test
import kotlin.test.assertEquals

class MicrosecondTimestampTest {
    @Test
    fun `format returns correct format for timestamp with microseconds`() {
        // Given
        val timestamp = MicrosecondTimestamp(1234567890123456L)

        // When
        val result = timestamp.format()

        // Then
        assertEquals("1234567890.123456", result)
    }

    @Test
    fun `format returns correct format for timestamp with zero microseconds`() {
        // Given
        val timestamp = MicrosecondTimestamp(1000000000000L)

        // When
        val result = timestamp.format()

        // Then
        assertEquals("1000000.000000", result)
    }

    @Test
    fun `format pads microseconds with leading zeros`() {
        // Given
        val timestamp = MicrosecondTimestamp(1000000000001L)

        // When
        val result = timestamp.format()

        // Then
        assertEquals("1000000.000001", result)
    }

    @Test
    fun `seconds returns correct value`() {
        // Given
        val timestamp = MicrosecondTimestamp(1234567890123456L)

        // When & Then
        assertEquals(1234567890L, timestamp.seconds)
    }

    @Test
    fun `microseconds returns correct value`() {
        // Given
        val timestamp = MicrosecondTimestamp(1234567890123456L)

        // When & Then
        assertEquals(123456L, timestamp.microseconds)
    }

    @Test
    fun `microseconds returns zero when no microseconds part`() {
        // Given
        val timestamp = MicrosecondTimestamp(1000000000000L)

        // When & Then
        assertEquals(0L, timestamp.microseconds)
    }
}
