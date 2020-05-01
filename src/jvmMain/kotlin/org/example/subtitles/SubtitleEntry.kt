package org.example.subtitles

import java.time.LocalTime

class SubtitleEntry {

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

    /**
     * The actual subtitle
     */
    var text: String = ""
}
