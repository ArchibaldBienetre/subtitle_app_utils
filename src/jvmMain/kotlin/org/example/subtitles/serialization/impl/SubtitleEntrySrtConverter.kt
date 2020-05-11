package org.example.subtitles.serialization.impl

import com.google.common.base.Strings
import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleEntryConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class SubtitleEntrySrtConverter : SubtitleEntryConverter {

    override fun entryToString(entry: SubtitleEntry): String {
        val builder = StringBuilder()
            .append("${entry.index + 1}")
            .append(lineEnding)
            .append(entry.fromTimestamp.format(dateTimeFormatter))
            .append(" --> ")
            .append(entry.toTimestamp.format(dateTimeFormatter))
            .append(lineEnding)
        entry.textLines.forEach {
            builder
                .append(it)
                .append(lineEnding)
        }
        builder.append(lineEnding)
        return builder.toString()
    }

    override fun stringToEntry(subtitleEntryString: String): SubtitleEntry {
        try {
            val lines = subtitleEntryString.lines()
            val indexLine = lines[0]
            val timeStampsLine = lines[1]
            val timeStampsLineSplits = timeStampsLine.split(" --> ")
            var textLines = lines.subList(2, lines.size - 1)


            val entry = SubtitleEntry()
            entry.index = Integer.valueOf(indexLine) - 1
            entry.fromTimestamp = LocalTime.parse(
                timeStampsLineSplits[0].substring(0, 12),
                dateTimeFormatter
            )
            entry.toTimestamp = LocalTime.parse(
                timeStampsLineSplits[1].substring(0, 12),
                dateTimeFormatter
            )
            while (textLines.isNotEmpty() && Strings.isNullOrEmpty(textLines.last())) {
                textLines = textLines.subList(0, textLines.size - 1)
            }
            entry.textLines = textLines

            return entry
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException(e)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException(e)
        }
    }

    companion object {
        const val lineEnding = "\r\n"
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS")
    }
}