package com.mattrobertson.greek.reader.di

import android.net.ConnectivityManager
import com.mattrobertson.greek.reader.audio.AudioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

	@ViewModelScoped
	@Provides
	fun provideAudioPlayer(connectivityManager: ConnectivityManager) = AudioPlayer(connectivityManager)

}