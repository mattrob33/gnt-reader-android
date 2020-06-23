package com.mattrobertson.greek.reader;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import com.mattrobertson.greek.reader.interfaces.*;
import com.mattrobertson.greek.reader.ui.*;
import java.io.*;
import java.util.*;
import com.mattrobertson.greek.reader.objects.*;
import android.view.GestureDetector.*;
import android.view.View.*;
import android.animation.*;

public class PlanReaderActivity extends Activity implements GreekTextProcessorInterface
{
	final int DAY_BTN_WIDTH = 200;
	SharedPreferences prefs;

	LinearLayout daysContainer, completedContainer;
	HorizontalScrollView scrollDays;
	ScrollView scrollerMain;
	TextView tvText;
	Button btnComplete,btnHome, btnReadAhead;

	String strRawGreekText = "", defWord ="";

	ArrayList<Word> words;
	ArrayList<WordSpan> wordSpans;

	int selectedWordId = -1;

	ProgressDialog progressDialog;

	int book = -1, chapter = -1;

	boolean showVerseNumbers, showVersesNewLines;

	Typeface greekFont;
	int fontSize;

	DataBaseHelper dbHelper;

	Toast mToast;

	public boolean selectMode = false;
	public boolean nightMode = false;
	
	ArrayList<Button> buttons;
	
	int[] curDayBooks, curDayChapters;
	
	int curPlan = 0;
	int curDay = 0;
	int curDayPart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.plan_reader);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		curPlan = getIntent().getIntExtra("plan",0);
		curDay = prefs.getInt("plan-"+curPlan+"-day",0);

		if (curDay >= AppConstants.READING_PLANS[curPlan].length)
			curDay = 0;

