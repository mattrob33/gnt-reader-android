package com.mattrobertson.greek.reader.interfaces;

public interface UpdateDialogInterface
{
	public static final int FEATURE_SPEEDREAD = 1;
	public static final int FEATURE_CHAPTER_VOCAB = 2;
	public static final int DOWNLOAD_HEBREW_APP = 3;
	
	public void onTryNewFeature(int featureCode);
}
