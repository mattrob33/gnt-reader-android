package com.mattrobertson.greek.reader.audio;

import android.app.*;
import android.content.*;
import android.media.*;
import android.widget.*;
import android.net.*;

public class AudioPlayer 
{
	public static final int PREPARING = -1;
	public static final int STOPPED = 0;
	public static final int PAUSED = 1;
	public static final int PLAYING = 2;
	
	private String urlBase = "http://www.helding.net/greeklatinaudio/greek/";

    int[] chapNums = {28,16,24,21,28,16,16,13,6,6,4,4,5,3,6,4,3,1,12,5,5,3,5,1,1,1,22};

	int numChapters = 1;

	int book = 0, chapter = 1;
	String bookName;
	
	MediaPlayer player;
	int state;
	int playProgress = 0;

	ProgressDialog mProgressDialog;

	ArrayAdapter<String> adapter;
	
	Context c;
	AudioPrepared callback;
	
	public AudioPlayer(Context c, AudioPrepared ap) {
		this.c = c;
		player = new MediaPlayer();
		state = STOPPED;
		callback = ap;
	}

	public boolean playChapter(int bk, int chap) {
		if ( ! isNetworkConnected())
			return false;
		
		book = bk;
		chapter = chap;
		
		try {
			state = PREPARING;
			
			mpStop();

			player = new MediaPlayer();
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);

			String strChap = chapter < 10 ? "0"+chapter : ""+ chapter;
			String[] abrvs = c.getResources().getStringArray(com.mattrobertson.greek.reader.R.array.abrvs);
			String bookAbrv = abrvs[book];
			
			String[] arrBooks = c.getResources().getStringArray(com.mattrobertson.greek.reader.R.array.books);
			bookName = arrBooks[book].toLowerCase().replace(" ","");
			
			player.setDataSource(urlBase+"/"+bookName+"/"+bookAbrv+strChap+"g.mp3");
			player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp)
				{
					player = mp;
					mpPlay();
				}
			});
			player.prepareAsync();

		} catch (Exception e) {
			Toast.makeText(c,e.getMessage(),Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}
	
	public boolean stop() {
		mpStop();
		return true;
	}
	
	public boolean pause() {
		mpPause();
		return true;
	}
	
	public boolean restart() {
		mpStop();
		playChapter(book,chapter);
		return true;
	}
	
	public int getState() {
		return state;
	}

	// PRIVATE FUNCTIONS
	
	private void mpPlay() {
		if (playProgress > 0)
			player.seekTo(playProgress);

		player.start();
		state = PLAYING;
		callback.onAudioPrepared();
	}

	private void mpPause() {
		if (player != null) {
			if (player.isPlaying()) {
				player.pause();
				state = PAUSED;
				playProgress = player.getCurrentPosition();
			}
		}
	}

	private void mpStop() {
		if (player != null)
		{
			if (player.isPlaying())
			{
				player.stop();
				state = STOPPED;
				playProgress = 0;
			}
		}
	}


	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null;
	}
}
