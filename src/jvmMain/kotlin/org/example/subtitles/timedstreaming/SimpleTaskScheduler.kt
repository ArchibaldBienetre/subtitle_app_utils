package org.example.subtitles.timedstreaming

import java.io.Closeable

typealias MyRunnable = () -> Unit

interface SimpleTaskScheduler : Closeable {

    fun scheduleAtMillisFromNow(millisFromNow: Long, runnable: MyRunnable)

    fun cancelAllScheduled()
}