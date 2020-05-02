package org.example.subtitles

import java.time.LocalTime

/**
 * @param textLines The actual subtitle text (text lines as list)
 */
data class SubtitleEntry(var textLines: List<String> = listOf("")) {

    /**
     * 0-based index of the subtitle entry
     */
    var index: Int = 0

    /**
     * Time from which a subtitle is displayed
     */
    var fromTimestamp: LocalTime = LocalTime.of(0, 0, 0, 0)

    /**
     * Time up until which a subtitle is displayed
     */
    var toTimestamp: LocalTime = LocalTime.of(0, 0, 0, 0)

    companion object Factory {
        fun createFromString(text: String = "") = SubtitleEntry(text.lines())
    }
}
