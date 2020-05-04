package org.example.subtitles.timedstreaming

import java.time.LocalTime

interface ScrollableTimedSubtitleStreamer : TimedSubtitleStreamer {

    /**
     * Set playback time to the given time.
     */
    fun scrollToTimestamp(timestamp: LocalTime)
}
