package com.mattrobertson.greek.reader.data

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.mattrobertson.greek.reader.verseref.VerseRef

object Recents {

    private const val MAX_SIZE = 30

    private var mList = mutableListOf<VerseRef>()

    var size = 0
        private set
        get() = synchronized(this) { mList.size }

    fun add(ref: VerseRef) {
        synchronized(this) {
            mList.removeAll { it == ref }
            mList.add(0, ref)
            if (mList.size > MAX_SIZE)
                mList.removeAt(mList.lastIndex)
        }
    }

    fun remove(ref: VerseRef) {
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

    fun getMostRecent(): VerseRef? = synchronized(this) {
        if (mList.isNotEmpty())
            mList[0]
        else
            null
    }

    fun getAll() = synchronized(this) { mList }

    fun clear() = synchronized(this) { mList.clear() }

    fun toJson(): String {
        return Gson().toJson(mList)
    }

    fun fromJson(json: String) {
        mList = try {
            Gson().fromJson(json, Array<VerseRef>::class.java)?.toMutableList() ?: mutableListOf()
        } catch (e: JsonSyntaxException) {
            mutableListOf()
        }
    }
}