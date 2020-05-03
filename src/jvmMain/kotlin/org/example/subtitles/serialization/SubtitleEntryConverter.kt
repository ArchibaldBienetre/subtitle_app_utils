package org.example.subtitles.serialization;

import org.example.subtitles.SubtitleEntry

interface SubtitleEntryConverter {
    fun toString(entry: SubtitleEntry): String

    fun fromString(subtitleEntryString: String): SubtitleEntry
}
