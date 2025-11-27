package dev.dai.compose.visibility.sample.domain.usecase

import dev.dai.compose.visibility.sample.domain.model.MicrosecondTimestamp
import dev.dai.compose.visibility.sample.domain.model.VisibilityLog
import kotlin.time.Clock.*
import kotlin.time.ExperimentalTime

class LogImageVisibilityUseCase {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(id: String, position: Int) {
        val currentEpochMillis = System.now().toEpochMilliseconds()
        val currentTimeMicros = currentEpochMillis * 1000 // ミリ秒をマイクロ秒に変換

        val timestamp = MicrosecondTimestamp(currentTimeMicros)
        val log = VisibilityLog(
            id = id,
            position = position,
            timestamp = timestamp
        )

        // LogCatに出力
        println("[VisibilityLog] ${log.toJson()}")
    }
}
