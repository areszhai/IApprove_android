package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;

public class ABContactInfoFormItem extends RelativeLayout {

	// address book contact info form item label and info textView
	private TextView _mLabelTextView;
	private TextView _mInfoTextView;

	private ABContactInfoFormItem(Context context) {
		super(context);

		// inflate address book contact info form item layout
		LayoutInflater.from(context).inflate(
				R.layout.addressbook_contact_info_form_item_layout, this);

		// get address book contact info form item label and info textView
		_mLabelTextView = (TextView) findViewById(R.id.abcifi_label_textView);
		_mInfoTextView = (TextView) findViewById(R.id.abcifi_info_textView);
	}

	@Override
	public void setClickable(boolean clickable) {
		super.setClickable(clickable);

		// check clickable
		if (clickable) {
			// show address book contact info form item detail info arrow
			// imageView
			findViewById(R.id.abcifi_detailInfo_imageView).setVisibility(
					View.VISIBLE);

			// get and check address book contact info form item info
			String _info = _mInfoTextView.getText().toString();
			if (null != _info && !"".equalsIgnoreCase(_info)) {
				// get address book contact info form item info spannable string
				SpannableString _infoSpannableString = new SpannableString(
						_info);

				// set span
				_infoSpannableString.setSpan(new ForegroundColorSpan(
						getResources().getColor(R.color.light_blue)), 0,
						_infoSpannableString.length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				// set address book contact info form item info again
				_mInfoTextView.setText(_infoSpannableString);
			}
		}
	}

	@Override
	public void setOnClickListener(OnClickListener onClickListener) {
		// set clickable
		setClickable(true);

		super.setOnClickListener(onClickListener);
	}

	// get address book contact info form item info
	public String getInfo() {
		return _mInfoTextView.getText().toString();
	}

	// generate address book contact info form item with label and info
	public static ABContactInfoFormItem generateABContactInfoFormItem(
			String label, String info) {
		// new address book contact info form item
		ABContactInfoFormItem _newABContactInfoFormItem = new ABContactInfoFormItem(
				CTApplication.getContext());

		// set address book contact info form item label and info
		_newABContactInfoFormItem._mLabelTextView.setText(label);
		_newABContactInfoFormItem._mInfoTextView.setText(info);

		return _newABContactInfoFormItem;
	}

}
