package com.mattrobertson.greek.reader.dialog;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.SeekBar.*;
import com.mattrobertson.greek.reader.R;
import com.mattrobertson.greek.reader.interfaces.*;

public class WordRunnerDialog extends Dialog implements android.view.View.OnClickListener
{
	private Activity c;
	private WordRunnerDialogInterface i;
	public Dialog d;
	private TextView tvWord, tvWpm;
	public Button btnClose, btnPause, btnInc, btnDec;
	private SeekBar seekWpm;

	private boolean isPaused = false;
	private int wpm = 120;
	private final int minWpm = 30;

	public WordRunnerDialog(Activity a, WordRunnerDialogInterface i) {
		super(a);
		this.c = a;
		this.i = i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.word_runner_dialog);

		btnClose = (Button) findViewById(R.id.btnClose);
		btnPause = (Button) findViewById(R.id.btnPause);
		//btnInc = (Button) findViewById(R.id.btnIncrease);
		//btnDec = (Button) findViewById(R.id.btnDecrease);
		tvWord = (TextView)findViewById(R.id.tvWord);
		tvWpm = (TextView)findViewById(R.id.tvWpm);
		seekWpm = (SeekBar)findViewById(R.id.seekWpm);

		tvWpm.setText(wpm + " wpm");
		seekWpm.setProgress(wpm);

		btnClose.setOnClickListener(this);
		btnPause.setOnClickListener(this);

		seekWpm.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar sb, int progress, boolean fromUser)
				{
					wpm = progress + minWpm;
					i.onChangeSpeed(wpm);
					tvWpm.setText(wpm + " wpm");
				}

				@Override
				public void onStartTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}

				@Override
				public void onStopTrackingTouch(SeekBar p1)
				{
					// TODO: Implement this method
				}
			});

		//btnInc.setOnClickListener(this);
		//btnDec.setOnClickListener(this);
	}

	public void setText(String str) {
		tvWord.setText(str);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
			case R.id.btnClose:
				i.onClose();
				dismiss();
				break;
			case R.id.btnPause:
				isPaused = ! isPaused;

				if (isPaused) {
					i.onPauseWordRunner();
					btnPause.setText(">");
				}
				else {
					i.onPlayWordRunner();
					btnPause.setText("||");
				}
				break;
				/*
				 case R.id.btnIncrease:
				 i.onIncreaseSpeed();
				 break;
				 case R.id.btnDecrease:
				 i.onDecreaseSpeed();
				 break;
				 */
		}
	}
}
