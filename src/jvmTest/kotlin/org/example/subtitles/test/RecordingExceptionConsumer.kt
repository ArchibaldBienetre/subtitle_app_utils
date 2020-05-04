package org.example.subtitles.test

import java.util.function.Consumer

class RecordingExceptionConsumer : Consumer<Exception> {
    val encounteredExceptions: MutableList<Exception> = ArrayList()
    override fun accept(e: Exception) {
        encounteredExceptions.add(e)
    }
}