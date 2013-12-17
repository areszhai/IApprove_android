package com.futuo.iapprove.addressbook;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.futuo.iapprove.R;
import com.richitec.commontoolkit.CTApplication;

public enum ABContactSex {

	// male and female
	MALE(0), FEMALE(1);

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
		sexIconDrawable = null == value
				|| (null != value && 0 == value.intValue()) ? _appContext
				.getResources().getDrawable(R.drawable.img_sex_male)
				: _appContext.getResources().getDrawable(
						R.drawable.img_sex_female);
	}

	public Integer getValue() {
		return sexValue;
	}

	public Drawable getIcon() {
		return sexIconDrawable;
	}

	// get address book contact sex with value
	public static final ABContactSex getSex(Integer value) {
		ABContactSex _sex = MALE;

		// check sex value
		if (null != value) {
			if (FEMALE.sexValue == value) {
				// female
				_sex = FEMALE;
			}
		} else {
			Log.e(LOG_TAG, "Can't init address book contact sex with value = "
					+ value);
		}

		return _sex;
	}

}
