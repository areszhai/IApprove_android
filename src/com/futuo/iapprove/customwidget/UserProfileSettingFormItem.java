package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;

public class UserProfileSettingFormItem extends LinearLayout {

	private static final String LOG_TAG = UserProfileSettingFormItem.class
			.getCanonicalName();

	// user profile setting form item info textView
	private TextView _mInfoTextView;

	public UserProfileSettingFormItem(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		// initialize user profile setting form item attributes
		initAttrs(context, attrs);
	}

	public UserProfileSettingFormItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		// initialize user profile setting form item attributes
		initAttrs(context, attrs);
	}

	public UserProfileSettingFormItem(Context context) {
		super(context);

		// initialize user profile setting form item attributes
		initAttrs(context, null);
	}

	// initialize user profile setting form item attributes
	private void initAttrs(Context context, AttributeSet attrs) {
		// define user profile setting form item typedArray, label, clickable
		// and single line
		TypedArray _typedArray = null;
		String _label = null;
		Boolean _clickable = null;
		Boolean _singleLine = null;

		try {
			// get user profile setting form item typedArray
			_typedArray = context.getTheme().obtainStyledAttributes(attrs,
					R.styleable.user_profile_setting_form_item, 0, 0);

			// get user profile setting form item label, clickable and single
			// line attributes
			_label = _typedArray
					.getString(R.styleable.user_profile_setting_form_item_upstfi_label);
			_clickable = _typedArray
					.getBoolean(
							R.styleable.user_profile_setting_form_item_upstfi_clickable,
							false);
			_singleLine = _typedArray
					.getBoolean(
							R.styleable.user_profile_setting_form_item_upstfi_singleline,
							true);
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"Get user profile setting form item label and clickable attributes error, exception massage = "
							+ e.getMessage());

			e.printStackTrace();
		} finally {
			// recycle user profile setting form item typedArray
			if (null != _typedArray) {
				_typedArray.recycle();
			}
		}

		// inflate user profile setting form item layout
		LayoutInflater.from(context).inflate(
				R.layout.userprofile_setting_form_item_layout, this);

		// check user profile setting form item label and set its text
		if (null != _label) {
			// set user profile setting form item label textView text
			((TextView) findViewById(R.id.upstfi_label_textView))
					.setText(_label);
		}

		// check user profile setting form item clickable then set its clickable
		// and show indicator imageView
		if (_clickable) {
			setClickable(true);
			((ImageView) findViewById(R.id.upstfi_indicator_imageView))
					.setVisibility(View.VISIBLE);
		}

		// get user profile setting form item info textView
		_mInfoTextView = (TextView) findViewById(R.id.upstfi_info_textView);

		// check user profile setting form item single line and set its info
		// textView single line and ellipsize
		if (_singleLine) {
			_mInfoTextView.setSingleLine(true);
			_mInfoTextView.setEllipsize(TruncateAt.END);
		}
	}

	public String getInfo() {
		return _mInfoTextView.getText().toString();
	}

	public void setInfo(String info) {
		// check info
		if (null != info) {
			_mInfoTextView.setText(info);
		}
	}

}
