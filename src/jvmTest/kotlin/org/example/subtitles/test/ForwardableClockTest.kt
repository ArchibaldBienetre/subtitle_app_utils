package org.example.subtitles.test

import org.junit.Test
import java.time.*
import java.time.ZoneOffset.*
import kotlin.test.*

class ForwardableClockTest {

    @Test
    fun constructor_unfixedClock_fails() {
        assertFailsWith(IllegalArgumentException::class) {
            ForwardableClock(Clock.systemUTC(), Duration.ofHours(2))
        }
    }

    @Test
    fun instant() {
        val clock = ForwardableClock()
        val instant1 = clock.instant()

        Thread.sleep(1_000L)

        val instant2 = clock.instant()
        assertEquals(instant1, instant2)
    }

    @Test
    fun forwardBy() {
        val clock = ForwardableClock()
        val instantBefore = clock.instant()

        val actual = clock.forwardBy(Duration.ofHours(1).plusSeconds(12))

        assertSame(clock, actual)
        val instantAfterForward = clock.instant()
        assertNotEquals(instantBefore, instantAfterForward)
        val i1Millis = instantBefore.toEpochMilli()
        val i2Millis = instantAfterForward.toEpochMilli()
        assertTrue(i2Millis > i1Millis)
        assertEquals(i2Millis, i1Millis + 3_612_000L)
    }

    @Test
    fun getZone() {
        val defaultClock = ForwardableClock()
        val now = Instant.now()
        val ect = ZoneId.of("Europe/Paris")
        val explicitBaseClock = Clock.fixed(now, ect)
        val explicitlyConstructedClock = ForwardableClock(explicitBaseClock, Duration.ofHours(1))
        val forwardedDefaultClock = defaultClock.forwardBy(Duration.ofHours(1))
        val forwardedExplicitlyConstructedClock = explicitlyConstructedClock.forwardBy(Duration.ofHours(1))
        val defaultZone = ZoneId.systemDefault()

        val defaultClockZone = defaultClock.zone
        val forwardedClockZone = forwardedDefaultClock.zone
        val explicitClockZone = explicitlyConstructedClock.zone
        val explicitForwardedClockZone = forwardedExplicitlyConstructedClock.zone

        assertEquals(defaultZone, defaultClockZone)
        assertEquals(defaultZone, forwardedClockZone)
        assertEquals(ect, explicitClockZone)
        assertEquals(ect, explicitForwardedClockZone)
    }


    @Test
    fun withZone() {
        val zeroTimeZone = ZoneId.ofOffset("UTC", UTC)
        val clock = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), zeroTimeZone),
            Duration.ofHours(0)
        )
        val differentTimeZone = ZoneId.ofOffset("UTC", ofHours(4))

        val clockWithDifferentTimezone = clock.withZone(differentTimeZone)
        val clockWithSameTimezone = clock.withZone(ZoneId.ofOffset("UTC", ofHours(0)))

        assertEquals(differentTimeZone, clockWithDifferentTimezone.zone)
        assertEquals(ZoneId.ofOffset("UTC", ofHours(0)), clockWithSameTimezone.zone)
        assertSame(clock, clockWithSameTimezone)
    }

    @Test
    fun millis() {
        val clock = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(0)
        )
        val clockAtTimeZoneOffset2 = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", ZoneOffset.ofHours(2))),
            Duration.ofHours(0)
        )
        val clockAtOffset3 = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(3)
        )

        val clockMillis = clock.millis()
        val clockAtTimeZoneOffset2Millis = clockAtTimeZoneOffset2.millis()
        val clockAtOffset3Millis = clockAtOffset3.millis()

        assertEquals(0L, clockMillis)
        // expected: Time zone does not count toward milli second value
        assertEquals(0L, clockAtTimeZoneOffset2Millis)
        assertEquals(10_800_000L, clockAtOffset3Millis)
    }

    @Test
    fun test_equals() {
        val clock1 = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(23)
        )
        val clock2 = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(23)
        )

        assertEquals(clock1, clock2)
    }

    @Test
    fun test_equals_notEqual() {
        val clock = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(23)
        )
        val clockWithDifferentBaseInstant = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(1), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(23)
        )
        val clockWithDifferentZone = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", MAX)),
            Duration.ofHours(23)
        )
        val clockWithDifferentOffset = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(22)
        )
        val clockOfDifferentType = Clock.fixed(Instant.ofEpochMilli(23 * 3_600_000L), ZoneId.ofOffset("UTC", UTC))

        assertNotEquals(clock, clockWithDifferentBaseInstant)
        assertNotEquals(clock, clockWithDifferentOffset)
        assertNotEquals(clock, clockWithDifferentZone)
        assertNotEquals(clock, clockOfDifferentType)
    }

    @Test
    fun test_equals_afterWithZone() {
        val clock1 = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(23)
        )
        val clock2 = clock1
            .withZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(4)))
            .withZone(ZoneId.ofOffset("UTC", UTC))

        assertEquals(clock1, clock2)
    }

    @Test
    fun test_hashCode() {
        val clock1 = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(23)
        )
        val clock2 = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(23)
        )
        val clock3 = clock1
            .withZone(ZoneId.ofOffset("GMT", ZoneOffset.ofHours(4)))
            .withZone(ZoneId.ofOffset("UTC", UTC))

        val hashCode1 = clock1.hashCode()
        val hashCode2 = clock2.hashCode()
        val hashCode3 = clock3.hashCode()

        assertEquals(hashCode1, hashCode2)
        assertEquals(hashCode2, hashCode3)
    }

    @Test
    fun test_toString() {
        val sut = ForwardableClock(
            Clock.fixed(Instant.ofEpochMilli(0), ZoneId.ofOffset("UTC", UTC)),
            Duration.ofHours(23)
        )

        val actual = sut.toString()

        assertEquals("ForwardableClock[FixedClock[1970-01-01T00:00:00Z,UTC],PT23H]", actual)
    }

}