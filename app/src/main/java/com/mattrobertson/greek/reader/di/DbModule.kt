package com.mattrobertson.greek.reader.di

import android.content.Context
import com.mattrobertson.greek.reader.db.dao.VersesDao
import com.mattrobertson.greek.reader.db.repo.VerseRepo
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
	fun provideDatabase(@ApplicationContext appContext: Context) = com.mattrobertson.greek.reader.db.VerseDatabase.getInstance(appContext)

	@Singleton
	@Provides
	fun provideVersesDao(db: com.mattrobertson.greek.reader.db.VerseDatabase) = db.versesDao()

	@Singleton
	@Provides
	fun provideVerseRepo(versesDao: VersesDao) = VerseRepo(versesDao)

}