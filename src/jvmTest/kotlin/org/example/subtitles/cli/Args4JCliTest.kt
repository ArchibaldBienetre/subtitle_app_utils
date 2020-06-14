package org.example.subtitles.cli

import org.example.subtitles.timedstreaming.impl.ScrollableTimedSubtitleStreamerImpl
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Files
import java.time.Duration
import java.time.LocalTime
import java.util.Arrays.asList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Args4JCliTest {

    private val validFilePath = "src/jvmTest/resources/Args4JCliTest_subtitles.srt"
    private lateinit var outBuffer: ByteArrayOutputStream
    private lateinit var out: PrintStream

    @Before
    fun setUp() {
        outBuffer = ByteArrayOutputStream()
        out = PrintStream(outBuffer)
    }

    @Test
    fun processParams_streaming() {
        val start = System.currentTimeMillis()
        val mockParams = listOf("").toTypedArray()
        val hardExitCalled = AtomicBoolean(false)
        val parser = object : ExtendedCommandLineArgsParser {
            override fun parseCommandLineParameters(
                args: Array<String>,
                errorStream: PrintStream
            ): BasicCommandLineParams {
                return StreamingCommandLineParams(File(validFilePath), LocalTime.of(0, 0))
            }

        }
        val hardExitBlock = {
            hardExitCalled.set(true)
            exitProcess(1)
        }
        val executor = Executors.newFixedThreadPool(1)
        val finishedLatch = CountDownLatch(1)

        val runnable = Runnable {
            processParams(out, hardExitBlock, parser, mockParams)
            finishedLatch.countDown()
        }
        executor.submit(runnable)

        val finishedGracefully = finishedLatch.await(3, TimeUnit.SECONDS)
        val end = System.currentTimeMillis()
        assertTrue(finishedGracefully)
        assertFalse(hardExitCalled.get())
        assertTrue(end - start > 2000)
        assertEquals(
            "Subtitle 1\nSubtitle 2\nSubtitle 3\n${ScrollableTimedSubtitleStreamerImpl.endOfSubtitlesMessage}\n",
            outBuffer.toString()
        )
    }

    @Test
    fun processParams_modification() {
        val mockParams = listOf("").toTypedArray()
        val hardExitCalled = AtomicBoolean(false)
        val outputFile = Files.createTempFile("modifiedSubtitles", ".srt").toFile()
        val parser = object : ExtendedCommandLineArgsParser {
            override fun parseCommandLineParameters(
                args: Array<String>,
                errorStream: PrintStream
            ): BasicCommandLineParams {
                return ModificationCommandLineParams(File(validFilePath), Duration.ofSeconds(1), outputFile)
            }

        }
        val hardExitBlock = {
            hardExitCalled.set(true)
            exitProcess(1)
        }

        processParams(out, hardExitBlock, parser, mockParams)

        assertFalse(hardExitCalled.get())
        assertTrue(outBuffer.toString().isEmpty())
        val actualOutput = Files.readAllLines(outputFile.toPath())
        assertEquals(
            asList(
                "1",
                "00:00:01,000 --> 00:00:02,000",
                "Subtitle 1",
                "",
                "2",
                "00:00:02,000 --> 00:00:03,000",
                "Subtitle 2",
                "",
                "3",
                "00:00:03,000 --> 00:00:04,000",
                "Subtitle 3",
                ""
            ), actualOutput
        )
    }

    @Test
    fun processParams_modification_failing() {
        val mockParams = listOf("").toTypedArray()
        val hardExitCalled = AtomicBoolean(false)
        val outputFile = Files.createTempFile("modifiedSubtitles", ".srt").toFile()
        val parser = object : ExtendedCommandLineArgsParser {
            override fun parseCommandLineParameters(
                args: Array<String>,
                errorStream: PrintStream
            ): BasicCommandLineParams {
                return ModificationCommandLineParams(File(validFilePath), Duration.ofMillis(-1001), outputFile)
            }

        }
        val hardExitBlock = {
            hardExitCalled.set(true)
            throw RuntimeException("hard exit")
        }
        val executor = Executors.newFixedThreadPool(1)
        val finishedLatch = CountDownLatch(1)

        val runnable = Runnable {
            processParams(out, hardExitBlock, parser, mockParams)
            finishedLatch.countDown()
        }
        executor.submit(runnable)

        val finishedGracefully = finishedLatch.await(2, TimeUnit.SECONDS)
        assertTrue(hardExitCalled.get())
        assertFalse(finishedGracefully)
        val actualOutput = Files.readAllLines(outputFile.toPath())
        assertTrue(actualOutput.isEmpty())
        assertTrue(outBuffer.toString().contains("negative timestamp"))
    }

    @Test
    fun processParams_parseException() {
        val mockParams = listOf("").toTypedArray()
        val hardExitCalled = AtomicBoolean(false)
        val throwingParser = object : ExtendedCommandLineArgsParser {
            override fun parseCommandLineParameters(
                args: Array<String>,
                errorStream: PrintStream
            ): BasicCommandLineParams {
                throw IllegalArgumentException("test exception")
            }

        }
        val hardExitBlock = {
            hardExitCalled.set(true)
            throw RuntimeException("hard exit")
        }

        assertFails {
            processParams(out, hardExitBlock, throwingParser, mockParams)
        }

        assertTrue(hardExitCalled.get())
        assertEquals("test exception\n", outBuffer.toString())
    }
}