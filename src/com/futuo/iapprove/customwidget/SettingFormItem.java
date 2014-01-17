package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;

public class SettingFormItem extends RelativeLayout {

	private static final String LOG_TAG = SettingFormItem.class
			.getCanonicalName();

	public SettingFormItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// initialize setting form item attributes
		initAttrs(context, attrs);
	}

	public SettingFormItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		// initialize setting form item attributes
		initAttrs(context, attrs);
	}

	public SettingFormItem(Context context) {
		super(context);

		// initialize setting form item attributes
		initAttrs(context, null);
	}

	// initialize setting form item attributes
	private void initAttrs(Context context, AttributeSet attrs) {
		// define setting form item typedArray and label
		TypedArray _typedArray = null;
		String _label = null;

		try {
			// get setting form item typedArray
			_typedArray = context.getTheme().obtainStyledAttributes(attrs,
					R.styleable.setting_form_item, 0, 0);

			// get setting form item label attributes
			_label = _typedArray
					.getString(R.styleable.setting_form_item_stfi_label);
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"Get setting form item label and clickable attributes error, exception massage = "
							+ e.getMessage());

			e.printStackTrace();
		} finally {
			// recycle setting form item typedArray
			if (null != _typedArray) {
				_typedArray.recycle();
			}
		}

		// inflate setting form item layout
		LayoutInflater.from(context).inflate(R.layout.setting_form_item_layout,
				this);

		// check setting form item label and set its text
		if (null != _label) {
			// set setting form item label textView text
			((TextView) findViewById(R.id.stfi_label_textView)).setText(_label);
		}

		// set setting form item clickable default
		setClickable(true);
	}

}
