package org.example.subtitles.timedstreaming.impl

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleReader
import org.example.subtitles.timedstreaming.Observable
import org.example.subtitles.timedstreaming.SimpleTaskScheduler
import org.example.subtitles.timedstreaming.SortedSubtitleEntryList
import org.example.subtitles.timedstreaming.TimedSubtitleStreamer
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.util.function.Consumer

/**
 * Implementation of Observable and TimedSubtitleStreamer.
 *
 * Implementation details:
 * * Will read all subtitles from the given source (SubtitleReader) right away
 * * Will do time-based scheduling of SubtitleEntry:s
 * * Uses java's built-in ScheduledThreadPoolExecutor for the timing part
 * * To avoid too much clock drift, will only schedule ahead a fixed window of entries.
 */
class TimedSubtitleStreamerImpl : Observable<SubtitleEntry>, TimedSubtitleStreamer {

    private val subtitleReader: SubtitleReader
    private val clock: Clock
    private val sortedSubtitles: SortedSubtitleEntryList

    /**
     * Start time of the playback - anchor point for absolute-time scheduling of subtitle streaming
     */
    private var startTime: Instant
    private var elapsedTime: LocalTime

    private var scheduler: SimpleTaskScheduler

    constructor(
        subtitleReader: SubtitleReader,
        readExceptionHandler: Consumer<Exception> = Consumer { },
        clock: Clock = Clock.systemUTC(),
        scheduler: SimpleTaskScheduler = SimpleTaskSchedulerImpl()
    ) : super() {
        this.subtitleReader = subtitleReader
        this.sortedSubtitles = SortedSubtitleEntryList.fromReader(
            subtitleReader,
            readExceptionHandler
        )
        this.clock = clock
        this.startTime = clock.instant()
        this.elapsedTime = LocalTime.ofSecondOfDay(0)
        this.scheduler = scheduler
    }

    override fun startOrContinue() {
        startPlayback()
    }

    override fun pause() {
        stopPlayback()
        elapsedTime = calculatePlaybackTime()
    }

    override fun stop() {
        stopPlayback()
        elapsedTime = LocalTime.ofSecondOfDay(0)
    }

    /* "plumbing" */

    /**
     * Represents where in the stream we are
     */
    private fun calculatePlaybackTime(): LocalTime {
        val diffMillis = clock.millis() - startTime.toEpochMilli()
        return LocalTime.ofNanoOfDay(millisToNanos(diffMillis))
    }

    private fun startPlayback() {
        val elapsedMillis = nanosToMillis(elapsedTime.toNanoOfDay())
        startTime = clock.instant().minusMillis(elapsedMillis)
        scheduleNext()
    }

    private fun stopPlayback() {
        elapsedTime = LocalTime.ofNanoOfDay(millisToNanos(clock.instant().toEpochMilli() - startTime.toEpochMilli()))
        cancelAllScheduled()
    }

    private fun scheduleNext() {
        val fromTimestamp = this.calculatePlaybackTime()
        val nextSubtitleEntries = this.sortedSubtitles
            .getNextSubtitleEntries(fromTimestamp, numEntriesToScheduleAhead)
        scheduleAll(nextSubtitleEntries)
    }

    private fun scheduleAll(nextSubtitleEntries: List<SubtitleEntry>) {
        if (nextSubtitleEntries.isEmpty()) {
            return
        }
        val outerPlaybackTimeNanos = calculatePlaybackTime().toNanoOfDay()
        nextSubtitleEntries.subList(0, nextSubtitleEntries.size - 1).forEach {
            scheduler.scheduleAtMillisFromNow(
                nanosToMillis(it.fromTimestamp.toNanoOfDay() - outerPlaybackTimeNanos)
            ) {
                notifyObservers(it)
            }
        }

        val outerObject = this
        val lastElement = nextSubtitleEntries.last()
        scheduler.scheduleAtMillisFromNow(
            nanosToMillis(lastElement.fromTimestamp.toNanoOfDay() - outerPlaybackTimeNanos)
        ) {
            notifyObservers(lastElement)
            outerObject.scheduleNext()
        }
    }

    private fun cancelAllScheduled() {
        this.scheduler.cancelAllScheduled()
    }

    override fun close() {
        this.scheduler.close()
    }

    companion object {
        const val numEntriesToScheduleAhead = 5
    }

    private fun nanosToMillis(nanos: Long): Long = nanos / 1_000_000L
    private fun millisToNanos(millis: Long): Long = millis * 1_000_000L
}


