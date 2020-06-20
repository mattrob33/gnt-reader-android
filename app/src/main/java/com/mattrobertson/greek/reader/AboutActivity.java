package com.mattrobertson.greek.reader;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.text.*;
import android.text.method.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import com.mattrobertson.greek.reader.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import com.mattrobertson.greek.reader.objects.*;
import com.mattrobertson.greek.reader.dev.*;

public class AboutActivity extends Activity
{
	
	String sbl = "<p>Scripture is from the <a href='http://sblgnt.com'>SBL Greek New Testament</a>. Copyright © 2010 <a href='http://www.sbl-site.org/'>Society of Biblical Literature</a> and <a href='http://www.logos.com/'>Logos Bible Software</a>.</p>";
	String morph = "<p>The <a href='https://github.com/morphgnt/sblgnt'>MorphGNT SBLGNT</a> project is used as the text base to identify lexical forms.</p>";
	String mounce = "<p>Dictionary entries from:<br /><a href='https://github.com/billmounce/dictionary'>Mounce Concise Greek-English Dictionary</a><br />Copyright 1993 All Rights Reserved<br /><a href='http://www.teknia.com/greek-dictionary'>www.teknia.com/greek-dictionary</a></p>";
	String audio = "<p>Audio is from <a href='http://www.greeknewtestamentaudio.com'>GreekNewTestamentAudio.com</a> (formerly GreekLatinAudio.com). Used with permission.</p>";
	
	String str = sbl+"\n"+morph+"\n"+mounce+"\n"+audio;
	
	TextView tvText;
	
	
	// For hunting only
	String[] fNames = {"matt","mark","luke","john","acts","rom","1cor","2cor","gal","eph","phil","col","1thes","2thes","1tim","2tim","tit","phlm","heb","jas","1pet","2pet","1jn","2jn","3jn","jude","rev"};
	
	String morphText = "", defWord ="";

	ArrayList<Word> words;
	HashSet<String> missingDefsWords, missingDefsBoth;
	
	ProgressDialog progressDialog;

	int book = 0, chapter = 1;

	DataBaseHelper dbHelper;
	SQLiteDatabase db;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		tvText = (TextView)findViewById(R.id.tvAbout);
		//tvText.setFocusable(true);
		//tvText.setTextIsSelectable(true);
		tvText.setText(Html.fromHtml(str));
		
