package com.mattrobertson.greek.reader.data

import com.mattrobertson.greek.reader.model.GntVerseRef
import java.util.*

object Recents {

    private val mList = mutableListOf<GntVerseRef>()

    var size = 0
        private set
        get() = synchronized(this) { mList.size }

    fun add(ref: GntVerseRef) {
        synchronized(this) {
            mList.removeAll { it == ref }
            mList.add(0, ref)
        }
    }

    fun remove(ref: GntVerseRef) {
        synchronized(this) {
            mList.removeAll { it == ref }
        }
    }

    fun removeAt(index: Int) {
        synchronized(this) {
            if (index in mList.indices) {
                mList.removeAt(index)
            }
            else {
                throw IndexOutOfBoundsException("Index $index requested on list size of $size")
            }
        }
    }

    fun getAll() = synchronized(this) { mList as List<GntVerseRef> }

    fun clear() = synchronized(this) { mList.clear() }
}