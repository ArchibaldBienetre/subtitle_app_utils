package org.example.subtitles.cli

import java.io.File
import java.io.PrintWriter
import java.time.Duration
import java.time.LocalTime

sealed class BasicCommandLineParams(open val inputFile: File)
data class StreamingCommandLineParams(
    override val inputFile: File,
    val startingOffset: LocalTime
) : BasicCommandLineParams(inputFile = inputFile)

data class ModificationCommandLineParams(
    override val inputFile: File,
    val modificationOffset: Duration,
    val outputFile: File
) : BasicCommandLineParams(inputFile = inputFile)

interface CommandLineArgsParser {
    fun parseCommandLineParameters(args: Array<String>, writer: PrintWriter): StreamingCommandLineParams
}

interface ExtendedCommandLineArgsParser {
    fun parseCommandLineParameters(args: Array<String>, writer: PrintWriter): BasicCommandLineParams
}