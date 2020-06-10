package org.example.subtitles.cli

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.impl.SubtitleEntrySrtConverter
import org.example.subtitles.serialization.impl.SubtitleReaderImpl
import org.example.subtitles.timedstreaming.Observer
import org.example.subtitles.timedstreaming.impl.ScrollableTimedSubtitleStreamerImpl
import java.io.Closeable
import java.io.FileInputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.lang.Runtime.getRuntime
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess


/**
 * Will start displaying subtitles from the given file right after start.
 *
 * <pre>
# build jar, e.g. for version 0.9.2-SNAPSHOT
./gradlew jvmJar
# execute main class in jar
java -jar build/libs/subtitle_app_utils-jvm-0.9.2-SNAPSHOT.jar -i "$PWD/src/jvmTest/resources/test_subtitles2.srt"
</pre>
 * For shutting it down, send an interrupt
 */
fun main(args: Array<String>) {
    val outputStream = System.out
    val hardExitBlock = { exitProcess(1) }
    val commandLineArgsParser = CommandLineArgsParserApacheCommonsImpl()
    processParams(outputStream, hardExitBlock, commandLineArgsParser, args)
}

fun processParams(
    outputStream: PrintStream,
    hardExitBlock: () -> Nothing,
    commandLineArgsParser: CommandLineArgsParser,
    args: Array<String>
) {
    val params: StreamingCommandLineParams
    try {
        params = commandLineArgsParser.parseCommandLineParameters(args, PrintWriter(outputStream))
    } catch (e: IllegalArgumentException) {
        outputStream.println(e.message)
        hardExitBlock()
    }

    var streamerCloseable: Closeable? = null
    val gracefulExitBlock: () -> Unit = {
        println("Shutting down")
        outputStream.flush()
        if (streamerCloseable != null) {
            streamerCloseable!!.close()
        }
    }

    getRuntime().addShutdownHook(Thread(gracefulExitBlock))

    SubtitleReaderImpl(FileInputStream(params.inputFile), SubtitleEntrySrtConverter()).use { reader ->
        ScrollableTimedSubtitleStreamerImpl(reader).use { streamerImpl ->
            streamerCloseable = streamerImpl
            val shouldKeepLooping = AtomicBoolean(true)
            streamerImpl.addObserver(TimedSubtitlePrinter(outputStream))
            streamerImpl.addObserver(object : Observer<SubtitleEntry> {
                override fun update(element: SubtitleEntry) {
                    val joinedToString = element.textLines.joinToString()
                    if (joinedToString == ScrollableTimedSubtitleStreamerImpl.endOfSubtitlesMessage) {
                        shouldKeepLooping.set(false)
                    }
                }
            })
            streamerImpl.scrollToTimestamp(params.startingOffset)
            streamerImpl.startOrContinue()
            do {
                // to be quit by end of subtitles or CTRL + C
            } while (shouldKeepLooping.get())
        }
    }

}

