package com.futuo.iapprove.assistant;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.futuo.iapprove.R;
import com.futuo.iapprove.customwidget.IApproveBarButtonItem;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;

public class FeedbackActivity extends IApproveNavigationActivity {

	private static final String LOG_TAG = FeedbackActivity.class
			.getCanonicalName();

	// input method manager
	private InputMethodManager _mInputMethodManager;

	// feedback input editText
	private EditText _mInputEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.feedback_activity_layout);

		// get input method manager
		_mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// set subViews
		// set title
		setTitle(R.string.feedback_nav_title);

		// set send feedback bar button item as right bar button item
		setRightBarButtonItem(new IApproveBarButtonItem(this,
				R.string.feedback_send_barButtonItem_title,
				new SendFeedbackBarBtnItemOnClickListener()));

		// get feedback input editText
		_mInputEditText = (EditText) findViewById(R.id.feedback_input_editText);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// set feedback input editText as focus
		if (null != _mInputEditText) {
			// set feedback input editText focusable
			_mInputEditText.setFocusable(true);
			_mInputEditText.setFocusableInTouchMode(true);
			_mInputEditText.requestFocus();

			// show soft input after 250 milliseconds
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					_mInputMethodManager.showSoftInput(_mInputEditText, 0);
				}

			}, 250);
		}
	}

	@Override
	protected void onBackBarButtonItemClick(View backBarBtnItem) {
		// hide soft input
		_mInputMethodManager.hideSoftInputFromWindow(
				backBarBtnItem.getWindowToken(), 0);

		super.onBackBarButtonItemClick(backBarBtnItem);
	}

	// inner class
	// send feedback bar button item on click listener
	class SendFeedbackBarBtnItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "Send feedback ="
					+ _mInputEditText.getText().toString());

			// hide soft input
			_mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

			// test by ares
			//

			// finish feedback activity
			finish();
		}

	}

}
