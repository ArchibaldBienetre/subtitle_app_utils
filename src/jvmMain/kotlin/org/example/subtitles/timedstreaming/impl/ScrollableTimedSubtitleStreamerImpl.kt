package org.example.subtitles.timedstreaming.impl

import org.example.subtitles.SubtitleEntry
import org.example.subtitles.serialization.SubtitleReader
import org.example.subtitles.timedstreaming.Observable
import org.example.subtitles.timedstreaming.ScrollableTimedSubtitleStreamer
import org.example.subtitles.timedstreaming.SimpleTaskScheduler
import org.example.subtitles.timedstreaming.SortedSubtitleEntryList
import java.time.Clock
import java.time.Instant
import java.time.LocalTime
import java.util.function.Consumer

/**
 * Implementation of Observable and ScrollableTimedSubtitleStreamer.
 *
 * Implementation details:
 * * Will read all subtitles from the given source (SubtitleReader) right away
 * * Will do time-based scheduling of SubtitleEntry:s
 * * Uses java's built-in ScheduledThreadPoolExecutor for the timing part
 * * To avoid too much clock drift, will only schedule ahead a fixed window of entries.
 */
class ScrollableTimedSubtitleStreamerImpl : Observable<SubtitleEntry>,
    ScrollableTimedSubtitleStreamer {

    private val subtitleReader: SubtitleReader
    private val clock: Clock
    private val sortedSubtitles: SortedSubtitleEntryList

    /**
     * Start time of the playback - anchor point for absolute-time scheduling of subtitle streaming
     */
    private var startTime: Instant
    private var elapsedTime: LocalTime

    private var isRunning: Boolean

    private var scheduler: SimpleTaskScheduler

    constructor(
        subtitleReader: SubtitleReader,
        readExceptionHandler: Consumer<Exception> = Consumer { },
        clock: Clock = Clock.systemUTC(),
        scheduler: SimpleTaskScheduler = SimpleTaskSchedulerScheduledThreadPoolImpl()
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
        this.isRunning = false
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

    override fun scrollToTimestamp(timestamp: LocalTime) {
        val wasRunning = isRunning
        stopPlayback()
        elapsedTime = timestamp
        if (wasRunning) {
            startPlayback()
        }
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
        isRunning = true
        val elapsedMillis = nanosToMillis(elapsedTime.toNanoOfDay())
        startTime = clock.instant().minusMillis(elapsedMillis)
        scheduleNext()
    }

    private fun stopPlayback() {
        isRunning = false
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
            notifyEndOfSubtitles()
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

    private fun notifyEndOfSubtitles() {
        val entry = SubtitleEntry.createFromString(endOfSubtitlesMessage)
        entry.index = Integer.MAX_VALUE
        entry.fromTimestamp = calculatePlaybackTime()
        entry.toTimestamp = entry.fromTimestamp.plusMinutes(5)
        notifyObservers(entry)
    }

    private fun cancelAllScheduled() {
        this.scheduler.cancelAllScheduled()
    }

    override fun close() {
        this.scheduler.close()
    }

    companion object {
        const val numEntriesToScheduleAhead = 5
        const val endOfSubtitlesMessage = "END OF SUBTITLES"
    }

    private fun nanosToMillis(nanos: Long): Long = nanos / 1_000_000L
    private fun millisToNanos(millis: Long): Long = millis * 1_000_000L
}


