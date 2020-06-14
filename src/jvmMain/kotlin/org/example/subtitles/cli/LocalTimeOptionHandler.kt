package org.example.subtitles.cli

import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.OptionDef
import org.kohsuke.args4j.spi.OneArgumentOptionHandler
import org.kohsuke.args4j.spi.Setter
import java.time.Duration
import java.time.LocalTime

class LocalTimeOptionHandler(parser: CmdLineParser, option: OptionDef, setter: Setter<in LocalTime>) :
    OneArgumentOptionHandler<LocalTime>(parser, option, setter) {

    override fun parse(argument: String): LocalTime? = LocalTime.parse(argument)
}