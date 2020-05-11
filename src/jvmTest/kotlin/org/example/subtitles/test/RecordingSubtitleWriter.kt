package org.example.subtitles.test

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleWriter

class RecordingSubtitleWriter : SubtitleWriter {
    val encounteredSubtitles: MutableList<SubtitleEntry> = ArrayList()
    override fun writeSubtitleEntry(entry: SubtitleEntry) {
        encounteredSubtitles.add(entry)
    }
}
