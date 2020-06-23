package com.mattrobertson.greek.reader;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.mattrobertson.greek.reader.dialog.NotificationDialog;
import com.mattrobertson.greek.reader.interfaces.UpdateDialogInterface;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class BooksActivity extends FragmentActivity
 {
    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
	
	Fragment booksFragment, planFragment, vocabFragment;
	
	SharedPreferences prefs;
	
	String[] arrBooks;

	int b1,b2,b3,b4,c1,c2,c3,c4;
	
	NotificationDialog notificationDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_level_picker);
		
		final ActionBar actionBar = getActionBar();

		arrBooks = getResources().getStringArray(R.array.books);

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.
        mCustomPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);
		mViewPager.setOnPageChangeListener(
            new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    // When swiping between pages, select the
                    // corresponding tab.
                    getActionBar().setSelectedNavigationItem(position);
                }
            });

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction p2)
			{
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(ActionBar.Tab p1, FragmentTransaction p2)
			{
				
			}

			@Override
			public void onTabReselected(ActionBar.Tab p1, FragmentTransaction p2)
			{
				
			}
		};

		actionBar.addTab(actionBar.newTab().setText("Books").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Plans").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Vocab").setTabListener(tabListener));
    
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean firstLaunch = ! prefs.getBoolean("hasLaunched",false);
		
		boolean hasShownAppsAd = prefs.getBoolean("hasShownAppsAd",false);
		
		if (!hasShownAppsAd && ! firstLaunch) {
			notificationDialog = new NotificationDialog(this, new UpdateDialogInterface() {
				@Override
				public void onTryNewFeature(int featureCode)
				{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Matt+Robertson"));
					startActivity(intent);
				}
			}, UpdateDialogInterface.DOWNLOAD_HEBREW_APP);
			
			prefs.edit().putBoolean("hasShownAppsAd",true).apply();
			
			notificationDialog.setTitle("More Apps");
			//notificationDialog.setDetails("Now available for free on the Google Play Store.");
			notificationDialog.show();
		}
		
		if (firstLaunch) {
			prefs.edit().putBoolean("hasLaunched",true).apply();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_list, menu);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		b1 = prefs.getInt("recentBook1",-1);
		c1 = prefs.getInt("recentChapter1",-1);
		b2 = prefs.getInt("recentBook2",-1);
		c2 = prefs.getInt("recentChapter2",-1);
		b3 = prefs.getInt("recentBook3",-1);
		c3 = prefs.getInt("recentChapter3",-1);
		b4 = prefs.getInt("recentBook4",-1);
		c4 = prefs.getInt("recentChapter4",-1);

		boolean qs = prefs.getBoolean("quickstart",false);

		if (b1 > -1 && c1 > -1) {
			if (qs || b2 < 0 || c2 < 0) {
				MenuItem miRecent = menu.findItem(R.id.menu_recent);
				miRecent.setVisible(true);
			}
			else {
				MenuItem miRecentList = menu.findItem(R.id.menu_recent_list);
				miRecentList.setVisible(true);

				MenuItem miRecent1 = menu.findItem(R.id.menu_recent1);
				miRecent1.setTitle(arrBooks[b1]+" "+c1);

				MenuItem miRecent2 = menu.findItem(R.id.menu_recent2);
				miRecent2.setTitle(arrBooks[b2]+" "+c2);

				if (b3 > -1 && c3 > -1) {
					MenuItem miRecent3 = menu.findItem(R.id.menu_recent3);
					miRecent3.setTitle(arrBooks[b3]+" "+c3);
					miRecent3.setVisible(true);
				}

				if (b4 > -1 && c4 > -1) {
					MenuItem miRecent4 = menu.findItem(R.id.menu_recent4);
					miRecent4.setTitle(arrBooks[b4]+" "+c4);
					miRecent4.setVisible(true);
				}
			}
		}

        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(BooksActivity.this,ReaderActivity.class);

		switch (item.getItemId()) {
			case R.id.menu_recent:
			case R.id.menu_recent1:
				i.putExtra("book", b1);
				i.putExtra("chapter", c1);
				startActivity(i);
				return true;
			case R.id.menu_recent2:
				i.putExtra("book", b2);
				i.putExtra("chapter", c2);
				startActivity(i);
				return true;
			case R.id.menu_recent3:
				i.putExtra("book", b3);
				i.putExtra("chapter", c3);
				startActivity(i);
				return true;
			case R.id.menu_recent4:
				i.putExtra("book", b4);
				i.putExtra("chapter", c4);
				startActivity(i);
				return true;

			// Others
			case R.id.menu_plans:
				mViewPager.setCurrentItem(1);
				return true;
			case R.id.menu_preferences:
//				i = new Intent(BooksActivity.this,AppPrefsActivity.class);
//				startActivity(i);
				return true;
			case R.id.menu_about:
//				i = new Intent(BooksActivity.this,AboutActivity.class);
//				startActivity(i);
				return true;
				// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

class CustomPagerAdapter extends FragmentPagerAdapter {
    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    @NonNull
	public Fragment getItem(int i) {
		if (i == 0)
			return booksFragment;
		else if (i == 1)
			return planFragment;
		else
			return vocabFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
			return "Books";
		else if (position == 1)
			return "Plans";
		else
			return "Vocab";
    }
}
}
