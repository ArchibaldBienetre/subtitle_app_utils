package org.example.subtitles.cli

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.modification.SubtitlesTimestampTransformer
import org.example.subtitles.serialization.impl.SubtitleEntrySrtConverter
import org.example.subtitles.serialization.impl.SubtitleReaderImpl
import org.example.subtitles.serialization.impl.SubtitleWriterImpl
import org.example.subtitles.timedstreaming.Observer
import org.example.subtitles.timedstreaming.impl.ScrollableTimedSubtitleStreamerImpl
import java.io.Closeable
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer
import kotlin.system.exitProcess

/**
 *
 * CLI that offers 2 basic operations:
 *
 * * "stream": Will start displaying subtitles from the given file right after start.
 * * "modify": Will modify the subtitles from given
 *
 * How to use this as a CLI:
 * ```
# build the jar file, e.g. for version 0.9.2-SNAPSHOT
./gradlew jvmJar
# execute main class in jar - e.g. streaming of subtitles from a file
java -jar build/libs/subtitle_app_utils-jvm-1.0.2-SNAPSHOT.jar stream -i "$PWD/src/jvmTest/resources/test_subtitles2.srt"
```
 * _NB:_ For shutting it down "stream" mode during execution, send an interrupt
 */
fun main(args: Array<String>) {
    val outputStream = System.out
    val hardExitBlock = { exitProcess(1) }
    val commandLineArgsParser = CommandLineArgsParserArgs4JImpl()
    processParams(outputStream, hardExitBlock, commandLineArgsParser, args)
}


fun processParams(
    outputStream: PrintStream,
    hardExitBlock: () -> Nothing,
    commandLineArgsParser: ExtendedCommandLineArgsParser,
    args: Array<String>
) {
    val params: BasicCommandLineParams
    try {
        params = commandLineArgsParser.parseCommandLineParameters(args, outputStream)
    } catch (e: IllegalArgumentException) {
        outputStream.println(e.message)
        hardExitBlock()
    }

    try {
        if (params is StreamingCommandLineParams) {
            streamForParams(params, outputStream)
        } else if (params is ModificationCommandLineParams) {
            modifyForParams(params)
        }
    } catch (e: Exception) {
        outputStream.println(e.message)
        hardExitBlock()
    }

}

private fun streamForParams(
    params: StreamingCommandLineParams,
    outputStream: PrintStream
) {
    var streamerCloseable: Closeable? = null
    val gracefulExitBlock: () -> Unit = {
        println("Shutting down")
        outputStream.flush()
        if (streamerCloseable != null) {
            streamerCloseable!!.close()
        }
    }

    Runtime.getRuntime().addShutdownHook(Thread(gracefulExitBlock))

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

private fun modifyForParams(
    params: ModificationCommandLineParams
) {
    val reader = SubtitleReaderImpl(FileInputStream(params.inputFile), SubtitleEntrySrtConverter())
    val writerStream = FileOutputStream(params.outputFile)
    val writer = SubtitleWriterImpl(writerStream, SubtitleEntrySrtConverter())
    val transformer = SubtitlesTimestampTransformer(reader, writer, params.modificationOffset)
    transformer.transformAll(Consumer {
        throw it
    })
    writerStream.flush()
    writerStream.close()
}


