package com.mattrobertson.greek.reader.di

import android.content.Context
import com.mattrobertson.greek.reader.data.Settings
import com.mattrobertson.greek.reader.data.VerseDatabase
import com.mattrobertson.greek.reader.data.VersesDao
import com.mattrobertson.greek.reader.presentation.reader.HtmlGenerator
import com.mattrobertson.greek.reader.repo.VerseRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

	@Singleton
	@Provides
	fun provideDatabase(@ApplicationContext appContext: Context) = VerseDatabase.getInstance(appContext)

	@Singleton
	@Provides
	fun provideVersesDao(db: VerseDatabase) = db.versesDao()

	@Singleton
	@Provides
	fun provideVerseRepo(versesDao: VersesDao) = VerseRepo(versesDao)

	@Singleton
	@Provides
	fun provideHtmlGenerator(verseRepo: VerseRepo) = HtmlGenerator(verseRepo)

	@Singleton
	@Provides
	fun provideSettings(@ApplicationContext appContext: Context) = Settings.getInstance(appContext)

}