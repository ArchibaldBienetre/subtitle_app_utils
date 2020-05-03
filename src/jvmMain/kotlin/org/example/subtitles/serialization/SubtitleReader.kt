package org.example.subtitles.serialization

import com.google.common.base.Strings
import org.example.subtitles.SubtitleEntry
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader
import java.util.function.Consumer
import javax.annotation.WillClose

class SubtitleReader(@WillClose inputStream: InputStream, private val converter: SubtitleEntryConverter) :
    Closeable {
    private val underlyingInputStream: InputStream = inputStream
    private val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))

    fun streamSubtitleEntries(errorHandler: Consumer<Exception> = Consumer { }): Sequence<SubtitleEntry> {
        val outerObject = this
        return sequence {
            outerObject.use {
                while (bufferedReader.ready()) {
                    readNextSubtitleEntry(errorHandler)?.also { yield(it) }
                }
            }
        }
    }

    private fun readNextSubtitleEntry(errorHandler: Consumer<Exception>): SubtitleEntry? {
        try {
            val currentEntryStrings: MutableList<String> = ArrayList()
            var started = false
            do {
                val currentLine = bufferedReader.readLine()

                // ignore leading empty lines
                if (!started && !Strings.isNullOrEmpty(currentLine)) {
                    started = true
                }
                if (started) {
                    currentEntryStrings.add(currentLine)
                }
            } while (bufferedReader.ready() && !Strings.isNullOrEmpty(currentLine))

            if (!currentEntryStrings.asSequence().all { Strings.isNullOrEmpty(it) }) {
                val srtString = currentEntryStrings.joinToString("\n", postfix = "\n")
                return converter.fromString(srtString)
            }
        } catch (e: Exception) {
            errorHandler.accept(e)
        }
        return null
    }

    override fun close() {
        underlyingInputStream.close()
    }
}