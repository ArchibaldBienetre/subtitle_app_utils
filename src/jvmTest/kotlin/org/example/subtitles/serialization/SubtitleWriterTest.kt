package org.example.subtitles.serialization

import org.example.subtitles.SubtitleEntry
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.test.assertEquals

class SubtitleWriterTest {
    @Test
    fun testWriteSubtitleEntry_nullValue() {
        val testEntry = SubtitleEntry()
        val converter = SubtitleEntrySrtConverter()
        val expected: String = converter.toString(testEntry)
        val outputStream = ByteArrayOutputStream()
        val sut = SubtitleWriter(outputStream, converter)

        sut.writeSubtitleEntry(testEntry)

        val actual: String = outputStream.toString(StandardCharsets.UTF_8)
        assertEquals(expected, actual)
    }

    @Test
    fun testWriteSubtitleEntry() {
        val converter = SubtitleEntrySrtConverter()
        val nl = SubtitleEntrySrtConverter.lineEnding
        val entryString1 = "1${nl}00:01:35,628 --> 00:01:36,654${nl}Dégage, toi.${nl}"
        val entryString2 =
            "29${nl}00:03:28,574 --> 00:03:31,490${nl}- Je roule pas à 180 pour m'amuser !${nl}- Oh !${nl}"
        val testEntry1 = converter.fromString(entryString1)
        val testEntry2 = converter.fromString(entryString2)
        val outputStream = ByteArrayOutputStream()
        val sut = SubtitleWriter(outputStream, converter)

        sut.writeSubtitleEntry(testEntry1)
        sut.writeSubtitleEntry(testEntry2)

        val actual: String = outputStream.toString(StandardCharsets.UTF_8)
        val expected = entryString1 + nl + entryString2 + nl
        assertEquals(expected, actual)
    }

    @Test
    fun testWriteSubtitleEntry_toFile() {
        val converter = SubtitleEntrySrtConverter()
        val nl = SubtitleEntrySrtConverter.lineEnding
        val entryString1 = "1${nl}00:01:35,628 --> 00:01:36,654${nl}Dégage, toi.${nl}"
        val entryString2 =
            "29${nl}00:03:28,574 --> 00:03:31,490${nl}- Je roule pas à 180 pour m'amuser !${nl}- Oh !${nl}"
        val testEntry1 = converter.fromString(entryString1)
        val testEntry2 = converter.fromString(entryString2)
        val outputFile = File.createTempFile(SubtitleWriterTest::class.simpleName, ".srt")
        val outputStream = FileOutputStream(outputFile)
        val sut = SubtitleWriter(outputStream, converter)

        sut.writeSubtitleEntry(testEntry1)
        sut.writeSubtitleEntry(testEntry2)
        outputStream.close()

        val actual: String = Files.readString(outputFile.toPath(), StandardCharsets.UTF_8)
        val expected = entryString1 + nl + entryString2 + nl
        assertEquals(expected, actual)
    }


}