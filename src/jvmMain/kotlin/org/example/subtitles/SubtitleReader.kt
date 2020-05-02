package org.example.subtitles

import com.google.common.base.Strings
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader
import javax.annotation.WillClose

class SubtitleReader(@WillClose inputStream: InputStream, private val converter: SubtitleEntrySrtConverter = SubtitleEntrySrtConverter()) :
    Closeable {
    private val underlyingInputStream: InputStream = inputStream
    private val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(inputStream))

    fun streamSubtitleEntries(): Sequence<SubtitleEntry> {
        val outerObject = this
        return sequence {
            outerObject.use {
                while (bufferedReader.ready()) {
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

                    if (currentEntryStrings.asSequence().all { Strings.isNullOrEmpty(it) }) {
                        continue;
                    }
                    val srtString = currentEntryStrings.joinToString("\n", postfix = "\n")
                    val entry = converter.fromSrtString(srtString)
                    yield(entry)
                }
            }
        }
    }

    override fun close() {
        underlyingInputStream.close()
    }
}