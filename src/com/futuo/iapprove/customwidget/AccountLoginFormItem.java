package com.futuo.iapprove.customwidget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;

public class AccountLoginFormItem extends LinearLayout {

	private static final String LOG_TAG = AccountLoginFormItem.class
			.getCanonicalName();

	// account login form item label, input editText hint and type
	private String _mLabel;
	private String _mInputEditTextHint;
	private String _mInputEditTextType;

	// account login form item input editText
	private EditText _mInputEditText;

	public AccountLoginFormItem(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		// init account login form item
		init(context, attrs);
	}

	public AccountLoginFormItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		// init account login form item
		init(context, attrs);
	}

	public AccountLoginFormItem(Context context) {
		super(context);

		// init account login form item
		init(context, null);
	}

	// initialize account login form item
	private void init(Context context, AttributeSet attrs) {
		// define account login form item typedArray
		TypedArray _typedArray = null;

		try {
			// get account login form item typedArray
			_typedArray = context.getTheme().obtainStyledAttributes(attrs,
					R.styleable.account_login_form_item, 0, 0);

			// get account login form item label, input editText hint and input
			// type attributes
			_mLabel = _typedArray
					.getString(R.styleable.account_login_form_item_label);
			_mInputEditTextHint = _typedArray
					.getString(R.styleable.account_login_form_item_inputEditTextHint);
			_mInputEditTextType = _typedArray
					.getString(R.styleable.account_login_form_item_inputEditTextType);
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"Get account login form item label, input editText hint and input type attributes error, exception massage = "
							+ e.getMessage());

			e.printStackTrace();
		} finally {
			// recycle account login form item typedArray
			if (null != _typedArray) {
				_typedArray.recycle();
			}
		}

		// inflate account login form item layout
		LayoutInflater.from(context).inflate(
				R.layout.account_login_form_item_layout, this);

		// check account login form item label and set its text
		if (null != _mLabel) {
			// set account login form item label textView text
			((TextView) findViewById(R.id.alfi_label_textView))
					.setText(_mLabel);
		}

		// get account login form item input editText
		_mInputEditText = (EditText) findViewById(R.id.alfi_input_editText);

		// check account login form item input editText hint and set its hint
		if (null != _mInputEditTextHint) {
			// set account login form item input editText hint
			_mInputEditText.setHint(_mInputEditTextHint);
		}

		// check account login form item input editText input type and set its
		// type
		if (null != _mInputEditTextType) {
			// set account login form item input editText input type
			if ("phone".equalsIgnoreCase(_mInputEditTextType)) {
				_mInputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
			} else if ("textPassword".equalsIgnoreCase(_mInputEditTextType)) {
				_mInputEditText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			} else {
				Log.w(LOG_TAG, _mInputEditTextType + " setting not implement");

				_mInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
			}
		}
	}

	public String getInputEditText() {
		return _mInputEditText.getText().toString();
	}

	public void setInputEditText(String text) {
		// check text
		if (null != text) {
			_mInputEditText.setText(text);
		}
	}

	// add input editText text changed listener
	public void addTextChangedListener(
			AccountLoginFormItemInputEditTextTextWatcher textChangedWatcher) {
		// check text changed watcher
		if (null != textChangedWatcher) {
			// add user input editText text changed watcher
			_mInputEditText
					.addTextChangedListener(new AccountLoginUserInputEditTextTextWatcher(
							textChangedWatcher));
		} else {
			Log.e(LOG_TAG,
					"Account login form item input editText text changed watcher is null");
		}
	}

	// set as focus
	public void setAsFocus() {
		// set input editText focusable
		_mInputEditText.setFocusable(true);
		_mInputEditText.setFocusableInTouchMode(true);
		_mInputEditText.requestFocus();

		// show soft input after 250 milliseconds
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				((InputMethodManager) _mInputEditText.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.showSoftInput(_mInputEditText, 0);
			}
		}, 250);
	}

	// inner class
	// account login form item input editText text watcher interface
	public static interface AccountLoginFormItemInputEditTextTextWatcher {

		// after text changed
		public void afterTextChanged(AccountLoginFormItem accountLoginFormItem,
				Editable s);

		// before text changed
		public void beforeTextChanged(
				AccountLoginFormItem accountLoginFormItem, CharSequence s,
				int start, int count, int after);

		// on text changed
		public void onTextChanged(AccountLoginFormItem accountLoginFormItem,
				CharSequence s, int start, int before, int count);

	}

	// account login form item input editText text changed watcher
	class AccountLoginUserInputEditTextTextWatcher implements TextWatcher {

		// account login form item input editText text watcher
		private AccountLoginFormItemInputEditTextTextWatcher _mAccountLoginFormItemInputEditTextTextWatcher;

		public AccountLoginUserInputEditTextTextWatcher(
				AccountLoginFormItemInputEditTextTextWatcher accountLoginFormItemInputEditTextTextWatcher) {
			super();

			// save account login form item input editText text watcher
			_mAccountLoginFormItemInputEditTextTextWatcher = accountLoginFormItemInputEditTextTextWatcher;
		}

		@Override
		public void afterTextChanged(Editable s) {
			_mAccountLoginFormItemInputEditTextTextWatcher.afterTextChanged(
					AccountLoginFormItem.this, s);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			_mAccountLoginFormItemInputEditTextTextWatcher.beforeTextChanged(
					AccountLoginFormItem.this, s, start, count, after);
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			_mAccountLoginFormItemInputEditTextTextWatcher.onTextChanged(
					AccountLoginFormItem.this, s, start, before, count);
		}

	}

}
