package com.mattrobertson.greek.reader;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.daimajia.swipe.*;
import com.mattrobertson.greek.reader.objects.*;
import com.mattrobertson.greek.reader.AppConstants;

import android.support.v4.app.Fragment;

public class VocabListFragment extends Fragment {

	String[] arrBooks;
	SharedPreferences prefs;
	
	ListView lvWords;
	DataBaseHelper dbHelper;
	VocabAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
        return inflater.inflate(R.layout.vocab_fragment, container, false);
    }

	@Override
	public void onStart()
	{
		super.onStart();
		
		// Assign the adapter to the lv
		lvWords = (ListView)(getActivity().findViewById(R.id.lvWords));
		lvWords.setEmptyView(getActivity().findViewById(R.id.emptyListItem));
		
		try
		{
			dbHelper = new DataBaseHelper(getActivity());
		}
		catch (Exception e)
		{
			System.out.println("Database error: " + e.getMessage());
		}
		
		populateList();
	}
	
	public void populateList() {
		dbHelper.opendatabase();
		String query = "SELECT rowid as _id,book,chapter FROM vocab GROUP BY book,chapter ORDER BY book,chapter";
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor c = db.rawQuery(query,null);
		adapter = new VocabAdapter(getActivity(),c);
		lvWords.setAdapter(adapter);
		lvWords.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Toast.makeText(getActivity(),""+id,Toast.LENGTH_SHORT).show();
				
				dbHelper.opendatabase();
				String query = "SELECT book,chapter FROM vocab WHERE rowid="+id;
				SQLiteDatabase db = dbHelper.getReadableDatabase();

				Cursor c = db.rawQuery(query,null);
				c.moveToFirst();
				int book = c.getInt(c.getColumnIndex("book"));
				int chap = c.getInt(c.getColumnIndex("chapter"));
				
				Intent i = new Intent(getActivity(),MyVocabActivity.class);
				i.putExtra("book",book);
				i.putExtra("chapter", chap);
				startActivity(i);
			}
		});

		dbHelper.close();
	}

	public class VocabAdapter extends CursorAdapter {

		private Context context;

		public VocabAdapter(Activity context, Cursor c) {
			super(context, c);
			this.context = context;
		}

		// The newView method is used to inflate a new view and return it; you don't bind any data to the view at this point. 
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.vocab_list_item, parent, false);
		}

		// The bindView method is used to bind all data to a given view such as setting the text on a TextView. 
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tvItem = (TextView)view.findViewById(R.id.tvVocabItem);

			int book = cursor.getInt(cursor.getColumnIndexOrThrow("book"));
			int chapter = cursor.getInt(cursor.getColumnIndexOrThrow("chapter"));
		
			tvItem.setText(AppConstants.bookTitles[book]+" "+chapter);
		}
	}
}
