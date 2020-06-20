package com.mattrobertson.greek.reader.objects;

import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.view.*;

public class WordSpan extends ClickableSpan
{
	int id;
	private boolean marking = false, nightMode = false;
	TextPaint tp;
	Typeface font;
	int color = Color.BLACK;

	public WordSpan(int id, Typeface font, boolean marked, boolean nightMode) {
		this.id = id;
		marking = marked;
		this.font = font;
		this.nightMode = nightMode;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(color);
		ds.setUnderlineText(false);

		if (marking)
			ds.setTypeface(Typeface.create(font,Typeface.BOLD));

		if (nightMode)
			ds.setColor(Color.parseColor("#CCCCCC"));

		tp = ds;


	}

	@Override
	public void onClick(View v) {

	}

	public void setMarking(boolean m) {
		marking = m;
		updateDrawState(tp);
	}
	
	public void setColor(int col) {
		color = col;
	}
}
