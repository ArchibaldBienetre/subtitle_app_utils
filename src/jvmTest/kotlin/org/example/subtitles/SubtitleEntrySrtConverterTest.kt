package org.example.subtitles

import org.junit.Test
import java.time.LocalTime
import kotlin.test.assertEquals

class SubtitleEntrySrtConverterTest {

    @Test
    fun testToSrtString_nullEntry() {
        val entry = SubtitleEntry()
        val sut = SubtitleEntrySrtConverter()

        val actual = sut.toSrtString(entry)

        assertEquals(
            "1\r\n" +
                    "00:00:00,000 --> 00:00:00,000\r\n" +
                    "\r\n" +
                    "\r\n", actual
        )
    }

    @Test
    fun testToSrtEntry() {
        val entry = SubtitleEntry("- Je double : 200 sur l'escorte.\n- Vous allez perdre.")
        entry.index = 11
        entry.fromTimestamp = LocalTime.of(1, 2, 50, 209_000_000)
        entry.toTimestamp = LocalTime.of(1, 2, 59, 583_000_000)
        val sut = SubtitleEntrySrtConverter()

        val actual = sut.toSrtString(entry)

        assertEquals(
            "12\r\n" +
                    "01:02:50,209 --> 01:02:59,583\r\n" +
                    "- Je double : 200 sur l'escorte.\r\n" +
                    "- Vous allez perdre.\r\n" +
                    "\r\n", actual
        )
    }
}