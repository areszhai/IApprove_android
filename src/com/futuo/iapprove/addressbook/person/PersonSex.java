package com.futuo.iapprove.addressbook.person;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;

public enum PersonSex {

	// male and female
	CLASSIFIED(-1), MALE(0), FEMALE(1);

	private static final String LOG_TAG = PersonSex.class.getCanonicalName();

	// sex rbgServer return value, value and icon drawable
	private Integer rbgServerRetValue;
	private Integer sexValue;
	private Drawable sexIconDrawable;

	// private constructor
	private PersonSex(Integer value) {
		// get application context
		Context _appContext = CTApplication.getContext();

		// save sex rbgServer return value, value and initialize icon drawable
		sexValue = value;
		if (-1 == value) {
			rbgServerRetValue = Integer
					.parseInt(_appContext
							.getResources()
							.getString(
									R.string.rbgServer_getEnterpriseABReqResp_employee_classifiedSex));
		} else if (0 == value) {
			rbgServerRetValue = Integer
					.parseInt(_appContext
							.getResources()
							.getString(
									R.string.rbgServer_getEnterpriseABReqResp_employee_maleSex));
			sexIconDrawable = _appContext.getResources().getDrawable(
					R.drawable.img_sex_male);
		} else if (1 == value) {
			rbgServerRetValue = Integer
					.parseInt(_appContext
							.getResources()
							.getString(
									R.string.rbgServer_getEnterpriseABReqResp_employee_femaleSex));
			sexIconDrawable = _appContext.getResources().getDrawable(
					R.drawable.img_sex_female);
		}
	}

	public Integer getValue() {
		return sexValue;
	}

	public Drawable getIcon() {
		return sexIconDrawable;
	}

	// get address book contact sex with rbgServer return value
	public static final PersonSex getSexWithRbgServerRetValue(
			Integer rbgServerRetValue) {
		PersonSex _sex = null;

		// check sex rbgServer return value
		if (null != rbgServerRetValue) {
			if (CLASSIFIED.rbgServerRetValue.intValue() == rbgServerRetValue
					.intValue()) {
				// classified
				_sex = CLASSIFIED;
			} else if (MALE.rbgServerRetValue.intValue() == rbgServerRetValue
					.intValue()) {
				// male
				_sex = MALE;
			} else if (FEMALE.rbgServerRetValue.intValue() == rbgServerRetValue
					.intValue()) {
				// female
				_sex = FEMALE;
			} else {
				Log.e(LOG_TAG,
						"Unrecognized person sex remote background server return value = "
								+ rbgServerRetValue);
			}
		} else {
			Log.e(LOG_TAG,
					"Can't init person sex with remote background server return value = "
							+ rbgServerRetValue);
		}

		return _sex;
	}

	// get address book contact sex with value
	public static final PersonSex getSex(Integer value) {
		PersonSex _sex = null;

		// check sex value
		if (null != value) {
			if (CLASSIFIED.sexValue.intValue() == value.intValue()) {
				// classified
				_sex = CLASSIFIED;
			} else if (MALE.sexValue.intValue() == value.intValue()) {
				// male
				_sex = MALE;
			} else if (FEMALE.sexValue.intValue() == value.intValue()) {
				// female
				_sex = FEMALE;
			} else {
				Log.e(LOG_TAG, "Unrecognized person sex value = " + value);
			}
		} else {
			Log.e(LOG_TAG, "Can't init person sex with value = " + value);
		}

		return _sex;
	}

}
