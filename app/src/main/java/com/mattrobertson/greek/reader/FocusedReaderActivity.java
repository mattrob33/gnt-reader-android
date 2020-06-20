package com.mattrobertson.greek.reader;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import com.mattrobertson.greek.reader.interfaces.*;
import com.mattrobertson.greek.reader.objects.*;
import com.mattrobertson.greek.reader.ui.*;
import java.io.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import android.transition.*;
import com.mattrobertson.greek.reader.dialog.*;

public class FocusedReaderActivity extends SwipeActivity implements UpdateDialogInterface, GreekTextProcessorInterface
{
	SharedPreferences prefs;

	CoordinatorLayout container;
	Toolbar toolbar;
	//CardView cvDef, cvConc;
	NestedScrollView scrollerMain;
	TextView tvText, tvLex, tvDef, tvConcordance, tvConcTitle;
	
	String strRawGreekText = "", defWord ="";

	ArrayList<Word> words;
	ArrayList<WordSpan> wordSpans;

	int selectedWordId = -1;

	ProgressDialog progressDialog;

	int book = -1, chapter = -1;
	
	int focusedVerse = 1, maxVerse = 1;
	
	int[] verseIndexAnchors = new int[100];

	boolean showVerseNumbers, showVersesNewLines;

	Typeface greekFont;
	int fontSize;

	DataBaseHelper dbHelper;

	Toast mToast;

	UpdateDialog updateDialog;

