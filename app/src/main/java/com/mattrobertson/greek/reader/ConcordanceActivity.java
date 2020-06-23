package com.mattrobertson.greek.reader;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.mattrobertson.greek.reader.objects.*;
import com.mattrobertson.greek.reader.util.AppConstants;

import java.io.*;
import android.text.method.*;
import android.text.style.*;

public class ConcordanceActivity extends Activity
{
	TextView tvTitle, tvText;
	ProgressDialog progressDialog;
	
	AsyncLookup async;
	DataBaseHelper dbHelper;
	SQLiteDatabase db;
	String lex;
	
	boolean showText = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.concordance);

		tvTitle = (TextView)findViewById(R.id.tvConcordanceTitle);
		tvText = (TextView)findViewById(R.id.tvConcordanceText);
		tvText.setMovementMethod(LinkMovementMethod.getInstance());
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Preparing...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		async = new AsyncLookup();
		lex = getIntent().getStringExtra("lex");
		
		tvTitle.setText(lex);
		
		async.execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_concordance, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_show_text:
				showText = ! showText;
				async = new AsyncLookup();
				async.execute();
				return true;
			
				// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void lookupConcordance(String lex) {
		if (lex.isEmpty())
			return;
		
		try
		{
			dbHelper = new DataBaseHelper(this);
			dbHelper.opendatabase();
			db = dbHelper.getReadableDatabase();
		}
		catch (IOException e)
		{}

		SpannableStringBuilder sb = new SpannableStringBuilder();
		
		Cursor c = db.rawQuery("SELECT * FROM concordance WHERE lex='"+lex+"'",null);
		Cursor cVerse;
		String strVerse,strWord;

		int i=0 ,size=c.getCount();

		String strLine = "";
		int totalLength = 0;

		ConcordanceWordSpan span;
		StyleSpan boldSpan;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			int _book = c.getInt(c.getColumnIndex("book"));
			int _chapter = c.getInt(c.getColumnIndex("chapter"));
			int _verse = c.getInt(c.getColumnIndex("verse"));
			
			i++;
			
			async.updateProgress(i,size);

			strLine = i + ". " + AppConstants.abbrvs[_book] +" "+_chapter+":"+_verse+"\n";

			sb.append(strLine);

			span = new ConcordanceWordSpan(_book,_chapter,_verse) {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(ConcordanceActivity.this,ReaderActivity.class);
					i.putExtra("book",getBook());
					i.putExtra("chapter", getChapter());
					i.putExtra("verse", getVerse());
					startActivity(i);
				}
			};

			sb.setSpan(span,totalLength,totalLength+strLine.length()-1,Spanned.SPAN_COMPOSING);

			totalLength += strLine.length();
			
			if (showText) {
				cVerse = db.rawQuery("SELECT morph,lex FROM concordance WHERE book='"+_book+"' AND chapter='"+_chapter+"' AND verse='"+_verse+"'",null);
				strVerse = "";
			
				for (cVerse.moveToFirst(); !cVerse.isAfterLast(); cVerse.moveToNext()) {
					strWord = cVerse.getString(0);
					strVerse = strWord + " ";
					
					sb.append(strVerse);
					
					// check if lex form of current word == lex
					if (lex.equals(cVerse.getString(1))) {
						boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
						// Set bold span
						sb.setSpan(boldSpan,totalLength,totalLength+strWord.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
					}
					
					totalLength += strVerse.length();
				}
				cVerse.close();
		
				sb.append("\n\n");
				totalLength += 2;
			}
		}
		
		c.close();

		showConcordance(sb);
	}
	
	private void showConcordance(SpannableStringBuilder sb) {
		final SpannableStringBuilder sbF = sb;
		
		runOnUiThread(new Runnable() {
			@Override
			public void run()
			{
				tvText.setText(sbF);
			}
		});
	}
	
	private class AsyncLookup extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

			lookupConcordance(lex);

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }
		
		public void updateProgress(final int cur, final int size) {
			final int per = (int)(((double)cur)/((double)size)*100);

			runOnUiThread(new Runnable() {
				@Override
				public void run()
				{
					progressDialog.setMessage("Preparing... ("+per+"%)");
				}
			});
		}
    }
}
