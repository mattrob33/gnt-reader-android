package com.mattrobertson.greek.reader.audio.data

import com.mattrobertson.greek.reader.verseref.VerseRef
import com.mattrobertson.greek.reader.audio.data.AudioNarrator.*
import com.mattrobertson.greek.reader.audio.data.Pronunciation.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioUrlProvider @Inject constructor() {

	private val baseUrl = "https://erasmus.dev/gnt-audio"

	fun getDefaultUrl(pronunciation: Pronunciation, ref: VerseRef): String {
		val book = (ref.book.num + 1).toString().padStart(2, '0')
		val chapter = ref.chapter.toString().padStart(2, '0')
		return "${baseUrl}/${pronunciation.defaultSlug}/${book}-${chapter}.mp3"
	}

	fun getUrl(narrator: AudioNarrator, ref: VerseRef): String {
		val book = (ref.book.num + 1).toString().padStart(2, '0')
		val chapter = ref.chapter.toString().padStart(2, '0')
		return "${baseUrl}/${narrator.slug}/${book}-${chapter}.mp3"
	}

	private val AudioNarrator.slug: String
		get() = when (this) {
			ErasmianPhemister -> "erasmian/phemister"
			ModernJohnSimon -> "modern/john-simon"
			ModernSblgnt -> "modern/sblgnt"
		}

	private val Pronunciation.defaultSlug: String
		get() = when (this) {
			Modern -> "modern/default"
			Erasmian -> "erasmian/default"
		}
}