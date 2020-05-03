package org.example.subtitles.serialization

import org.example.subtitles.SubtitleEntry
import org.junit.Test
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SubtitleEntrySrtConverterTest {

    @Test
    fun testToSrtString_nullEntry() {
        val entry = SubtitleEntry()
        val sut = SubtitleEntrySrtConverter()

        val actual = sut.toString(entry)

        assertEquals(
            "1\r\n" +
                    "00:00:00,000 --> 00:00:00,000\r\n" +
                    "\r\n" +
                    "\r\n", actual
        )
    }

    @Test
    fun testToSrtEntry() {
        val entry =
            SubtitleEntry.createFromString("- Je double : 200 sur l'escorte.\n- Vous allez perdre.")
        entry.index = 11
        entry.fromTimestamp = LocalTime.of(1, 2, 50, 209_000_000)
        entry.toTimestamp = LocalTime.of(1, 2, 59, 583_000_000)
        val sut = SubtitleEntrySrtConverter()

        val actual = sut.toString(entry)

        assertEquals(
            "12\r\n" +
                    "01:02:50,209 --> 01:02:59,583\r\n" +
                    "- Je double : 200 sur l'escorte.\r\n" +
                    "- Vous allez perdre.\r\n" +
                    "\r\n", actual
        )
    }


    @Test
    fun testFromSrtString() {
        val srtString = "12\r\n" +
                "01:02:50,209 --> 01:02:59,583\r\n" +
                "- Je double : 200 sur l'escorte.\r\n" +
                "- Vous allez perdre.\r\n" +
                "\r\n"
        val sut = SubtitleEntrySrtConverter()

        val actual = sut.fromString(srtString)

        assertEquals(11, actual.index)
        assertEquals(LocalTime.of(1, 2, 50, 209_000_000), actual.fromTimestamp)
        assertEquals(LocalTime.of(1, 2, 59, 583_000_000), actual.toTimestamp)
        assertEquals(listOf("- Je double : 200 sur l'escorte.", "- Vous allez perdre."), actual.textLines)
    }

    @Test
    fun testFromSrtString2() {
        val srtString = "12\r" +
                "01:02:50,209 --> 01:02:59,583\r" +
                "- Je double : 200 sur l'escorte.\r" +
                "- Vous allez perdre.\r"
        val sut = SubtitleEntrySrtConverter()

        val actual = sut.fromString(srtString)

        assertEquals(11, actual.index)
        assertEquals(LocalTime.of(1, 2, 50, 209_000_000), actual.fromTimestamp)
        assertEquals(LocalTime.of(1, 2, 59, 583_000_000), actual.toTimestamp)
        assertEquals(listOf("- Je double : 200 sur l'escorte.", "- Vous allez perdre."), actual.textLines)
    }

    @Test
    fun testFromSrtString_nullEntry() {
        val srtString = "1\r\n" +
                "00:00:00,000 --> 00:00:00,000\r\n" +
                "\r\n" +
                "\r\n"
        val sut = SubtitleEntrySrtConverter()

        val actual = sut.fromString(srtString)

        assertEquals(0, actual.index)
        val zeroTime = LocalTime.of(0, 0, 0, 0)
        assertEquals(zeroTime, actual.fromTimestamp)
        assertEquals(zeroTime, actual.toTimestamp)
        assertEquals(emptyList(), actual.textLines)
    }

    @Test
    fun testFromSrtString_invalidEntry_oneLine() {
        val srtString = "1 00:00:00,000 --> 00:00:00,000"
        val sut = SubtitleEntrySrtConverter()

        assertFailsWith(IllegalArgumentException::class) {
            sut.fromString(srtString)
        }
    }

    @Test
    fun testFromSrtString_invalidEntry_invalidDateValue() {
        val srtString = "1\r\n" +
                "00:00:00,000 --> 00:00:99,000\r\n" +
                "\r\n" +
                "\r\n"
        val sut = SubtitleEntrySrtConverter()

        assertFailsWith(IllegalArgumentException::class) {
            sut.fromString(srtString)
        }
    }


    @Test
    fun testFromSrtString_invalidEntry_invalidDateLine() {
        val srtString = "1\r\n" +
                "00:00:00,000 wrong delimiter 00:00:00,000\r\n" +
                "\r\n" +
                "\r\n"
        val sut = SubtitleEntrySrtConverter()

        assertFailsWith(IllegalArgumentException::class) {
            sut.fromString(srtString)
        }
    }

}