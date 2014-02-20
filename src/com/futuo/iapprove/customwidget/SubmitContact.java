package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.addressbook.ABContactBean;
import com.richitec.commontoolkit.CTApplication;

public class SubmitContact extends FrameLayout {

	private static final String LOG_TAG = SubmitContact.class
			.getCanonicalName();

	// new approve application or to-do task submit contact
	private ABContactBean _mSubmitContact;

	// new approve application or to-do task submit contact name textView
	private TextView _mSubmitContactNameTextView;

	// new approve application or to-do task submit contact on long click
	// listener
	private OnLongClickListener _mSubmitContactOnLongClickListener;

	private SubmitContact(Context context) {
		super(context);

		// inflate new approve application or to-do task submit contact layout
		LayoutInflater.from(context).inflate(R.layout.submit_contact_layout,
				this);

		// get new approve application or to-do task submit contact name
		// textView
		_mSubmitContactNameTextView = (TextView) findViewById(R.id.naa6tdt_submit_contactName_textView);

		// set its on long click listener
		_mSubmitContactNameTextView
				.setOnLongClickListener(new SubmitContactNameTextViewOnLongClickListener());
	}

	private SubmitContact(Context context, ABContactBean submitContact) {
		this(context);

		// save new approve application or to-do task submit contact
		_mSubmitContact = submitContact;

		// set new approve application or to-do task submit contact name
		// textView text
		_mSubmitContactNameTextView.setText(submitContact.getEmployeeName());
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		super.setOnLongClickListener(l);

		// save submit contact on long click listener
		_mSubmitContactOnLongClickListener = l;
	}

	public ABContactBean getSubmitContact() {
		return _mSubmitContact;
	}

	// inner class
	// new approve application or to-do task submit contact name textView on
	// long click listener
	class SubmitContactNameTextViewOnLongClickListener implements
			OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			boolean _ret = false;

			// check submit contact on long click listener
			if (null != _mSubmitContactOnLongClickListener) {
				_ret = _mSubmitContactOnLongClickListener
						.onLongClick(SubmitContact.this);
			}

			return _ret;
		}

	}

	// generate new approve application or to-do task submit contact with
	// selected submit contact object
	public static SubmitContact generateNAA6TodoTaskSubmitContact(
			ABContactBean submitContact) {
		// define new approve application or to-do task submit contact
		SubmitContact _submitContact = null;

		// check new approve application or to-do task submit contact object
		if (null != submitContact) {
			// new new approve application or to-do task submit contact
			_submitContact = new SubmitContact(CTApplication.getContext(),
					submitContact);
		} else {
			Log.d(LOG_TAG,
					"Generate new new approve application or to-do task submit contact error, submit contact = "
							+ _submitContact);
		}

		return _submitContact;
	}

}
