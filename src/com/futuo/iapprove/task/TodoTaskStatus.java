package com.futuo.iapprove.task;

import android.util.Log;

public enum TodoTaskStatus {

	// unread and read
	UNREAD(0), READ(1);

	private static final String LOG_TAG = TodoTaskStatus.class
			.getCanonicalName();

	// status value
	private Integer statusValue;

	// private constructor
	private TodoTaskStatus(Integer value) {
		// save status value
		statusValue = value;
	}

	public Integer getValue() {
		return statusValue;
	}

	// get user enterprise to-do list task status with value
	public static final TodoTaskStatus getStatus(Integer value) {
		TodoTaskStatus _status = TodoTaskStatus.UNREAD;

		// check status value
		if (null != value) {
			if (UNREAD.statusValue.intValue() == value.intValue()) {
				// unread
				_status = UNREAD;
			} else if (READ.statusValue.intValue() == value.intValue()) {
				// read
				_status = READ;
			} else {
				Log.e(LOG_TAG,
						"Unrecognized user enterprise to-do list task status value = "
								+ value + " then use the default unread");
			}
		} else {
			Log.e(LOG_TAG,
					"Can't init user enterprise to-do list task status with value = "
							+ value + " then use the default unread");
		}

		return _status;
	}

}
