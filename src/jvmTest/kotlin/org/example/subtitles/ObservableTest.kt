package org.example.subtitles

import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

class ObservableTest {

    @Test
    fun testAddObserver() {
        val sut = object : Observable<Int>() {
            fun observerCount(): Int = observers.size
        }
        val observer: Observer<Int> = object : Observer<Int> {
            override fun update(element: Int) {
                // do nothing
            }
        }
        assertEquals(0, sut.observerCount())

        sut.addObserver(observer)
        assertEquals(1, sut.observerCount())
        sut.addObserver(observer)
        assertEquals(2, sut.observerCount())
    }


    @Test
    fun testRemoveObserver() {
        val sut = object : Observable<Int>() {
            fun observerCount(): Int = observers.size
        }
        val observer1: Observer<Int> = object : Observer<Int> {
            override fun update(element: Int) {
                // do nothing
            }
        }
        val observer2: Observer<Int> = object : Observer<Int> {
            override fun update(element: Int) {
                // do nothing
            }
        }
        assertEquals(0, sut.observerCount())
        sut.addObserver(observer1)
        sut.addObserver(observer2)
        assertEquals(2, sut.observerCount())

        sut.removeObserver(observer1)

        assertEquals(1, sut.observerCount())

    }


    @Test
    fun testNotifyObservers_emptyList() {
        val sut = Observable<Int>()

        // assert it does not fail
        sut.notifyObservers(42)
    }


    @Test
    fun testNotifyObservers_singletonList() {
        val sut = Observable<Int>()
        val actualValueHolder = AtomicInteger()
        val observer: Observer<Int> = object : Observer<Int> {

            override fun update(element: Int) {
                actualValueHolder.set(element)
            }
        }
        sut.addObserver(observer)

        sut.notifyObservers(42)

        assertEquals(42, actualValueHolder.get())
    }


    @Test
    fun testNotifyObservers_multiplesList() {
        val sut = Observable<Int>()
        val actualValueHolder1 = AtomicInteger()
        val actualValueHolder2 = AtomicInteger()
        val observer1: Observer<Int> = object : Observer<Int> {
            override fun update(element: Int) {
                actualValueHolder1.set(element)
            }
        }
        val observer2: Observer<Int> = object : Observer<Int> {
            override fun update(element: Int) {
                actualValueHolder2.set(element)
            }
        }
        sut.addObserver(observer1)
        sut.addObserver(observer2)

        sut.notifyObservers(42)

        assertEquals(42, actualValueHolder1.get())
        assertEquals(42, actualValueHolder2.get())
    }

}