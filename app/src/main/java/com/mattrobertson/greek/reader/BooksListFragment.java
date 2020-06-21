package com.mattrobertson.greek.reader;

import android.content.*;
import android.os.*;
import android.preference.*;
import androidx.core.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import androidx.fragment.app.Fragment;

public class BooksListFragment extends Fragment {
	
	String[] arrBooks;
	SharedPreferences prefs;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
        return inflater.inflate(R.layout.books_fragment, container, false);
    }

	@Override
	public void onStart()
	{
		super.onStart();
			//prefs = getActivity().getSharedPreferences("com.mattrobertson.greek.reader.prefs",Context.MODE_PRIVATE);
			prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			
		boolean hasShownVocabUpdate = prefs.getBoolean("hasShownVocabUpdate",false);
		
		if ( ! hasShownVocabUpdate) {
			final LinearLayout updateView = (LinearLayout)(getActivity().findViewById(R.id.updateView));
			updateView.setVisibility(View.VISIBLE);
			
			Button btnDismiss = (Button)(getActivity().findViewById(R.id.btnDismiss));
			btnDismiss.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					prefs.edit().putBoolean("hasShownVocabUpdate",true).commit();
					updateView.setVisibility(View.GONE);
				}
			});
		}
			
		// Create an ArrayAdapter that will contain all list items
		ArrayAdapter<String> adapter;

		arrBooks = getResources().getStringArray(R.array.books);    

		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrBooks);

		// Assign the adapter to the lv
		ListView lvBooks = (ListView)(getActivity().findViewById(R.id.lvBooks));
		lvBooks.setAdapter(adapter);
		lvBooks.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int book = position;

				Intent i;

				// If single chapter book, just launch the text
				if (book != 17 && book != 23 && book != 24 && book != 25) {
					if (prefs.getBoolean("altchap",false) == false)
						i = new Intent(getActivity(),ChapterPickerActivity.class);
					else
						i = new Intent(getActivity(),ChapterPickerAlt.class);

					i.putExtra("book",book);
					i.putExtra("title",arrBooks[book]);
					startActivity(i);
				}
				else {
					i = new Intent(getActivity(),ReaderActivity.class);
					i.putExtra("book",book);
					i.putExtra("chapter", 1);
					startActivity(i);
				}
			}
		});
	}
}
