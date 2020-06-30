package com.mattrobertson.greek.reader.presentation.util;

import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.view.*;

import androidx.core.content.res.ResourcesCompat;

public class ConcordanceWordSpan extends ClickableSpan
{
	public static final int SHOW_MORE = -1;
	
	int book, chapter, verse;
	int color;

	public ConcordanceWordSpan(int b, int c, int v, int color) {
		book = b;
		chapter = c;
		verse = v;
		this.color = color;
	}
	
	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(color);
		ds.setUnderlineText(false);
	}

	public int getBook()
	{
		return book;
	}

	public int getChapter()
	{
		return chapter;
	}

	public int getVerse()
	{
		return verse;
	}

	@Override
	public void onClick(View v) {

	}
}
