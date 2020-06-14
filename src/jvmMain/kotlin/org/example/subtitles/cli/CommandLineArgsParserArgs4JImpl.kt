package org.example.subtitles.cli

import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import org.kohsuke.args4j.spi.SubCommand
import org.kohsuke.args4j.spi.SubCommandHandler
import org.kohsuke.args4j.spi.SubCommands
import java.io.File
import java.io.PrintStream
import java.io.PrintWriter
import java.time.Duration
import java.time.LocalTime
import java.util.*


class Args4JCommandLineArgsParserImpl : ExtendedCommandLineArgsParser {

    override fun parseCommandLineParameters(
        args: Array<String>,
        writer: PrintWriter
    ): BasicCommandLineParams {
        val paramsList = ArrayList(args.asList())
        val cmdLineOptions = Args4JCommand()
        val parser = CmdLineParser(cmdLineOptions)
        try {
            parser.parseArgument(paramsList)
        } catch (e: CmdLineException) {
            printErrorAndUsage(parser, System.err, e)
        }
        return cmdLineOptions.command!!.toParams()
    }
}

class Args4JCommand {
    @Argument(handler = SubCommandHandler::class)
    @SubCommands(
        SubCommand(name = Companion.STREAMING_COMMAND_NAME, impl = StreamingOptions::class),
        SubCommand(name = Companion.MODIFICATION_COMMAND_NAME, impl = ModificationOptions::class)
    )
    var command: AbstractOptions? = null
}

abstract class AbstractOptions {
    abstract fun toParams(): BasicCommandLineParams
}

class StreamingOptions : AbstractOptions() {
    @Option(
        name = "-i",
        aliases = ["--inputFile"],
        metaVar = "FILE",
        required = true,
        usage = "path to input file in SRT format"
    )
    var inputFile: File? = null


    @Option(
        name = "-t",
        aliases = ["--timestamp"],
        metaVar = "TIMESTAMP",
        required = true,
        usage = "timestamp where to start streaming, format: 12:34:56.123456789",
        handler = LocalTimeOptionHandler::class
    )
    var startingOffset: LocalTime? = null


    override fun toParams() = StreamingCommandLineParams(inputFile!!, startingOffset!!)
}


class ModificationOptions : AbstractOptions() {
    @Option(
        name = "-i",
        aliases = ["--inputFile"],
        metaVar = "FILE",
        required = true,
        usage = "path to input file in SRT format"
    )
    var inputFile: File? = null

    @Option(
        name = "-d",
        aliases = ["--delta"],
        metaVar = "DURATION",
        required = true,
        usage = "duration by which to modify the given file",
        handler = DurationOptionHandler::class
    )
    var duration: Duration? = null


    @Option(
        name = "-o",
        aliases = ["--outputFile"],
        metaVar = "FILE",
        required = true,
        usage = "path to to-be-created output file in SRT format"
    )
    var outputFile: File? = null

    override fun toParams() = ModificationCommandLineParams(inputFile!!, duration!!, outputFile!!)
}


private fun printErrorAndUsage(
    parser: CmdLineParser,
    printStream: PrintStream,
    e: Exception
) {
    printStream.print("Error: ")
    printStream.println(e.message)
    printStream.print("Usage: ")
    parser.printSingleLineUsage(printStream)
    printStream.println()

    printStream.println("Usage for '${Companion.STREAMING_COMMAND_NAME}'")
    val tempParserOptions = CmdLineParser(StreamingOptions())
    tempParserOptions.printUsage(printStream)

    printStream.println()
    printStream.println("Usage for '${Companion.MODIFICATION_COMMAND_NAME}'")
    val tempArgs = CmdLineParser(ModificationOptions())
    tempArgs.printUsage(printStream)
}


object Companion {
    const val STREAMING_COMMAND_NAME = "stream"
    const val MODIFICATION_COMMAND_NAME = "modify"
}
