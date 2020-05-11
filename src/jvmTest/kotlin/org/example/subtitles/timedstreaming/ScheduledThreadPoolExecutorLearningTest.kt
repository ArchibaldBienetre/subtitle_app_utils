package org.example.subtitles.timedstreaming

import org.junit.Test
import java.lang.System.currentTimeMillis
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicLong

/**
 * LearningTest: simple test class where I commit what I played around with when exploring this new bit of technology.
 *
 * Usually, some things out of such experiments are worth preserving - that's a LearningTest.
 */
class ScheduledThreadPoolExecutorLearningTest {

    @Test
    fun testScheduledRunning() {
        val threadPoolExecutor = ScheduledThreadPoolExecutor(1)
        val startTime = AtomicLong(currentTimeMillis())
        threadPoolExecutor.schedule(
            {
                println("Setting baseline...")
                startTime.set(currentTimeMillis())
            },
            1_000,
            MILLISECONDS
        )
        threadPoolExecutor.schedule({ printTimeAndStats(startTime, threadPoolExecutor) }, 2_000, MILLISECONDS)
        threadPoolExecutor.schedule({ printTimeAndStats(startTime, threadPoolExecutor) }, 60_000, MILLISECONDS)
        threadPoolExecutor.schedule({ printTimeAndStats(startTime, threadPoolExecutor) }, 9_000, MILLISECONDS)
        threadPoolExecutor.schedule({ printTimeAndStats(startTime, threadPoolExecutor) }, 5_000, MILLISECONDS)
        threadPoolExecutor.shutdown()
        threadPoolExecutor.awaitTermination(60_000, MILLISECONDS)
    }

    @Test
    fun testScheduledCancellation() {
        val threadPoolExecutor = ScheduledThreadPoolExecutor(1)
        val startTime = AtomicLong(currentTimeMillis())
        val f0 = threadPoolExecutor.schedule(
            {
                println("Setting baseline...")
                startTime.set(currentTimeMillis())
            },
            1_000,
            MILLISECONDS
        )
        val f1 = threadPoolExecutor.schedule({ printTimeAndStats(startTime, threadPoolExecutor) }, 2_000, MILLISECONDS)
        val f2 = threadPoolExecutor.schedule({ printTimeAndStats(startTime, threadPoolExecutor) }, 60_000, MILLISECONDS)
        val f3 = threadPoolExecutor.schedule({ printTimeAndStats(startTime, threadPoolExecutor) }, 9_000, MILLISECONDS)
        threadPoolExecutor.schedule({
            printTimeAndStats(startTime, threadPoolExecutor)
            if (!f0.cancel(false)) {
                println("f0 not cancelable")
            }
            if (f1.cancel(false)) {
                println("f1 cancelled")
            } else {
                println("f1 not cancelable")
            }
            if (f2.cancel(false)) {
                println("f2 cancelled")
            }
            if (f3.cancel(false)) {
                println("f3 cancelled")
            }
            System.out.flush()
        }, 5_000, MILLISECONDS)

        threadPoolExecutor.shutdown()

        threadPoolExecutor.awaitTermination(60_000, MILLISECONDS)
    }

    private fun printTimeAndStats(
        startTime: AtomicLong,
        executor: ScheduledThreadPoolExecutor
    ) {
        println("Current time: ${currentTimeMillis() - startTime.get()}, queue size: ${executor.queue.size}")
    }
}
