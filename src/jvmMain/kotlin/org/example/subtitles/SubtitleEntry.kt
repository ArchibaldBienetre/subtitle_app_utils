package org.example.subtitles

import java.time.LocalTime

/**
 * Mutable class.
 *
 * One set of subtitle texts #textLines at index #index
 * that will be displayed at one time
 * between #fromTimestamp and #toTimestamp.
 *
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
        /**
         * Creates a default-valued subtitle entry, but will be initialized with the given text as lines
         */
        fun createFromString(text: String = "") = SubtitleEntry(text.lines())

        fun copyOf(entry: SubtitleEntry) = run {
            val newEntry = SubtitleEntry(entry.textLines)
            newEntry.index = entry.index
            newEntry.fromTimestamp = entry.fromTimestamp
            newEntry.toTimestamp = entry.toTimestamp
            newEntry
        }
    }
}
