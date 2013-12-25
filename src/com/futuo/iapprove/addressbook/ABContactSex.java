package com.futuo.iapprove.addressbook;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;

public enum ABContactSex {

	// male and female
	CLASSIFIED(0), MALE(1), FEMALE(2);

	private static final String LOG_TAG = ABContactSex.class.getCanonicalName();

	// sex value and icon drawable
	private Integer sexValue;
	private Drawable sexIconDrawable;

	// private constructor
	private ABContactSex(Integer value) {
		// get application context
		Context _appContext = CTApplication.getContext();

		// save sex value and initialize icon drawable
		sexValue = value;
		if (1 == value) {
			// male icon
			sexIconDrawable = _appContext.getResources().getDrawable(
					R.drawable.img_sex_male);
		} else if (2 == value) {
			// female icon
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

	// get address book contact sex with value
	public static final ABContactSex getSex(Integer value) {
		ABContactSex _sex = null;

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
				Log.e(LOG_TAG, "Unrecognized address book contact sex value = "
						+ value);
			}
		} else {
			Log.e(LOG_TAG, "Can't init address book contact sex with value = "
					+ value);
		}

		return _sex;
	}

}
