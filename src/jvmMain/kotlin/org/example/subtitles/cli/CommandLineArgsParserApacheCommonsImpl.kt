package org.example.subtitles.cli

import org.apache.commons.cli.*
import org.apache.commons.cli.HelpFormatter.*
import java.io.File
import java.io.PrintWriter
import java.time.LocalTime
import java.time.format.DateTimeParseException


class CommandLineArgsParserApacheCommonsImpl : CommandLineArgsParser {
    override fun parseCommandLineParameters(
        args: Array<String>,
        writer: PrintWriter
    ): StreamingCommandLineParams {
        val options = Options()
        val inputFileOption = Option("i", "inputFile", true, "path to input file in SRT format")
        inputFileOption.isRequired = true
        options.addOption(inputFileOption)
        val timeStampOption =
            Option("t", "timeStamp", true, "timestamp where to start streaming, format: 12:34:56.123456789")
        options.addOption(timeStampOption)

        val parser = DefaultParser()
        val formatter = HelpFormatter()

        val cmd: CommandLine
        try {
            cmd = parser.parse(options, args)
        } catch (e: ParseException) {
            printUsage(formatter, writer, options)
            throw IllegalArgumentException(parseExceptionMessage, e)
        }

        val inputFile = File(cmd.getOptionValue("inputFile"))
        if (!inputFile.exists()) {
            throw IllegalArgumentException(fileNotFoundExceptionMessage)
        }
        val timeStampString = cmd.getOptionValue("timeStamp", "00:00")
        val timeStamp: LocalTime
        try {
            timeStamp = LocalTime.parse(timeStampString)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException(timeStampExceptionMessage)
        }
        return StreamingCommandLineParams(inputFile, timeStamp)
    }

    companion object {
        const val parseExceptionMessage = "Parameters could not be parsed"
        const val fileNotFoundExceptionMessage = "Input file does not exist!"
        const val timeStampExceptionMessage = "Wrong timestamp format. Try something like 12:34:56.123456789"
    }

    private fun printUsage(
        formatter: HelpFormatter,
        writer: PrintWriter,
        options: Options
    ) {
        formatter.printHelp(
            writer,
            DEFAULT_WIDTH,
            "Subtitle display CLI",
            "",
            options,
            DEFAULT_LEFT_PAD,
            DEFAULT_DESC_PAD,
            "",
            false
        )
        writer.flush()
    }
}

