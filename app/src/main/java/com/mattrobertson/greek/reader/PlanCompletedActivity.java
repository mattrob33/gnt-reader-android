package com.mattrobertson.greek.reader;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;

import com.mattrobertson.greek.reader.presentation.CoreActivity;
import com.mattrobertson.greek.reader.util.AppConstants;

public class PlanCompletedActivity extends Activity
{
	
	TextView tvTitle, tvDesc, tvChapters;
	Button btnReturn;

	int plan;

	int[] books, chapters;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.plan_completed);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		plan = getIntent().getIntExtra("plan",0);

		getActionBar().hide();

		tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvDesc = (TextView)findViewById(R.id.tvMessage);
		tvChapters = (TextView)findViewById(R.id.tvChapters);
		btnReturn = (Button)findViewById(R.id.btnReturn);
		
		

		tvDesc.setText("You've completed the reading plan \"" + AppConstants.READING_PLAN_TITLES[plan] + "!\" Here are the chapters you read along the way:");

		btnReturn.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					finish();
					Intent i = new Intent(PlanCompletedActivity.this, CoreActivity.class);
					startActivity(i);
				}
			});
			
		updateChaptersText();
	}

	public void updateChaptersText() {
		String strText = "";

		for (int day = 0; day < AppConstants.READING_PLANS[plan].length; day++) {
			for (int dayPart=0; dayPart < AppConstants.READING_PLANS[plan][day][0].length; dayPart++) {
				strText += AppConstants.abbrvs[AppConstants.READING_PLANS[plan][day][0][dayPart]] +" "+AppConstants.READING_PLANS[plan][day][1][dayPart]+"\n";
			}
		}

		tvChapters.setText(strText);
	}
}
