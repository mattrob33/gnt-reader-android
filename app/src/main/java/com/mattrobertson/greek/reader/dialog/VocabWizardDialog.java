package com.mattrobertson.greek.reader.dialog;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.mattrobertson.greek.reader.R;
import com.mattrobertson.greek.reader.interfaces.*;
import com.mattrobertson.greek.reader.AppConstants;
import android.content.*;
import android.preference.*;

public class VocabWizardDialog extends Dialog
{
	private Activity c;
	VocabWizardDialogInterface i;
	
	private Button btnGo, btnDelete;
	private Spinner spnLevel;
	
	SharedPreferences prefs;

	public VocabWizardDialog(Activity a, VocabWizardDialogInterface i) {
		super(a,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		//super(a,android.R.style.Theme_Light_NoTitleBar);
		
		this.c = a;
		this.i = i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vocab_wizard_dialog);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(c);
		
		spnLevel = (Spinner)findViewById(R.id.spnWizLevel);
		
		int level = prefs.getInt("vocabSpinnerLevel",0);
		spnLevel.setSelection(level);
		spnLevel.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
			{
				prefs.edit().putInt("vocabSpinnerLevel",p3).commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> p1) {}
		});

		btnGo = (Button) findViewById(R.id.btnWiz);
		btnGo.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				int level = AppConstants.expertLevelVocabOcc[spnLevel.getSelectedItemPosition()];
				dismiss();
				i.onVocabWizardGo(level);
			}
		});

		btnDelete = (Button) findViewById(R.id.btnWizDeleteAll);
		btnDelete.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					i.onVocabWizardGo(VocabWizardDialogInterface.DELETE_ALL);
				}
			});
	}
}
