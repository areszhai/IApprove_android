package com.futuo.iapprove.provider;

import android.net.Uri;
import android.util.Log;

public class UnknownCPContentUriException extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 397600385464144727L;

	private static final String LOG_TAG = UnknownCPContentUriException.class
			.getCanonicalName();

	// exception detail message
	private static String _mExceptionDetailMsg;

	public UnknownCPContentUriException(Uri contentUri) {
		super(_mExceptionDetailMsg = "Unknown content provider content uri = "
				+ contentUri);

		Log.e(LOG_TAG, _mExceptionDetailMsg);
	}

}
