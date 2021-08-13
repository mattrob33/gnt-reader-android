package com.mattrobertson.greek.reader.db.internal.di

import android.content.Context
import com.mattrobertson.greek.reader.db.internal.GntDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbInternalModule {

	@Singleton
	@Provides
	fun provideDatabase(@ApplicationContext appContext: Context) = GntDatabase.getInstance(appContext)

	@Singleton
	@Provides
	fun provideVersesDao(db: GntDatabase) = db.versesDao()

	@Singleton
	@Provides
	fun provideConcordanceDao(db: GntDatabase) = db.concordanceDao()

	@Singleton
	@Provides
	fun provideGlossesDao(db: GntDatabase) = db.glossesDao()

	@Singleton
	@Provides
	fun provideVocabDao(db: GntDatabase) = db.vocabDao()

}