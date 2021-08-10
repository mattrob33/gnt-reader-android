package com.mattrobertson.greek.reader.di

import android.content.Context
import android.net.ConnectivityManager
import com.mattrobertson.greek.reader.settings.Settings
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
object NetworkModule {

	@Singleton
	@Provides
	fun provideConnectivityManager(@ApplicationContext appContext: Context) = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

}