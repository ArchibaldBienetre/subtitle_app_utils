package org.example.subtitles

import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class SubtitleEntryTest {

    @Test
    fun testDefaultValues() {
        val zeroTime = LocalTime.of(0, 0, 0, 0)

        val sut = SubtitleEntry()

        assertEquals(0, sut.index)
        assertEquals(zeroTime, sut.fromTimestamp)
        assertEquals(zeroTime, sut.toTimestamp)
        assertEquals("", sut.text)
    }

    @Test
    fun testValues() {
        val expectedIndex = 11
        val expectedFromTimestamp = LocalTime.of(1, 2, 50, 209)
        val expectedToTimestamp = LocalTime.of(1, 2, 59, 583)
        val expectedText = "- Je double : 200 sur l'escorte.\n" +
                "- Vous allez perdre."

        val sut = SubtitleEntry()
        sut.index = expectedIndex
        sut.fromTimestamp = expectedFromTimestamp
        sut.toTimestamp = expectedToTimestamp
        sut.text = expectedText

        assertEquals(expectedIndex, sut.index)
        assertEquals(expectedFromTimestamp, sut.fromTimestamp)
        assertEquals(expectedToTimestamp, sut.toTimestamp)
        assertEquals(expectedText, sut.text)
    }
}