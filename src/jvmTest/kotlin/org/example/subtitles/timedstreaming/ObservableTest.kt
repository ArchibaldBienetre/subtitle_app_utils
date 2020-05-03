package org.example.subtitles.timedstreaming

import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.assertEquals

class ObservableTest {

    open class TestObserver : Observer<Int> {

        val actualValueHolder: AtomicInteger = AtomicInteger()
        val exceptionValueHolder: AtomicInteger = AtomicInteger()
        val exceptionHolder: AtomicReference<Exception> = AtomicReference()

        override fun update(element: Int) {
            actualValueHolder.set(element)
        }

        override fun onFailure(element: Int, exception: Exception) {
            exceptionValueHolder.set(element)
            exceptionHolder.set(exception)
        }
    }

    @Test
    fun addObserver() {
        val sut = object : Observable<Int>() {
            fun observerCount(): Int = observers.size
        }
        val observer: Observer<Int> = object :
            Observer<Int> {
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
    fun removeObserver() {
        val sut = object : Observable<Int>() {
            fun observerCount(): Int = observers.size
        }
        val observer1: Observer<Int> = object :
            Observer<Int> {
            override fun update(element: Int) {
                // do nothing
            }
        }
        val observer2: Observer<Int> = object :
            Observer<Int> {
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
    fun notifyObservers_emptyList() {
        val sut = Observable<Int>()

        // assert it does not fail
        sut.notifyObservers(42)
    }


    @Test
    fun notifyObservers_singletonList() {
        val sut = Observable<Int>()
        val observer = TestObserver()
        sut.addObserver(observer)

        sut.notifyObservers(42)

        assertEquals(42, observer.actualValueHolder.get())
        assertEquals(0, observer.exceptionValueHolder.get())
        assertEquals(null, observer.exceptionHolder.get())
    }


    @Test
    fun notifyObservers_multiplesList() {
        val sut = Observable<Int>()
        val observer1 = TestObserver()
        val observer2 = TestObserver()
        sut.addObserver(observer1)
        sut.addObserver(observer2)

        sut.notifyObservers(42)

        assertEquals(42, observer1.actualValueHolder.get())
        assertEquals(42, observer2.actualValueHolder.get())
        assertEquals(0, observer1.exceptionValueHolder.get())
        assertEquals(0, observer2.exceptionValueHolder.get())
        assertEquals(null, observer1.exceptionHolder.get())
        assertEquals(null, observer2.exceptionHolder.get())
    }


    @Test
    fun notifyObservers_throwingObserver() {
        val sut = Observable<Int>()
        val testException = RuntimeException("test exception")
        val observer1Throwing = object : TestObserver() {
            override fun update(element: Int) {
                throw testException
            }
        }
        val observer2 = TestObserver()
        sut.addObserver(observer1Throwing)
        sut.addObserver(observer2)

        // should not fail!
        sut.notifyObservers(42)

        assertEquals(0, observer1Throwing.actualValueHolder.get())
        assertEquals(42, observer2.actualValueHolder.get())
        assertEquals(42, observer1Throwing.exceptionValueHolder.get())
        assertEquals(0, observer2.exceptionValueHolder.get())
        assertEquals(testException, observer1Throwing.exceptionHolder.get())
        assertEquals(null, observer2.exceptionHolder.get())
    }
}