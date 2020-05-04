package org.example.subtitles.timedstreaming

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleReader
import java.lang.Math.min
import java.time.LocalTime
import java.util.*
import java.util.Collections.binarySearch
import java.util.function.Consumer

class SortedSubtitleEntryList(entryList: List<SubtitleEntry>) {

    private val sortedSubtitleMap: SortedMap<LocalTime, SubtitleEntry>
    private val sortedKeys: List<LocalTime>

    init {
        val tempMap = entryList.map {
            it.fromTimestamp to it
        }.toMap()
        sortedSubtitleMap = TreeMap(tempMap)
        sortedKeys = sortedSubtitleMap.keys.toList()
    }

    fun getNextSubtitleEntries(fromTimestamp: LocalTime, count: Int = 5): List<SubtitleEntry> {
        val searchResultIndex = binarySearch(sortedKeys, fromTimestamp)
        val fromIndex = calculateFromIndex(searchResultIndex)
        if (fromIndex >= sortedKeys.size) {
            return emptyList()
        }
        val toIndex = min(fromIndex + count, sortedKeys.size)
        return sortedKeys.subList(fromIndex, toIndex)
            .map { sortedSubtitleMap[it] }
            .requireNoNulls()
            .toList()
    }

    /**
     * See documentation on the result of {@link java.util.Collections#binarySearch}.
     */
    private fun calculateFromIndex(searchResultIndex: Int): Int {
        return if (searchResultIndex >= 0) {
            searchResultIndex
        } else {
            // searchResultIndex is (-(insertionPoint) -1)
            -searchResultIndex - 1
        }
    }

    companion object {
        fun fromReader(
            reader: SubtitleReader,
            exceptionHandler: Consumer<Exception> = Consumer { }
        ) = SortedSubtitleEntryList(reader.streamSubtitleEntries(exceptionHandler).toList())

    }
}