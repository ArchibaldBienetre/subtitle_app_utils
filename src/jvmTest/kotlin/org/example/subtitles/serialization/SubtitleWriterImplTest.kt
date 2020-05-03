package org.example.subtitles.serialization

import org.example.subtitles.SubtitleEntry
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.test.assertEquals

class SubtitleWriterImplTest {
    @Test
    fun writeSubtitleEntry_nullValue() {
        val testEntry = SubtitleEntry()
        val converter = SubtitleEntrySrtConverter()
        val expected: String = converter.entryToString(testEntry)
        val outputStream = ByteArrayOutputStream()
        val sut = SubtitleWriterImpl(outputStream, converter)

        sut.writeSubtitleEntry(testEntry)

        val actual: String = outputStream.toString(StandardCharsets.UTF_8.name())
        assertEquals(expected, actual)
    }

    @Test
    fun writeSubtitleEntry() {
        val converter = SubtitleEntrySrtConverter()
        val nl = SubtitleEntrySrtConverter.lineEnding
        val entryString1 = "1${nl}00:01:35,628 --> 00:01:36,654${nl}Dégage, toi.${nl}"
        val entryString2 =
            "29${nl}00:03:28,574 --> 00:03:31,490${nl}- Je roule pas à 180 pour m'amuser !${nl}- Oh !${nl}"
        val testEntry1 = converter.stringToEntry(entryString1)
        val testEntry2 = converter.stringToEntry(entryString2)
        val outputStream = ByteArrayOutputStream()
        val sut = SubtitleWriterImpl(outputStream, converter)

        sut.writeSubtitleEntry(testEntry1)
        sut.writeSubtitleEntry(testEntry2)

        val actual: String = outputStream.toString(StandardCharsets.UTF_8.name())
        val expected = entryString1 + nl + entryString2 + nl
        assertEquals(expected, actual)
    }

    @Test
    fun writeSubtitleEntry_toFile() {
        val converter = SubtitleEntrySrtConverter()
        val nl = SubtitleEntrySrtConverter.lineEnding
        val entryString1 = "1${nl}00:01:35,628 --> 00:01:36,654${nl}Dégage, toi.${nl}"
        val entryString2 =
            "29${nl}00:03:28,574 --> 00:03:31,490${nl}- Je roule pas à 180 pour m'amuser !${nl}- Oh !${nl}"
        val testEntry1 = converter.stringToEntry(entryString1)
        val testEntry2 = converter.stringToEntry(entryString2)
        val outputFile = File.createTempFile(SubtitleWriterImplTest::class.simpleName, ".srt")
        val outputStream = FileOutputStream(outputFile)
        val sut = SubtitleWriterImpl(outputStream, converter)

        sut.writeSubtitleEntry(testEntry1)
        sut.writeSubtitleEntry(testEntry2)
        outputStream.close()

        val actual: String = String(Files.readAllBytes(outputFile.toPath()))
        val expected = entryString1 + nl + entryString2 + nl
        assertEquals(expected, actual)
    }


}