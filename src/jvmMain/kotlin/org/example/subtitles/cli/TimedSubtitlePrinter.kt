package org.example.subtitles.cli

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.timedstreaming.Observer
import java.io.PrintStream

class TimedSubtitlePrinter(private val out: PrintStream) : Observer<SubtitleEntry> {
    override fun update(element: SubtitleEntry) {
        element.textLines.forEach {
            out.println(it)
        }
    }
}