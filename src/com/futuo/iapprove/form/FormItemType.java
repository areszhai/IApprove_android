package com.futuo.iapprove.form;

import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;

public enum FormItemType {

	// text edit text, text edit number, text area, date and spinner
	TEXTEDIT_TEXT(0), TEXTEDIT_NUMBER(1), TEXTAREA(2), DATE(3), SPINNER(4);

	private static final String LOG_TAG = FormItemType.class.getCanonicalName();

	// type value and name
	private Integer typeValue;
	private String typeName;

	// private constructor
	private FormItemType(Integer value) {
		// get application context
		Context _appContext = CTApplication.getContext();

		// save type value and initialize type name
		typeValue = value;
		if (0 == value) {
			typeName = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getEnterpriseFormInfoReqResp_item_textEditTextType);
		} else if (1 == value) {
			typeName = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getEnterpriseFormInfoReqResp_item_textEditNumberType);
		} else if (2 == value) {
			typeName = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getEnterpriseFormInfoReqResp_item_textAreaType);
		} else if (3 == value) {
			typeName = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getEnterpriseFormInfoReqResp_item_dateType);
		} else if (4 == value) {
			typeName = _appContext
					.getResources()
					.getString(
							R.string.rbgServer_getEnterpriseFormInfoReqResp_item_spinnerType);
		}
	}

	public Integer getValue() {
		return typeValue;
	}

	// get enterprise form item type with name
	public static final FormItemType getType(String name) {
		FormItemType _type = FormItemType.TEXTEDIT_TEXT;

		// check type name
		if (null != name) {
			if (TEXTEDIT_TEXT.typeName.equalsIgnoreCase(name)) {
				// text edit text
				_type = TEXTEDIT_TEXT;
			} else if (TEXTEDIT_NUMBER.typeName.equalsIgnoreCase(name)) {
				// text edit number
				_type = TEXTEDIT_NUMBER;
			} else if (TEXTAREA.typeName.equalsIgnoreCase(name)) {
				// text area
				_type = TEXTAREA;
			} else if (DATE.typeName.equalsIgnoreCase(name)) {
				// date
				_type = DATE;
			} else if (SPINNER.typeName.equalsIgnoreCase(name)) {
				// spinner
				_type = SPINNER;
			} else {
				Log.e(LOG_TAG, "Unrecognized enterprise form item name = "
						+ name + " then use the default text");
			}
		} else {
			Log.e(LOG_TAG, "Can't init enterprise form item type with name = "
					+ name + " then use the default text");
		}

		return _type;
	}

	// get enterprise form item type with value
	public static final FormItemType getType(Integer value) {
		FormItemType _type = FormItemType.TEXTEDIT_TEXT;

		// check type value
		if (null != value) {
			if (TEXTEDIT_TEXT.typeValue.intValue() == value.intValue()) {
				// text edit text
				_type = TEXTEDIT_TEXT;
			} else if (TEXTEDIT_NUMBER.typeValue.intValue() == value.intValue()) {
				// text edit number
				_type = TEXTEDIT_NUMBER;
			} else if (TEXTAREA.typeValue.intValue() == value.intValue()) {
				// text area
				_type = TEXTAREA;
			} else if (DATE.typeValue.intValue() == value.intValue()) {
				// date
				_type = DATE;
			} else if (SPINNER.typeValue.intValue() == value.intValue()) {
				// spinner
				_type = SPINNER;
			} else {
				Log.e(LOG_TAG, "Unrecognized enterprise form item value = "
						+ value + " then use the default text");
			}
		} else {
			Log.e(LOG_TAG, "Can't init enterprise form item type with value = "
					+ value + " then use the default text");
		}

		return _type;
	}

}
