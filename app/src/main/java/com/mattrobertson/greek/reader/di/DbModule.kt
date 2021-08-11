package com.mattrobertson.greek.reader.di

import android.content.Context
import com.mattrobertson.greek.reader.db.GntDatabase
import com.mattrobertson.greek.reader.db.dao.ConcordanceDao
import com.mattrobertson.greek.reader.db.dao.GlossesDao
import com.mattrobertson.greek.reader.db.dao.VersesDao
import com.mattrobertson.greek.reader.db.dao.VocabDao
import com.mattrobertson.greek.reader.db.repo.ConcordanceRepo
import com.mattrobertson.greek.reader.db.repo.GlossesRepo
import com.mattrobertson.greek.reader.db.repo.VerseRepo
import com.mattrobertson.greek.reader.db.repo.VocabRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

	@Singleton
	@Provides
	fun provideDatabase(@ApplicationContext appContext: Context) = GntDatabase.getInstance(appContext)

	@Singleton
	@Provides
	fun provideVersesDao(db: GntDatabase) = db.versesDao()

	@Singleton
	@Provides
	fun provideVerseRepo(versesDao: VersesDao) = VerseRepo(versesDao)

	@Singleton
	@Provides
	fun provideConcordanceDao(db: GntDatabase) = db.concordanceDao()

	@Singleton
	@Provides
	fun provideConcordanceRepo(concordanceDao: ConcordanceDao) = ConcordanceRepo(concordanceDao)

	@Singleton
	@Provides
	fun provideGlossesDao(db: GntDatabase) = db.glossesDao()

	@Singleton
	@Provides
	fun provideGlossesRepo(glossesDao: GlossesDao) = GlossesRepo(glossesDao)

	@Singleton
	@Provides
	fun provideVocabDao(db: GntDatabase) = db.vocabDao()

	@Singleton
	@Provides
	fun provideVocabRepo(vocabDao: VocabDao) = VocabRepo(vocabDao)

}