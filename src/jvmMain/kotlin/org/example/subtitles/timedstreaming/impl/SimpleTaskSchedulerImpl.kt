package org.example.subtitles.timedstreaming.impl

import org.example.subtitles.timedstreaming.SimpleTaskScheduler
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * ScheduledThreadPoolExecutor-based implementation
 **/
class SimpleTaskSchedulerImpl : SimpleTaskScheduler {

    private val threadPoolExecutor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    private val scheduledFutures: MutableList<ScheduledFuture<*>> = ArrayList()

    override fun scheduleAtMillisFromNow(millisFromNow: Long, runnable: () -> Unit) {
        val future = threadPoolExecutor.schedule(
            runnable, millisFromNow, MILLISECONDS
        )
        this.scheduledFutures.add(future)
    }

    override fun cancelAllScheduled() {
        this.scheduledFutures.forEach {
            it.cancel(false)
        }
    }

    override fun close() {
        this.threadPoolExecutor.shutdown()
    }

}