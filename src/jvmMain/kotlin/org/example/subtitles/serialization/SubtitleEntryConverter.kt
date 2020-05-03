package org.example.subtitles.serialization;

import org.example.subtitles.SubtitleEntry

interface SubtitleEntryConverter {
    fun entryToString(entry: SubtitleEntry): String

    fun stringToEntry(subtitleEntryString: String): SubtitleEntry
}
