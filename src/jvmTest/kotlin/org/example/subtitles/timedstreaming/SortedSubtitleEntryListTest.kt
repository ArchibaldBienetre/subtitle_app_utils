package org.example.subtitles.timedstreaming

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.impl.SubtitleEntrySrtConverter
import org.example.subtitles.test.MockedReturnSubtitleReader
import org.junit.Before
import org.junit.Test
import java.time.LocalTime
import kotlin.test.assertEquals

class SortedSubtitleEntryListTest {

    private var exampleContent: Sequence<SubtitleEntry> = emptySequence()
    private var entry1 = SubtitleEntry()
    private var entry2 = SubtitleEntry()
    private var entry3 = SubtitleEntry()
    private var entry4 = SubtitleEntry()
    private var entry5 = SubtitleEntry()
    private var entry6 = SubtitleEntry()

    @Before
    fun setUp() {
        val converter = SubtitleEntrySrtConverter()
        entry1 = converter.stringToEntry(
            "1\n00:01:35,628 --> 00:01:36,654\n1) Dégage, toi.\n\n"
        )
        entry2 = converter.stringToEntry(
            "2\n00:01:58,209 --> 00:01:59,006\n2) Les voilà.\n\n"
        )
        entry3 = converter.stringToEntry(
            "11\n00:02:50,209 --> 00:02:53,583\n3) - Je double : 200 sur l'escorte.\n- Vous allez perdre.\n\n"
        )
        entry4 = converter.stringToEntry(
            "29\n00:03:28,574 --> 00:03:31,490\n4) - Je roule pas à 180 pour m'amuser !\n- Oh !\n\n"
        )
        entry5 = converter.stringToEntry(
            "1370\n01:42:10,779 --> 01:42:12,963\n5) ...je l'ai enfin retrouvé.\n\n"
        )
        entry6 = converter.stringToEntry(
            "1371\n01:42:14,312 --> 01:42:15,962\n6) Embrassez-la pour moi.\n\n"
        )
        exampleContent = sequenceOf(entry1, entry2, entry3, entry4, entry5, entry6)
    }

    @Test
    fun getNextSubtitleEntries_getFirst() {
        val reader = MockedReturnSubtitleReader(exampleContent)
        val sut = SortedSubtitleEntryList.fromReader(reader)

        val actual = sut.getNextSubtitleEntries(LocalTime.of(0, 0), 1)

        assertEquals(listOf(entry1), actual)
    }

    @Test
    fun getNextSubtitleEntries_getAllFromReader() {
        val reader = MockedReturnSubtitleReader(exampleContent)
        val sut = SortedSubtitleEntryList.fromReader(reader)

        val actual = sut.getNextSubtitleEntries(LocalTime.of(0, 0), 100)

        assertEquals(exampleContent.toList(), actual)
    }

    @Test
    fun getNextSubtitleEntries_getAllFromList() {
        val sut = SortedSubtitleEntryList(exampleContent.toList())

        val actual = sut.getNextSubtitleEntries(LocalTime.of(0, 0), 100)

        assertEquals(exampleContent.toList(), actual)
    }

    @Test
    fun getNextSubtitleEntries_getAfterLast() {
        val sut = SortedSubtitleEntryList(exampleContent.toList())

        val actual = sut.getNextSubtitleEntries(LocalTime.of(1, 42, 15), 100)

        assertEquals(emptyList(), actual)
    }

    @Test
    fun getNextSubtitleEntries() {
        val sut = SortedSubtitleEntryList(exampleContent.toList())

        val actual = sut.getNextSubtitleEntries(LocalTime.of(0, 0))

        assertEquals(listOf(entry1, entry2, entry3, entry4, entry5), actual)
    }

    @Test
    fun getNextSubtitleEntries_perfectHit() {
        val sut = SortedSubtitleEntryList(exampleContent.toList())

        val actual = sut.getNextSubtitleEntries(LocalTime.of(0, 2, 50, 209_000_000), 2)

        assertEquals(listOf(entry3, entry4), actual)
    }

    @Test
    fun getNextSubtitleEntries_oneNanoPast() {
        val sut = SortedSubtitleEntryList(exampleContent.toList())

        val actual = sut.getNextSubtitleEntries(LocalTime.of(0, 2, 50, 209_000_001), 1)

        assertEquals(listOf(entry4), actual)
    }
}