		tvText.setMovementMethod(LinkMovementMethod.getInstance());
		tvText.setHighlightColor(Color.TRANSPARENT);
/*
		AsyncConcordanceBuilder acb = new AsyncConcordanceBuilder(this);
		acb.run();
*/
		//buildPlanArray();
		
/*
		// For hunting only
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Preparing...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		words = new ArrayList<Word>();
		missingDefsWords = new HashSet<String>();
		missingDefsBoth = new HashSet<String>();
		
		try
		{
			dbHelper = new DataBaseHelper(this);
		}
		catch (Exception e)
		{
			msg("Database error: " + e.getMessage());
		}
		
		AsyncFileReader async = new AsyncFileReader();
		async.execute();
*/
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
				// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void msg(String s) {
		Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
	}
	
	private String readFromFile() {
		try {
			InputStream is = getAssets().open(fNames[book] + ".txt");

			Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
		catch (Exception e) {
			msg(e.getMessage());
		}

		return "";
	}
	
	private boolean parseMorphText() {
		String[] arrWords = morphText.split("\n");

		//final int index;

		String[] arrLine;

		//words = new ArrayList<Word>();
		
		String x,r,l,p;
		String str = "";
		Word word;

		//final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
		
		for (int n=0; n<arrWords.length; n++) {
			str = arrWords[n];
			arrLine = str.split(" ");

			r = arrLine.length > 0 ? arrLine[0] : "";
			x = arrLine.length > 2 ? arrLine[3] : "";
			p = arrLine.length > 1 ? arrLine[1] + " " + arrLine[2] : "";
			l = arrLine.length > 5 ? arrLine[6] : "";
			
			//lookupDef(x);

			word = new Word(words.size(),x,r,l,p);

			//index = words.size();

			words.add(word);

			//spannableStringBuilder.append(x + " ");
		}

		return true;
	}
	
	public void lookupDef(String lex) {
		if (lex.isEmpty())
			return;

		Cursor c = db.rawQuery("SELECT * FROM words WHERE lemma='"+lex+"'",null);

		if (c.moveToFirst() == false) {
			
			missingDefsWords.add(lex);
			
			// Temp hack to find missing words
			Cursor c2 = db.rawQuery("SELECT * FROM glosses WHERE gk='"+lex+"'",null);

			if (c2.moveToFirst() == false) {
				missingDefsWords.remove(missingDefsWords.size()-1);
				missingDefsBoth.add(lex);
				c2.close();
				return;
			}
		}
		c.close();
	}
	
	public void writeToFile(String s) {
		try {
			File f = new File("/sdcard/missingWords.txt");
			f.createNewFile();
			
			FileOutputStream fOut = new FileOutputStream(f);
			OutputStreamWriter ow = new OutputStreamWriter(fOut);
			ow.append(s);
			ow.close();
			fOut.close();
		}
		catch (Exception e) {
			
		}
	}
	
	private class AsyncFileReader extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

			try {
				morphText = readFromFile();
			}
			catch (Exception e) {
				msg(e.getMessage());
			}

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialog.dismiss();
			
			AsyncParser aParser = new AsyncParser();
			aParser.execute();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }
    }

	private class AsyncParser extends AsyncTask<String, String, String> {

		boolean success = false;

        @Override
        protected String doInBackground(String... params) {
			success = parseMorphText();

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
			if (!success) {
				msg("Error: Unable to parse file");
				return;
			}
			
			if (book < 26) {
				book++;

				AsyncFileReader aReader = new AsyncFileReader();
				aReader.execute();
			}
			else {	
				AsyncLookup aLookup = new AsyncLookup();
				aLookup.execute();
			}
        }

        @Override
        protected void onPreExecute() {

        }
    }

	private class AsyncLookup extends AsyncTask<String, String, String> {
        
		int i;
		
		@Override
        protected String doInBackground(String... params) {
			dbHelper.opendatabase();

			db = dbHelper.getReadableDatabase();

			for (i=0; i < words.size(); i++) {
				lookupDef(words.get(i).getLex());
				
				if (i % 1000 == 0) {
					runOnUiThread(new Runnable() {
							@Override
							public void run()
							{
								msg(i+"/"+words.size());
							}
					});
				}
				
				//tvText.append(s+"\n");
			}

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
			runOnUiThread(new Runnable() {
					@Override
					public void run()
					{
						String txt = "";
						
						txt += "---BOTH---\n";
						for (String s : missingDefsBoth) {
							//lookupDef(w.lex);
							txt += s+"\n";
						}
						
						txt += "\n\n---WORDS---\n";
						for (String s : missingDefsWords) {
							//lookupDef(w.lex);
							txt += s+"\n";
						}
						
						writeToFile(txt);
					}
			});
				progressDialog.dismiss();
        }

        protected void onPreExecute() {

        }
    }
	
	public void buildPlanArray() {
		StringBuilder out = new StringBuilder();
		int bookIndex = 0;
		int iGroup = 0;
		int curGroup = 0;
		
		int chap = 1;
		
		String strGroupBooks="", strGroupChaps="";
		
		int[] booksOrder = {3,22,23,24,17,26,12,13,10,1,0,5,9,11,8,19,6,7,14,15,16,20,21,25,2,4,18};
		int[] groupSize = {11,10,8,11,11,8,10,10,10,10,8,8,8,10,11,10,10,9,13,9,8,8,8,10,9,9,7,6};
		
		while (true) {
			if (iGroup == 0) {
				strGroupBooks += "{";
				strGroupChaps+= "{";
			}
			
			strGroupBooks += booksOrder[bookIndex];
			// attach either comma or }
			
			strGroupChaps += chap;
			chap++;
			
			// on first chapter of new book?
			if (chap > AppConstants.verses[booksOrder[bookIndex]].length) {
				bookIndex++;
				
				//if (bookIndex > 26)
					//break;

				chap = 1;
			}
			
			iGroup++; // advance within group
			
			// end of group
			if (iGroup >= groupSize[curGroup]) {
				curGroup++;
				iGroup = 0;
				
				strGroupBooks += "}";
				strGroupChaps += "}";
				
				out.append("{"+strGroupBooks+","+strGroupChaps+"},\n");
				strGroupBooks = "";
				strGroupChaps = "";
				
				
				if (curGroup > 27)
					break;
			}
			else {
				strGroupBooks += ",";
				strGroupChaps += ",";
			}
		}
		
		tvText.setText(out);
	}
}
