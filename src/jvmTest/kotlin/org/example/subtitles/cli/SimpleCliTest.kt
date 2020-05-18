package org.example.subtitles.cli

import org.example.subtitles.cli.CommandLineArgsParser.CommandLineParams
import org.example.subtitles.timedstreaming.impl.ScrollableTimedSubtitleStreamerImpl.Companion.endOfSubtitlesMessage
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.io.PrintWriter
import java.time.LocalTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimpleCliTest {

    private val validFilePath = "src/jvmTest/resources/SimpleCliTest_subtitles.srt"
    private lateinit var outBuffer: ByteArrayOutputStream
    private lateinit var out: PrintStream

    @Before
    fun setUp() {
        outBuffer = ByteArrayOutputStream()
        out = PrintStream(outBuffer)
    }

    @Test
    fun processParams() {
        val start = System.currentTimeMillis()
        val mockParams = listOf("").toTypedArray()
        val hardExitCalled = AtomicBoolean(false)
        val parser = object : CommandLineArgsParser {
            override fun parseCommandLineParameters(args: Array<String>, writer: PrintWriter): CommandLineParams {
                return CommandLineParams(File(validFilePath), LocalTime.of(0, 0))
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

        val finishedGracefully = finishedLatch.await(6, SECONDS)
        val end = System.currentTimeMillis()
        assertTrue(end - start > 5000)
        assertTrue(finishedGracefully)
        assertFalse(hardExitCalled.get())
        assertEquals(
            "Subtitle 1\nSubtitle 2\nSubtitle 3\nSubtitle 4\n"
                    + "Subtitle 5\nSubtitle 6\n$endOfSubtitlesMessage\n",
            outBuffer.toString()
        )
    }

    @Test
    fun processParams_parseException() {
        val mockParams = listOf("").toTypedArray()
        val hardExitCalled = AtomicBoolean(false)
        val throwingParser = object : CommandLineArgsParser {
            override fun parseCommandLineParameters(args: Array<String>, writer: PrintWriter): CommandLineParams {
                throw IllegalArgumentException("test exception")
            }

        }
        val hardExitBlock = {
            hardExitCalled.set(true)
            throw RuntimeException("hard exit")
        }
        val executor = Executors.newFixedThreadPool(1)
        val finishedLatch = CountDownLatch(1)

        val runnable = Runnable {
            processParams(out, hardExitBlock, throwingParser, mockParams)
            finishedLatch.countDown()
        }
        executor.submit(runnable)

        val finishedGracefully = finishedLatch.await(2, SECONDS)
        assertFalse(finishedGracefully)
        assertTrue(hardExitCalled.get())
        assertEquals("test exception\n", outBuffer.toString())
    }
}