package org.example.subtitles.test

import java.lang.IllegalArgumentException
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*

/**
 * Like java.time.Clock.OffsetClock, but with mutable offset
 */
class ForwardableClock : Clock {
    private var baseClock: Clock
    private var offset: Duration

    constructor () {
        this.baseClock = Clock.fixed(Instant.now(), TimeZone.getDefault().toZoneId())
        this.offset = Duration.ofMillis(0)
    }

    constructor (clock: Clock, offset: Duration) {
        if (clock::class.qualifiedName != "java.time.Clock.FixedClock") {
            if (clock::class != ForwardableClock::class) {
                throw IllegalArgumentException("invalid base class")
            }
        }
        this.baseClock = clock
        this.offset = offset
    }

    fun forwardBy(offset: Duration): ForwardableClock {
        this.offset = this.offset.plus(offset)
        return this
    }

    override fun getZone(): ZoneId {
        return baseClock.zone
    }

    override fun withZone(zone: ZoneId): Clock {
        return if (zone == baseClock.zone) {  // intentional NPE
            this
        } else {
            ForwardableClock(baseClock.withZone(zone), offset)
        }
    }

    override fun millis(): Long {
        return Math.addExact(baseClock.millis(), offset.toMillis())
    }

    override fun instant(): Instant {
        return baseClock.instant().plus(offset)
    }

    override fun equals(other: Any?): Boolean {
        if (other is ForwardableClock) {
            val otherForwardableClock = other as ForwardableClock?
            return baseClock == otherForwardableClock!!.baseClock && offset == otherForwardableClock.offset
        }
        return false
    }

    override fun hashCode(): Int {
        return baseClock.hashCode() xor offset.hashCode()
    }

    override fun toString(): String {
        return "ForwardableClock[$baseClock,$offset]"
    }
}