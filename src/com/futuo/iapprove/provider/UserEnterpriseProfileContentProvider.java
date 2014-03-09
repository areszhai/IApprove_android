package com.futuo.iapprove.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.futuo.iapprove.addressbook.person.PersonConstants;
import com.futuo.iapprove.provider.UserEnterpriseProfileContentProvider.EnterpriseProfiles.EnterpriseProfile;

public class UserEnterpriseProfileContentProvider extends
		LocalStorageContentProvider {

	private static final String LOG_TAG = UserEnterpriseProfileContentProvider.class
			.getCanonicalName();

	// register custom uri for uri matcher
	static {
		URI_MATCHER.addURI(EnterpriseProfiles.AUTHORITY, "enterpriseProfiles",
				UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILES);
		URI_MATCHER.addURI(EnterpriseProfiles.AUTHORITY, "enterpriseProfile",
				UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILE);
		URI_MATCHER.addURI(EnterpriseProfiles.AUTHORITY, "enterpriseProfile/#",
				UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILE_ID);
	}

	@Override
	public String getType(Uri uri) {
		String _contentType = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		case UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILES:
			_contentType = EnterpriseProfile.ENTERPRISEPROFILES_CONTENT_TYPE;
			break;

		case UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILE:
		case UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILE_ID:
			_contentType = EnterpriseProfile.ENTERPRISEPROFILE_CONTENT_TYPE;
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		return _contentType;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri _newInsertEnterpriseUri = null;

		// get local storage database writable instance
		SQLiteDatabase _lswDB = _mLocalStorageDBHelper.getWritableDatabase();

		// define insert table name
		String _insertTableName = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		case UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILES:
		case UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILE_ID:
			// nothing to do
			break;

		case UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILE:
			// set user enterprise profile table as insert table
			_insertTableName = EnterpriseProfiles.ENTERPRISE_PROFILES_TABLE;
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		Log.d(LOG_TAG, "Insert user enterprise profile with values = " + values);

		// insert user enterprise profile into its local storage table and
		// return new insert row id
		long _newInsertRowId = _lswDB.insert(_insertTableName,
				EnterpriseProfile._ID, values);

		// check the insert process result
		if (0 <= _newInsertRowId) {
			// update new insert enterprise uri
			_newInsertEnterpriseUri = ContentUris.withAppendedId(uri,
					_newInsertRowId);

			// notify data has been changed
			getContext().getContentResolver().notifyChange(
					_newInsertEnterpriseUri, null);
		} else {
			Log.d(LOG_TAG,
					"Insert user enterprise profile to local storage database error, values = "
							+ values);
		}

		return _newInsertEnterpriseUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor _queryCursor = null;

		// get local storage database readable instance
		SQLiteDatabase _lsrDB = _mLocalStorageDBHelper.getReadableDatabase();

		// check uri
		switch (URI_MATCHER.match(uri)) {
		case UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILES:
		case UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILE:
			// nothing to do
			break;

		case UserEnterpriseProfileTableAccessType.ENTERPRISEPROFILE_ID:
			// get query enterprise profile id and generate where condition
			String _where = EnterpriseProfile._ID + "="
					+ ContentUris.parseId(uri);

			// check and update selection
			if (null != selection && !"".equalsIgnoreCase(selection)) {
				selection += EnterpriseProfile._AND_SELECTION + _where;
			} else {
				selection = _where;
			}
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		Log.d(LOG_TAG, "Query user enterprise profile with selection = "
				+ selection);

		// query enterprise profiles from user enterprise profile table with
		// projection, selection and order
		_queryCursor = _lsrDB.query(
				EnterpriseProfiles.ENTERPRISE_PROFILES_TABLE, projection,
				selection, selectionArgs, null, null, sortOrder);

		// set notification with uri
		_queryCursor.setNotificationUri(getContext().getContentResolver(),
				EnterpriseProfile.ENTERPRISEPROFILES_NOTIFICATION_CONTENT_URI);

		return _queryCursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	// inner class
	// user enterprise profile table access type
	static class UserEnterpriseProfileTableAccessType {

		// user enterprise profile table access type
		private static final int ENTERPRISEPROFILES = 10;
		private static final int ENTERPRISEPROFILE = 11;
		private static final int ENTERPRISEPROFILE_ID = 12;

	}

	// user enterprise profiles
	public static final class EnterpriseProfiles {

		// user enterprise profile content provider authority
		private static final String AUTHORITY = UserEnterpriseProfileContentProvider.class
				.getCanonicalName();

		// user enterprise profile table name
		public static final String ENTERPRISE_PROFILES_TABLE = "ia_user_enterprise_profile";

		// inner class
		// user enterprise profile
		public static final class EnterpriseProfile implements
				SimpleBaseColumns {

			// user enterprise profile content provider process data columns
			public static final String NAME = PersonConstants.NAME;
			public static final String AVATAR = PersonConstants.AVATAR;
			public static final String SEX = PersonConstants.SEX;
			public static final String BIRTHDAY = PersonConstants.BIRTHDAY;
			public static final String DEPARTMENT = PersonConstants.DEPARTMENT;
			public static final String APPROVE_NUMBER = PersonConstants.APPROVE_NUMBER;
			public static final String MOBILE_PHONE = PersonConstants.MOBILE_PHONE;
			public static final String OFFICE_PHONE = PersonConstants.OFFICE_PHONE;
			public static final String EMAIL = PersonConstants.EMAIL;
			public static final String NOTE = PersonConstants.NOTE;

			public static final String ENTERPRISE_ID = PersonConstants.ENTERPRISE_ID;
			public static final String ENTERPRISE_ABBREVIATION = "enterpriseAbbreviation";

			// content uri
			private static final Uri ENTERPRISEPROFILES_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY);
			public static final Uri ENTERPRISEPROFILES_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + "/enterpriseProfiles");
			public static final Uri ENTERPRISEPROFILE_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + "/enterpriseProfile");

			// content type
			private static final String ENTERPRISEPROFILES_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ EnterpriseProfiles.class.getCanonicalName();
			private static final String ENTERPRISEPROFILE_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ EnterpriseProfiles.class.getCanonicalName();

			// user enterprise profiles condition
			public static final String USER_ENTERPRISEPROFILES_WITHLOGINNAME_CONDITION = APPROVE_NUMBER
					+ "=?";

			// user enterprise profile condition
			public static final String USER_ENTERPRISEPROFILE_WITHENTERPRISEID7LOGINNAME_CONDITION = ENTERPRISE_ID
					+ "=?" + _AND_SELECTION + APPROVE_NUMBER + "=?";

		}

	}

}
