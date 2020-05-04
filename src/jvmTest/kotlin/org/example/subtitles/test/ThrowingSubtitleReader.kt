package org.example.subtitles.test

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleReader
import java.util.function.Consumer

class ThrowingSubtitleReader(
    val mockedResult1: Sequence<SubtitleEntry> = emptySequence(),
    val exceptionToThrow: RuntimeException,
    val mockedResult2: Sequence<SubtitleEntry> = emptySequence()
) : SubtitleReader {
    override fun streamSubtitleEntries(exceptionHandler: Consumer<Exception>): Sequence<SubtitleEntry> {
        return sequence {
            yieldAll(mockedResult1)

            // emulates behavior of real SubtitleReader (Exception will not come through)
            exceptionHandler.accept(exceptionToThrow)
            yieldAll(mockedResult2)
        }
    }
}