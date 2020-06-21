package com.mattrobertson.greek.reader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

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
			final LinearLayout updateView = getActivity().findViewById(R.id.updateView);
			updateView.setVisibility(View.VISIBLE);
			
			Button btnDismiss = getActivity().findViewById(R.id.btnDismiss);
			btnDismiss.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View p1)
				{
					prefs.edit().putBoolean("hasShownVocabUpdate",true).apply();
					updateView.setVisibility(View.GONE);
				}
			});
		}
			
		// Create an ArrayAdapter that will contain all list items
		ArrayAdapter<String> adapter;

		arrBooks = getResources().getStringArray(R.array.books);    

		adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrBooks);

		// Assign the adapter to the lv
		ListView lvBooks = getActivity().findViewById(R.id.lvBooks);
		lvBooks.setAdapter(adapter);
		lvBooks.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int book = position;
				Intent i;

				// If single chapter book, just launch the text
				if (book != 17 && book != 23 && book != 24 && book != 25) {
					if (!prefs.getBoolean("altchap",false))
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
