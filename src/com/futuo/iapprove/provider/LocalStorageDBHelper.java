package com.futuo.iapprove.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormTypes;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.Forms;

public class LocalStorageDBHelper extends SQLiteOpenHelper {

	private static final String LOG_TAG = LocalStorageDBHelper.class
			.getCanonicalName();

	// local storage database name
	private static final String LOCALSTORAGE_DBNAME = "iApprove_localStorage.db3";

	// context
	private Context _mContext;

	public LocalStorageDBHelper(Context context, int version) {
		// initialize local storage database helper
		super(context, LOCALSTORAGE_DBNAME, null, version);

		// save context
		_mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "Initialize local storage database");

		// create enterprise address book and employee contact info table
		db.execSQL(getSqlStatementFromAssets(Employees.EMPLOYEES_TABLE));
		db.execSQL(getSqlStatementFromAssets(Employees.EMPLOYEE_CONTACTINFOS_TABLE));

		// create enterprise employee contact info view
		db.execSQL(getSqlStatementFromAssets(Employees.ENTERPRISE_EMPLOYEE_CONTACTINFO_VIEW));

		// create enterprise form type, form and info table
		db.execSQL(getSqlStatementFromAssets(FormTypes.FORMTYPES_TABLE));
		db.execSQL(getSqlStatementFromAssets(Forms.FORMS_TABLE));
		db.execSQL(getSqlStatementFromAssets(FormItems.FORMITEMS_TABLE));
		db.execSQL(getSqlStatementFromAssets(FormItems.FORMITEM_SELECTORCONTENTS_TABLE));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(LOG_TAG, "SQLite database = " + db + " upgrade, old version = "
				+ oldVersion + " and new version = " + newVersion);

		//
	}

	// get sql statement from assets
	private String getSqlStatementFromAssets(String sqlFileName) {
		// define sql statement and file input stream
		String _sqlStatement = "";
		InputStreamReader _isReader;

		try {
			// check sql file name
			if (!sqlFileName.endsWith(".sql")) {
				sqlFileName += ".sql";
			}

			// open assets file
			_isReader = new InputStreamReader(_mContext.getResources()
					.getAssets().open(sqlFileName));

			// read a line
			BufferedReader _bufReader = new BufferedReader(_isReader);
			String _line = "";

			while (null != (_line = _bufReader.readLine())) {
				_sqlStatement += _line;
			}
		} catch (IOException e) {
			e.printStackTrace();

			Log.e(LOG_TAG,
					"Get sql statement from assets file error, exception message = "
							+ e.getMessage());
		}

		return _sqlStatement;
	}

	// inner class
	// local storage data dirty type
	public enum LocalStorageDataDirtyType {

		NORMAL, DELETE

	}

}
