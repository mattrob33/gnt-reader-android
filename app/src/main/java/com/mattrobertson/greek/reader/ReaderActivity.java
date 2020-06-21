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

import androidx.core.widget.*;
import androidx.appcompat.widget.*;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mattrobertson.greek.reader.interfaces.*;
import com.mattrobertson.greek.reader.objects.*;
import com.mattrobertson.greek.reader.ui.*;
import java.io.*;
import java.util.*;

import androidx.appcompat.widget.Toolbar;

import com.mattrobertson.greek.reader.dialog.*;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ReaderActivity extends SwipeActivity implements WordRunnerDialogInterface, UpdateDialogInterface, GreekTextProcessorInterface, AudioPrepared
{
	SharedPreferences prefs;

	CoordinatorLayout container;
	Toolbar toolbar;
	//CardView cvDef, cvConc;
	NestedScrollView scrollerMain;
	TextView tvText, tvLex, tvDef, tvConcordance, tvConcTitle;
	FloatingActionButton fabMediaPlay, fabMediaRestart;

	String strRawGreekText = "", defWord ="";

	ArrayList<Word> words;
	ArrayList<WordSpan> wordSpans;
	
	int selectedWordId = -1;
	
	ProgressDialog progressDialog;

	int book = -1, chapter = -1;
	
	boolean showVerseNumbers, showVersesNewLines, showAudioBtn;
	
	Typeface greekFont;
	int fontSize;
	
	DataBaseHelper dbHelper;
	
	Toast mToast;
	
	public boolean selectMode = false;
	public boolean nightMode = false;
	
	private int mInterval = 350; // delay in ms
	private Handler mHandler;
	int curWordRunnerWordId = -3;
	WordRunnerDialog wr;
	boolean isWordRunnerPaused = false;
	
	AudioPlayer audio;
	
	UpdateDialog updateDialog;
	
	private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.reader);

		toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		audio = new AudioPlayer(this,this);

		container = (CoordinatorLayout)findViewById(R.id.mainContainer);
		//cvDef = (CardView)findViewById(R.id.defCard);
		//cvConc = (CardView)findViewById(R.id.concordCard);
		
		tvLex = (TextView)findViewById(R.id.tvLex);
		tvDef = (TextView)findViewById(R.id.tvDef);
		tvConcordance = (TextView)findViewById(R.id.tvConcordance);
		tvConcTitle = (TextView)findViewById(R.id.tvConcordTitle);
		tvText = (TextView)findViewById(R.id.tvText);
		fabMediaPlay = (FloatingActionButton)findViewById(R.id.fabMediaPlay);
		fabMediaRestart = (FloatingActionButton)findViewById(R.id.fabMediaRestart);
		
		tvText.setHighlightColor(Color.TRANSPARENT);
		
		fabMediaPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View p1)
			{
				if (audio.getState() == audio.PLAYING) {
					audio.pause();
				}
				else {
					boolean result = audio.playChapter(book,chapter);
					
					if (result == false)
						msg("Could not play audio. Please check your network connection.");
				}
				
				refreshAudioUI();
			}
		});
		
		fabMediaRestart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View p1)
			{
				fabMediaPlay.setEnabled(false);
				fabMediaRestart.animate().rotationBy(-360);
				audio.restart();
			}
		});

		scrollerMain = (NestedScrollView)findViewById(R.id.scroller);
		scrollerMain.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
			@Override
			public void onScrollChange(NestedScrollView v, int newX, int newY, int oldX, int oldY)
			{
				if (newY > oldY) {
					fabMediaPlay.hide();
					fabMediaRestart.hide();
				}
				else if ((newY < oldY) && showAudioBtn) {
					fabMediaPlay.show();
					fabMediaRestart.show();
				}
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

		showAudioBtn = prefs.getBoolean("audio", true);
		
		if ( ! showAudioBtn)
			fabMediaPlay.setVisibility(View.INVISIBLE);
		
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

		wr = new WordRunnerDialog(this,this);
		wr.setOnCancelListener(
			new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					isWordRunnerPaused = false;
					wr.setText("");
					mHandler.removeCallbacks(mWordRunner);
				}
			}
		);
		
		try
		{
			dbHelper = new DataBaseHelper(this);
		}
		catch (Exception e)
		{
			msg("Database error: " + e.getMessage());
		}
		
		selectMode = false;
		
		mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

		nightMode = prefs.getBoolean("nightmode",false);
		showVerseNumbers = prefs.getBoolean("showVerseNumbers",true);
		showVersesNewLines = prefs.getBoolean("showVersesNewLines",false);

		// New launch
		if (savedInstanceState == null) {
			String q = getIntent().getStringExtra(SearchManager.QUERY);

			if ( q != null) { // arrived from Voice Search
				String[] qParts = q.split(" ");

				if (qParts.length != 2 && qParts.length != 3) { // invalid search; go to Matt 1
					book = 0;
					chapter = 1;
				}
				else {
					book = 0;
					chapter = 1;

					String qTitle = "", qChap = "";

					qTitle = qParts.length == 2 ? qParts[0] : qParts[0] + " " + qParts[1];
					qChap = qParts.length == 2 ? qParts[1] : qParts[2];

					qTitle = qTitle.replace("First","1");
					qTitle = qTitle.replace("1st","1");
					qTitle = qTitle.replace("Second","2");
					qTitle = qTitle.replace("2nd","2");
					qTitle = qTitle.replace("Third","3");
					qTitle = qTitle.replace("3rd","3");

					String[] arrBooks = getResources().getStringArray(R.array.books);
					boolean matched = false;

					for (int i = 0; i < arrBooks.length; i ++) {
						if (qTitle.equalsIgnoreCase(arrBooks[i])) {
							;book = i;
							matched = true;
							break;
						}
					}

					if (matched == false)
						chapter = 1;

					try {
						chapter = Integer.parseInt(qChap);
					}
					catch (NumberFormatException e) {
						chapter = 1;
					}
				}
			}
			else { // arrived from chapter picker
				book = getIntent().getIntExtra("book",5);
				chapter = getIntent().getIntExtra("chapter",5);
			}

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

		// NIGHT MODE
		if (nightMode) {
			container.setBackgroundColor(Color.BLACK);
			tvText.setTextColor(Color.parseColor("#CCCCCC"));
		}
		else {
			container.setBackgroundColor(Color.WHITE);
			tvText.setTextColor(Color.BLACK);
		}
    }

	// Returning from the user changing the settings? If so, recreate the layout
	@Override
	public void onStart() {
		super.onStart();
	
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Returning from the user changing the settings? If so, recreate the layout
		if (prefs.getInt("fontSizeSeek",12) != fontSize -10 || prefs.getBoolean("nightmode",nightMode) != nightMode || showVerseNumbers !=  prefs.getBoolean("showVerseNumbers",showVerseNumbers) || showVersesNewLines != prefs.getBoolean("showVersesNewLines",showVersesNewLines)) {
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
			case R.id.menu_my_vocab:
				i = new Intent(this,MyVocabActivity.class);
				i.putExtra("book",book);
				i.putExtra("chapter",chapter);
				startActivity(i);
				return true;
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
			case R.id.menu_toggle_mode:
				selectMode = ! selectMode;
				
				if (selectMode) {
					tvText.setHighlightColor(Color.argb(100,50,200,0));
					tvText.setBackgroundColor(Color.parseColor("#e7f5fe"));
					scrollerMain.setBackgroundColor(Color.parseColor("#333333"));
					tvDef.setTextColor(Color.parseColor("#EEEEEE"));
					
					// Show Help on the first time
					if ( ! prefs.getBoolean("hasShownSelectMode",false)) {
						Toast.makeText(this,"Select text to copy!",Toast.LENGTH_LONG).show();

						SharedPreferences.Editor editor = prefs.edit();
						editor.putBoolean("hasShownSelectMode",true);
						editor.commit();
					}
				}
				else {
					Intent intent = getIntent();
					finish();
					startActivity(intent);
					overridePendingTransition(0,0);
				}
				
				doNewBook();
				
				if (selectMode) {
					runOnUiThread(new Runnable() {
							@Override
							public void run()
							{
								tvDef.setText("Selection Mode is ON: Text is selectable");
								tvDef.invalidate();
							}
						});
				}
				
				return true;
			case R.id.menu_speed_read:
				launchWordRunner();
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
		// Ignore if in text select mode
		if (selectMode) return;
		
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
		// Ignore if in text select mode
		if (selectMode) return;
	
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
	protected void onSwipeUp(){}
	
	@Override
	protected void onSwipeDown(){}

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
		
		audio.stop();
		refreshAudioUI();
		
		selectedWordId = -1;
		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

		AsyncFileReader async = new AsyncFileReader();
		async.execute();
	}
	
	public void doNewChapter() {
		String refStr = AppConstants.abbrvs[book] + " " + chapter;
		setTitle(refStr);
		
		audio.stop();
		refreshAudioUI();
		
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
			int curVerse = Integer.parseInt(r.substring(4));

			if (curChap < chapter)
				continue;
				
			if (curChap > chapter)
				break;
	
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

			curSpan = new WordSpan(index,greekFont,index==selectedWordId,nightMode) {
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

	public void handleWordClick(int id, WordSpan w) {
		// Ignore if in text selext mode
		if (selectMode) return;
		
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
					Intent i = new Intent(ReaderActivity.this,ReaderActivity.class);
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
						Intent i = new Intent(ReaderActivity.this,ConcordanceActivity.class);
						i.putExtra("lex",lexF);
						startActivity(i);
					}
					
					@Override
					public void updateDrawState(TextPaint ds) {
						ds.setColor(Color.parseColor("#0D47A1"));
						ds.setUnderlineText(false);
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
		
		saveVocabWord(lex,def,freq);
	}
	
	public void saveVocabWord(String lex, String def, int freq) {
		dbHelper.opendatabase();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("book",book);
		values.put("chapter",chapter);
		values.put("lex",lex);
		values.put("gloss",def);
		values.put("occ",freq);
		values.put("added_by",DataBaseHelper.ADDED_BY_USER);
		values.put("date_added",System.currentTimeMillis());
		values.put("learned",0);
		
		db.insertWithOnConflict("vocab",null,values,SQLiteDatabase.CONFLICT_IGNORE);
		db.close();
	}
	
	public void showConcordance(SpannableStringBuilder sb) {
		tvConcordance.setText(sb);
	}
	
	public void refreshAudioUI() {
		if (audio.getState() == audio.PLAYING) {
			fabMediaPlay.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
			fabMediaRestart.setVisibility(View.VISIBLE);
			fabMediaRestart.animate().translationX((int)(-1 * (fabMediaRestart.getWidth()*1.5)));
		}
		else if (audio.getState() == audio.PREPARING) {
			fabMediaPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_loading));
			fabMediaPlay.animate().rotationBy(-360)
			.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						if (audio.getState() == audio.PREPARING)
							fabMediaPlay.animate().rotationBy(-360);
						else {
							fabMediaPlay.animate().cancel();
							fabMediaPlay.setRotation(0);
							refreshAudioUI();
						}
					}
				});
		}
		else {
			fabMediaPlay.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
			fabMediaRestart.animate().translationX(0)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						if (audio.getState() == audio.PAUSED)
							fabMediaRestart.setVisibility(View.INVISIBLE);
					}
				});
		}
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

	Runnable mWordRunner = new Runnable() {
		String strWordRunnerWord;
		boolean isPunctuation = false;

		@Override 
		public void run() {

			try {
				if ( ! isWordRunnerPaused) {
					if (curWordRunnerWordId < 0) {
						// starts at -3, giving a 3 second countdown
						strWordRunnerWord = "" + curWordRunnerWordId * -1;
					}
					else {
						isPunctuation = false;

						strWordRunnerWord = words.get(curWordRunnerWordId).toString().trim();

						if (strWordRunnerWord.substring(strWordRunnerWord.length()-1).matches("[.,··;;]")) {
							isPunctuation = true;
						}
					}
					
					wr.setText(strWordRunnerWord);
					curWordRunnerWordId++;
				}
			} finally {
				if (curWordRunnerWordId < words.size()) {
					int interval = mInterval;

					if (isPunctuation)
						interval = interval * 4;
					
					if (curWordRunnerWordId < 1) // ensure 3 sec countdown
						interval = 1000;

					mHandler.postDelayed(mWordRunner, interval);
				}
			}
		}
	};
	
	public void launchWordRunner() {
		curWordRunnerWordId = -3;
		wr.show();
		mHandler = new Handler();
		mWordRunner.run();
	}

	@Override
	public void onPauseWordRunner()
	{
		isWordRunnerPaused = true;
	}

	@Override
	public void onPlayWordRunner()
	{
		isWordRunnerPaused = false;
	}

	@Override
	public void onChangeSpeed(int wpm)
	{
		double wps = (double)wpm / 60.0;
		mInterval = (int)(1000.0 / wps);
	}

	@Override
	public void onClose()
	{
		mHandler.removeCallbacks(mWordRunner);
	}
	
	@Override
	public void onTryNewFeature(int featureCode)
	{
		if (featureCode == UpdateDialogInterface.FEATURE_SPEEDREAD) {
			launchWordRunner();
		}
		else if (featureCode == UpdateDialogInterface.FEATURE_CHAPTER_VOCAB) {
			Intent i = new Intent(this,ChapterVocabActivity.class);
			i.putExtra("book",book);
			i.putExtra("chapter",chapter);
			startActivity(i);
		}
	}

	@Override
	public void onGreekTextProcessingComplete(SpannableStringBuilder sb)
	{
		tvText.setText(sb);
		tvText.setTextIsSelectable(selectMode);
		tvText.setMovementMethod(AppConstants.createMovementMethod(ReaderActivity.this));
		tvText.setScrollY(0);
		scrollerMain.scrollTo(0,0);
	}
	
	@Override
	public void onAudioPrepared()
	{
		refreshAudioUI();
		fabMediaPlay.setEnabled(true);
	}
}
