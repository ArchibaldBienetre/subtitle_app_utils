package org.example.subtitles.cli

import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.io.File
import java.util.*


// copied from here and modified:
// https://web.archive.org/web/20190329164336/https://community.oracle.com/blogs/kohsuke/2005/05/10/parsing-command-line-options-jdk-50-style-args4j

fun main(args: Array<String>) {
    val paramsList = ArrayList(args.asList())
    val cmdLineOptions: MyOptions = MyOptions()
    val parser: CmdLineParser = CmdLineParser(cmdLineOptions)
    try {
        parser.parseArgument(paramsList)
        println("recursive: ${cmdLineOptions.recursive}")
        println("num: ${cmdLineOptions.num}")
        println("out: ${cmdLineOptions.out}")
        println("out.exists: ${cmdLineOptions.out?.exists()}")
        println("str: ${cmdLineOptions.str}")
        println("Other: ${cmdLineOptions.otherArguments}")
    } catch (e: CmdLineException) {
        System.err.println(e.message)
        System.err.println("java -jar myprogram.jar [options...] arguments...")
        parser.printUsage(System.err)
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

    // receives other command line parameters than options
    @Argument
    var otherArguments: List<String> = ArrayList()
}
