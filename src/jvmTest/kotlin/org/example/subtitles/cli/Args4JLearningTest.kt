package org.example.subtitles.cli

import org.kohsuke.args4j.*
import org.kohsuke.args4j.spi.SubCommand
import org.kohsuke.args4j.spi.SubCommandHandler
import org.kohsuke.args4j.spi.SubCommands
import java.io.File
import java.util.*


// copied from here and modified:
// https://web.archive.org/web/20190329164336/https://community.oracle.com/blogs/kohsuke/2005/05/10/parsing-command-line-options-jdk-50-style-args4j

fun main(args: Array<String>) {
    val paramsList = ArrayList(args.asList())
    val cmdLineOptions = MainCommand()
    val parser = CmdLineParser(cmdLineOptions)
    try {
        parser.parseArgument(paramsList)
        println("parsed: ${cmdLineOptions.cmd}")
        if (cmdLineOptions.cmd is MyOptions?) {
            println("### MyOptions")
            println("recursive: ${(cmdLineOptions.cmd as MyOptions).recursive}")
            println("num: ${(cmdLineOptions.cmd as MyOptions).num}")
            println("out: ${(cmdLineOptions.cmd as MyOptions).out}")
            println("out.exists: ${(cmdLineOptions.cmd as MyOptions).out?.exists()}")
            println("str: ${(cmdLineOptions.cmd as MyOptions).str}")
            println("otherArguments: ${(cmdLineOptions.cmd as MyOptions).otherArguments}")
        } else if (cmdLineOptions.cmd is ArgOptions) {
            println("### ArgOptions")
            println("mainArgument: ${(cmdLineOptions.cmd as ArgOptions).mainArgument}")
            println("otherArguments: ${(cmdLineOptions.cmd as ArgOptions).otherArguments}")
        }

    } catch (e: CmdLineException) {
        System.err.println(e.message)
//        System.err.println("java -jar myprogram.jar [...] arguments...")
        parser.printUsage(System.err)
        parser.printExample(OptionHandlerFilter.ALL)
        parser.printExample(OptionHandlerFilter.REQUIRED)
        return
    }
}

class MyOptions {
    @Option(name = "-r", usage = "recursively run something")
    var recursive: Boolean? = null

    @Option(name = "-o", usage = "output to this file")
    var out: File? = null

    // no usage
    @Option(name = "-str")
    var str: String = "(default value)"

    @Option(
        name = "-n",
        required = true,
        aliases = ["number"],
        metaVar = "NUM",
        usage = "usage can have new lines in it\n and also it can be long"
    )
    var num: Int? = null

    // all that have no "-" in front
    @Argument
    var otherArguments: List<String> = ArrayList()
}


class ArgOptions {

    @Argument(multiValued = false, index = 0)
    var mainArgument: String = ""

    @Argument(multiValued = true, index = 1)
    var otherArguments: List<String> = ArrayList()
}


class MainCommand {
    @Argument(handler = SubCommandHandler::class)
    @SubCommands(
        SubCommand(name = "commandWithOptions", impl = MyOptions::class),
        SubCommand(
            name = "commandWithArgs", impl = ArgOptions::class
        )
    )
    var cmd: Any? = null
}
