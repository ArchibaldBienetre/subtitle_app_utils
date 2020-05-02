package org.example.subtitles

import com.google.common.annotations.VisibleForTesting

open class Observable<E> {

    @VisibleForTesting
    protected val observers: MutableList<Observer<E>> = ArrayList()


    fun addObserver(observer: Observer<E>) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer<E>) {
        observers.remove(observer)
    }

    fun notifyObservers(element: E) {
        for (observer in observers) {
            try {
                observer.update(element)
            } catch (e: Exception) {
                observer.onFailure(element, e)
            }
        }
    }
}
