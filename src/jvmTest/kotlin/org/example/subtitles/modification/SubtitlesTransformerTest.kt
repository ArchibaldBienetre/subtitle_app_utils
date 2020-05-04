package org.example.subtitles.modification

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.test.MockedReturnSubtitleReader
import org.example.subtitles.test.RecordingExceptionConsumer
import org.example.subtitles.test.RecordingSubtitleWriter
import org.example.subtitles.test.ThrowingSubtitleReader
import org.junit.Test
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubtitlesTransformerTest {

    private val doNothingTransform: (SubtitleEntry) -> SubtitleEntry = { it }

    @Test
    fun testTransformAll_nullInput() {
        val reader = MockedReturnSubtitleReader(emptySequence())
        val recordingWriter = RecordingSubtitleWriter()
        val sut = SubtitlesTransformer(reader, doNothingTransform, recordingWriter)
        val exceptionRecorder = RecordingExceptionConsumer()

        sut.transformAll(exceptionRecorder)

        assertTrue(recordingWriter.encounteredSubtitles.isEmpty())
        assertEquals(0, exceptionRecorder.encounteredExceptions.size)
    }


    @Test
    fun testTransformAll() {
        val entry1 = SubtitleEntry.createFromString("entry1")
        val entry2 = SubtitleEntry.createFromString("entry2")
        val entry3 = SubtitleEntry.createFromString("entry3")
        val expectedIndex = 1
        val expectedFromTimestamp = LocalTime.of(1, 1, 1, 1_000_000)
        val expectedToTimestamp = LocalTime.of(2, 2, 2, 2_000_000)
        val expectedEntry1 = SubtitleEntry.createFromString("entry1\nentry1")
        expectedEntry1.index = expectedIndex
        expectedEntry1.fromTimestamp = expectedFromTimestamp
        expectedEntry1.toTimestamp = expectedToTimestamp
        val expectedEntry2 = SubtitleEntry.createFromString("entry2\nentry2")
        expectedEntry2.index = expectedIndex
        expectedEntry2.fromTimestamp = expectedFromTimestamp
        expectedEntry2.toTimestamp = expectedToTimestamp
        val expectedEntry3 = SubtitleEntry.createFromString("entry3\nentry3")
        expectedEntry3.index = expectedIndex
        expectedEntry3.fromTimestamp = expectedFromTimestamp
        expectedEntry3.toTimestamp = expectedToTimestamp
        val reader = MockedReturnSubtitleReader(sequenceOf(entry1, entry2, entry3))
        val recordingWriter = RecordingSubtitleWriter()
        val exceptionRecorder = RecordingExceptionConsumer()
        val changeAllTransform: (SubtitleEntry) -> SubtitleEntry = {
            val newEntry = SubtitleEntry.copyOf(it)
            newEntry.index += 1
            newEntry.fromTimestamp = it.fromTimestamp.plusHours(1).plusMinutes(1).plusSeconds(1).plusNanos(1_000_000)
            newEntry.toTimestamp = it.toTimestamp.plusHours(2).plusMinutes(2).plusSeconds(2).plusNanos(2_000_000)
            val newText = ArrayList(it.textLines)
            newText.addAll(it.textLines)
            newEntry.textLines = newText

            newEntry
        }
        val sut = SubtitlesTransformer(reader, changeAllTransform, recordingWriter)

        sut.transformAll(exceptionRecorder)

        assertEquals(0, exceptionRecorder.encounteredExceptions.size)
        assertEquals(3, recordingWriter.encounteredSubtitles.size)
        assertEquals(listOf(expectedEntry1, expectedEntry2, expectedEntry3), recordingWriter.encounteredSubtitles)
    }


    @Test
    fun testTransformAll_exception() {
        val exceptionToThrow = IllegalArgumentException("test")
        val reader = ThrowingSubtitleReader(exceptionToThrow = exceptionToThrow)
        val recordingWriter = RecordingSubtitleWriter()
        val exceptionRecorder = RecordingExceptionConsumer()
        val sut = SubtitlesTransformer(reader, doNothingTransform, recordingWriter)

        sut.transformAll(exceptionRecorder)

        assertEquals(0, recordingWriter.encounteredSubtitles.size)
        assertEquals(1, exceptionRecorder.encounteredExceptions.size)
        assertEquals(exceptionToThrow, exceptionRecorder.encounteredExceptions[0])
    }

    @Test
    fun testTransformAll_valuesAndException() {
        val entry1 = SubtitleEntry.createFromString("entry1")
        val entry2 = SubtitleEntry.createFromString("entry2")
        val exceptionToThrow = IllegalArgumentException("test")
        val entry3 = SubtitleEntry.createFromString("entry3")
        val reader = ThrowingSubtitleReader(sequenceOf(entry1, entry2), exceptionToThrow, sequenceOf(entry3))
        val recordingWriter = RecordingSubtitleWriter()
        val exceptionRecorder = RecordingExceptionConsumer()
        val sut = SubtitlesTransformer(reader, doNothingTransform, recordingWriter)

        sut.transformAll(exceptionRecorder)

        assertEquals(3, recordingWriter.encounteredSubtitles.size)
        assertEquals(listOf(entry1, entry2, entry3), recordingWriter.encounteredSubtitles)
        assertEquals(1, exceptionRecorder.encounteredExceptions.size)
        assertEquals(exceptionToThrow, exceptionRecorder.encounteredExceptions[0])
    }


}