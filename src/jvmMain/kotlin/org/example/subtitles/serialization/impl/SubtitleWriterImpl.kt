package org.example.subtitles.serialization.impl

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleEntryConverter
import org.example.subtitles.serialization.SubtitleWriter
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import javax.annotation.WillNotClose

class SubtitleWriterImpl(@WillNotClose outputStream: OutputStream, private val converter: SubtitleEntryConverter) :
    SubtitleWriter {
    private val bufferedWriter: BufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))

    override fun writeSubtitleEntry(entry: SubtitleEntry) {
        bufferedWriter.write(converter.entryToString(entry))
        bufferedWriter.flush()
    }
}