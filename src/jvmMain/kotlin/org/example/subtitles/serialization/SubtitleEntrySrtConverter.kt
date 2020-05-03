package org.example.subtitles.serialization

import com.google.common.base.Strings
import org.example.subtitles.SubtitleEntry
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class SubtitleEntrySrtConverter : SubtitleEntryConverter {

    override fun toString(entry: SubtitleEntry): String {
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

    override fun fromString(subtitleEntryString: String): SubtitleEntry {
        try {
            val lines = subtitleEntryString.lines()
            val indexLine = lines.get(0)
            val timeStampsLine = lines.get(1)
            val timeStampsLineSplits = timeStampsLine.split(" --> ")
            var textLines = lines.subList(2, lines.size - 1)


            val entry = SubtitleEntry()
            entry.index = Integer.valueOf(indexLine) - 1
            entry.fromTimestamp = LocalTime.parse(timeStampsLineSplits.get(0),
                dateTimeFormatter
            )
            entry.toTimestamp = LocalTime.parse(timeStampsLineSplits.get(1),
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