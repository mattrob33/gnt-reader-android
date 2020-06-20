package com.mattrobertson.greek.reader.objects;

import android.os.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import java.io.*;
import java.util.*;
import android.content.*;
import com.mattrobertson.greek.reader.*;
import android.app.*;

public class GreekTextProcessor
{
/*
	private Context context;
	private int book,chapter;
	
	private ArrayList<Word> words;
	private ArrayList<WordSpan> wordSpans;
	
	private String strRawGreekText = "";
	
	ProgressDialog progressDialog;
	
	public GreekTextProcessor(Context c) {
		context = c;
		book = -1;
		chapter = -1;
		
		progressDialog = new ProgressDialog(c);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Preparing...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}
	
	public void run() {
		
	}
	
	private String readFromFile() {
		try {
			InputStream is = context.getAssets().open(AppConstants.fNames[book] + ".txt");

			Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
		catch (Exception e) {
			//msg(e.getMessage());
		}

		return "";
	}
	
	private SpannableStringBuilder processRawGreekText() {
		String[] arrWords = strRawGreekText.split("\n");

		final int index;

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

			curSpan = new WordSpan(index,greekFont,index==selectedWordId,nightMode) {
				@Override
				public void onClick(View view) {

					handleWordClick(index,this);
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
        }

        @Override
        protected void onPreExecute() {

        }
    }
*/
}
