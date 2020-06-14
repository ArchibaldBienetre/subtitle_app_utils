package org.example.subtitles.cli

import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import org.kohsuke.args4j.spi.SubCommand
import org.kohsuke.args4j.spi.SubCommandHandler
import org.kohsuke.args4j.spi.SubCommands
import java.io.File
import java.io.PrintStream
import java.time.Duration
import java.time.LocalTime
import java.util.*


class CommandLineArgsParserArgs4JImpl : ExtendedCommandLineArgsParser {

    override fun parseCommandLineParameters(
        args: Array<String>,
        errorStream: PrintStream
    ): BasicCommandLineParams {
        val paramsList = ArrayList(args.asList())
        val cmdLineOptions = Args4JCommand()
        val parser = CmdLineParser(cmdLineOptions)
        try {
            parser.parseArgument(paramsList)
            return cmdLineOptions.command!!.toParams()
        } catch (e: Exception) {
            printErrorAndUsage(parser, errorStream, e)
            throw IllegalArgumentException(e)
        }
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
        usage = "timestamp where to start streaming, format: 12:34:56.123456789",
        handler = LocalTimeOptionHandler::class
    )
    var startingOffset: LocalTime = LocalTime.of(0, 0)


    override fun toParams(): BasicCommandLineParams {
        val result = StreamingCommandLineParams(inputFile!!, startingOffset)
        if (!inputFile!!.exists()) {
            throw IllegalArgumentException("Input file must exist")
        }
        return result
    }
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

    override fun toParams(): BasicCommandLineParams {
        val result = ModificationCommandLineParams(inputFile!!, duration!!, outputFile!!)

        if (!inputFile!!.exists()) {
            throw IllegalArgumentException("Input file must exist")
        }
        if (outputFile!!.exists()) {
            throw IllegalArgumentException("Output file must not exist")
        }

        // I found no other way to check if the path is valid
        outputFile!!.createNewFile()
        outputFile!!.delete()
        return result
    }
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
