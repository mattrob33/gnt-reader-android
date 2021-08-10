package com.mattrobertson.greek.reader.di

import android.content.Context
import android.net.ConnectivityManager
import com.mattrobertson.greek.reader.data.Settings
import com.mattrobertson.greek.reader.data.VerseDatabase
import com.mattrobertson.greek.reader.data.dao.VersesDao
import com.mattrobertson.greek.reader.repo.VerseRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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
	fun provideSettings(@ApplicationContext appContext: Context) = Settings.getInstance(appContext)

	@Singleton
	@Provides
	fun provideConnectivityManager(@ApplicationContext appContext: Context) = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

}