package org.example.subtitles.timedstreaming.impl

import org.example.subtitles.timedstreaming.MyRunnable
import org.example.subtitles.timedstreaming.SimpleTaskScheduler
import java.time.Clock
import java.time.Instant

/**
 * ScheduledThreadPoolExecutor-based implementation
 **/
class SimpleTaskSchedulerPollingImpl : SimpleTaskScheduler {

    private val clock: Clock
    private val schedulerThread: Thread
    /**
     * Contains pair: absolute time -> runnable
     */
    private val schedule: MutableList<Pair<Instant, MyRunnable>>

    constructor(clock: Clock = Clock.systemUTC(), pollingIntervalMs: Long = 50L) {
        this.clock = clock
        this.schedule = ArrayList()
        this.schedulerThread = Thread {
            try {
                while (!Thread.currentThread().isInterrupted) {
                    val now = Instant.now(this.clock)
                    ArrayList(this.schedule).forEach {
                        if (it.first.isBefore(now)) {
                            this.schedule.remove(it)
                            it.second()
                        }
                    }
                    Thread.sleep(pollingIntervalMs)
                }
            } catch (e: InterruptedException) {
                // do nothing - graceful shutdown
            }
        }
        this.schedulerThread.start()
    }

    override fun scheduleAtMillisFromNow(millisFromNow: Long, runnable: MyRunnable) {
        schedule.add(Pair(Instant.now(clock).plusMillis(millisFromNow), runnable))
    }

    override fun cancelAllScheduled() {
        schedule.clear()
    }

    override fun close() {
        this.schedulerThread.interrupt()
    }

}