package com.futuo.iapprove.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class DateStringUtils {

	private static final String LOG_TAG = DateStringUtils.class
			.getCanonicalName();

	// short and long date format string
	private static final String SHORTDATEFORMATSTRING = "yyyy-MM-dd";
	private static final String LONGDATEFORMATSTRING = "yyyy-MM-dd HH:mm:ss";

	// short date string to date
	public static Date shortDateString2Date(String shortDateString) {
		return dateString2Date(shortDateString, SHORTDATEFORMATSTRING);
	}

	// long date string to date
	public static Date longDateString2Date(String longDateString) {
		return dateString2Date(longDateString, LONGDATEFORMATSTRING);
	}

	// date string to date
	private static Date dateString2Date(String dateString, String dateFormatter) {
		Date _date = null;

		// check date string
		if (null != dateString && !"".equalsIgnoreCase(dateString)) {
			// parse data with formatter and locale
			_date = new SimpleDateFormat(dateFormatter, Locale.getDefault())
					.parse(dateString, new ParsePosition(0));

			Log.d(LOG_TAG, "Convert date string = " + dateString
					+ " to date = " + _date);
		} else {
			Log.e(LOG_TAG, "Can't convert date string = " + dateString
					+ " to date");
		}

		return _date;
	}

}
