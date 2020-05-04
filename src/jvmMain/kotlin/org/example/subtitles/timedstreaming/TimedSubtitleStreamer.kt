package org.example.subtitles.timedstreaming

import java.io.Closeable

interface TimedSubtitleStreamer : Closeable {

    /**
     * Start or continue streaming from the current playback time.
     */
    fun startOrContinue()


    /**
     * Temporarily pause streaming. Will not affect playback time.
     */
    fun pause()

    /**
     * Stop playback, reset playback time to the beginning.
     */
    fun stop()
}