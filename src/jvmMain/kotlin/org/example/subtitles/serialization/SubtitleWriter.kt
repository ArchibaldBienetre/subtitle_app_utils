package org.example.subtitles.serialization

import org.example.subtitles.SubtitleEntry

interface SubtitleWriter {
    fun writeSubtitleEntry(entry: SubtitleEntry)
}