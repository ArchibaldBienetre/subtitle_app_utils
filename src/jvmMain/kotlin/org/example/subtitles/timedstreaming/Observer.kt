package org.example.subtitles.timedstreaming

interface Observer<E> {
    fun update(element: E)
    fun onFailure(element: E, exception: Exception) {
        // default: do nothing
    }
}
