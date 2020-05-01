package org.example.subtitles

import java.time.format.DateTimeFormatter

class SubtitleEntrySrtConverter {

    fun toSrtString(entry: SubtitleEntry): String {
        val builder = StringBuilder()
            .append("${entry.index + 1}")
            .append(lineEnding)
            .append(entry.fromTimestamp.format(dateTimeFormatter))
            .append(" --> ")
            .append(entry.toTimestamp.format(dateTimeFormatter))
            .append(lineEnding)
        entry.text.forEach {
            builder
                .append(it)
                .append(lineEnding)
        }
        builder.append(lineEnding)
        return builder.toString()
    }

    companion object {
        const val lineEnding = "\r\n"
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS")
    }
}