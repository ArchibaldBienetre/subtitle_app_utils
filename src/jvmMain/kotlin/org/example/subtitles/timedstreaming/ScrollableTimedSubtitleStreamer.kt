package org.example.subtitles.timedstreaming

import java.time.LocalTime

interface ScrollableTimedSubtitleStreamer : TimedSubtitleStreamer {
    fun scrollToTimestamp(timestamp: LocalTime)
}