	private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.focused_reader);

		toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		container = (CoordinatorLayout)findViewById(R.id.mainContainerFR);
		
		tvLex = (TextView)findViewById(R.id.tvLex);
		tvDef = (TextView)findViewById(R.id.tvDef);
		tvConcordance = (TextView)findViewById(R.id.tvConcordance);
		tvConcTitle = (TextView)findViewById(R.id.tvConcordTitle);
		tvText = (TextView)findViewById(R.id.tvTextFR);
		
		tvText.setHighlightColor(Color.TRANSPARENT);

		scrollerMain = (NestedScrollView)findViewById(R.id.scrollerFR);
		scrollerMain.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View p1, MotionEvent p2)
			{
				return true;
			}
		});

		NestedScrollView bottomSheet = (NestedScrollView)findViewById( R.id.bottomSheet);
		mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
		mBottomSheetBehavior.setPeekHeight(400);
		mBottomSheetBehavior.setHideable(true);
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

		greekFont = Typeface.createFromAsset(getAssets(), "fonts/sblgreek.ttf");
		tvText.setTypeface(greekFont);
		tvLex.setTypeface(greekFont);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		fontSize = prefs.getInt("fontSizeSeek",12) + 10;

		tvText.setTextSize(fontSize);
		tvText.setLineSpacing((int)(0.625*fontSize-3.75),1);

		tvConcordance.setMovementMethod(LinkMovementMethod.getInstance());

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Preparing...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		updateDialog = new UpdateDialog(this,this, UpdateDialogInterface.FEATURE_CHAPTER_VOCAB);
		updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(150,10,10,10)));
		updateDialog.setTitle("New Feature: Chapter Vocabulary!");
		updateDialog.setDetails("View all vocabulary for the chapter, grouped by number of occurrences. Access through the menu.");

		try
		{
			dbHelper = new DataBaseHelper(this);
		}
		catch (Exception e)
		{
			msg("Database error: " + e.getMessage());
		}

		mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

		showVerseNumbers = prefs.getBoolean("showVerseNumbers",true);
		showVersesNewLines = prefs.getBoolean("showVersesNewLines",false);

		// New launch
		if (savedInstanceState == null) {		
			book = getIntent().getIntExtra("book",5);
			chapter = getIntent().getIntExtra("chapter",5);

			doNewBook();
		}
		else { // screen orientation change
			strRawGreekText = savedInstanceState.getString("morphText");
			book = savedInstanceState.getInt("book",5);
			chapter = savedInstanceState.getInt("chapter",5);
			selectedWordId = savedInstanceState.getInt("selectedWordId",-1);
			defWord = savedInstanceState.getString("defWord");

			AsyncGreekTextProcessor async = new AsyncGreekTextProcessor();
			async.execute();
		}
    }

	// Returning from the user changing the settings? If so, recreate the layout
	@Override
	public void onStart() {
		super.onStart();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Returning from the user changing the settings? If so, recreate the layout
		if (prefs.getInt("fontSizeSeek",12) != fontSize -10 || showVerseNumbers !=  prefs.getBoolean("showVerseNumbers",showVerseNumbers) || showVersesNewLines != prefs.getBoolean("showVersesNewLines",showVersesNewLines)) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

		if ( ! prefs.getBoolean("hasShownChapterVocabUpdate",false)) {
			updateDialog.show();

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("hasShownChapterVocabUpdate",true);
			editor.commit();
		}

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the current state
		savedInstanceState.putString("parsedText",tvText.getText().toString());
		savedInstanceState.putString("morphText",strRawGreekText);
		savedInstanceState.putInt("book",book);
		savedInstanceState.putInt("chapter",chapter);
		savedInstanceState.putInt("scroll",scrollerMain.getScrollY());
		savedInstanceState.putString("defWord",defWord);
		savedInstanceState.putInt("selectedWordId",selectedWordId);

		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;

		switch (item.getItemId()) {
			case R.id.menu_chapter_vocab:
				i = new Intent(this,ChapterVocabActivity.class);
				i.putExtra("book",book);
				i.putExtra("chapter",chapter);
				startActivity(i);
				return true;
			case R.id.menu_prefs:
				i = new Intent(this,AppPrefsActivity.class);
				startActivity(i);
				return true;
				// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSwipeRight()
	{
		if (chapter > 1) {
			chapter--;
			doNewChapter();
		}
		else if (book > 0) {
			book--;
			chapter = AppConstants.verses[book].length;
			doNewBook();
		}
	}

	@Override
	protected void onSwipeLeft()
	{
		if (chapter < AppConstants.verses[book].length) {
			chapter++;
			doNewChapter();
		}
		else if (book < 26) {
			book++;
			chapter = 1;
			doNewBook();
		}
	}
	
	@Override
	protected void onSwipeDown() {
		if (focusedVerse > 1) {
			focusedVerse--;
		}
		
		updateFocusVerse();
		
		//msg(""+focusedVerse);
	}

	@Override
	protected void onSwipeUp() {
		if (focusedVerse < maxVerse) {
			focusedVerse++;
		}
		
		updateFocusVerse();
		
		//msg(""+focusedVerse);
	}

	private String readFromFile() {
		try {
			InputStream is = getAssets().open(AppConstants.fNames[book] + ".txt");

			Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
		catch (Exception e) {
			msg(e.getMessage());
		}

		return "";
	}

	public void doNewBook() {
		String refStr = AppConstants.abbrvs[book] + " " + chapter;
		setTitle(refStr);

		focusedVerse = 1;
		selectedWordId = -1;
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

		AsyncFileReader async = new AsyncFileReader();
		async.execute();
	}

	public void doNewChapter() {
		String refStr = AppConstants.abbrvs[book] + " " + chapter;
		setTitle(refStr);

		focusedVerse = 1;
		selectedWordId = -1;
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

		AsyncGreekTextProcessor async = new AsyncGreekTextProcessor();
		async.execute();
	}

	private SpannableStringBuilder processRawGreekText() {
		updateRecentReadingPrefList();

		String[] arrWords = strRawGreekText.split("\n");

		int index;

		String[] arrLine;

		words = new ArrayList<Word>();
		wordSpans = new ArrayList<WordSpan>();

		String x,r,l,p;
		String str = "";
		Word word;

		int totalLength = 0, lastVerse = 0;

		final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
		WordSpan curSpan;
		
		int curVerse;
		
		// Used for graying/blacking
		verseIndexAnchors[1] = 0;

		for (int n=0; n<arrWords.length; n++) {
			str = arrWords[n];
			arrLine = str.split(" ");

			r = arrLine.length > 0 ? arrLine[0] : "";
			x = arrLine.length > 2 ? arrLine[3] : "";
			p = arrLine.length > 1 ? arrLine[1] + " " + arrLine[2] : "";
			l = arrLine.length > 5 ? arrLine[6] : "";

			if (r.length() < 6)
				continue;

			int curChap = Integer.parseInt(r.substring(2,4));
			curVerse = Integer.parseInt(r.substring(4));

			if (curChap < chapter)
				continue;

			if (curChap > chapter) {
				maxVerse = lastVerse;
				break;
			}

			word = new Word(words.size(),x,r,l,p);

			index = words.size();

			words.add(word);

			boolean isUppercase = word.toString().substring(0,1).toUpperCase().equals(word.toString().substring(0,1));

			// Paragraph divisions
			if (words.size() > 1) {
				String lastWord = words.get(index-1).toString().trim();

				if (lastWord.contains(".") && isUppercase) {
					spannableStringBuilder.append("\n\t\t\t\t\t");
					totalLength += 6;
				}
			}
			else if (isUppercase) {
				spannableStringBuilder.append("\t\t\t\t\t");
				totalLength += 5;
			}

			// Verse numbers
			if (curVerse > lastVerse) {
				// Track where each verse starts for use in graying/blacking
				verseIndexAnchors[curVerse] = index;
				
				if (showVersesNewLines) {
					spannableStringBuilder.append("\n");
					totalLength += 1;
				}

				if (showVerseNumbers) {
					String strVerse = ""+curVerse+"";
					spannableStringBuilder.append(strVerse);
					spannableStringBuilder.setSpan(new SuperscriptSpan(), totalLength, totalLength + strVerse.length(), Spanned.SPAN_COMPOSING);
					spannableStringBuilder.setSpan(new RelativeSizeSpan(0.65f), totalLength, totalLength + strVerse.length(), Spanned.SPAN_COMPOSING);

					totalLength += strVerse.length();
				}

				lastVerse = curVerse;
			}

			spannableStringBuilder.append(x + " ");

			final int _index = index;

			curSpan = new WordSpan(index,greekFont,index==selectedWordId,false) {
				@Override
				public void onClick(View view) {

					handleWordClick(_index,this);
					setMarking(true);
					tvText.refreshDrawableState();
					tvText.forceLayout();
					tvText.invalidate();
				}
			};

			spannableStringBuilder.setSpan(curSpan, totalLength, totalLength + word.toString().length(), Spanned.SPAN_COMPOSING);
			totalLength += word.toString().length() + 1;

			wordSpans.add(curSpan);

		}

		return spannableStringBuilder;
	}

	// Gray/black verses 
	public void updateFocusVerse() {
		for (int i=0; i<verseIndexAnchors[focusedVerse]; i++) {
			wordSpans.get(i).setColor(Color.LTGRAY);
		}
		for (int i=verseIndexAnchors[focusedVerse]; i<verseIndexAnchors[focusedVerse+1] || (focusedVerse==maxVerse && i < wordSpans.size()); i++) {
			wordSpans.get(i).setColor(Color.BLACK);
		}
		
		if (focusedVerse < maxVerse) {
			for (int i=verseIndexAnchors[focusedVerse+1]; i<wordSpans.size(); i++) {
				wordSpans.get(i).setColor(Color.LTGRAY);
			}
		}
		
		tvText.refreshDrawableState();
		tvText.forceLayout();
		tvText.invalidate();
		
		scrollerMain.post(new Runnable() {
				@Override
				public void run() {
					if (focusedVerse == maxVerse)
						scrollerMain.fullScroll(ScrollView.FOCUS_DOWN);
					else {
						int line = verseIndexAnchors[focusedVerse] / 6; // assume avg 7 words per line
						int scrollLine = line - 2;
						if (scrollLine < 0) scrollLine = 0;
						int y = tvText.getLayout().getLineTop(scrollLine); // scroll to line
						scrollerMain.scrollTo(0, y);
					}
				}
			});
	}

	public void handleWordClick(int id, WordSpan w) {
		String lex = words.get(id).getLex();
		String parsing = words.get(id).getParsing();

		// mark last click set false
		if (selectedWordId > -1) {
			wordSpans.get(selectedWordId).setMarking(false);
			//tvText.invalidate();
		}

		selectedWordId = id;

		lookupDef(lex,parsing);
		lookupConcordance(lex);

		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}

	public void lookupDef(String lex, String parsing) {
		if (lex.isEmpty())
			return;

		dbHelper.opendatabase();

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM words WHERE lemma='"+lex+"'",null);

		if (c.moveToFirst() == false){
			// Temp hack to find missing words
			Cursor c2 = db.rawQuery("SELECT * FROM glosses WHERE gk='"+lex+"'",null);

			if (c2.moveToFirst()) {
				String gloss = c2.getString(c2.getColumnIndex("gloss"));
				int freq = c2.getInt(c2.getColumnIndex("occ"));

				showDef(lex, freq, gloss, parsing);
			}
		}
		else {
			int freq = c.getInt(c.getColumnIndex("freq"));
			String def = c.getString(c.getColumnIndex("def"));
			String gloss = c.getString(c.getColumnIndex("gloss"));

			String strDef = gloss == null ? def : gloss;

			showDef(lex, freq, strDef, parsing);
		}
	}

	public void lookupConcordance(String lex) {
		if (lex.isEmpty())
			return;

		SpannableStringBuilder sb = new SpannableStringBuilder();

		dbHelper.opendatabase();

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM concordance WHERE lex='"+lex+"'",null);

		int i=0;

		int size = c.getCount();

		String strLine = "";
		int totalLength = 0;

		ConcordanceWordSpan span;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			int _book = c.getInt(c.getColumnIndex("book"));
			int _chapter = c.getInt(c.getColumnIndex("chapter"));
			int _verse = c.getInt(c.getColumnIndex("verse"));

			i++;

			strLine = i + ". " + AppConstants.abbrvs[_book] +" "+_chapter+":"+_verse+"\n";

			sb.append(strLine);

			span = new ConcordanceWordSpan(_book,_chapter,_verse) {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(FocusedReaderActivity.this,ReaderActivity.class);
					i.putExtra("book",getBook());
					i.putExtra("chapter", getChapter());
					i.putExtra("verse", getVerse());
					startActivity(i);
				}
			};

			sb.setSpan(span,totalLength,totalLength+strLine.length()-1,Spanned.SPAN_COMPOSING);

			totalLength += strLine.length();

			if (i >= 10) {
				String strMore = "..."+(size-i)+" more";

				sb.append(strMore);

				final String lexF = lex;

				ClickableSpan spanMore = new ClickableSpan() {
					@Override
					public void onClick(View v) {
						Intent i = new Intent(FocusedReaderActivity.this,ConcordanceActivity.class);
						i.putExtra("lex",lexF);
						startActivity(i);
					}
				};

				sb.setSpan(spanMore,totalLength,totalLength+strMore.length(),Spanned.SPAN_COMPOSING);

				break;
			}
		}

		showConcordance(sb);
	}

	public void msg(String s) {
		mToast.setText(s);
		mToast.show();
	}

	public void showDef(String lex, int freq, String def, String parsing) {
		String strDefDisplay = def;

		if (!parsing.isEmpty()) {
			strDefDisplay += "\n"+parsing+"";
		}

		tvLex.setText(lex);
		tvDef.setText(strDefDisplay);
	}

	public void showConcordance(SpannableStringBuilder sb) {
		tvConcordance.setText(sb);
	}

	public void updateRecentReadingPrefList() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();

		int b1,b2,b3,b4,c1,c2,c3,c4;

		b1 = prefs.getInt("recentBook1",-1);
		b2 = prefs.getInt("recentBook2",-1);
		b3 = prefs.getInt("recentBook3",-1);
		b4 = prefs.getInt("recentBook4",-1);
		c1 = prefs.getInt("recentChapter1",-1);
		c2 = prefs.getInt("recentChapter2",-1);
		c3 = prefs.getInt("recentChapter3",-1);
		c4 = prefs.getInt("recentChapter4",-1);

		if (book == b1 && chapter == c1) {
			return; // alrwady the most recent
		}
		else if (book == b2 && chapter == c2) {
			editor.putInt("recentBook2",b1);
			editor.putInt("recentChapter2",c1);
			editor.putInt("recentBook1",book);
			editor.putInt("recentChapter1",chapter);
			editor.commit();
		}
		else if (book == b3 && chapter == c3) {
			editor.putInt("recentBook3",b2);
			editor.putInt("recentChapter3",c2);
			editor.putInt("recentBook2",b1);
			editor.putInt("recentChapter2",c1);
			editor.putInt("recentBook1",book);
			editor.putInt("recentChapter1",chapter);
			editor.commit();
		}
		else if (book == b4 && chapter == c4) {
			editor.putInt("recentBook4",b3);
			editor.putInt("recentChapter4",c3);
			editor.putInt("recentBook3",b2);
			editor.putInt("recentChapter3",c2);
			editor.putInt("recentBook2",b1);
			editor.putInt("recentChapter2",c1);
			editor.putInt("recentBook1",book);
			editor.putInt("recentChapter1",chapter);
			editor.commit();
		}
		else {
			if (b3 > -1 && c3 > -1) {
				editor.putInt("recentBook4",b3);
				editor.putInt("recentChapter4",c3);
				editor.commit();
			}

			if (b2 > -1 && c2 > -1) {
				editor.putInt("recentBook3",b2);
				editor.putInt("recentChapter3",c2);
				editor.commit();
			}

			if (b1 > -1 && c1 > -1) {
				editor.putInt("recentBook2",b1);
				editor.putInt("recentChapter2",c1);
				editor.commit();
			}

			editor.putInt("recentBook1",book);
			editor.putInt("recentChapter1",chapter);
			editor.commit();
		}
	}

	private class AsyncFileReader extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

			try {
				strRawGreekText = readFromFile();
			}
			catch (Exception e) {
				msg(e.getMessage());
			}

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialog.dismiss();
			AsyncGreekTextProcessor aParser = new AsyncGreekTextProcessor();
			aParser.execute();
        }

        @Override
        protected void onPreExecute() {
            //progressDialog.show();
        }
    }

	private class AsyncGreekTextProcessor extends AsyncTask<String, String, String> {
		SpannableStringBuilder sb;

        @Override
        protected String doInBackground(String... params) {
			sb = processRawGreekText();

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
			onGreekTextProcessingComplete(sb);
        }

        @Override
        protected void onPreExecute() {

        }
    }

	@Override
	public void onTryNewFeature(int featureCode)
	{
		if (featureCode == UpdateDialogInterface.FEATURE_CHAPTER_VOCAB) {
			Intent i = new Intent(this,ChapterVocabActivity.class);
			i.putExtra("book",book);
			i.putExtra("chapter",chapter);
			startActivity(i);
		}
	}

	@Override
	public void onGreekTextProcessingComplete(SpannableStringBuilder sb)
	{
		updateFocusVerse();
		
		tvText.setText(sb);
		tvText.setTextIsSelectable(false);
		tvText.setMovementMethod(AppConstants.createMovementMethod(FocusedReaderActivity.this));
		tvText.setScrollY(0);
		scrollerMain.scrollTo(0,0);
	}
}