//		getActionBar().setDisplayHomeAsUpEnabled(true);

		tvText = (TextView)findViewById(R.id.tvText);
		tvText.setHighlightColor(Color.TRANSPARENT);

		scrollerMain = (ScrollView)findViewById(R.id.scroller);

		greekFont = Typeface.createFromAsset(getAssets(), "fonts/sblgreek.ttf");
		tvText.setTypeface(greekFont);

		fontSize = prefs.getInt("fontSizeSeek",12) + 10;

		tvText.setTextSize(fontSize);
		tvText.setLineSpacing((int)(0.625*fontSize-3.75),1);
		
		btnComplete = (Button)findViewById(R.id.btnComplete);
		
		daysContainer = (LinearLayout)findViewById(R.id.daysContainer);
		
		Button btn;
		buttons = new ArrayList<Button>();
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DAY_BTN_WIDTH, DAY_BTN_WIDTH);
		params.setMargins(0,0,5,0);

		
		for (int i=0; i< AppConstants.READING_PLANS[curPlan].length; i++) {
			btn = new Button(this);
			btn.setText(""+(i+1));
			btn.setLayoutParams(params);
			btn.setTag(""+i);
			
			if (i < curDay)
				btn.setBackgroundResource(R.drawable.day_square_completed);
			else if (i == curDay)
				btn.setBackgroundResource(R.drawable.day_square_focus);
			else
				btn.setBackgroundResource(R.drawable.day_square);
/*
			btn.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					book = AppConstants.READING_PLANS[curPlan][Integer.parseInt(v.getTag().toString())][0][0];
					chapter = AppConstants.READING_PLANS[curPlan][Integer.parseInt(v.getTag().toString())][1][0];
					doNewChapter();
				}
			});
*/
			buttons.add(btn);
			daysContainer.addView(btn);
		}
		
		scrollDays = (HorizontalScrollView)findViewById(R.id.scrollDays);
		scrollDays.post(new Runnable() {
			@Override
			public void run()
			{
				int scroll = (DAY_BTN_WIDTH+5)*(curDay-2);
				if (scroll < 0)
					scroll = 0;
				
				scrollDays.scrollTo(scroll,0);
			}
		});
		
		btnComplete.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View p1)
			{
				if (curDayPart < curDayBooks.length - 1) {
					curDayPart++;
					
					designCompleteButton();
					
					int prevBook = book;
					
					book = curDayBooks[curDayPart];
					chapter = curDayChapters[curDayPart];
					
					if (prevBook == book) {
						doNewChapter();
					}
					else {
						doNewBook();
					}
				}
				else if (curDayPart == curDayBooks.length-1) {
					completeDay();
				}
			}
		});

		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Preparing...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

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

		curDayBooks = AppConstants.READING_PLANS[curPlan][curDay][0];
		curDayChapters = AppConstants.READING_PLANS[curPlan][curDay][1];

		book = curDayBooks[0];
		chapter = curDayChapters[0];

		designCompleteButton();

		doNewBook();

		RelativeLayout container = (RelativeLayout)findViewById(R.id.mainContainer);

		if (nightMode) {
			container.setBackgroundColor(Color.BLACK);
			scrollerMain.setBackgroundColor(Color.BLACK);
			tvText.setBackgroundColor(Color.BLACK);

			tvText.setTextColor(Color.parseColor("#CCCCCC"));
		}
		else {
			container.setBackgroundColor(Color.WHITE);
			scrollerMain.setBackgroundColor(Color.WHITE);
			tvText.setBackgroundColor(Color.WHITE);

			tvText.setTextColor(Color.BLACK);
		}
		
		completedContainer = (LinearLayout)findViewById(R.id.completedContainer);
		btnHome = (Button)findViewById(R.id.btnHome);
		btnReadAhead = (Button)findViewById(R.id.btnReadAhead);
		
		btnHome.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View p1)
			{
				finish();
			}
		});
		
		btnReadAhead.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View p1)
			{
				// [day][book,chap][subsequent book/chap]
				curDayBooks = AppConstants.READING_PLANS[curPlan][curDay][0];
				curDayChapters = AppConstants.READING_PLANS[curPlan][curDay][1];

				book = curDayBooks[0];
				chapter = curDayChapters[0];
				
				buttons.get(curDay).setBackgroundResource(R.drawable.day_square_focus);
				doNewBook();
				
				completedContainer.setVisibility(View.GONE);
				btnComplete.setVisibility(View.VISIBLE);
				designCompleteButton();
			}
		});
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
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_plan_reader, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_prefs:
				Intent i = new Intent(this,AppPrefsActivity.class);
				startActivity(i);
				return true;
				// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void previous()
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

	protected void next()
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

		AsyncFileReader async = new AsyncFileReader();
		async.execute();
	}

	public void doNewChapter() {
		String refStr = AppConstants.abbrvs[book] + " " + chapter;
		setTitle(refStr);

		AsyncGreekTextProcessor async = new AsyncGreekTextProcessor();
		async.execute();
	}

	private SpannableStringBuilder processRawGreekText() {

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

			if (c2.moveToFirst() == false) {
				msg("Definition unavailable for "+lex);

				return;
			}
			else {
				String gloss = c2.getString(c2.getColumnIndex("gloss"));
				int freq = c2.getInt(c2.getColumnIndex("occ"));

				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

				if (prefs.getBoolean("showParsing",false) == false)
					parsing = "";

				showDef(lex, freq, gloss, parsing);
			}
		}
		else {
			int freq = c.getInt(c.getColumnIndex("freq"));
			String def = c.getString(c.getColumnIndex("def"));
			String gloss = c.getString(c.getColumnIndex("gloss"));

			String strDef = gloss == null ? def : gloss;

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

			if (prefs.getBoolean("showParsing",false) == false)
				parsing = "";

			showDef(lex, freq, strDef, parsing);
		}
	}

	public void msg(String s) {
		mToast.setText(s);
		mToast.show();
	}

	public void showDef(String lex, int freq, String def, String parsing) {
		String strDefDisplay = "";

		defWord = lex;
		final SpannableStringBuilder sb = new SpannableStringBuilder(lex);
		final StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
		sb.setSpan(styleSpan,0,lex.length(),SpannedString.SPAN_EXCLUSIVE_EXCLUSIVE);

		strDefDisplay = sb.toString();

		strDefDisplay += " ("+freq+"): "+def;

		if (!parsing.isEmpty()) {
			strDefDisplay += " ["+parsing+"]";
		}

			msg(strDefDisplay);
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
	public void onGreekTextProcessingComplete(SpannableStringBuilder sb)
	{
		tvText.setText(sb);
		tvText.setTextIsSelectable(selectMode);
		tvText.setMovementMethod(AppConstants.createMovementMethod(PlanReaderActivity.this));
		scrollerMain.scrollTo(0,0);

		btnComplete.setVisibility(View.VISIBLE);
	}
	
	public void completeDay() {
		buttons.get(curDay).setBackgroundResource(R.drawable.day_square_completed);
		prefs.edit().putInt("plan-"+curPlan+"-day",++curDay).commit();
		curDayPart = 0;

		completedContainer = (LinearLayout)findViewById(R.id.completedContainer);
		btnComplete.setVisibility(View.GONE);
		completedContainer.setVisibility(View.VISIBLE);

		if (curDay == AppConstants.READING_PLANS[curPlan].length) {
			btnReadAhead.setVisibility(View.GONE);
			
			finish();
			Intent i = new Intent(PlanReaderActivity.this,PlanCompletedActivity.class);
			i.putExtra("plan",curPlan);
			startActivity(i);
		}
	}
	
	public void designCompleteButton() {
		// On the last part for the day?
		if (curDayPart == curDayBooks.length-1) {
			btnComplete.setBackgroundColor(getResources().getColor(R.color.green));
			btnComplete.setText("Complete");
		}
		else {
			btnComplete.setBackgroundColor(getResources().getColor(R.color.dark_blue));
			btnComplete.setText("Next");
		}
	}
}
