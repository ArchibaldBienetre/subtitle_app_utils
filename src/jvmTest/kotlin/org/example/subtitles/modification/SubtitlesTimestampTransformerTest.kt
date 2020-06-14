package org.example.subtitles.modification

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.test.MockedReturnSubtitleReader
import org.example.subtitles.test.RecordingExceptionConsumer
import org.example.subtitles.test.RecordingSubtitleWriter
import org.junit.Test
import java.time.Duration
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class SubtitlesTimestampTransformerTest {

    @Test
    fun testTransformAll_positiveDelta() {
        val entry1 = SubtitleEntry.createFromString("entry1")
        entry1.index = 0
        entry1.fromTimestamp = LocalTime.ofSecondOfDay(5)
        entry1.toTimestamp = LocalTime.ofSecondOfDay(6)
        val entry2 = SubtitleEntry.createFromString("entry2")
        entry2.index = 1
        entry2.fromTimestamp = LocalTime.ofSecondOfDay(7)
        entry2.toTimestamp = LocalTime.ofSecondOfDay(8)
        val reader = MockedReturnSubtitleReader(sequenceOf(entry1, entry2))
        val recordingWriter = RecordingSubtitleWriter()
        val exceptionRecorder = RecordingExceptionConsumer()
        val sut = SubtitlesTimestampTransformer(reader, recordingWriter, Duration.ofSeconds(10L))

        sut.transformAll(exceptionRecorder)

        val expectedEntry1 = SubtitleEntry.createFromString("entry1")
        expectedEntry1.index = 0
        expectedEntry1.fromTimestamp = LocalTime.ofSecondOfDay(15)
        expectedEntry1.toTimestamp = LocalTime.ofSecondOfDay(16)
        val expectedEntry2 = SubtitleEntry.createFromString("entry2")
        expectedEntry2.index = 1
        expectedEntry2.fromTimestamp = LocalTime.ofSecondOfDay(17)
        expectedEntry2.toTimestamp = LocalTime.ofSecondOfDay(18)
        assertEquals(0, exceptionRecorder.encounteredExceptions.size)
        assertEquals(2, recordingWriter.encounteredSubtitles.size)
        assertEquals(listOf(expectedEntry1, expectedEntry2), recordingWriter.encounteredSubtitles)
    }


    @Test
    fun testTransformAll_negativeDelta() {
        val entry1 = SubtitleEntry.createFromString("entry1")
        entry1.index = 0
        entry1.fromTimestamp = LocalTime.ofSecondOfDay(5)
        entry1.toTimestamp = LocalTime.ofSecondOfDay(6)
        val entry2 = SubtitleEntry.createFromString("entry2")
        entry2.index = 1
        entry2.fromTimestamp = LocalTime.ofSecondOfDay(7)
        entry2.toTimestamp = LocalTime.ofSecondOfDay(8)
        val reader = MockedReturnSubtitleReader(sequenceOf(entry1, entry2))
        val recordingWriter = RecordingSubtitleWriter()
        val exceptionRecorder = RecordingExceptionConsumer()
        val sut = SubtitlesTimestampTransformer(reader, recordingWriter, Duration.ofSeconds(-5L))

        sut.transformAll(exceptionRecorder)

        val expectedEntry1 = SubtitleEntry.createFromString("entry1")
        expectedEntry1.index = 0
        expectedEntry1.fromTimestamp = LocalTime.ofSecondOfDay(0)
        expectedEntry1.toTimestamp = LocalTime.ofSecondOfDay(1)
        val expectedEntry2 = SubtitleEntry.createFromString("entry2")
        expectedEntry2.index = 1
        expectedEntry2.fromTimestamp = LocalTime.ofSecondOfDay(2)
        expectedEntry2.toTimestamp = LocalTime.ofSecondOfDay(3)
        assertEquals(0, exceptionRecorder.encounteredExceptions.size)
        assertEquals(2, recordingWriter.encounteredSubtitles.size)
        assertEquals(listOf(expectedEntry1, expectedEntry2), recordingWriter.encounteredSubtitles)
    }


    @Test
    fun testTransformAll_negativeDelta_tooFar() {
        val entry1 = SubtitleEntry.createFromString("entry1")
        entry1.index = 0
        entry1.fromTimestamp = LocalTime.ofSecondOfDay(5)
        entry1.toTimestamp = LocalTime.ofSecondOfDay(6)
        val entry2 = SubtitleEntry.createFromString("entry2")
        entry2.index = 1
        entry2.fromTimestamp = LocalTime.ofSecondOfDay(7)
        entry2.toTimestamp = LocalTime.ofSecondOfDay(8)
        val reader = MockedReturnSubtitleReader(sequenceOf(entry1, entry2))
        val recordingWriter = RecordingSubtitleWriter()
        val exceptionRecorder = RecordingExceptionConsumer()
        val sut = SubtitlesTimestampTransformer(reader, recordingWriter, Duration.ofMillis(-5001L))

        val caughtException = assertFails {
            sut.transformAll(exceptionRecorder)
        }

        assertTrue(caughtException.message!!.contains("negative timestamp"))
        assertEquals(0, exceptionRecorder.encounteredExceptions.size)
        assertEquals(0, recordingWriter.encounteredSubtitles.size)
    }

}