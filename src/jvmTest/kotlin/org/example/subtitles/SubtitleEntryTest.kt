package org.example.subtitles

import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class SubtitleEntryTest {

    @Test
    fun testWithNullValues() {
        val sut = SubtitleEntry()

        val zeroTime = LocalTime.of(0, 0, 0, 0)
        assertEquals(0, sut.getIndex())
        assertEquals(zeroTime, sut.getFromTimestamp())
        assertEquals(zeroTime, sut.getToTimestamp())
        assertEquals("", sut.getText())
    }

}