package com.mattrobertson.greek.reader.util;

import android.content.*;
import android.text.*;
import android.text.method.*;
import android.view.*;
import android.widget.*;
import com.mattrobertson.greek.reader.objects.*;

public class AppConstants
{
	public static final int[][] verses = {{25,23,17,25,48,34,29,34,38,42,30,50,58,36,39,28,27,35,30,34,46,46,39,51,46,75,66,20},
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

	public static final String[] fNames = {"matt","mark","luke","john","acts","rom","1cor","2cor","gal","eph","phil","col","1thes","2thes","1tim","2tim","tit","phlm","heb","jas","1pet","2pet","1jn","2jn","3jn","jude","rev"};

	public static final String[] abbrvs = {"Matt","Mark","Luke","John","Acts","Rom","1 Cor","2 Cor","Gal","Eph","Phil","Col","1 Thess","2 Thess","1 Tim","2 Tim","Titus","Phlm","Heb","James","1 Pet","2 Pet","1 John","2 John","3 John","Jude","Rev"};
	
	
	public static final String[] bookTitles = {"Matthew","Mark","Luke","John","Acts","Romans","1 Corinthians","2 Corinthians","Galatians","Ephesians","Philippians","Colossians","1 Thessalonians","2 Thessalonians","1 Timothy","2 Timothy","Titus","Philemon","Hebrews","James","1 Peter","2 Peter","1 John","2 John","3 John","Jude","Revelation"};
	
	public static final String[] expertLevels = {"Beginner","Novice","Intermediate","Advanced","Expert","Mastery"};
	
	public static final int[] expertLevelVocabOcc = {50,30,20,15,10,5};
	
	public static final String[] READING_PLAN_TITLES = {
		"NT in 1 Year (by Dan Wallace)",
		"NT in 1 Month (by Dan Wallace)",
		"Gospels in 3 months",
		"Romans in 1 Month",
		"Prison Epistles in 15 days",
		"General Epistles in 3 weeks"
	};
	
	public static final String[] READING_PLAN_DESCS = {
		"Read through the NT in a year (taking weekends off) by reading 3 chapters a day with the 'revolving door' principle. Each day adds one new chapter and reviews two of the previous day's chapters. Organized by difficulty and genre. Developed by Greek professor Dan Wallace.",
		"Read through the NT in 28 days by reading about 10 chapters each day. 'Not for the faint of heart.' Organized by difficulty and genre. Developed by Greek professor Dan Wallace.",
		"Read the four gospels in 3 months (89 days), reading one chapter each day.",
		"Read Romans in 31 days, reading a new chapter every two days.",
		"Read Paul's \"prison epistles\" in 15 days, reading a chapter each day.",
		"Read through the General Epistles in 3 weeks, reading a chapter each day."
	};
	
	public static final int[][][][] READING_PLANS = {
		{ {{3},{1}},{{3,3},{1,2}},{{3,3,3},{1,2,3}},{{3,3,3},{2,3,4}},{{3,3,3},{3,4,5}},{{3,3,3},{4,5,6}},{{3,3,3},{5,6,7}},{{3,3,3},{6,7,8}},{{3,3,3},{7,8,9}},{{3,3,3},{8,9,10}},{{3,3,3},{9,10,11}},{{3,3,3},{10,11,12}},{{3,3,3},{11,12,13}},{{3,3,3},{12,13,14}},{{3,3,3},{13,14,15}},{{3,3,3},{14,15,16}},{{3,3,3},{15,16,17}},{{3,3,3},{16,17,18}},{{3,3,3},{17,18,19}},{{3,3,3},{18,19,20}},{{3,3,3},{19,20,21}},{{3,3,22},{20,21,1}},{{3,22,22},{21,1,2}},{{22,22,22},{1,2,3}},{{22,22,22},{2,3,4}},{{22,22,22},{3,4,5}},{{22,22,23},{4,5,1}},{{22,23,24},{5,1,1}},{{23,24,17},{1,1,1}},{{24,17,26},{1,1,1}},{{17,26,26},{1,1,2}},{{26,26,26},{1,2,3}},{{26,26,26},{2,3,4}},{{26,26,26},{3,4,5}},{{26,26,26},{4,5,6}},{{26,26,26},{5,6,7}},{{26,26,26},{6,7,8}},{{26,26,26},{7,8,9}},{{26,26,26},{8,9,10}},{{26,26,26},{9,10,11}},{{26,26,26},{10,11,12}},{{26,26,26},{11,12,13}},{{26,26,26},{12,13,14}},{{26,26,26},{13,14,15}},{{26,26,26},{14,15,16}},{{26,26,26},{15,16,17}},{{26,26,26},{16,17,18}},{{26,26,26},{17,18,19}},{{26,26,26},{18,19,20}},{{26,26,26},{19,20,21}},{{26,26,26},{20,21,22}},{{26,26,12},{21,22,1}},{{26,12,12},{22,1,2}},{{12,12,12},{1,2,3}},{{12,12,12},{2,3,4}},{{12,12,12},{3,4,5}},{{12,12,13},{4,5,1}},{{12,13,13},{5,1,2}},{{13,13,13},{1,2,3}},{{13,13,10},{2,3,1}},{{13,10,10},{3,1,2}},{{10,10,10},{1,2,3}},{{10,10,10},{2,3,4}},{{10,10,1},{3,4,1}},{{10,1,1},{4,1,2}},{{1,1,1},{1,2,3}},{{1,1,1},{2,3,4}},{{1,1,1},{3,4,5}},{{1,1,1},{4,5,6}},{{1,1,1},{5,6,7}},{{1,1,1},{6,7,8}},{{1,1,1},{7,8,9}},{{1,1,1},{8,9,10}},{{1,1,1},{9,10,11}},{{1,1,1},{10,11,12}},{{1,1,1},{11,12,13}},{{1,1,1},{12,13,14}},{{1,1,1},{13,14,15}},{{1,1,1},{14,15,16}},{{1,1,0},{15,16,1}},{{1,0,0},{16,1,2}},{{0,0,0},{1,2,3}},{{0,0,0},{2,3,4}},{{0,0,0},{3,4,5}},{{0,0,0},{4,5,6}},{{0,0,0},{5,6,7}},{{0,0,0},{6,7,8}},{{0,0,0},{7,8,9}},{{0,0,0},{8,9,10}},{{0,0,0},{9,10,11}},{{0,0,0},{10,11,12}},{{0,0,0},{11,12,13}},{{0,0,0},{12,13,14}},{{0,0,0},{13,14,15}},{{0,0,0},{14,15,16}},{{0,0,0},{15,16,17}},{{0,0,0},{16,17,18}},{{0,0,0},{17,18,19}},{{0,0,0},{18,19,20}},{{0,0,0},{19,20,21}},{{0,0,0},{20,21,22}},{{0,0,0},{21,22,23}},{{0,0,0},{22,23,24}},{{0,0,0},{23,24,25}},{{0,0,0},{24,25,26}},{{0,0,0},{25,26,27}},{{0,0,0},{26,27,28}},{{0,0,5},{27,28,1}},{{0,5,5},{28,1,2}},{{5,5,5},{1,2,3}},{{5,5,5},{2,3,4}},{{5,5,5},{3,4,5}},{{5,5,5},{4,5,6}},{{5,5,5},{5,6,7}},{{5,5,5},{6,7,8}},{{5,5,5},{7,8,9}},{{5,5,5},{8,9,10}},{{5,5,5},{9,10,11}},{{5,5,5},{10,11,12}},{{5,5,5},{11,12,13}},{{5,5,5},{12,13,14}},{{5,5,5},{13,14,15}},{{5,5,5},{14,15,16}},{{5,5,9},{15,16,1}},{{5,9,9},{16,1,2}},{{9,9,9},{1,2,3}},{{9,9,9},{2,3,4}},{{9,9,9},{3,4,5}},{{9,9,9},{4,5,6}},{{9,9,11},{5,6,1}},{{9,11,11},{6,1,2}},{{11,11,11},{1,2,3}},{{11,11,11},{2,3,4}},{{11,11,8},{3,4,1}},{{11,8,8},{4,1,2}},{{8,8,8},{1,2,3}},{{8,8,8},{2,3,4}},{{8,8,8},{3,4,5}},{{8,8,8},{4,5,6}},{{8,8,19},{5,6,1}},{{8,19,19},{6,1,2}},{{19,19,19},{1,2,3}},{{19,19,19},{2,3,4}},{{19,19,19},{3,4,5}},{{19,19,6},{4,5,1}},{{19,6,6},{5,1,2}},{{6,6,6},{1,2,3}},{{6,6,6},{2,3,4}},{{6,6,6},{3,4,5}},{{6,6,6},{4,5,6}},{{6,6,6},{5,6,7}},{{6,6,6},{6,7,8}},{{6,6,6},{7,8,9}},{{6,6,6},{8,9,10}},{{6,6,6},{9,10,11}},{{6,6,6},{10,11,12}},{{6,6,6},{11,12,13}},{{6,6,6},{12,13,14}},{{6,6,6},{13,14,15}},{{6,6,6},{14,15,16}},{{6,6,7},{15,16,1}},{{6,7,7},{16,1,2}},{{7,7,7},{1,2,3}},{{7,7,7},{2,3,4}},{{7,7,7},{3,4,5}},{{7,7,7},{4,5,6}},{{7,7,7},{5,6,7}},{{7,7,7},{6,7,8}},{{7,7,7},{7,8,9}},{{7,7,7},{8,9,10}},{{7,7,7},{9,10,11}},{{7,7,7},{10,11,12}},{{7,7,7},{11,12,13}},{{7,7,14},{12,13,1}},{{7,14,14},{13,1,2}},{{14,14,14},{1,2,3}},{{14,14,14},{2,3,4}},{{14,14,14},{3,4,5}},{{14,14,14},{4,5,6}},{{14,14,15},{5,6,1}},{{14,15,15},{6,1,2}},{{15,15,15},{1,2,3}},{{15,15,15},{2,3,4}},{{15,15,16},{3,4,1}},{{15,16,16},{4,1,2}},{{16,16,16},{1,2,3}},{{16,16,20},{2,3,1}},{{16,20,20},{3,1,2}},{{20,20,20},{1,2,3}},{{20,20,20},{2,3,4}},{{20,20,20},{3,4,5}},{{20,20,21},{4,5,1}},{{20,21,21},{5,1,2}},{{21,21,21},{1,2,3}},{{21,21,25},{2,3,1}},{{21,25,2},{3,1,1}},{{25,2,2},{1,1,2}},{{2,2,2},{1,2,3}},{{2,2,2},{2,3,4}},{{2,2,2},{3,4,5}},{{2,2,2},{4,5,6}},{{2,2,2},{5,6,7}},{{2,2,2},{6,7,8}},{{2,2,2},{7,8,9}},{{2,2,2},{8,9,10}},{{2,2,2},{9,10,11}},{{2,2,2},{10,11,12}},{{2,2,2},{11,12,13}},{{2,2,2},{12,13,14}},{{2,2,2},{13,14,15}},{{2,2,2},{14,15,16}},{{2,2,2},{15,16,17}},{{2,2,2},{16,17,18}},{{2,2,2},{17,18,19}},{{2,2,2},{18,19,20}},{{2,2,2},{19,20,21}},{{2,2,2},{20,21,22}},{{2,2,2},{21,22,23}},{{2,2,2},{22,23,24}},{{2,2,4},{23,24,1}},{{2,4,4},{24,1,2}},{{4,4,4},{1,2,3}},{{4,4,4},{2,3,4}},{{4,4,4},{3,4,5}},{{4,4,4},{4,5,6}},{{4,4,4},{5,6,7}},{{4,4,4},{6,7,8}},{{4,4,4},{7,8,9}},{{4,4,4},{8,9,10}},{{4,4,4},{9,10,11}},{{4,4,4},{10,11,12}},{{4,4,4},{11,12,13}},{{4,4,4},{12,13,14}},{{4,4,4},{13,14,15}},{{4,4,4},{14,15,16}},{{4,4,4},{15,16,17}},{{4,4,4},{16,17,18}},{{4,4,4},{17,18,19}},{{4,4,4},{18,19,20}},{{4,4,4},{19,20,21}},{{4,4,4},{20,21,22}},{{4,4,4},{21,22,23}},{{4,4,4},{22,23,24}},{{4,4,4},{23,24,25}},{{4,4,4},{24,25,26}},{{4,4,4},{25,26,27}},{{4,4,4},{26,27,28}},{{4,4,18},{27,28,1}},{{4,18,18},{28,1,2}},{{18,18,18},{1,2,3}},{{18,18,18},{2,3,4}},{{18,18,18},{3,4,5}},{{18,18,18},{4,5,6}},{{18,18,18},{5,6,7}},{{18,18,18},{6,7,8}},{{18,18,18},{7,8,9}},{{18,18,18},{8,9,10}},{{18,18,18},{9,10,11}},{{18,18,18},{10,11,12}},{{18,18,18},{11,12,13}} },
		{ {{3,3,3,3,3,3,3,3,3,3,3},{1,2,3,4,5,6,7,8,9,10,11}},{{3,3,3,3,3,3,3,3,3,3},{12,13,14,15,16,17,18,19,20,21}},{{22,22,22,22,22,23,24,17},{1,2,3,4,5,1,1,1}},{{26,26,26,26,26,26,26,26,26,26,26},{1,2,3,4,5,6,7,8,9,10,11}},{{26,26,26,26,26,26,26,26,26,26,26},{12,13,14,15,16,17,18,19,20,21,22}},{{12,12,12,12,12,13,13,13},{1,2,3,4,5,1,2,3}},{{10,10,10,10,1,1,1,1,1,1},{1,2,3,4,1,2,3,4,5,6}},{{1,1,1,1,1,1,1,1,1,1},{7,8,9,10,11,12,13,14,15,16}},{{0,0,0,0,0,0,0,0,0,0},{1,2,3,4,5,6,7,8,9,10}},{{0,0,0,0,0,0,0,0,0,0},{11,12,13,14,15,16,17,18,19,20}},{{0,0,0,0,0,0,0,0},{21,22,23,24,25,26,27,28}},{{5,5,5,5,5,5,5,5},{1,2,3,4,5,6,7,8}},{{5,5,5,5,5,5,5,5},{9,10,11,12,13,14,15,16}},{{9,9,9,9,9,9,11,11,11,11},{1,2,3,4,5,6,1,2,3,4}},{{8,8,8,8,8,8,19,19,19,19,19},{1,2,3,4,5,6,1,2,3,4,5}},{{6,6,6,6,6,6,6,6,6,6},{1,2,3,4,5,6,7,8,9,10}},{{6,6,6,6,6,6,7,7,7,7},{11,12,13,14,15,16,1,2,3,4}},{{7,7,7,7,7,7,7,7,7},{5,6,7,8,9,10,11,12,13}},{{14,14,14,14,14,14,15,15,15,15,16,16,16},{1,2,3,4,5,6,1,2,3,4,1,2,3}},{{20,20,20,20,20,21,21,21,25},{1,2,3,4,5,1,2,3,1}},{{2,2,2,2,2,2,2,2},{1,2,3,4,5,6,7,8}},{{2,2,2,2,2,2,2,2},{9,10,11,12,13,14,15,16}},{{2,2,2,2,2,2,2,2},{17,18,19,20,21,22,23,24}},{{4,4,4,4,4,4,4,4,4,4},{1,2,3,4,5,6,7,8,9,10}},{{4,4,4,4,4,4,4,4,4},{11,12,13,14,15,16,17,18,19}},{{4,4,4,4,4,4,4,4,4},{20,21,22,23,24,25,26,27,28}},{{18,18,18,18,18,18,18},{1,2,3,4,5,6,7}},{{18,18,18,18,18,18},{8,9,10,11,12,13}} },
		{ {{0},{1}}, {{0},{2}}, {{0},{3}}, {{0},{4}}, {{0},{5}}, {{0},{6}}, {{0},{7}}, {{0},{8}}, {{0},{9}}, {{0},{10}}, {{0},{11}}, {{0},{12}}, {{0},{13}}, {{0},{14}}, {{0},{15}}, {{0},{16}}, {{0},{17}}, {{0},{18}}, {{0},{19}}, {{0},{20}}, {{0},{21}}, {{0},{22}}, {{0},{23}}, {{0},{24}}, {{0},{25}}, {{0},{26}}, {{0},{27}}, {{0},{28}}, {{1},{1}}, {{1},{2}}, {{1},{3}}, {{1},{4}}, {{1},{5}}, {{1},{6}}, {{1},{7}}, {{1},{8}}, {{1},{9}}, {{1},{10}}, {{1},{11}}, {{1},{12}}, {{1},{13}}, {{1},{14}}, {{1},{15}}, {{1},{16}}, {{2},{1}}, {{2},{2}}, {{2},{3}}, {{2},{4}}, {{2},{5}}, {{2},{6}}, {{2},{7}}, {{2},{8}}, {{2},{9}}, {{2},{10}}, {{2},{11}}, {{2},{12}}, {{2},{13}}, {{2},{14}}, {{2},{15}}, {{2},{16}}, {{2},{17}}, {{2},{18}}, {{2},{19}}, {{2},{20}}, {{2},{21}}, {{2},{22}}, {{2},{23}}, {{2},{24}}, {{3},{1}}, {{3},{2}}, {{3},{3}}, {{3},{4}}, {{3},{5}}, {{3},{6}}, {{3},{7}}, {{3},{8}}, {{3},{9}}, {{3},{10}}, {{3},{11}}, {{3},{12}}, {{3},{13}}, {{3},{14}}, {{3},{15}}, {{3},{16}}, {{3},{17}}, {{3},{18}}, {{3},{19}}, {{3},{20}}, {{3},{21}} },
		{ {{5},{1}}, {{5},{1}}, {{5},{2}}, {{5},{2}}, {{5},{3}}, {{5},{3}}, {{5},{4}}, {{5},{4}}, {{5},{5}}, {{5},{5}}, {{5},{6}}, {{5},{6}}, {{5},{7}}, {{5},{7}}, {{5},{8}}, {{5},{8}}, {{5},{9}}, {{5},{9}}, {{5},{10}}, {{5},{10}}, {{5},{11}}, {{5},{11}}, {{5},{12}}, {{5},{12}}, {{5},{13}}, {{5},{13}}, {{5},{14}}, {{5},{14}}, {{5},{15}}, {{5},{15}}, {{5},{16}} },
		{ {{10},{1}},{{10},{2}},{{10},{3}},{{10},{4}},{{9},{1}},{{9},{2}},{{9},{3}},{{9},{4}},{{9},{5}},{{9},{6}},{{11},{1}},{{11},{2}},{{11},{3}},{{11},{4}},{{17},{1}} },
		{ {{19},{1}},{{19},{2}},{{19},{3}},{{19},{4}},{{19},{5}},{{20},{1}},{{20},{2}},{{20},{3}},{{20},{4}},{{20},{5}},{{21},{1}},{{21},{2}},{{21},{3}},{{22},{1}},{{22},{2}},{{22},{3}},{{22},{4}},{{22},{5}},{{23},{1}},{{24},{1}},{{25},{1}} }
	};
	
	public static MovementMethod createMovementMethod ( Context context ) {
		final GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onSingleTapUp ( MotionEvent e ) {
					return true;
				}

				@Override
				public boolean onSingleTapConfirmed ( MotionEvent e ) {
					return false;
				}

				@Override
				public boolean onDown ( MotionEvent e ) {
					return false;
				}

				@Override
				public boolean onDoubleTap ( MotionEvent e ) {
					return false;
				}

				@Override
				public void onShowPress ( MotionEvent e ) {
					return;
				}
			});

