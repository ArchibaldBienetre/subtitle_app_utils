package org.example.subtitles.timedstreaming.impl

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.impl.SubtitleEntrySrtConverter
import org.example.subtitles.test.ForwardableClock
import org.example.subtitles.test.MockedReturnSubtitleReader
import org.example.subtitles.test.RecordingExceptionConsumer
import org.example.subtitles.test.RecordingTaskScheduler
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.lang.Math.min
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TimedSubtitleStreamerImplTest {

    companion object {
        private var exampleContent: Sequence<SubtitleEntry> = emptySequence()
        private var entry1 = SubtitleEntry()
        private var entry2 = SubtitleEntry()
        private var entry3 = SubtitleEntry()
        private var entry4 = SubtitleEntry()
        private var entry5 = SubtitleEntry()
        private var entry6 = SubtitleEntry()

        @BeforeClass
        @JvmStatic
        fun setUp() {
            val converter = SubtitleEntrySrtConverter()
            entry1 = converter.stringToEntry(
                "1\n00:00:00,000 --> 00:00:01,000\n1) Dégage, toi.\n\n"
            )
            entry2 = converter.stringToEntry(
                "2\n00:00:01,000 --> 00:00:02,000\n2) Les voilà.\n\n"
            )
            entry3 = converter.stringToEntry(
                "11\n00:00:02,000 --> 00:00:03,000\n3) - Je double : 200 sur l'escorte.\n- Vous allez perdre.\n\n"
            )
            entry4 = converter.stringToEntry(
                "29\n00:00:03,000 --> 00:00:04,000\n4) - Je roule pas à 180 pour m'amuser !\n- Oh !\n\n"
            )
            entry5 = converter.stringToEntry(
                "1370\n00:00:04,000 --> 00:00:05,000\n5) ...je l'ai enfin retrouvé.\n\n"
            )
            entry6 = converter.stringToEntry(
                "1371\n00:00:15,000 --> 00:00:16,000\n6) Embrassez-la pour moi.\n\n"
            )
            exampleContent = sequenceOf(entry1, entry2, entry3, entry4, entry5, entry6)
        }
    }

    private val testSequence = exampleContent
    private val clock = ForwardableClock()

    private var exceptionConsumer: RecordingExceptionConsumer = RecordingExceptionConsumer()
    private var recordingScheduler: RecordingTaskScheduler = RecordingTaskScheduler(clock)

    private lateinit var sut: TimedSubtitleStreamerImpl

    @Before
    fun setUpMethod() {
        clock.forwardBy(Duration.ofHours(2))
        recordingScheduler = RecordingTaskScheduler(clock)
        exceptionConsumer = RecordingExceptionConsumer()
        sut = TimedSubtitleStreamerImpl(
            MockedReturnSubtitleReader(testSequence),
            exceptionConsumer,
            clock,
            recordingScheduler
        )
    }


    @After
    fun tearDownMethod() {
        sut.close()
    }


    @Test
    fun noExecution_beforeStartOrContinue() {
        assertEquals(0, recordingScheduler.recordedScheduled.size)
        assertEquals(HashMap(), recordingScheduler.recordedCanceled)
        assertEquals(0, recordingScheduler.recordedExecuted.size)
        assertFalse(recordingScheduler.closeCalled)

        clock.forwardBy(Duration.ofMillis(500))
        recordingScheduler.executeAllUntilNow()

        assertEquals(0, recordingScheduler.recordedScheduled.size)
        assertEquals(HashMap(), recordingScheduler.recordedCanceled)
        assertEquals(0, recordingScheduler.recordedExecuted.size)
        assertFalse(recordingScheduler.closeCalled)
    }

    @Test
    fun startOrContinue() {
        sut.startOrContinue()

        val actualScheduled = recordingScheduler.recordedScheduled.size
        val expectedScheduled = min(TimedSubtitleStreamerImpl.numEntriesToScheduleAhead, 6)
        assertEquals(expectedScheduled, actualScheduled)
        assertEquals(HashMap(), recordingScheduler.recordedCanceled)
        assertEquals(0, recordingScheduler.recordedExecuted.size)
        assertFalse(recordingScheduler.closeCalled)
    }

    @Test
    fun startOrContinue_noReschedulesAfter1Execution() {
        assertEquals(0, recordingScheduler.recordedScheduled.size)
        assertEquals(HashMap(), recordingScheduler.recordedCanceled)
        assertEquals(0, recordingScheduler.recordedExecuted.size)
        assertFalse(recordingScheduler.closeCalled)

        sut.startOrContinue()
        clock.forwardBy(Duration.ofMillis(500))
        recordingScheduler.executeAllUntilNow()

        val actualScheduled = recordingScheduler.recordedScheduled.size
        val expectedScheduled = min(TimedSubtitleStreamerImpl.numEntriesToScheduleAhead, 6)
        assertEquals(expectedScheduled, actualScheduled)
        assertEquals(HashMap(), recordingScheduler.recordedCanceled)
        assertEquals(1, recordingScheduler.recordedExecuted.size)
        assertFalse(recordingScheduler.closeCalled)
    }

    @Test
    fun startOrContinue_loops() {
        sut.startOrContinue()

        clock.forwardBy(Duration.ofMillis(5_000))
        recordingScheduler.executeAllUntilNow()

        assertEquals(0, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        // supposed to fail if numEntriesToScheduleAhead is changed
        assertEquals(6, recordingScheduler.recordedScheduled.size)
        assertEquals(5, recordingScheduler.recordedExecuted.size)
    }

    @Test
    fun startOrContinue_loopsToEnd() {
        sut.startOrContinue()

        clock.forwardBy(Duration.ofMillis(5_000))
        recordingScheduler.executeAllUntilNow()
        clock.forwardBy(Duration.ofMillis(25_000))
        recordingScheduler.executeAllUntilNow()

        assertEquals(0, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        // supposed to fail if numEntriesToScheduleAhead is changed
        assertEquals(6, recordingScheduler.recordedScheduled.size)
        assertEquals(6, recordingScheduler.recordedExecuted.size)
    }

    @Test
    fun stop_noFurtherExecution() {
        sut.startOrContinue()
        clock.forwardBy(Duration.ofMillis(3_500))
        recordingScheduler.executeAllUntilNow()
        assertEquals(0, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        assertEquals(5, recordingScheduler.recordedScheduled.size)
        assertEquals(4, recordingScheduler.recordedExecuted.size)

        sut.stop()

        clock.forwardBy(Duration.ofHours(1))
        recordingScheduler.executeAllUntilNow()
        assertEquals(5, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        assertEquals(0, recordingScheduler.recordedScheduled.size)
        assertEquals(4, recordingScheduler.recordedExecuted.size)
    }

    @Test
    fun stop_startsFromBeginning() {
        sut.startOrContinue()
        clock.forwardBy(Duration.ofMillis(3_500))
        recordingScheduler.executeAllUntilNow()
        assertEquals(0, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        assertEquals(5, recordingScheduler.recordedScheduled.size)
        assertEquals(4, recordingScheduler.recordedExecuted.size)

        sut.stop()
        sut.startOrContinue()

        clock.forwardBy(Duration.ofMillis(3_500))
        recordingScheduler.executeAllUntilNow()
        assertEquals(5, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        assertEquals(5, recordingScheduler.recordedScheduled.size)
        assertEquals(8, recordingScheduler.recordedExecuted.size)
    }

    @Test
    fun pause_noFurtherExecution() {
        sut.startOrContinue()
        clock.forwardBy(Duration.ofMillis(3_500))
        recordingScheduler.executeAllUntilNow()
        assertEquals(0, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        assertEquals(5, recordingScheduler.recordedScheduled.size)
        assertEquals(4, recordingScheduler.recordedExecuted.size)

        sut.pause()

        clock.forwardBy(Duration.ofHours(1))
        recordingScheduler.executeAllUntilNow()
        assertEquals(5, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        assertEquals(0, recordingScheduler.recordedScheduled.size)
        assertEquals(4, recordingScheduler.recordedExecuted.size)
    }

    @Test
    fun pause_continuesFromPreviousPosition() {
        sut.startOrContinue()
        clock.forwardBy(Duration.ofMillis(3_500))
        recordingScheduler.executeAllUntilNow()
        assertEquals(0, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        assertEquals(5, recordingScheduler.recordedScheduled.size)
        assertEquals(4, recordingScheduler.recordedExecuted.size)

        sut.pause()
        clock.forwardBy(Duration.ofHours(1))
        recordingScheduler.executeAllUntilNow()
        sut.startOrContinue()
        clock.forwardBy(Duration.ofMillis(3_500))

        recordingScheduler.executeAllUntilNow()
        assertEquals(5, recordingScheduler.recordedCanceled.values.size)
        assertFalse(recordingScheduler.closeCalled)
        assertEquals(2, recordingScheduler.recordedScheduled.size)
        assertEquals(5, recordingScheduler.recordedExecuted.size)
    }

}