package com.mattrobertson.greek.reader.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mattrobertson.greek.reader.model.GntVerseRef
import java.lang.reflect.Type
import java.util.*

object Recents {

    private const val MAX_SIZE = 30

    private var mList = mutableListOf<GntVerseRef>()

    var size = 0
        private set
        get() = synchronized(this) { mList.size }

    fun add(ref: GntVerseRef) {
        synchronized(this) {
            mList.removeAll { it == ref }
            mList.add(0, ref)
            if (mList.size > MAX_SIZE)
                mList.removeAt(mList.lastIndex)
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

    fun get(index: Int) = synchronized(this) { mList[index] }

    fun getAll() = synchronized(this) { mList as List<GntVerseRef> }

    fun clear() = synchronized(this) { mList.clear() }

    fun toJson(): String {
        return Gson().toJson(mList)
    }

    fun fromJson(json: String) {
        mList = Gson().fromJson(json, Array<GntVerseRef>::class.java)?.toMutableList() ?: mutableListOf()
    }
}