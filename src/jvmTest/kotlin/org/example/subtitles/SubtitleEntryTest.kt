package org.example.subtitles

import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class SubtitleEntryTest {

    @Test
    fun testDefaultValues() {
        val zeroTime = LocalTime.of(0, 0, 0, 0)

        val sut = SubtitleEntry()

        assertEquals(0, sut.index)
        assertEquals(zeroTime, sut.fromTimestamp)
        assertEquals(zeroTime, sut.toTimestamp)
        assertEquals(listOf(""), sut.textLines)
    }

    @Test
    fun testValues() {
        val expectedIndex = 11
        val expectedFromTimestamp = LocalTime.of(1, 2, 50, 209)
        val expectedToTimestamp = LocalTime.of(1, 2, 59, 583)
        val expectedText = listOf("- Je double : 200 sur l'escorte.", "- Vous allez perdre.")

        val sut = SubtitleEntry()
        sut.index = expectedIndex
        sut.fromTimestamp = expectedFromTimestamp
        sut.toTimestamp = expectedToTimestamp
        sut.textLines = expectedText

        assertEquals(expectedIndex, sut.index)
        assertEquals(expectedFromTimestamp, sut.fromTimestamp)
        assertEquals(expectedToTimestamp, sut.toTimestamp)
        assertEquals(expectedText, sut.textLines)
    }

    @Test
    fun testLineSplittingLF() {
        val expectedText = listOf("- Je double : 200 sur l'escorte.", "- Vous allez perdre.")

        val sut = SubtitleEntry.createFromString("- Je double : 200 sur l'escorte.\n- Vous allez perdre.")

        assertEquals(expectedText, sut.textLines)
    }


    @Test
    fun testLineSplittingCrlf() {
        val expectedText = listOf("- Je double : 200 sur l'escorte.", "- Vous allez perdre.")

        val sut = SubtitleEntry.createFromString("- Je double : 200 sur l'escorte.\r\n- Vous allez perdre.")

        assertEquals(expectedText, sut.textLines)
    }

    @Test
    fun copyOf() {
        val expectedIndex = 11
        val expectedFromTimestamp = LocalTime.of(1, 2, 50, 209)
        val expectedToTimestamp = LocalTime.of(1, 2, 59, 583)
        val expectedText = listOf("- Je double : 200 sur l'escorte.", "- Vous allez perdre.")
        val entry = SubtitleEntry()
        entry.index = expectedIndex
        entry.fromTimestamp = expectedFromTimestamp
        entry.toTimestamp = expectedToTimestamp
        entry.textLines = expectedText

        val actual = SubtitleEntry.copyOf(entry)

        assertEquals(entry, actual)
        assertNotSame(entry, actual)
    }
}