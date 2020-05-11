package org.example.subtitles.test

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleReader
import java.util.function.Consumer

class MockedReturnSubtitleReader(val mockedResult: Sequence<SubtitleEntry>) : SubtitleReader {
    override fun streamSubtitleEntries(exceptionHandler: Consumer<Exception>): Sequence<SubtitleEntry> {
        return mockedResult
    }
}