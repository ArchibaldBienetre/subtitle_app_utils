package org.example.subtitles.serialization

import org.example.subtitles.SubtitleEntry
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import javax.annotation.WillNotClose

class SubtitleWriter(@WillNotClose outputStream: OutputStream, private val converter: SubtitleEntryConverter) {
    private val bufferedWriter: BufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))

    fun writeSubtitleEntry(entry: SubtitleEntry) {
        bufferedWriter.write(converter.toString(entry))
        bufferedWriter.flush()
    }
}