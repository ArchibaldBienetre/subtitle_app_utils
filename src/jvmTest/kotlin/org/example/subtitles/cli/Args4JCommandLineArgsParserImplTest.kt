package org.example.subtitles.cli

import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintWriter
import java.time.Duration
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Args4JCommandLineArgsParserImplTest {

    private val validFilePath = "src/jvmTest/resources/test_subtitles_start_00-00-00.srt"
    private val validOutputFilePath = "src/jvmTest/resources/nonExistentOutputFile.srt"
    private val invalidFilePath = "?:X/\\\"''"
    private val nonExistentFilePath = "src/jvmTest/resources/noSuchFile.srt"


    val sut = Args4JCommandLineArgsParserImpl()
    lateinit var out: ByteArrayOutputStream
    lateinit var writer: PrintWriter

    @Before
    fun setUp() {
        out = ByteArrayOutputStream()
        writer = PrintWriter(out)
    }

    @Test
    fun parseCommandlineParameters_onValidStreamingParams_succeeds() {
        val testArgs = listOf("stream", "-i", validFilePath, "-t", "00:00:03.000000000").toTypedArray()

        val actual = sut.parseCommandLineParameters(testArgs, writer)

        assertTrue(actual is StreamingCommandLineParams);
        assertEquals(actual.inputFile, File(validFilePath))
        assertEquals(actual.startingOffset, LocalTime.of(0, 0, 3))
        assertTrue(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_onValidModificationParams_succeeds() {
        val testArgs = listOf(
            "modify",
            "-i",
            validFilePath,
            "-d",
            "00:00:03.000000000",
            "-o",
            validOutputFilePath
        ).toTypedArray()

        val actual = sut.parseCommandLineParameters(testArgs, writer)

        assertTrue(actual is ModificationCommandLineParams);
        assertEquals(actual.inputFile, File(validFilePath))
        assertEquals(actual.modificationOffset, Duration.ofSeconds(3))
        assertEquals(actual.outputFile, File(validOutputFilePath))
        assertTrue(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_onNegativeModificationParams_succeeds() {
        val testArgs =
            listOf("modify", "-i", validFilePath, "-d", "-00:00:20", "-o", validOutputFilePath).toTypedArray()

        val actual = sut.parseCommandLineParameters(testArgs, writer)

        assertTrue(actual is ModificationCommandLineParams);
        assertEquals(actual.inputFile, File(validFilePath))
        assertEquals(actual.modificationOffset, Duration.ofSeconds(-20))
        assertEquals(actual.outputFile, File(validOutputFilePath))
        assertTrue(out.toString().isEmpty())
    }
}
