package dev.dai.compose.visibility.sample.core.domain.image.model

import kotlinx.serialization.Serializable

@Serializable
data class VisibilityLog(
    val id: String,
    val position: Int,
    val time: String // "秒.マイクロ秒"の形式（例: "1234567890.123456"）
) {
    constructor(id: String, position: Int, timestamp: MicrosecondTimestamp) : this(
        id = id,
        position = position,
        time = timestamp.format()
    )

    fun toJson(): String {
        return """{"id":"$id","position":$position,"time":$time}"""
    }
}
