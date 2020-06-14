package org.example.subtitles.cli

import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.time.Duration
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommandLineArgsParserArgs4JImplTest {

    private val validFilePath = "src/jvmTest/resources/test_subtitles_start_00-00-00.srt"
    private val validOutputFilePath = "src/jvmTest/resources/nonExistentOutputFile.srt"
    private val invalidFilePath = "/../..?&#:X/\\\"''"
    private val nonExistentFilePath = "src/jvmTest/resources/noSuchFile.srt"


    val sut = CommandLineArgsParserArgs4JImpl()
    lateinit var out: ByteArrayOutputStream
    lateinit var errorStream: PrintStream

    @Before
    fun setUp() {
        out = ByteArrayOutputStream()
        errorStream = PrintStream(out)
    }

    @Test
    fun parseCommandlineParameters_onValidStreamingParams_succeeds() {
        val testArgs = listOf("stream", "-i", validFilePath, "-t", "00:00:03.000000000").toTypedArray()

        val actual = sut.parseCommandLineParameters(testArgs, errorStream)

        assertTrue(actual is StreamingCommandLineParams);
        assertEquals(actual.inputFile, File(validFilePath))
        assertEquals(actual.startingOffset, LocalTime.of(0, 0, 3))
        assertTrue(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_streaming_defaults() {
        val testArgs = listOf("stream", "-i", validFilePath).toTypedArray()

        val actual = sut.parseCommandLineParameters(testArgs, errorStream)

        assertTrue(actual is StreamingCommandLineParams);
        assertEquals(actual.inputFile, File(validFilePath))
        assertEquals(actual.startingOffset, LocalTime.of(0, 0))
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

        val actual = sut.parseCommandLineParameters(testArgs, errorStream)

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

        val actual = sut.parseCommandLineParameters(testArgs, errorStream)

        assertTrue(actual is ModificationCommandLineParams);
        assertEquals(actual.inputFile, File(validFilePath))
        assertEquals(actual.modificationOffset, Duration.ofSeconds(-20))
        assertEquals(actual.outputFile, File(validOutputFilePath))
        assertTrue(out.toString().isEmpty())
    }


    @Test
    fun parseCommandlineParameters_missingCommandParams_fails() {
        val testArgs = emptyList<String>().toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_missingModificationParams_fails() {
        val testArgs = listOf("modify").toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }


    @Test
    fun parseCommandlineParameters_missingStreamingParams_fails() {
        val testArgs = listOf("stream").toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_missingStreamingInputFile_fails() {
        val testArgs = listOf("stream", "-i", nonExistentFilePath, "-t", "00:00:03.000000000").toTypedArray()


        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_missingStreamingInputFileParam_fails() {
        val testArgs = listOf("stream", "-t", "00:00:03.000000000").toTypedArray()


        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_malformedStreamingInputFile_fails() {
        val testArgs = listOf("stream", "-i", invalidFilePath, "-t", "00:00:03.000000000").toTypedArray()


        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }


    @Test
    fun parseCommandlineParameters_malformedStreamingOffset_fails() {
        val testArgs = listOf("stream", "-i", validFilePath, "-t", "malformed").toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_missingModificationInputFile_fails() {
        val testArgs = listOf(
            "modify",
            "-i",
            nonExistentFilePath,
            "-d",
            "00:00:03.000000000",
            "-o",
            validOutputFilePath
        ).toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }


    @Test
    fun parseCommandlineParameters_missingModificationInputFileParam_fails() {
        val testArgs = listOf(
            "modify",
            "-d",
            "00:00:03.000000000",
            "-o",
            validOutputFilePath
        ).toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_malformedModificationInputFile_fails() {
        val testArgs = listOf(
            "modify",
            "-i",
            invalidFilePath,
            "-d",
            "00:00:03.000000000",
            "-o",
            validOutputFilePath
        ).toTypedArray()
        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_missingModificationDelta_fails() {
        val testArgs = listOf(
            "modify",
            "-i",
            validFilePath,
            "-o",
            validOutputFilePath
        ).toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_malformedModificationDelta_fails() {
        val testArgs = listOf(
            "modify",
            "-i",
            validFilePath,
            "-d",
            "malformed",
            "-o",
            validOutputFilePath
        ).toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_missingModificationOutputFileParam_fails() {
        val testArgs = listOf(
            "modify",
            "-i",
            validFilePath,
            "-d",
            "00:00:03.000000000"
        ).toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_existingModificationOutputFile_fails() {
        val testArgs = listOf(
            "modify",
            "-i",
            validFilePath,
            "-d",
            "00:00:03.000000000",
            "-o",
            validFilePath
        ).toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }

    @Test
    fun parseCommandlineParameters_malformedModificationOutputFile_fails() {
        val testArgs = listOf(
            "modify",
            "-i",
            validFilePath,
            "-d",
            "00:00:03.000000000",
            "-o",
            invalidFilePath
        ).toTypedArray()

        assertFails {
            sut.parseCommandLineParameters(testArgs, errorStream)
        }

        assertFalse(out.toString().isEmpty())
    }
}
