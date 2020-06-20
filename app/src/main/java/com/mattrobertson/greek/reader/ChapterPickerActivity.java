package com.mattrobertson.greek.reader;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class ChapterPickerActivity extends Activity
{
	// # of chapters & verses in every chapter
	int[][] verses = {{25,23,17,25,48,34,29,34,38,42,30,50,58,36,39,28,27,35,30,34,46,46,39,51,46,75,66,20},
		{45,28,35,41,43,56,37,38,50,52,33,44,37,72,47,20},
		{80,52,38,44,39,49,50,56,62,42,54,59,35,35,32,31,37,43,48,47,38,71,56,53},
		{51,25,36,54,47,71,53,59,41,42,57,50,38,31,27,33,26,40,42,31,25},
		{26,47,26,37,42,15,60,40,43,48,30,25,52,28,41,40,34,28,40,38,40,30,35,27,27,32,44,31},
		{32,29,31,25,21,23,25,39,33,21,36,21,14,23,33,27},
		{31,16,23,21,13,20,40,13,27,33,34,31,13,40,58,24},
		{24,17,18,18,21,18,16,24,15,18,33,21,13},
		{24,21,29,31,26,18},
		{23,22,21,32,33,24},
		{30,30,21,23},
		{29,23,25,18},
		{10,20,13,18,28},
		{12,17,18},
		{20,15,16,16,25,21},
		{18,26,17,22},
		{16,15,15},
		{25},
		{14,18,19,16,14,20,28,13,28,39,40,29,25},
		{27,26,18,17,20},
		{25,25,22,19,14},
		{21,22,18},
		{10,29,24,21,21},
		{13},
		{15},
		{25},
		{20,29,22,11,14,17,17,13,21,11,19,17,18,20,8,21,18,24,21,15,27,21}};
	
	
	int book;
	int numChapters = 1;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_picker);
		
		book = getIntent().getIntExtra("book",9);
		String title = getIntent().getStringExtra("title");
		setTitle(title);

		numChapters = verses[book].length;

		LinearLayout cpContainer = (LinearLayout)findViewById(R.id.cp_container);
		
		Display display = getWindowManager().getDefaultDisplay();
		
		Point size = new Point();
		display.getSize(size);
		int wScreen = size.x - 40;
		
/*
		// *** TEMP TEST HACK -- DELETE THIS!!!
		wScreen = 600;
*/

		int wTarget = 180;
		int numSq = wScreen / wTarget; // # of squares per row

		// *** Attempted hack to fix display issues *** \\
		if (numSq < 4) {
			numSq = 4;
			wTarget = wScreen / numSq;
			/*
			* Note: Ideally numSq is calculated on screen size (allows for
			* devices of phones/tablets in portrait/landscape. But if a
			* is so small that it has only 1-3 per row with the preferred
			* button width, force it to have 4 per row and recalculate the
			* preferred button width based on this number.
			*/
		}

		int rem = wScreen % wTarget; // total remaining space on each row
		int remPer = rem / numSq; // reamining pixels per square, per row
		int wActual = wTarget + remPer;
		int numRows = (numChapters+numSq-1)/numSq; // # of rows needed
		
		ButtonRow curRow;
		
		ChapterButton btn;
		
		int ch = 1;
		
		for (int i=0; i<numRows; i++) {
			curRow = new ButtonRow(this);
			cpContainer.addView(curRow);

			for (int j=0; j<numSq; j++) {
				final int chap = ch;
				btn  = new ChapterButton(this,wActual);
				btn.setText(""+ch);curRow.addView(btn);
				btn.setOnClickListener(new Button.OnClickListener() {
						@Override
						public void onClick(View v)
						{
							boolean focusedReader = false;
							
							Intent i;
							
							if (focusedReader)
								i = new Intent(ChapterPickerActivity.this,FocusedReaderActivity.class);
							else
								i = new Intent(ChapterPickerActivity.this,ReaderActivity.class);
								
							i.putExtra("book",book);
							i.putExtra("chapter", chap);
							startActivity(i);
						}
					});
				
				ch++;
				if (ch > numChapters)
					break;
			}
		}

		//Toast.makeText(this,wActual+", "+numSq,Toast.LENGTH_LONG).show();
    }
	
	class ChapterButton extends Button {
		public ChapterButton(Context c, int sidePx) {
			super(c);
			setLayoutParams(new LinearLayout.LayoutParams(sidePx,sidePx));
			setBackground(getResources().getDrawable(R.drawable.chapter_button));
			setTextColor(Color.WHITE);
		}
	}
	
	class ButtonRow extends LinearLayout {
		public ButtonRow(Context c) {
			super(c);
			setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
			setOrientation(LinearLayout.HORIZONTAL);
		}
	}
	
}
