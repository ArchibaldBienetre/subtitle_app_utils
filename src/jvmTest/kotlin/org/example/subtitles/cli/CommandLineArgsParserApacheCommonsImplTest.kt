package org.example.subtitles.cli

import org.example.subtitles.cli.CommandLineArgsParserApacheCommonsImpl.Companion.fileNotFoundExceptionMessage
import org.example.subtitles.cli.CommandLineArgsParserApacheCommonsImpl.Companion.parseExceptionMessage
import org.example.subtitles.cli.CommandLineArgsParserApacheCommonsImpl.Companion.timeStampExceptionMessage
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintWriter
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CommandLineArgsParserApacheCommonsImplTest {

    private val validFilePath = "src/jvmTest/resources/test_subtitles_start_00-00-00.srt"
    private val invalidFilePath = "?:X/\\\"''"
    private val nonExistentFilePath = "src/jvmTest/resources/noSuchFile.srt"

    var sut = CommandLineArgsParserApacheCommonsImpl()
    lateinit var out: ByteArrayOutputStream
    lateinit var writer: PrintWriter

    @Before
    fun setUp() {
        out = ByteArrayOutputStream()
        writer = PrintWriter(out)
    }

    @Test
    fun parseCommandLineParameters_validParamsShort() {
        val testArgs = listOf("-i", validFilePath, "-t", "00:00:03.000000000").toTypedArray()

        val actual = sut.parseCommandLineParameters(testArgs, writer)

        assertEquals(actual.inputFile, File(validFilePath))
        assertEquals(actual.startingOffset, LocalTime.of(0, 0, 3))
        assertTrue(out.toString().isEmpty())
    }

    @Test
    fun parseCommandLineParameters_validParamsLong() {
        val testArgs = listOf("-inputFile", validFilePath, "-timeStamp", "00:00:03").toTypedArray()

        val actual = sut.parseCommandLineParameters(testArgs, writer)

        assertEquals(actual.inputFile, File(validFilePath))
        assertEquals(actual.startingOffset, LocalTime.of(0, 0, 3))
        assertTrue(out.toString().isEmpty())
    }

    @Test
    fun parseCommandLineParameters_validParamsDefaultTimestamp() {
        val testArgs = listOf("-i", validFilePath).toTypedArray()

        val actual = sut.parseCommandLineParameters(testArgs, writer)

        assertEquals(actual.inputFile, File(validFilePath))
        assertEquals(actual.startingOffset, LocalTime.of(0, 0))
        assertTrue(out.toString().isEmpty())
    }

    @Test
    fun parseCommandLineParameters_noParams() {
        val testArgs = listOf("").toTypedArray()

        assertFailsWith(IllegalArgumentException::class, parseExceptionMessage) {
            sut.parseCommandLineParameters(testArgs, writer)
        }

        assertTrue(out.toString().isNotBlank())
    }

    @Test
    fun parseCommandLineParameters_unknownParam() {
        val testArgs = listOf("-i", validFilePath, "-x", "unknown").toTypedArray()

        assertFailsWith(IllegalArgumentException::class, parseExceptionMessage) {
            sut.parseCommandLineParameters(testArgs, writer)
        }

        assertTrue(out.toString().isNotBlank())
    }

    @Test
    fun parseCommandLineParameters_invalidPath() {
        val testArgs = listOf("-i", invalidFilePath).toTypedArray()

        assertFailsWith(IllegalArgumentException::class, fileNotFoundExceptionMessage) {
            sut.parseCommandLineParameters(testArgs, writer)
        }

        assertTrue(out.toString().isEmpty())
    }

    @Test
    fun parseCommandLineParameters_nonExistentFilePath() {
        val testArgs = listOf("-i", nonExistentFilePath).toTypedArray()

        assertFailsWith(IllegalArgumentException::class, fileNotFoundExceptionMessage) {
            sut.parseCommandLineParameters(testArgs, writer)
        }

        assertTrue(out.toString().isEmpty())
    }

    @Test
    fun parseCommandLineParameters_invalidTimestamp() {
        val testArgs = listOf("-i", validFilePath, "-t", "00:00:00.1234567890").toTypedArray()

        assertFailsWith(IllegalArgumentException::class, timeStampExceptionMessage) {
            sut.parseCommandLineParameters(testArgs, writer)
        }

        assertTrue(out.toString().isEmpty())
    }

    @Test
    fun parseCommandLineParameters_weirdTimestamp() {
        val testArgs = listOf("-i", validFilePath, "-t", "25:00:00").toTypedArray()

        assertFailsWith(IllegalArgumentException::class, timeStampExceptionMessage) {
            sut.parseCommandLineParameters(testArgs, writer)
        }

        assertTrue(out.toString().isEmpty())
    }


}