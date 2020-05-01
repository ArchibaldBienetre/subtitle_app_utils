package org.example.subtitles

import java.time.LocalTime

class SubtitleEntry {

    /**
     * 0-based index of the subtitle entry
     */
    fun getIndex(): Int {
        return 0
    }

    /**
     * Time from which a subtitle is displayed
     */
    fun getFromTimestamp(): LocalTime {
        return LocalTime.of(0, 0, 0, 0)
    }

    /**
     * Time up until which a subtitle is displayed
     */
    fun getToTimestamp(): LocalTime {
        return LocalTime.of(0, 0, 0, 0)
    }


    /**
     * The actual subtitle
     */
    fun getText(): String {
        return ""
    }


}