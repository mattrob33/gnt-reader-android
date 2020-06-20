package com.mattrobertson.greek.reader.objects;

import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.view.*;

public class ConcordanceWordSpan extends ClickableSpan
{
	public static final int SHOW_MORE = -1;
	
	int book,chapter,verse;

	public ConcordanceWordSpan(int b, int c, int v) {
		book = b;
		chapter = c;
		verse = v;
	}
	
	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(Color.parseColor("#0D47A1"));
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
