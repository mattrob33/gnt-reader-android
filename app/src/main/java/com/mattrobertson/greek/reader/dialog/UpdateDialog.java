package com.mattrobertson.greek.reader.dialog;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.mattrobertson.greek.reader.R;
import com.mattrobertson.greek.reader.interfaces.*;

public class UpdateDialog extends Dialog implements android.view.View.OnClickListener
{
	private Activity c;
	UpdateDialogInterface i;
	public Dialog d;
	private TextView tvUpdateTitle, tvUpdateDetails;
	public Button btnClose, btnTry;
	
	private int updateCode;
	
	private String title = "", details = "";
	
	public UpdateDialog(Activity a, UpdateDialogInterface i, int code) {
		super(a,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		this.c = a;
		this.i = i;
		updateCode = code;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_dialog);

		btnClose = (Button) findViewById(R.id.btnClose);
		btnTry = (Button) findViewById(R.id.btnTry);
		tvUpdateTitle = (TextView)findViewById(R.id.tvUpdateTitle);
		tvUpdateDetails = (TextView)findViewById(R.id.tvUpdateDetails);

		btnClose.setOnClickListener(this);
		btnTry.setOnClickListener(this);
		
		tvUpdateTitle.setText(title);
		tvUpdateDetails.setText(details);
	}

	public void setTitle(String str) {
		title = str;
	}
	
	public void setDetails(String str) {
		details = str;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
			case R.id.btnClose:
				dismiss();
				break;
			case R.id.btnTry:
				dismiss();
				i.onTryNewFeature(updateCode);
				break;
		}
	}
}
