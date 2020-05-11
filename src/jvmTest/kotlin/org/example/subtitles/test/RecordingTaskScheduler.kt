package org.example.subtitles.test

import org.example.subtitles.timedstreaming.MyRunnable
import org.example.subtitles.timedstreaming.SimpleTaskScheduler
import org.jetbrains.annotations.TestOnly
import java.time.Clock


class RecordingTaskScheduler(private val sourceForNow: Clock) : SimpleTaskScheduler {


    val recordedScheduled: MutableMap<Long, MyRunnable> = HashMap()
    val recordedExecuted: MutableMap<Long, MyRunnable> = HashMap()
    val recordedCanceled: MutableMap<Long, MyRunnable> = HashMap()
    var closeCalled = false

    override fun scheduleAtMillisFromNow(millisFromNow: Long, runnable: MyRunnable) {
        recordedScheduled[sourceForNow.millis() + millisFromNow] = runnable
        // executing right away would cause a stack overflow
    }

    override fun cancelAllScheduled() {
        recordedCanceled.putAll(recordedScheduled)
        recordedScheduled.clear()
    }

    override fun close() {
        closeCalled = true
    }

    @TestOnly
    fun executeAllUntilNow() {
        val nowMillis = sourceForNow.millis()
        HashMap(recordedScheduled).forEach {
            if (it.key <= nowMillis) {
                it.value()
                recordedExecuted[it.key] = it.value
            }
        }
    }
}