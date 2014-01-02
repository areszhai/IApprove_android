package com.futuo.iapprove.addressbook;

import android.content.Context;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;

public enum ABContactPhoneType {

	// mobile, office and others
	MOBILE(0), OFFICE(1), OTHERS(3);

	// phone type value and label
	private Integer phoneTypeValue;
	private String phoneTypeLabel;

	// private constructor
	private ABContactPhoneType(Integer value) {
		// get application context
		Context _appContext = CTApplication.getContext();

		// save phone type value and and initialize label
		phoneTypeValue = value;
		if (0 == value) {
			// mobile
			phoneTypeLabel = _appContext.getResources().getString(
					R.string.abc_mobilePhone_type_name);
		} else if (1 == value) {
			// office
			phoneTypeLabel = _appContext.getResources().getString(
					R.string.abc_officePhone_type_name);
		} else if (3 == value) {
			// others
			phoneTypeLabel = _appContext.getResources().getString(
					R.string.abc_othersPhone_type_name);
		}
	}

	public Integer getPhoneTypeValue() {
		return phoneTypeValue;
	}

	public String getPhoneTypeLabel() {
		return phoneTypeLabel;
	}

}
