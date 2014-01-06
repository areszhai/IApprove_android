package com.futuo.iapprove.customwidget;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futuo.iapprove.R;
import com.futuo.iapprove.form.FormItemBean;
import com.futuo.iapprove.form.FormItemType;
import com.richitec.commontoolkit.CTApplication;

public class EnterpriseFormItemFormItem extends LinearLayout {

	private static final String LOG_TAG = EnterpriseFormItemFormItem.class
			.getCanonicalName();

	// enterprise form item object
	private FormItemBean _mFormItem;

	// enterprise form item form item label and info textView
	private TextView _mLabelTextView;
	private TextView _mInfoTextView;

	private EnterpriseFormItemFormItem(Context context) {
		super(context);

		// inflate enterprise form item form item layout
		LayoutInflater.from(context).inflate(
				R.layout.enterprise_formitem_form_item_layout, this);
	}

	private EnterpriseFormItemFormItem(Context context, FormItemBean formItem) {
		this(context);

		// save enterprise form item object
		_mFormItem = formItem;

		// get enterprise form item form item label and info textView
		_mLabelTextView = (TextView) findViewById(R.id.efifi_label_textView);
		_mInfoTextView = (TextView) findViewById(R.id.efifi_info_textView);
	}

	@Override
	public void setOnClickListener(OnClickListener onClickListener) {
		// set clickable
		setClickable(true);

		super.setOnClickListener(onClickListener);
	}

	// add info textView text changed listener
	public void addTextChangedListener(
			EnterpriseFormItemInfoTextWatcher textChangedWatcher) {
		// check text changed watcher
		if (null != textChangedWatcher) {
			// add info textView text changed watcher
			_mInfoTextView
					.addTextChangedListener(new EnterpriseFormItemFormItemInfoTextViewTextWatcher(
							textChangedWatcher));
		} else {
			Log.e(LOG_TAG,
					"Enterprise form item form item info textView text changed watcher is null");
		}
	}

	public FormItemBean getFormItem() {
		return _mFormItem;
	}

	public void setFormItem(FormItemBean formItem) {
		_mFormItem = formItem;

		// check enterprise form item type
		if (FormItemType.TEXTAREA != formItem.getItemType()) {
			// set enterprise form item form item info textView single line and
			// ellipsize is end
			_mInfoTextView.setSingleLine();
			_mInfoTextView.setEllipsize(TruncateAt.END);
		}

		// check enterprise form item must write flag and formula
		if (formItem.mustWrite() && null == formItem.getFormula()) {

			// set enterprise form item form item label spannable string
			_mLabelTextView
					.setText(getFormItemFormItemLabelSpannableString(_mFormItem
							.getItemName()));
		} else {
			// set enterprise form item form item label string
			_mLabelTextView.setText(formItem.getItemName());
		}
	}

	// get enterprise form item form item info
	public String getInfo() {
		return _mInfoTextView.getText().toString();
	}

	// set enterprise form item form item info
	public void setInfo(String info) {
		// check info
		if (null != info) {
			// set enterprise form item form item info
			_mInfoTextView.setText(info);
		}
	}

	// get user enterprise form item form item label spannable string with form
	// item name
	private SpannableStringBuilder getFormItemFormItemLabelSpannableString(
			String formItemName) {
		// define user enterprise form item form item label spannable string
		// builder
		SpannableStringBuilder _formItemLabelSpannableStringBuilder = new SpannableStringBuilder(
				formItemName);

		// append must write flag
		_formItemLabelSpannableStringBuilder.append("*");

		// set span
		_formItemLabelSpannableStringBuilder.setSpan(new ForegroundColorSpan(
				Color.RED), _formItemLabelSpannableStringBuilder.length() - 1,
				_formItemLabelSpannableStringBuilder.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		return _formItemLabelSpannableStringBuilder;
	}

	// generate enterprise form item form item with form item object
	public static EnterpriseFormItemFormItem generateEnterpriseFormItemFormItem(
			FormItemBean formItem) {
		// define enterprise form item form item
		EnterpriseFormItemFormItem _newEnterpriseFormItemFormItem = null;

		// check form item
		if (null != formItem) {
			// new enterprise form item form item
			_newEnterpriseFormItemFormItem = new EnterpriseFormItemFormItem(
					CTApplication.getContext(), formItem);

			// check enterprise form item type
			if (FormItemType.TEXTAREA != formItem.getItemType()) {
				// set enterprise form item form item info textView single line
				// and ellipsize is end
				_newEnterpriseFormItemFormItem._mInfoTextView.setSingleLine();
				_newEnterpriseFormItemFormItem._mInfoTextView
						.setEllipsize(TruncateAt.END);
			}

			// check enterprise form item must write flag and formula
			if (formItem.mustWrite() && null == formItem.getFormula()) {
				// set enterprise form item form item label spannable string
				_newEnterpriseFormItemFormItem._mLabelTextView
						.setText(_newEnterpriseFormItemFormItem
								.getFormItemFormItemLabelSpannableString(_newEnterpriseFormItemFormItem._mFormItem
										.getItemName()));

				// set enterprise form item form item info textView hint
				_newEnterpriseFormItemFormItem._mInfoTextView
						.setHint(CTApplication.getContext().getResources()
								.getString(R.string.naa_formItem_info_notWrite));
			} else {
				// set enterprise form item form item label string
				_newEnterpriseFormItemFormItem._mLabelTextView.setText(formItem
						.getItemName());
			}
		} else {
			Log.d(LOG_TAG,
					"Generate new enterprise form item form item error, form item = "
							+ formItem);
		}

		return _newEnterpriseFormItemFormItem;
	}

	// inner class
	// enterprise form item info text watcher interface
	public static interface EnterpriseFormItemInfoTextWatcher {

		// after text changed
		public void afterTextChanged(
				EnterpriseFormItemFormItem enterpriseFormItemFormItem,
				Editable s);

		// before text changed
		public void beforeTextChanged(
				EnterpriseFormItemFormItem enterpriseFormItemFormItem,
				CharSequence s, int start, int count, int after);

		// on text changed
		public void onTextChanged(
				EnterpriseFormItemFormItem enterpriseFormItemFormItem,
				CharSequence s, int start, int before, int count);

	}

	// enterprise form item form item info textView text changed watcher
	class EnterpriseFormItemFormItemInfoTextViewTextWatcher implements
			TextWatcher {

		// enterprise form item info text watcher
		private EnterpriseFormItemInfoTextWatcher _mEnterpriseFormItemInfoTextWatcher;

		public EnterpriseFormItemFormItemInfoTextViewTextWatcher(
				EnterpriseFormItemInfoTextWatcher enterpriseFormItemInfoTextWatcher) {
			super();

			// save enterprise form item info text watcher
			_mEnterpriseFormItemInfoTextWatcher = enterpriseFormItemInfoTextWatcher;
		}

		@Override
		public void afterTextChanged(Editable s) {
			_mEnterpriseFormItemInfoTextWatcher.afterTextChanged(
					EnterpriseFormItemFormItem.this, s);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			_mEnterpriseFormItemInfoTextWatcher.beforeTextChanged(
					EnterpriseFormItemFormItem.this, s, start, count, after);
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			_mEnterpriseFormItemInfoTextWatcher.onTextChanged(
					EnterpriseFormItemFormItem.this, s, start, before, count);
		}

	}

}
