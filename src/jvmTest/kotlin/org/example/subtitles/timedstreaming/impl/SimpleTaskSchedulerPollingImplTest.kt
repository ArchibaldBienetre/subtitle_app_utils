package org.example.subtitles.timedstreaming.impl

import org.example.subtitles.test.ForwardableClock
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimpleTaskSchedulerPollingImplTest {

    private lateinit var sut: SimpleTaskSchedulerPollingImpl
    private val clock: ForwardableClock = ForwardableClock()
    private val pollingIntervalMs: Long = 5
    @Before
    fun setUp() {
        sut = SimpleTaskSchedulerPollingImpl(clock, pollingIntervalMs)
    }

    @After
    fun tearDown() {
        sut.close()
    }

    @Test
    fun scheduleAtMillisFromNow() {
        val executed = AtomicBoolean(false)

        sut.scheduleAtMillisFromNow(2000) {
            executed.set(true)
        }
        assertFalse(executed.get())
        clock.forwardBy(Duration.ofSeconds(1))
        assertFalse(executed.get())
        clock.forwardBy(Duration.ofSeconds(1))
        assertFalse(executed.get())
        clock.forwardBy(Duration.ofNanos(1))
        Thread.sleep(pollingIntervalMs)
        assertTrue(executed.get())
    }

    @Test
    fun scheduleAtMillisFromNow_multiple() {
        val executedTask1 = AtomicBoolean(false)
        val executedTask2 = AtomicBoolean(false)
        val executedTask3 = AtomicBoolean(false)
        sut.scheduleAtMillisFromNow(5000) {
            executedTask3.set(true)
        }
        sut.scheduleAtMillisFromNow(4000) {
            executedTask2.set(true)
        }
        sut.scheduleAtMillisFromNow(2000) {
            executedTask1.set(true)
        }
        clock.forwardBy(Duration.ofSeconds(5).plusNanos(1))
        Thread.sleep(pollingIntervalMs)
        assertTrue(executedTask1.get())
        assertTrue(executedTask2.get())
        assertTrue(executedTask3.get())
    }

    @Test
    fun cancelAllScheduled() {
        val executedTask1 = AtomicBoolean(false)
        val executedTask2 = AtomicBoolean(false)
        val executedTask3 = AtomicBoolean(false)
        sut.scheduleAtMillisFromNow(5000) {
            executedTask3.set(true)
        }
        sut.scheduleAtMillisFromNow(4000) {
            executedTask2.set(true)
        }
        sut.scheduleAtMillisFromNow(2000) {
            executedTask1.set(true)
        }

        sut.cancelAllScheduled()

        clock.forwardBy(Duration.ofSeconds(10))
        Thread.sleep(pollingIntervalMs)
        assertFalse(executedTask1.get())
        assertFalse(executedTask2.get())
        assertFalse(executedTask3.get())
    }
}