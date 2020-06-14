package org.example.subtitles.cli

import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.OptionDef
import org.kohsuke.args4j.spi.OneArgumentOptionHandler
import org.kohsuke.args4j.spi.Setter
import java.time.Duration
import java.time.LocalTime

class DurationOptionHandler(parser: CmdLineParser, option: OptionDef, setter: Setter<in Duration>) :
    OneArgumentOptionHandler<Duration>(parser, option, setter) {

    override fun parse(rawArgument: String): Duration? {
        val argument = rawArgument.strip()
        val negative = argument.startsWith("-")
        val argumentToProcess = if (negative) argument.substring(1) else argument
        val parsedLocalTime = LocalTime.parse(argumentToProcess)

        if (negative) {
            return Duration.ofNanos(-1L * parsedLocalTime.toNanoOfDay())
        }
        return Duration.ofNanos(parsedLocalTime.toNanoOfDay())
    }
}