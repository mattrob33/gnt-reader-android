package com.mattrobertson.greek.reader;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class PlanSplashActivity extends Activity
{
	final int DAY_BTN_WIDTH = 200;
	
	LinearLayout daysContainer;
	HorizontalScrollView scrollDays;
	TextView tvTitle, tvDesc, tvChapters;
	Button btnBegin;
	
	int plan, day=0;
	
	int[] books, chapters;
	
	ArrayList<Button> buttons;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.plan_splash);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		plan = getIntent().getIntExtra("plan",0);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		daysContainer = (LinearLayout)findViewById(R.id.daysContainer);
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvDesc = (TextView)findViewById(R.id.tvDesc);
		tvChapters = (TextView)findViewById(R.id.tvChapters);
		btnBegin = (Button)findViewById(R.id.btnBegin);
		
		tvTitle.setText(AppConstants.READING_PLAN_TITLES[plan]);
		tvDesc.setText(AppConstants.READING_PLAN_DESCS[plan]);

		Button btn;
		buttons = new ArrayList<Button>();
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DAY_BTN_WIDTH, DAY_BTN_WIDTH);
		params.setMargins(0,0,5,0);
		
		for (int i=0; i< AppConstants.READING_PLANS[plan].length; i++) {
			btn = new Button(this);
			btn.setText(""+(i+1));
			btn.setTag(""+i);
			btn.setLayoutParams(params);
			btn.setTextSize(16);
			
			btn.setBackgroundResource(R.drawable.day_square);
			btn.setTextColor(Color.parseColor("#000000"));
			
			btn.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					buttons.get(day).setBackgroundResource(R.drawable.day_square);
					buttons.get(day).setTextColor(Color.parseColor("#000000"));
					
					day = Integer.parseInt(v.getTag().toString());
					books = AppConstants.READING_PLANS[plan][day][0];
					chapters = AppConstants.READING_PLANS[plan][day][1];
					
					v.setBackgroundResource(R.drawable.day_square_focus);
					
					
					updateChaptersText();
						
				}
			});

			buttons.add(btn);
			daysContainer.addView(btn);
		}
		
		buttons.get(0).performClick();
		
		btnBegin.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				finish();
				Intent i = new Intent(PlanSplashActivity.this,PlanReaderActivity.class);
				i.putExtra("plan",plan);
				startActivity(i);
			}
		});
	}
	
	public void updateChaptersText() {
		String strText = "";
		
		for (int i = 0; i < books.length; i ++) {
			strText += AppConstants.abbrvs[books[i]] +" "+chapters[i]+"\n";
		}
		
		tvChapters.setText(strText.trim());
	}
}
