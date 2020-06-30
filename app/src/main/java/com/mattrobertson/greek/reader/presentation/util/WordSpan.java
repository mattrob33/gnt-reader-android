package com.mattrobertson.greek.reader.presentation.util;

import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.util.Log;
import android.view.*;

public class WordSpan extends ClickableSpan
{
	int id;
	private boolean marking = false;
	TextPaint tp;
	Typeface font;
	int color;

	public WordSpan(int id, Typeface font, boolean marked, int color) {
		this.id = id;
		marking = marked;
		this.font = font;
		this.color = color;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(color);
		ds.setUnderlineText(false);
		if (marking)
			ds.setTypeface(Typeface.create(font, Typeface.BOLD));

		tp = ds;
	}

	@Override
	public void onClick(View v) {
		Log.v("sblgnt", "Clicked " + id);
	}

	public void setMarking(boolean m) {
		marking = m;
		updateDrawState(tp);
	}
	
	public void setColor(int col) {
		color = col;
	}
}
