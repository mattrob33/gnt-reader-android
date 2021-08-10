package com.mattrobertson.greek.reader.compose.settings

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.mattrobertson.greek.reader.ScrollLocation
import java.io.InputStream
import java.io.OutputStream

object ScrollLocationSerializer : Serializer<ScrollLocation> {

    override val defaultValue: ScrollLocation = ScrollLocation.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ScrollLocation {
        try {
            return ScrollLocation.parseFrom(input)
        }
        catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: ScrollLocation, output: OutputStream) {
        t.writeTo(output)
    }
}

val Context.scrollLocationDataStore: DataStore<ScrollLocation> by dataStore(
    fileName = "scroll_location.pb",
    serializer = ScrollLocationSerializer
)