package com.mattrobertson.greek.reader;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.util.*;

import android.view.View.OnClickListener;
import com.mattrobertson.greek.reader.ui.*;
import android.preference.*;

public class ChapterPickerAlt extends Activity 
{
	
	int[][] verses = {{25,23,17,25,48,34,29,34,38,42,30,50,58,36,39,28,27,35,30,34,46,46,39,51,46,75,66,20},
		{45,28,35,41,43,56,37,38,50,52,33,44,37,72,47,20},
		{80,52,38,44,39,49,50,56,62,42,54,59,35,35,32,31,37,43,48,47,38,71,56,53},
		{51,25,36,54,47,71,53,59,41,42,57,50,38,31,27,33,26,40,42,31,25},
		{26,47,26,37,42,15,60,40,43,48,30,25,52,28,41,40,34,28,40,38,40,30,35,27,27,32,44,31},
		{32,29,31,25,21,23,25,39,33,21,36,21,14,23,33,27},
		{31,16,23,21,13,20,40,13,27,33,34,31,13,40,58,24},
		{24,17,18,18,21,18,16,24,15,18,33,21,13},
		{24,21,29,31,26,18},
		{23,22,21,32,33,24},
		{30,30,21,23},
		{29,23,25,18},
		{10,20,13,18,28},
		{12,17,18},
		{20,15,16,16,25,21},
		{18,26,17,22},
		{16,15,15},
		{25},
		{14,18,19,16,14,20,28,13,28,39,40,29,25},
		{27,26,18,17,20},
		{25,25,22,19,14},
		{21,22,18},
		{10,29,24,21,21},
		{13},
		{15},
		{25},
		{20,29,22,11,14,17,17,13,21,11,19,17,18,20,8,21,18,24,21,15,27,21}};
	
	String[] arrChaps;
	
	int book;
	
	int numChapters = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_picker_alt);
		
		book = getIntent().getIntExtra("book",9);
		String title = getIntent().getStringExtra("title");
		setTitle(title);

		numChapters = verses[book].length;

		// Create an ArrayAdapter that will contain all list items
        ArrayAdapter<String> adapter;

        arrChaps = new String[numChapters];
		
		for (int i=0; i < numChapters; i++)
			arrChaps[i] = i+1+"";

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrChaps);

        // Assign the adapter to the lv
		ListView lvChaps = (ListView)findViewById(R.id.lvChapters);
		lvChaps.setAdapter(adapter);
		lvChaps.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					int chap = position + 1;

					Intent i = new Intent(ChapterPickerAlt.this,ReaderActivity.class);
					i.putExtra("book",book);
					i.putExtra("chapter", chap);
					startActivity(i);
				}
			});
    }
}
