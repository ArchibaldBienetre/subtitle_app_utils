package org.example.subtitles.convenience

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.impl.SubtitleEntrySrtConverter
import org.example.subtitles.serialization.impl.SubtitleWriterImpl
import java.io.File
import java.io.FileOutputStream
import java.time.LocalTime


// just a convenience script for generating files
fun main() {
    val outFile = File("src/jvmTest/resources/SimpleCliTest_subtitles_fast.srt")
    val outStream = FileOutputStream(outFile)
    val writer = SubtitleWriterImpl(
        outStream,
        SubtitleEntrySrtConverter()
    )
    for (i in 1..1000) {
        val entry = SubtitleEntry.createFromString("$i")
        entry.index = i - 1
        // 10 millisecond steps
        entry.fromTimestamp = LocalTime.ofNanoOfDay(i * 10_000_000L)
        entry.toTimestamp = LocalTime.ofNanoOfDay((i + 1) * 10_000_000L - 1_000_000L)
        writer.writeSubtitleEntry(entry)
    }
    outStream.flush()
    outStream.close()
}