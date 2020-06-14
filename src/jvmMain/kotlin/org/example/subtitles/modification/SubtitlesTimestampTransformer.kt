package org.example.subtitles.modification

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleReader
import org.example.subtitles.serialization.SubtitleWriter
import java.time.Duration

/**
 * A [SubtitlesTransformer] that adjusts all timestamps by the given delta
 */
class SubtitlesTimestampTransformer(
    reader: SubtitleReader,
    writer: SubtitleWriter,
    private val delta: Duration
) : SubtitlesTransformer(
    reader,
    { entry ->
        val deltaNanos = delta.toNanos()
        val newEntry = SubtitleEntry.copyOf(entry)
        if (newEntry.fromTimestamp.toNanoOfDay() < -1 * deltaNanos) {
            throw IllegalArgumentException("Modification would create a negative timestamp")
        }
        newEntry.fromTimestamp = entry.fromTimestamp.plusNanos(deltaNanos)
        newEntry.toTimestamp = entry.toTimestamp.plusNanos(deltaNanos)
        newEntry
    },
    writer
)