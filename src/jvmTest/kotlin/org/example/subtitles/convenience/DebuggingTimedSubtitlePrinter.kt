package org.example.subtitles.convenience

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.timedstreaming.Observer
import java.io.PrintStream

/**
 * Use this instead of TimedSubtitle printer to debug your implementation.
 * The file {@code src/jvmTest/resources/SimpleCliTest_subtitles_fast.srt} is a good performance test that shows if extraordinary many items will get skipped.
 * With a very fast pace like this
 * (way beyond any user-friendly subtitles),
 * SimpleTaskSchedulerPollingImpl with default configuration leads to about 2 skips for those 1000 elements,
 * a faster pace would cause repetitions.
 */
class DebuggingTimedSubtitlePrinter(private val out: PrintStream) : Observer<SubtitleEntry> {

    // quick fix for the timing bug that sometimes entries are emitted twice
    private var latestIndex = 0
    private var numberOfLateArrivingOrRepeatedElements = 0
    private var numberOfSkips = 0

    override fun update(element: SubtitleEntry) {
        if (latestIndex >= element.index) {
            numberOfLateArrivingOrRepeatedElements++
            out.println(
                "An element got repeated (time: $numberOfLateArrivingOrRepeatedElements! " +
                        "(Index: ${element.index}\t Text: ${element.textLines})"
            )
            return
        } else if (latestIndex + 1 != element.index) {
            numberOfSkips++
            out.println(
                "An element got skipped (time: $numberOfSkips! " +
                        "(Current index: ${element.index}\t Current text: ${element.textLines})"
            )
        }
        latestIndex = element.index
        element.textLines.forEach {
            out.println(it)
        }
    }
}