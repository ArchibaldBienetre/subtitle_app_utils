package org.example.subtitles.cli

import java.io.File
import java.io.PrintWriter
import java.time.LocalTime

interface CommandLineArgsParser {
    data class CommandLineParams(val inputFile: File, val startingOffset: LocalTime)

    fun parseCommandLineParameters(args: Array<String>, writer: PrintWriter): CommandLineParams
}