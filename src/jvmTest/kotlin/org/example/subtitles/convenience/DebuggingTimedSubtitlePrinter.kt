package org.example.subtitles.convenience

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.timedstreaming.Observer
import java.io.PrintStream

class DebuggingTimedSubtitlePrinter(private val out: PrintStream) : Observer<SubtitleEntry> {

    // quick fix for the timing bug that sometimes entries are emitted twice
    private var latestIndex = 0
    private var numberOfOutOfOrderElements = 0

    override fun update(element: SubtitleEntry) {
        if (latestIndex >= element.index) {
            numberOfOutOfOrderElements++
            out.println(
                "It happened again (time: $numberOfOutOfOrderElements! " +
                        "(Index: ${element.index}\t Text: ${element.textLines})"
            )
            return
        }
        latestIndex = element.index
        element.textLines.forEach {
            out.println(it)
        }
    }
}