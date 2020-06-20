package com.mattrobertson.greek.reader;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class PlanListFragment extends Fragment {
	String[] arrPlans; // = {"New Testament: 1 Year (Wallace)", "New Testament: 1 Month (Wallace)", "Gospels: 3 Months"};
	SharedPreferences prefs;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.plans_fragment, container, false);
    }
	
	@Override
	public void onStart()
	{
		super.onStart();
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		// Create an ArrayAdapter that will contain all list items
		ArrayAdapter<String> adapter;

		arrPlans = AppConstants.READING_PLAN_TITLES;

		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrPlans);

		// Assign the adapter to the lv
		ListView lvPlan = (ListView)(getActivity().findViewById(R.id.lvPlan));
		lvPlan.setAdapter(adapter);
		
		lvPlan.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = null;
				
				if (prefs.getInt("plan-"+position+"-day",-1) >= 0) {
					i = new Intent(getActivity(),PlanReaderActivity.class);
					i.putExtra("plan",position);
				}
				else {
					i = new Intent(getActivity(),PlanSplashActivity.class);
					i.putExtra("plan",position);
				}
				
				startActivity(i);
			}
		});
	}
}
