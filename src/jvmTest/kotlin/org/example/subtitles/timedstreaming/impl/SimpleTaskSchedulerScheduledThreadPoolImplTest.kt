package org.example.subtitles.timedstreaming.impl

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// not really great to test, a java.time.Clock-based implementation would probably be easier and faster to test
class SimpleTaskSchedulerScheduledThreadPoolImplTest {

    private lateinit var sut: SimpleTaskSchedulerScheduledThreadPoolImpl

    @Before
    fun setUp() {
        sut = SimpleTaskSchedulerScheduledThreadPoolImpl()
    }

    @After
    fun tearDown() {
        sut.close()
    }

    @Test
    fun scheduleAtMillisFromNow() {
        val latch = CountDownLatch(1)

        sut.scheduleAtMillisFromNow(2000) {
            latch.countDown()
        }

        val wasExecuted = latch.await(3, SECONDS)
        assertTrue(wasExecuted)
    }

    @Test
    fun scheduleAtMillisFromNow_multiple() {
        val latch1 = CountDownLatch(1)
        val latch2 = CountDownLatch(1)
        val latch3 = CountDownLatch(1)

        sut.scheduleAtMillisFromNow(5000) {
            latch1.countDown()
        }
        sut.scheduleAtMillisFromNow(4000) {
            latch2.countDown()
        }
        sut.scheduleAtMillisFromNow(2000) {
            latch3.countDown()
        }

        val wasExecuted1 = latch1.await(5, SECONDS)
        val wasExecuted2 = latch2.await(1, SECONDS)
        val wasExecuted3 = latch3.await(1, SECONDS)
        assertTrue(wasExecuted1)
        assertTrue(wasExecuted2)
        assertTrue(wasExecuted3)
    }

    @Test
    fun cancelAllScheduled() {
        val latch1 = CountDownLatch(1)
        val latch2 = CountDownLatch(1)
        val latch3 = CountDownLatch(1)
        sut.scheduleAtMillisFromNow(5000) {
            latch1.countDown()
        }
        sut.scheduleAtMillisFromNow(4000) {
            latch2.countDown()
        }
        sut.scheduleAtMillisFromNow(2000) {
            latch3.countDown()
        }

        sut.cancelAllScheduled()

        val wasExecuted1 = latch1.await(5, SECONDS)
        val wasExecuted2 = latch2.await(1, SECONDS)
        val wasExecuted3 = latch3.await(1, SECONDS)
        assertFalse(wasExecuted1)
        assertFalse(wasExecuted2)
        assertFalse(wasExecuted3)
    }
}