		return new ScrollingMovementMethod() {

			@Override
			public boolean canSelectArbitrarily () {
				return true;
			}

			@Override
			public void initialize(TextView widget, Spannable text) {
				Selection.setSelection(text, text.length());
			}

			@Override
			public void onTakeFocus(TextView view, Spannable text, int dir) {

				if ((dir & (View.FOCUS_FORWARD | View.FOCUS_DOWN)) != 0) {
					if (view.getLayout() == null) {
						// This shouldn't be null, but do something sensible if it is.
						Selection.setSelection(text, text.length());
					}
				} else {
					Selection.setSelection(text, text.length());
				}
			}

			@Override
			public boolean onTouchEvent ( TextView widget, Spannable buffer, MotionEvent event ) {
				// check if event is a single tab
				boolean isClickEvent = detector.onTouchEvent(event);

				//Toast.makeText(MainActivity.this,"type "+event.getAction(),Toast.LENGTH_SHORT).show();

				// detect span that was clicked
				if (isClickEvent) {
					int x = (int) event.getX();
					int y = (int) event.getY();

					x -= widget.getTotalPaddingLeft();
					y -= widget.getTotalPaddingTop();

					x += widget.getScrollX();
					y += widget.getScrollY();

					Layout layout = widget.getLayout();
					int line = layout.getLineForVertical(y);
					int off = layout.getOffsetForHorizontal(line, x);

					WordSpan[] link = buffer.getSpans(off, off, WordSpan.class);

					if (link.length != 0) {
						// execute click only for first clickable span
						// can be a for each loop to execute every one

						if (event.getAction() == MotionEvent.ACTION_UP) {
							link[0].onClick(widget);
							return true;
						}
						else if (event.getAction() == MotionEvent.ACTION_DOWN) {
							Selection.setSelection(buffer,
												   buffer.getSpanStart(link[0]),
												   buffer.getSpanEnd(link[0]));

							return false;
						}
					}
					else {

					}
				}

				// let scroll movement handle the touch
				return super.onTouchEvent(widget, buffer, event);
			}
		};
	}
}