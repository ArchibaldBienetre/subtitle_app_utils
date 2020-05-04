package org.example.subtitles.timedstreaming

import java.io.Closeable

interface TimedSubtitleStreamer : Closeable {

    fun startOrContinue()

    fun pause()

    fun stop()
}