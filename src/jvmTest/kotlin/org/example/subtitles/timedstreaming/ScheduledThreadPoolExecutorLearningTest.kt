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
        val startTime: AtomicLong = AtomicLong(currentTimeMillis())
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

    private fun printTimeAndStats(
        startTime: AtomicLong,
        executor: ScheduledThreadPoolExecutor
    ) {
        println("Current time: ${currentTimeMillis() - startTime.get()}, queue size: ${executor.queue.size}")
    }
}
