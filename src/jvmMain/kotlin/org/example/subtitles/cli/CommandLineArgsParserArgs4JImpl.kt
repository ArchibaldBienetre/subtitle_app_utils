package org.example.subtitles.cli

import java.io.PrintWriter


class Args4JCommandLineArgsParserImpl : ExtendedCommandLineArgsParser {

    override fun parseCommandLineParameters(
        args: Array<String>,
        writer: PrintWriter
    ): BasicCommandLineParams {
        TODO("not implemented")
    }
}

object Companion {
    const val STREAMING_COMMAND_NAME = "stream"
    const val MODIFICATION_COMMAND_NAME = "modify"
}
