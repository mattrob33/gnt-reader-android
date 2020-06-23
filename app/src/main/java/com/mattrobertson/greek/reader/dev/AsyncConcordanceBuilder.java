package com.mattrobertson.greek.reader.dev;

import android.app.*;
import android.content.*;
import android.database.sqlite.*;
import android.os.*;
import android.text.*;

import com.mattrobertson.greek.reader.objects.*;
import com.mattrobertson.greek.reader.util.AppConstants;

import java.io.*;
import java.util.*;

public class AsyncConcordanceBuilder
{

	private Context context;
	private int book,chapter;

	private ArrayList<Word> words;

	private String strRawGreekText = "";

	ProgressDialog progressDialog;
	
	InputStream is;
	Scanner s;
	
	AsyncFileReader afr;
	AsyncGreekTextProcessor aParser;
	
	DataBaseHelper dbHelper;
	SQLiteDatabase db;
	

	public AsyncConcordanceBuilder(Context c) {
		context = c;
		book = 0;
		chapter = 1;

		progressDialog = new ProgressDialog(c);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Preparing...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		try {
			dbHelper = new DataBaseHelper(c);
			dbHelper.opendatabase();
			db = dbHelper.getWritableDatabase();
		}
		catch (IOException e) {
			
		}

	}

	public void run() {
		//db.execSQL("DELETE FROM concordance");
		afr = new AsyncFileReader();
		afr.execute();
	}

	private String readFromFile() {
		try {
			is = context.getAssets().open(AppConstants.fNames[book] + ".txt");

			s = new java.util.Scanner(is).useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
		catch (Exception e) {
			//msg(e.getMessage());
		}

		return "";
	}

	private void processRawGreekText() {
		String[] arrWords = strRawGreekText.split("\n");

		String[] arrLine;

		String x,r,l,p;
		String str = "", sql;

		for (chapter=1; chapter <= AppConstants.verses[book].length; chapter++)
		{
			for (int n=0; n<arrWords.length; n++)
			{
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
			
			sql = "INSERT INTO concordance (lex,morph,parsing,book,chapter,verse) VALUES ('"+l+"','"+x+"','"+p+"',"+book+","+curChap+","+curVerse+");";
			writeToFile(sql+"\n");
			
			//db.execSQL(sql);
			}
		}

		return;
	}
	
	private void writeToFile(String data) {
		File path = context.getExternalFilesDir(null);
		File file = new File(path, "concord.txt");
		
		try {
			FileOutputStream stream = new FileOutputStream(file,true);
			stream.write(data.getBytes());
			stream.close();
		} catch (Exception e) {
			
		}
	}

	private class AsyncFileReader extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

			try {
				strRawGreekText = readFromFile();
			}
			catch (Exception e) {
				//msg(e.getMessage());
			}

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialog.dismiss();
			aParser = new AsyncGreekTextProcessor();
			aParser.execute();
        }

        @Override
        protected void onPreExecute() {
			if (book == 0)
            	progressDialog.show();
        }
    }

	private class AsyncGreekTextProcessor extends AsyncTask<String, String, String> {
		SpannableStringBuilder sb;

        @Override
        protected String doInBackground(String... params) {
			processRawGreekText();

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
			if (book < AppConstants.fNames.length) {
				book++;
				progressDialog.setMessage("Book " + book + " of " + AppConstants.fNames.length);
				afr = new AsyncFileReader();
				afr.execute();
			}
			else {
            	progressDialog.dismiss();
			}
        }

        @Override
        protected void onPreExecute() {

        }
    }
}
