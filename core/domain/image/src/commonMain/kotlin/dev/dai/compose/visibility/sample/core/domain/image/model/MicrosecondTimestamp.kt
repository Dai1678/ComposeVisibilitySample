package dev.dai.compose.visibility.sample.core.domain.image.model

import kotlin.jvm.JvmInline

/**
 * マイクロ秒単位のタイムスタンプを表すクラス
 *
 * @property value マイクロ秒単位のタイムスタンプ値
 */
@JvmInline
value class MicrosecondTimestamp(val value: Long) {
    /**
     * 秒部分を取得
     */
    val seconds: Long
        get() = value / 1_000_000

    /**
     * マイクロ秒部分を取得（0-999999）
     */
    val microseconds: Long
        get() = value % 1_000_000

    /**
     * "秒.マイクロ秒"の形式でフォーマット
     *
     * 例:
     * - 1234567890123456L → "1234567890.123456"
     * - 1000000000000L → "1000000.000000"
     */
    fun format(): String {
        return "$seconds.${microseconds.toString().padStart(6, '0')}"
    }
}
