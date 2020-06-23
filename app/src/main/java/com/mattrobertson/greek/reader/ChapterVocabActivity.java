package com.mattrobertson.greek.reader;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;

import android.util.Log;
import android.view.*;
import android.widget.*;

import com.mattrobertson.greek.reader.objects.*;
import com.mattrobertson.greek.reader.util.AppConstants;

import java.io.*;
import java.util.*;

import androidx.appcompat.widget.Toolbar;

public class ChapterVocabActivity extends Activity
{
	SharedPreferences prefs;
	
	Toolbar toolbar;
	ScrollView scrollerMain;
	TextView tvText;

	String strRawGreekText = "";

	ArrayList<DefInfo> words, words5, words10, words20, words30, words50, words100, wordsOver100;
	
	ProgressDialog progressDialog;

	int book = -1, chapter = -1;
	
	Typeface greekFont;
	int fontSize;
	
	DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.chapter_vocab);
		
		tvText = (TextView)findViewById(R.id.tvChapterVocabText);

		scrollerMain = (ScrollView)findViewById(R.id.chapterVocabScroll);

		greekFont = Typeface.createFromAsset(getAssets(), "fonts/sblgreek.ttf");
		tvText.setTypeface(greekFont);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		fontSize = prefs.getInt("fontSizeSeek",12) + 8;

		tvText.setTextSize(fontSize);
		tvText.setLineSpacing((int)(0.625*fontSize-3.75),1);
		tvText.setTextColor(Color.BLACK);
		
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
			//msg("Database error: " + e.getMessage());
		}

		book = getIntent().getIntExtra("book",5);
		chapter = getIntent().getIntExtra("chapter",5);

		setTitle(AppConstants.abbrvs[book] +" "+ chapter +" Vocab");

		AsyncFileReader async = new AsyncFileReader();
		async.execute();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		
		switch (item.getItemId()) {
			case R.id.menu_preferences:
				i = new Intent(this,AppPrefsActivity.class);
				startActivity(i);
				return true;
			case R.id.menu_about:
//				i = new Intent(this,AboutActivity.class);
//				startActivity(i);
				return true;
				// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private String readFromFile() {
		try {
			InputStream is = getAssets().open(AppConstants.fNames[book] + ".txt");

			Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
		catch (Exception e) {
			Log.e("sblgnt", "Failed reading from file.");
			Log.e("sblgnt", e.getMessage());
		}

		return "";
	}

	private void buildWordList() {
		String[] arrWords = strRawGreekText.split("\n");
		String[] arrLine;
		
		HashSet<String> wordSet = new HashSet<String>();
		
		DefInfo curDef;
		
		words = new ArrayList<DefInfo>();
		words5 = new ArrayList<DefInfo>();
		words10 = new ArrayList<DefInfo>();
		words20 = new ArrayList<DefInfo>();
		words30 = new ArrayList<DefInfo>();
		words50 = new ArrayList<DefInfo>();
		words100 = new ArrayList<DefInfo>();
		wordsOver100 = new ArrayList<DefInfo>();

		String lex, ref;
		String str = "";

		for (int n=0; n<arrWords.length; n++) {
			str = arrWords[n];
			arrLine = str.split(" ");

			ref = arrLine.length > 0 ? arrLine[0] : "";
			lex = arrLine.length > 5 ? arrLine[6] : "";

			if (ref.length() < 6)
				continue;

			int curChap = Integer.parseInt(ref.substring(2,4));

			if (curChap < chapter)
				continue;
				
			if (curChap > chapter)
				break;
				
			if (wordSet.contains(lex))
				continue;
				
			wordSet.add(lex);
				
			curDef = getDef(lex);
			
			if (curDef.getOcc() < 1)
				continue;
			
			words.add(curDef);
			
			if (curDef.getOcc() <= 5) {
				words5.add(curDef);
			}
			else if (curDef.getOcc() <= 10) {
				words10.add(curDef);
			}
			else if (curDef.getOcc() <= 20) {
				words20.add(curDef);
			}
			else if (curDef.getOcc() <= 30) {
				words30.add(curDef);
			}
			else if (curDef.getOcc() <= 50) {
				words50.add(curDef);
			}
			else if (curDef.getOcc() <= 100) {
				words100.add(curDef);
			}
			else {
				wordsOver100.add(curDef);
			}
		}
	}
	
	public DefInfo getDef(String lex) {
		if (lex.isEmpty())
			return null;
	
		dbHelper.opendatabase();

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM words WHERE lemma='"+lex+"'",null);
		
		if (c.moveToFirst() == false){
			// Temp hack to find missing words
			Cursor c2 = db.rawQuery("SELECT * FROM glosses WHERE gk='"+lex+"'",null);
			
			if (c2.moveToFirst()) {
				String gloss = c2.getString(c2.getColumnIndex("gloss"));
				int freq = c2.getInt(c2.getColumnIndex("occ"));
				
				dbHelper.close();

				return new DefInfo(lex,gloss,freq);
			}
		}
		else {
			int freq = c.getInt(c.getColumnIndex("freq"));
			String def = c.getString(c.getColumnIndex("def"));
			String gloss = c.getString(c.getColumnIndex("gloss"));
		
			String strDef = gloss == null ? def : gloss;
			
			dbHelper.close();

			return new DefInfo(lex,strDef,freq);
		}
		
		return null;
	}
	
	private class DefInfo {
		private String lex, def;
		private int occ;
		
		DefInfo(String lex, String def, int occ) {
			this.lex = lex;
			this.def = def;
			this.occ = occ;
		}
		
		public String getLex() {
			return lex;
		}
		
		public String getDef() {
			return def;
		}
		
		public int getOcc() {
			return occ;
		}
	}

	private class AsyncFileReader extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

			try {
				strRawGreekText = readFromFile();
			}
			catch (Exception e) {
				Log.e("sblgnt", "Failed reading Greek text from file for chapter vocab");
				Log.e("sblgnt", e.getMessage());
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
            progressDialog.show();
        }
    }

	private class AsyncGreekTextProcessor extends AsyncTask<String, String, String> {
		
        @Override
        protected String doInBackground(String... params) {
			buildWordList();

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
			renderText();
        }

        @Override
        protected void onPreExecute() {
            
        }
    }

	public void renderText()
	{
		tvText.append("1-5\n");
		for (int i=0; i<words5.size(); i++) {
			tvText.append(words5.get(i).getLex() +" (" + words5.get(i).getOcc() +"): " + words5.get(i).getDef() +"\n");
		}
		tvText.append("\n");
		
		tvText.append("6-10\n");
		for (int i=0; i<words10.size(); i++) {
			tvText.append(words10.get(i).getLex() +" (" + words10.get(i).getOcc() +"): " + words10.get(i).getDef() +"\n");
		}
		tvText.append("\n");
		
		tvText.append("11-20\n");
		for (int i=0; i<words20.size(); i++) {
			tvText.append(words20.get(i).getLex() +" (" + words20.get(i).getOcc() +"): " + words20.get(i).getDef() +"\n");
		}
		tvText.append("\n");
		
		tvText.append("21-30\n");
		for (int i=0; i<words30.size(); i++) {
			tvText.append(words30.get(i).getLex() +" (" + words30.get(i).getOcc() +"): " + words30.get(i).getDef() +"\n");
		}
		tvText.append("\n");
		
		tvText.append("31-50\n");
		for (int i=0; i<words50.size(); i++) {
			tvText.append(words50.get(i).getLex() +" (" + words50.get(i).getOcc() +"): " + words50.get(i).getDef() +"\n");
		}
		tvText.append("\n");
		
		tvText.append("51-100\n");
		for (int i=0; i<words100.size(); i++) {
			tvText.append(words100.get(i).getLex() +" (" + words100.get(i).getOcc() +"): " + words100.get(i).getDef() +"\n");
		}
		tvText.append("\n");
		
		tvText.append("101+\n");
		for (int i=0; i<wordsOver100.size(); i++) {
			tvText.append(wordsOver100.get(i).getLex() +" (" + wordsOver100.get(i).getOcc() +"): " + wordsOver100.get(i).getDef() +"\n");
		}
	}
}
