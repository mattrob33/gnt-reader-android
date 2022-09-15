package com.mattrobertson.greek.reader.db.api.di

import com.mattrobertson.greek.reader.db.api.repo.ConcordanceRepo
import com.mattrobertson.greek.reader.db.api.repo.GlossesRepo
import com.mattrobertson.greek.reader.db.api.repo.VerseRepo
import com.mattrobertson.greek.reader.db.api.repo.VocabRepo
import com.mattrobertson.greek.reader.db.internal.dao.ConcordanceDao
import com.mattrobertson.greek.reader.db.internal.dao.GlossesDao
import com.mattrobertson.greek.reader.db.internal.dao.VersesDao
import com.mattrobertson.greek.reader.db.internal.dao.VocabDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbApiModule {

	@Singleton
	@Provides
	fun provideVerseRepo(versesDao: VersesDao) = VerseRepo(versesDao)

	@Singleton
	@Provides
	fun provideConcordanceRepo(concordanceDao: ConcordanceDao) = ConcordanceRepo(concordanceDao)

	@Singleton
	@Provides
	fun provideGlossesRepo(glossesDao: GlossesDao) = GlossesRepo(glossesDao)

	@Singleton
	@Provides
	fun provideVocabRepo(vocabDao: VocabDao) = VocabRepo(vocabDao)

}