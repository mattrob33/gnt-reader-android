package com.mattrobertson.greek.reader;
import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.WindowManager.*;
import android.widget.*;
import com.daimajia.swipe.*;
import com.mattrobertson.greek.reader.dialog.*;
import com.mattrobertson.greek.reader.interfaces.*;
import com.mattrobertson.greek.reader.objects.*;
import com.mattrobertson.greek.reader.AppConstants;
import java.io.*;
import java.util.*;

public class MyVocabActivity extends ActionBarActivity implements VocabWizardDialogInterface
{
	ListView lvWords;
	FloatingActionButton fabWizard;
	
	VocabWizardDialog wizard;
	
	int book, chapter, maxOcc;
	String strRawGreekText;
	
	ArrayList<DefInfo> words;
	
	ProgressDialog progressDialog;
	
	DataBaseHelper dbHelper;
	VocabSwipeAdapter adapter;
	Typeface greekFont;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.my_vocab);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		book = getIntent().getIntExtra("book",5);
		chapter = getIntent().getIntExtra("chapter",5);
		
		getSupportActionBar().setTitle("MyVocab: " + AppConstants.abbrvs[book] +" "+ chapter);
		
		wizard = new VocabWizardDialog(this,this);
		Window window = wizard.getWindow();
		window.setBackgroundDrawableResource(android.R.color.transparent);
		window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.CENTER);
		
		lvWords = (ListView)findViewById(R.id.lvMyVocab);
		fabWizard = (FloatingActionButton)findViewById(R.id.fabWizard);
		fabWizard.setOnClickListener(new FloatingActionButton.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				wizard.show();
			}
		});
		
		greekFont = Typeface.createFromAsset(getAssets(), "fonts/sblgreek.ttf");
		
		try
		{
			dbHelper = new DataBaseHelper(this);
		}
		catch (Exception e)
		{
			System.out.println("Database error: " + e.getMessage());
		}
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Preparing...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		populateList();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_my_vocab, menu);
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
			case R.id.menu_export_vocab:
				exportToCSV();
				return true;
				// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void populateList() {
		dbHelper.opendatabase();
		String query = "SELECT rowid _id,lex,gloss,occ,learned FROM vocab WHERE book="+book+" AND chapter="+chapter + " ORDER BY occ DESC";
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor c = db.rawQuery(query,null);
		adapter = new VocabSwipeAdapter(this,c);
		lvWords.setAdapter(adapter);
		
		dbHelper.close();
	}
	
	@Override
	public void onVocabWizardGo(int level)
	{
		if (level == VocabWizardDialogInterface.DELETE_ALL) {
			dbHelper.opendatabase();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL("DELETE FROM vocab WHERE book="+book+" AND chapter="+chapter);
			dbHelper.close();
			
			populateList();
		}
		else {
			maxOcc = level;
			AsyncFileReader async = new AsyncFileReader();
			async.execute();
		}
	}
	
	public void exportToCSV() {
		dbHelper.opendatabase();
		String query = "SELECT lex,gloss FROM vocab WHERE book="+book+" AND chapter="+chapter + " ORDER BY occ DESC";
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor c = db.rawQuery(query,null);
		
		String strExport = "", strLex, strGloss, strEntry;
		
		while (c.moveToNext()) {
			strLex = c.getString(c.getColumnIndex("lex"));
			strGloss = c.getString(c.getColumnIndex("gloss"));
			
			strLex = strLex.replace(",",";");
			strGloss = strGloss.replace(",",";");
			
			strEntry = strLex+","+strGloss+"\n";
			strExport += strEntry;
		}

		dbHelper.close();
		
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, strExport);
		sendIntent.setType("text/csv");
		sendIntent.putExtra(Intent.EXTRA_TITLE,"vocab - " + AppConstants.abbrvs[book]+" "+chapter);
		startActivity(sendIntent);
	}
	
	private String readFromFile() {
		try {
			InputStream is = getAssets().open(AppConstants.fNames[book] + ".txt");

			Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
		catch (Exception e) {
			//msg(e.getMessage());
		}

		return "";
	}

	private void buildWordList(int maxOcc) {
		String[] arrWords = strRawGreekText.split("\n");
		String[] arrLine;

		HashSet<String> wordSet = new HashSet<String>();

		DefInfo curDef;

		words = new ArrayList<DefInfo>();

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

			if (curDef.getOcc() < maxOcc && curDef.getOcc() > 0) {
				words.add(curDef);
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
	
	public void saveVocabWord(String lex, String def, int freq) {
		dbHelper.opendatabase();
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("book",book);
		values.put("chapter",chapter);
		values.put("lex",lex);
		values.put("gloss",def);
		values.put("occ",freq);
		values.put("added_by",DataBaseHelper.ADDED_BY_WIZARD);
		values.put("date_added",System.currentTimeMillis());
		values.put("learned",0);

		db.insertWithOnConflict("vocab",null,values,SQLiteDatabase.CONFLICT_IGNORE);
		db.close();
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
				//msg(e.getMessage());
			}

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialog.dismiss();
			AsyncVocabBuilder aParser = new AsyncVocabBuilder();
			aParser.execute();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }
    }

	private class AsyncVocabBuilder extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
			buildWordList(maxOcc);

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
			for (DefInfo d : words) {
				saveVocabWord(d.lex,d.def,d.occ);
			}
			
			populateList();
			
			progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {}
    }

	public class VocabSwipeAdapter extends CursorAdapter {

		private Context context;

		public VocabSwipeAdapter(Activity context, Cursor c) {
			super(context, c);
			this.context = context;
		}
		
		// The newView method is used to inflate a new view and return it; you don't bind any data to the view at this point. 
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.vocab_swipe_list_item, parent, false);
		}

		// The bindView method is used to bind all data to a given view such as setting the text on a TextView. 
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			SwipeLayout swipeLayout = (SwipeLayout)view.findViewById(R.id.swipe_layout);
			RelativeLayout btnDelete = (RelativeLayout)view.findViewById(R.id.delete);
			TextView tvLex = (TextView)view.findViewById(R.id.tvVocabLex);
			TextView tvGloss = (TextView)view.findViewById(R.id.tvVocabGloss);

			swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
			tvLex.setTypeface(greekFont);

			String lex = cursor.getString(cursor.getColumnIndexOrThrow("lex"));
			String gloss = cursor.getString(cursor.getColumnIndexOrThrow("gloss"));
			int occ = cursor.getInt(cursor.getColumnIndexOrThrow("occ"));

			// Populate fields with extracted properties
			tvLex.setText(lex);
			tvGloss.setText(gloss +" ["+ occ +"]");
			
			btnDelete.setOnClickListener(onDeleteListener(lex,swipeLayout));
		}

		private View.OnClickListener onDeleteListener(final String lex, final SwipeLayout swipeLayout) {
			return new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// TODO: delete from db
					//Toast.makeText(context,"Deleting "+lex,Toast.LENGTH_SHORT).show();
					dbHelper.opendatabase();
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					String[] args = {lex};
					db.delete("vocab","lex=?",args);
					dbHelper.close();
					
					swipeLayout.close();
					populateList(); // adapter.notifyDataSet();
				}
			};
		}
	}
}
