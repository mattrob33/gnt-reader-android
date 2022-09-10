package com.mattrobertson.greek.reader.settings.di

import android.content.Context
import com.mattrobertson.greek.reader.settings.SettingsStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

	@Singleton
	@Provides
	fun provideSettings(@ApplicationContext appContext: Context) = SettingsStore(appContext)

}