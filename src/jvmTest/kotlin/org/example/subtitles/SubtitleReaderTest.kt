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
        val converter = SubtitleEntrySrtConverter()
        assertEquals(converter.fromSrtString("1\n00:01:35,628 --> 00:01:36,654\nDégage, toi.\n\n"), sequenceEntries.get(0))
        assertEquals(converter.fromSrtString("2\n00:01:58,209 --> 00:01:59,006\nLes voilà.\n\n"), sequenceEntries.get(1))
        assertEquals(converter.fromSrtString("11\n00:02:50,209 --> 00:02:53,583\n- Je double : 200 sur l'escorte.\n- Vous allez perdre.\n\n"), sequenceEntries.get(2))
        assertEquals(converter.fromSrtString("29\n00:03:28,574 --> 00:03:31,490\n- Je roule pas à 180 pour m'amuser !\n- Oh !\n\n"), sequenceEntries.get(3))
        assertEquals(converter.fromSrtString("1370\n01:42:10,779 --> 01:42:12,963\n...je l'ai enfin retrouvé.\n\n"), sequenceEntries.get(4))
        assertEquals(converter.fromSrtString("1371\n01:42:14,312 --> 01:42:15,962\nEmbrassez-la pour moi.\n\n"), sequenceEntries.get(5))
    }
}