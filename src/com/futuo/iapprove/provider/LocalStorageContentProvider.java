package com.futuo.iapprove.provider;

import android.content.ContentProvider;
import android.content.UriMatcher;

public abstract class LocalStorageContentProvider extends ContentProvider {

	// uri matcher
	protected static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	// local storage database helper
	protected LocalStorageDBHelper _mLocalStorageDBHelper;

	@Override
	public boolean onCreate() {
		// get local storage database helper
		_mLocalStorageDBHelper = new LocalStorageDBHelper(this.getContext(), 1);

		return true;
	}

}
