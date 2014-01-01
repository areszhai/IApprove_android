package com.futuo.iapprove.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.futuo.iapprove.addressbook.ABContactPhoneType;
import com.futuo.iapprove.addressbook.person.PersonConstants;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.Employee;
import com.futuo.iapprove.provider.EnterpriseABContentProvider.Employees.EmployeeContactInfo;
import com.richitec.commontoolkit.utils.CommonUtils;

public class EnterpriseABContentProvider extends LocalStorageContentProvider {

	private static final String LOG_TAG = EnterpriseABContentProvider.class
			.getCanonicalName();

	// register custom uri for uri matcher
	static {
		URI_MATCHER.addURI(Employees.AUTHORITY, "employees",
				EnterpriseABTableAccessType.EMPLOYEES);
		URI_MATCHER.addURI(Employees.AUTHORITY, "employee",
				EnterpriseABTableAccessType.EMPLOYEE);
		URI_MATCHER.addURI(Employees.AUTHORITY, "employee/#",
				EnterpriseABTableAccessType.EMPLOYEE_ID);
		URI_MATCHER.addURI(Employees.AUTHORITY, "enterprise/#",
				EnterpriseABTableAccessType.EMPLOYEE_ENTERPRISEID);
	}

	@Override
	public String getType(Uri uri) {
		String _contentType = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		case EnterpriseABTableAccessType.EMPLOYEES:
			_contentType = Employee.EMPLOYEES_CONTENT_TYPE;
			break;

		case EnterpriseABTableAccessType.EMPLOYEE:
		case EnterpriseABTableAccessType.EMPLOYEE_ID:
		case EnterpriseABTableAccessType.EMPLOYEE_ENTERPRISEID:
			_contentType = Employee.EMPLOYEE_CONTENT_TYPE;
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		return _contentType;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri _newInsertEmployeeUri = null;

		// get local storage database writable instance
		SQLiteDatabase _lswDB = _mLocalStorageDBHelper.getWritableDatabase();

		// define insert table name
		String _insertTableName = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		case EnterpriseABTableAccessType.EMPLOYEES:
		case EnterpriseABTableAccessType.EMPLOYEE_ID:
		case EnterpriseABTableAccessType.EMPLOYEE_ENTERPRISEID:
			// nothing to do
			break;

		case EnterpriseABTableAccessType.EMPLOYEE:
			// set enterprise address book table as insert table
			_insertTableName = Employees.EMPLOYEES_TABLE;
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		Log.d(LOG_TAG, "Insert enterprise address book with values = " + values);

		// begin transaction
		_lswDB.beginTransaction();

		try {
			// get employee and its contact info content values list map
			Map<String, List<ContentValues>> _employeeAndContactInfoContentValuesMap = getEmployeeAndContactInfoContentValuesMap(values);

			// get employee content values
			ContentValues _employeeContentValues = _employeeAndContactInfoContentValuesMap
					.get(Employees.EMPLOYEES_TABLE).get(0);

			// insert got enterprise employee into enterprise address book table
			// and return new insert row id
			long _newInsertRowId = _lswDB.insert(_insertTableName,
					Employee._ID, _employeeContentValues);

			// check the insert process result
			if (0 <= _newInsertRowId) {
				for (ContentValues employeeContactInfoContentValues : _employeeAndContactInfoContentValuesMap
						.get(Employees.EMPLOYEE_CONTACTINFOS_TABLE)) {
					// put employee row id into employee contact info content
					// values
					employeeContactInfoContentValues
							.put(EmployeeContactInfo.EMPLOYEE_ROWID,
									_newInsertRowId);

					// insert got enterprise employee contact info into employee
					// contact info table and return new insert row id
					long __newInsertRowId = _lswDB.insert(
							Employees.EMPLOYEE_CONTACTINFOS_TABLE,
							EmployeeContactInfo._ID,
							employeeContactInfoContentValues);

					// check the insert process result
					if (0 > __newInsertRowId) {
						Log.e(LOG_TAG,
								"Insert enterprise employee contact info to local storage database error, values = "
										+ employeeContactInfoContentValues);
					}
				}

				// set transaction successful
				_lswDB.setTransactionSuccessful();

				// update new insert employee uri
				_newInsertEmployeeUri = ContentUris.withAppendedId(uri,
						_newInsertRowId);

				// notify data has been changed
				getContext().getContentResolver().notifyChange(
						_newInsertEmployeeUri, null);
			} else {
				Log.d(LOG_TAG,
						"Insert enterprise employee to local storage database error, values = "
								+ _employeeContentValues);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"Insert enterprise employee to local storage database error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} finally {
			// end transaction
			_lswDB.endTransaction();
		}

		return _newInsertEmployeeUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor _queryCursor = null;

		// get local storage database readable instance
		SQLiteDatabase _lsrDB = _mLocalStorageDBHelper.getReadableDatabase();

		// check uri
		switch (URI_MATCHER.match(uri)) {
		case EnterpriseABTableAccessType.EMPLOYEES:
		case EnterpriseABTableAccessType.EMPLOYEE:
			// nothing to do
			break;

		case EnterpriseABTableAccessType.EMPLOYEE_ID:
		case EnterpriseABTableAccessType.EMPLOYEE_ENTERPRISEID:
			// get query employee id or enterprise id and generate where
			// condition
			String _where = (EnterpriseABTableAccessType.EMPLOYEE_ID == URI_MATCHER
					.match(uri) ? Employee._ID : Employee.ENTERPRISE_ID)
					+ "="
					+ ContentUris.parseId(uri);

			// check and update selection
			if (null != selection && !"".equalsIgnoreCase(selection)) {
				selection += Employee._AND_SELECTION + _where;
			} else {
				selection = _where;
			}
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		Log.d(LOG_TAG, "Query enterprise address book with selection = "
				+ selection);

		// query employees from enterprise address book table with projection,
		// selection and order
		_queryCursor = _lsrDB.query(
				Employees.ENTERPRISE_EMPLOYEE_CONTACTINFO_VIEW, projection,
				selection, selectionArgs, null, null, sortOrder);

		// set notification with uri
		_queryCursor.setNotificationUri(getContext().getContentResolver(),
				Employee.EMPLOYEES_NOTIFICATION_CONTENT_URI);

		return _queryCursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int _updateNumber = 0;

		// get local storage database writable instance
		SQLiteDatabase _lswDB = _mLocalStorageDBHelper.getWritableDatabase();

		// define employee and its contact info selection
		String _employeeSelection = selection;
		String _employeeContactInfoSelection = selection;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		case EnterpriseABTableAccessType.EMPLOYEES:
		case EnterpriseABTableAccessType.EMPLOYEE:
			// nothing to do
			break;

		case EnterpriseABTableAccessType.EMPLOYEE_ID:
		case EnterpriseABTableAccessType.EMPLOYEE_ENTERPRISEID:
			// get employee id or enterprise id and generate where condition
			String _where = (EnterpriseABTableAccessType.EMPLOYEE_ID == URI_MATCHER
					.match(uri) ? Employee._ID : Employee.ENTERPRISE_ID)
					+ "="
					+ ContentUris.parseId(uri);

			// check and update selection
			if (null != selection && !"".equalsIgnoreCase(selection)) {
				_employeeSelection += Employee._AND_SELECTION + _where;
			} else {
				_employeeSelection = _where;
			}
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		Log.d(LOG_TAG, "Update enterprise address book with selection = "
				+ _employeeSelection + " and update values = " + values);

		// begin transaction
		_lswDB.beginTransaction();

		try {
			// get employee and its contact info content values list map
			Map<String, List<ContentValues>> _employeeAndContactInfoContentValuesMap = getEmployeeAndContactInfoContentValuesMap(values);

			// get employee content values
			ContentValues _employeeContentValues = _employeeAndContactInfoContentValuesMap
					.get(Employees.EMPLOYEES_TABLE).get(0);

			// update employees from enterprise address book table with
			// projection, selection and order
			_updateNumber = _lswDB.update(Employees.EMPLOYEES_TABLE,
					_employeeContentValues, _employeeSelection, selectionArgs);

			// check uri again
			switch (URI_MATCHER.match(uri)) {
			case EnterpriseABTableAccessType.EMPLOYEES:
			case EnterpriseABTableAccessType.EMPLOYEE:
			case EnterpriseABTableAccessType.EMPLOYEE_ENTERPRISEID:
				// nothing to do
				break;

			case EnterpriseABTableAccessType.EMPLOYEE_ID:
				// get employee id and generate where condition
				String _where = EmployeeContactInfo.EMPLOYEE_ROWID + "="
						+ ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					_employeeContactInfoSelection += EmployeeContactInfo._AND_SELECTION
							+ _where;
				} else {
					_employeeContactInfoSelection = _where;
				}
				break;

			default:
				throw new UnknownCPContentUriException(uri);
			}

			for (ContentValues employeeContactInfoContentValues : _employeeAndContactInfoContentValuesMap
					.get(Employees.EMPLOYEE_CONTACTINFOS_TABLE)) {

				Log.d(LOG_TAG,
						"Update enterprise address book contact info with selection = "
								+ _employeeContactInfoSelection);

				// update employee contact info from employee contact info table
				// with projection, selection and order
				_lswDB.update(Employees.EMPLOYEE_CONTACTINFOS_TABLE,
						employeeContactInfoContentValues,
						_employeeContactInfoSelection, selectionArgs);
			}

			// set transaction successful
			_lswDB.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"Update enterprise employee to local storage database error, exception message = "
							+ e.getMessage());

			e.printStackTrace();
		} finally {
			// end transaction
			_lswDB.endTransaction();
		}

		// notify data has been changed
		getContext().getContentResolver().notifyChange(uri, null);

		return _updateNumber;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int _deleteNumber = 0;

		// get local storage database writable instance
		SQLiteDatabase _lswDB = _mLocalStorageDBHelper.getWritableDatabase();

		// check uri
		switch (URI_MATCHER.match(uri)) {
		case EnterpriseABTableAccessType.EMPLOYEES:
		case EnterpriseABTableAccessType.EMPLOYEE:
			// nothing to do
			break;

		case EnterpriseABTableAccessType.EMPLOYEE_ID:
		case EnterpriseABTableAccessType.EMPLOYEE_ENTERPRISEID:
			// get employee id or enterprise id and generate where condition
			String _where = (EnterpriseABTableAccessType.EMPLOYEE_ID == URI_MATCHER
					.match(uri) ? Employee._ID : Employee.ENTERPRISE_ID)
					+ "="
					+ ContentUris.parseId(uri);

			// check and update selection
			if (null != selection && !"".equalsIgnoreCase(selection)) {
				selection += Employee._AND_SELECTION + _where;
			} else {
				selection = _where;
			}
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		Log.d(LOG_TAG, "Delete enterprise address book with selection = "
				+ selection);

		// delete employees from enterprise address book table with projection,
		// selection and order
		_deleteNumber = _lswDB.delete(Employees.EMPLOYEES_TABLE, selection,
				selectionArgs);

		// notify data has been changed
		getContext().getContentResolver().notifyChange(uri, null);

		return _deleteNumber;
	}

	// get employee and its contact info content values list map with given
	// content values
	@SuppressWarnings("unchecked")
	private Map<String, List<ContentValues>> getEmployeeAndContactInfoContentValuesMap(
			ContentValues values) {
		Map<String, List<ContentValues>> _employeeAndContactInfoContentValuesMap = new HashMap<String, List<ContentValues>>();

		// check given content values
		if (null != values) {
			// define employee and its contact info content values list
			ContentValues _employeeContentValues = new ContentValues(values);
			List<ContentValues> _employeeContactInfoContentValuesList = new ArrayList<ContentValues>();

			for (String contentValuesKey : values.keySet()) {
				// check values
				if (Employee.MOBILE_PHONE.equalsIgnoreCase(contentValuesKey)
						|| Employee.OFFICE_PHONE
								.equalsIgnoreCase(contentValuesKey)
						|| Employee.EMAIL.equalsIgnoreCase(contentValuesKey)) {
					// define the employee contact info content values
					ContentValues _employeeContactInfoContentValues = new ContentValues();

					// generate employee contact info content values
					// put employee contact info type into employee contact info
					// content values
					if (Employee.MOBILE_PHONE
							.equalsIgnoreCase(contentValuesKey)) {
						// mobile phone
						_employeeContactInfoContentValues.put(
								EmployeeContactInfo.TYPE,
								EmployeeContactInfo.MOBILEPHONE_TYPE);
					} else if (Employee.OFFICE_PHONE
							.equalsIgnoreCase(contentValuesKey)) {
						// office phone
						_employeeContactInfoContentValues.put(
								EmployeeContactInfo.TYPE,
								EmployeeContactInfo.OFFICEPHONE_TYPE);
					} else if (Employee.EMAIL
							.equalsIgnoreCase(contentValuesKey)) {
						// email
						_employeeContactInfoContentValues.put(
								EmployeeContactInfo.TYPE,
								EmployeeContactInfo.EMAIL_TYPE);
					}

					// put employee contact info in to employee contact info
					// content values
					_employeeContactInfoContentValues.put(
							EmployeeContactInfo.INFO,
							values.getAsString(contentValuesKey));

					// add employee contact info content values to list
					_employeeContactInfoContentValuesList
							.add(_employeeContactInfoContentValues);

					// remove the content value from employee content values
					_employeeContentValues.remove(contentValuesKey);
				}
			}

			// put employee content values and its contact info content values
			// list into map using its table name as key
			_employeeAndContactInfoContentValuesMap
					.put(Employees.EMPLOYEES_TABLE,
							(List<ContentValues>) CommonUtils
									.array2List(new ContentValues[] { _employeeContentValues }));
			_employeeAndContactInfoContentValuesMap.put(
					Employees.EMPLOYEE_CONTACTINFOS_TABLE,
					_employeeContactInfoContentValuesList);
		}

		return _employeeAndContactInfoContentValuesMap;
	}

	// inner class
	// enterprise address book table access type
	static class EnterpriseABTableAccessType {

		// enterprise address book table access type
		private static final int EMPLOYEES = 30;
		private static final int EMPLOYEE = 31;
		private static final int EMPLOYEE_ID = 32;
		private static final int EMPLOYEE_ENTERPRISEID = 33;

	}

	// enterprise address book employees
	public static final class Employees {

		// enterprise address book content provider authority
		private static final String AUTHORITY = EnterpriseABContentProvider.class
				.getCanonicalName();

		// enterprise address book and employee contact info table name
		public static final String EMPLOYEES_TABLE = "ia_enterprise_addressbook";
		public static final String EMPLOYEE_CONTACTINFOS_TABLE = "ia_employee_contactinfo";

		// enterprise employee contact info view
		public static final String ENTERPRISE_EMPLOYEE_CONTACTINFO_VIEW = "ia_enterprise_employee_contactinfo_view";

		// inner class
		// enterprise address book employee
		public static final class Employee implements SimpleBaseColumns {

			// enterprise address book content provider process data columns
			public static final String USER_ID = "userId";
			public static final String ENTERPRISE_ID = PersonConstants.ENTERPRISE_ID;
			public static final String NAME = PersonConstants.NAME;
			public static final String AVATAR = PersonConstants.AVATAR;
			public static final String SEX = PersonConstants.SEX;
			public static final String NICKNAME = "nickname";
			public static final String BIRTHDAY = PersonConstants.BIRTHDAY;
			public static final String DEPARTMENT = PersonConstants.DEPARTMENT;
			public static final String APPROVE_NUMBER = PersonConstants.APPROVE_NUMBER;
			public static final String NOTE = PersonConstants.NOTE;
			public static final String FREQUENCY = "frequency";

			public static final String MOBILE_PHONE = PersonConstants.MOBILE_PHONE;
			public static final String OFFICE_PHONE = PersonConstants.OFFICE_PHONE;
			public static final String EMAIL = PersonConstants.EMAIL;

			// content uri
			private static final Uri EMPLOYEES_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY);
			public static final Uri EMPLOYEES_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + "/employees");
			public static final Uri EMPLOYEE_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + "/employee");
			public static final Uri ENTERPRISE_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + "/enterprise");

			// content type
			private static final String EMPLOYEES_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ Employees.class.getCanonicalName();
			private static final String EMPLOYEE_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ Employees.class.getCanonicalName();

		}

		// employee contact info
		static final class EmployeeContactInfo implements SimpleBaseColumns {

			// employee contact info data columns
			public static final String EMPLOYEE_ROWID = "employeeRowId";
			public static final String TYPE = "type";
			public static final String INFO = "info";

			public static final Integer MOBILEPHONE_TYPE = ABContactPhoneType.MOBILE
					.getPhoneTypeValue();
			public static final Integer OFFICEPHONE_TYPE = ABContactPhoneType.OFFICE
					.getPhoneTypeValue();
			public static final Integer EMAIL_TYPE = 10;

		}

	}

}
