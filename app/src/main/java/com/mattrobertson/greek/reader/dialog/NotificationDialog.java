package com.mattrobertson.greek.reader.dialog;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.mattrobertson.greek.reader.R;
import com.mattrobertson.greek.reader.interfaces.*;

public class NotificationDialog extends Dialog implements android.view.View.OnClickListener
{
	private Activity c;
	UpdateDialogInterface i;
	public Dialog d;
	private TextView tvNotificationTitle, tvNotificationDetailsHeb , tvNotificationDetailsAF;
	private ImageView imgIconHeb, imgIconAF;
	public Button btnClose, btnTry;

	private int updateCode;

	private String title = "", details = "";

	public NotificationDialog(Activity a, UpdateDialogInterface i, int code) {
		super(a,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		this.c = a;
		this.i = i;
		updateCode = code;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.notification_dialog);

		btnClose = (Button) findViewById(R.id.btnClose);
		btnTry = (Button) findViewById(R.id.btnTry);
		tvNotificationTitle = (TextView)findViewById(R.id.tvNotificationTitle);
		tvNotificationDetailsHeb = (TextView)findViewById(R.id.tvNotificationDetailsHeb);
		imgIconHeb = (ImageView)findViewById(R.id.imgIconHeb);

		btnClose.setOnClickListener(this);
		btnTry.setOnClickListener(this);

		tvNotificationTitle.setText(title);
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
