package org.example.subtitles.modification

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleReader
import org.example.subtitles.serialization.SubtitleWriter
import java.util.function.Consumer

/**
 * Does basically one thing:
 * * Reads SubtitleEntry:s using a SubtitleReader
 * * Applies a given Transformation to them
 * * then writes SubtitleEntry:s using a SubtitleWriter.
 *
 * @param transformation  may transform the (mutable) SubtitleEntry directly
 */
class SubtitlesTransformer(
    private val reader: SubtitleReader,
    private val transformation: (SubtitleEntry) -> SubtitleEntry,
    private val writer: SubtitleWriter
) {

    fun transformAll(exceptionHandler: Consumer<Exception> = Consumer { }) {
        val it = reader.streamSubtitleEntries(exceptionHandler).iterator()
        while (it.hasNext()) {
            val entry: SubtitleEntry = it.next()
            val transformed: SubtitleEntry = transformation.invoke(entry)
            writer.writeSubtitleEntry(transformed)
        }
    }

}