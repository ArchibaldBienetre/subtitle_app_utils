package org.example.subtitles.timedstreaming

import java.io.Closeable

typealias MyRunnable = () -> Unit

interface SimpleTaskScheduler : Closeable {

    fun scheduleAtMillisFromNow(epochMillis: Long, runnable: MyRunnable)

    fun cancelAllScheduled()
}