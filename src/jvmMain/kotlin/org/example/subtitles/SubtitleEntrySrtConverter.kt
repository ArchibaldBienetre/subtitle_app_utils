package org.example.subtitles

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

    fun fromSrtString(srtString: String): SubtitleEntry {
        try {
            val lines = srtString.lines()
            val indexLine = lines.get(0)
            val timeStampsLine = lines.get(1)
            val timeStampsLineSplits = timeStampsLine.split(" --> ")
            val textLines = lines.subList(2, lines.size - 2)

            val entry = SubtitleEntry()
            entry.index = Integer.valueOf(indexLine) - 1
            entry.fromTimestamp = LocalTime.parse(timeStampsLineSplits.get(0), dateTimeFormatter)
            entry.toTimestamp = LocalTime.parse(timeStampsLineSplits.get(1), dateTimeFormatter)
            entry.text = textLines

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