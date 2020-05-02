package org.example.subtitles

import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.time.LocalTime
import kotlin.test.assertEquals

class SubtitleReaderTest {


    @Test
    fun readTestSubtitlesEmpty() {
        val entriesStream = ByteArrayOutputStream()
        val emptyStream = ByteArrayInputStream(entriesStream.toByteArray())
        val sut = SubtitleReader(emptyStream)

        val actual = sut.streamSubtitleEntries()

        val sequenceEntries = actual.take(10).toList()
        assertEquals(emptyList(), sequenceEntries)
        assertEquals(0, sequenceEntries.size)
    }

    @Test
    fun readTestSubtitlesSingleEntry() {
        val converter = SubtitleEntrySrtConverter()
        val entry = SubtitleEntry.createFromString("This is a two-line\nSubtitle!")
        entry.index = 42
        entry.fromTimestamp = LocalTime.NOON
        entry.toTimestamp = LocalTime.NOON.plusSeconds(42)
        val entriesStream = ByteArrayOutputStream()
        entriesStream.writeBytes(converter.toSrtString(entry).toByteArray())
        val inputStream = ByteArrayInputStream(entriesStream.toByteArray())
        val sut = SubtitleReader(inputStream)

        val actual = sut.streamSubtitleEntries()

        val sequenceEntries = actual.take(1).toList()
        assertEquals(1, sequenceEntries.size)
        assertEquals(entry, sequenceEntries.get(0))
    }

    @Test
    fun readTestSubtitlesSingleEntry_leadingNewlines() {
        val converter = SubtitleEntrySrtConverter()
        val entry = SubtitleEntry.createFromString("This is a two-line\nSubtitle!")
        entry.index = 42
        entry.fromTimestamp = LocalTime.NOON
        entry.toTimestamp = LocalTime.NOON.plusSeconds(42)
        val entriesStream = ByteArrayOutputStream()
        entriesStream.writeBytes("\n\r\n\r\r\n\n".toByteArray())
        entriesStream.writeBytes(converter.toSrtString(entry).toByteArray())
        val inputStream = ByteArrayInputStream(entriesStream.toByteArray())
        val sut = SubtitleReader(inputStream)

        val actual = sut.streamSubtitleEntries()

        val sequenceEntries = actual.take(1).toList()
        assertEquals(1, sequenceEntries.size)
        assertEquals(entry, sequenceEntries.get(0))
    }

    @Test
    fun readTestSubtitlesFromFile() {
        val testFile = File("src/jvmTest/resources/test_subtitles.srt")
        val inputStream = FileInputStream(testFile)
        val sut = SubtitleReader(inputStream)

        val actual = sut.streamSubtitleEntries()

        val sequenceEntries = actual.take(10).toList()
        assertEquals(6, sequenceEntries.size)
    }
}