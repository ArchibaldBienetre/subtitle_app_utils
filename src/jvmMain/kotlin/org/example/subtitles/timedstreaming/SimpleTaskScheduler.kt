package org.example.subtitles.timedstreaming

import java.io.Closeable

interface SimpleTaskScheduler : Closeable {

    fun scheduleAtMillisFromNow(epochMillis: Long, runnable: () -> Unit)

    fun cancelAllScheduled()
}