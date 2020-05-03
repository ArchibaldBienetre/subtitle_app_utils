package org.example.subtitles.serialization

import org.example.subtitles.SubtitleEntry
import java.util.function.Consumer

interface SubtitleReader {
    fun streamSubtitleEntries(exceptionHandler: Consumer<Exception> = Consumer { }): Sequence<SubtitleEntry>
}