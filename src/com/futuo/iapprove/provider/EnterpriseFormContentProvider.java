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
import android.provider.BaseColumns;
import android.util.Log;

import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems.FormItem;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems.FormItemSelectorContent;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormTypes.FormType;
import com.futuo.iapprove.provider.EnterpriseFormContentProvider.Forms.Form;
import com.richitec.commontoolkit.utils.CommonUtils;

public class EnterpriseFormContentProvider extends LocalStorageContentProvider {

	private static final String LOG_TAG = EnterpriseFormContentProvider.class
			.getCanonicalName();

	// enterprise form content provider authority
	private static final String AUTHORITY = EnterpriseFormContentProvider.class
			.getCanonicalName();

	// register custom uri for uri matcher
	static {
		// form type
		URI_MATCHER.addURI(AUTHORITY, FormTypes.PATH + "/formTypes",
				EnterpriseFormTypeTableAccessType.FORMTYPES);
		URI_MATCHER.addURI(AUTHORITY, FormTypes.PATH + "/formType",
				EnterpriseFormTypeTableAccessType.FORMTYPE);
		URI_MATCHER.addURI(AUTHORITY, FormTypes.PATH + "/formType/#",
				EnterpriseFormTypeTableAccessType.FORMTYPE_ID);
		URI_MATCHER.addURI(AUTHORITY, FormTypes.PATH + "/enterprise/#",
				EnterpriseFormTypeTableAccessType.FORMTYPE_ENTERPRISEID);

		// form
		URI_MATCHER.addURI(AUTHORITY, Forms.PATH + "/forms",
				EnterpriseFormTableAccessType.FORMS);
		URI_MATCHER.addURI(AUTHORITY, Forms.PATH + "/form",
				EnterpriseFormTableAccessType.FORM);
		URI_MATCHER.addURI(AUTHORITY, Forms.PATH + "/form/#",
				EnterpriseFormTableAccessType.FORM_ID);

		// form item
		URI_MATCHER.addURI(AUTHORITY, FormItems.PATH + "/formItems",
				EnterpriseFormItemTableAccessType.FORMITEMS);
		URI_MATCHER.addURI(AUTHORITY, FormItems.PATH + "/formItem",
				EnterpriseFormItemTableAccessType.FORMITEM);
		URI_MATCHER.addURI(AUTHORITY, FormItems.PATH + "/formItem/#",
				EnterpriseFormItemTableAccessType.FORMITEM_ID);
	}

	@Override
	public String getType(Uri uri) {
		String _contentType = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		// form type
		case EnterpriseFormTypeTableAccessType.FORMTYPES:
			_contentType = FormType.FORMTYPES_CONTENT_TYPE;
			break;

		case EnterpriseFormTypeTableAccessType.FORMTYPE:
		case EnterpriseFormTypeTableAccessType.FORMTYPE_ID:
		case EnterpriseFormTypeTableAccessType.FORMTYPE_ENTERPRISEID:
			_contentType = FormType.FORMTYPE_CONTENT_TYPE;
			break;

		// form
		case EnterpriseFormTableAccessType.FORMS:
			_contentType = Form.FORMS_CONTENT_TYPE;
			break;

		case EnterpriseFormTableAccessType.FORM:
		case EnterpriseFormTableAccessType.FORM_ID:
			_contentType = Form.FORM_CONTENT_TYPE;
			break;

		// form item
		case EnterpriseFormItemTableAccessType.FORMITEMS:
			_contentType = FormItem.FORMITEMS_CONTENT_TYPE;
			break;

		case EnterpriseFormItemTableAccessType.FORMITEM:
		case EnterpriseFormItemTableAccessType.FORMITEM_ID:
			_contentType = FormItem.FORMITEM_CONTENT_TYPE;
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		return _contentType;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri _newInsertUri = null;

		// get local storage database writable instance
		SQLiteDatabase _lswDB = _mLocalStorageDBHelper.getWritableDatabase();

		// define insert table name
		String _insertTableName = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		// form type
		case EnterpriseFormTypeTableAccessType.FORMTYPES:
		case EnterpriseFormTypeTableAccessType.FORMTYPE_ID:
		case EnterpriseFormTypeTableAccessType.FORMTYPE_ENTERPRISEID:
			// nothing to do
			break;

		case EnterpriseFormTypeTableAccessType.FORMTYPE:
			// set enterprise form type table as insert table
			_insertTableName = FormTypes.FORMTYPES_TABLE;

			Log.d(LOG_TAG, "Insert enterprise form type with values = "
					+ values);
			break;

		// form
		case EnterpriseFormTableAccessType.FORMS:
		case EnterpriseFormTableAccessType.FORM_ID:
			// nothing to do
			break;

		case EnterpriseFormTableAccessType.FORM:
			// set enterprise form table as insert table
			_insertTableName = Forms.FORMS_TABLE;

			Log.d(LOG_TAG, "Insert enterprise form with values = " + values);
			break;

		// form item
		case EnterpriseFormItemTableAccessType.FORMITEMS:
		case EnterpriseFormItemTableAccessType.FORMITEM_ID:
			// nothing to do
			break;

		case EnterpriseFormItemTableAccessType.FORMITEM:
			// set enterprise form item table as insert table
			_insertTableName = FormItems.FORMITEMS_TABLE;

			Log.d(LOG_TAG, "Insert enterprise form item with values = "
					+ values);
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		// check uri again, only for form item insert
		if (EnterpriseFormItemTableAccessType.FORMITEM == URI_MATCHER
				.match(uri)) {
			// begin transaction
			_lswDB.beginTransaction();

			try {
				// get form item and its selector content info content values
				// list map
				Map<String, List<ContentValues>> _formItemAndSelectorContentInfoContentValuesMap = getFormItemAndSelectorContentInfoContentValuesMap(values);

				// get form items content values
				ContentValues _formItemContentValues = _formItemAndSelectorContentInfoContentValuesMap
						.get(FormItems.FORMITEMS_TABLE).get(0);

				// insert got form item into form items table and return new
				// insert row id
				long _newInsertRowId = _lswDB.insert(_insertTableName,
						FormItem._ID, _formItemContentValues);

				// check the insert process result
				if (0 <= _newInsertRowId) {
					for (ContentValues formItemSelectorContentInfoContentValues : _formItemAndSelectorContentInfoContentValuesMap
							.get(FormItems.FORMITEM_SELECTORCONTENTS_TABLE)) {
						// put form item row id into form item selector content
						// info content values
						formItemSelectorContentInfoContentValues.put(
								FormItemSelectorContent.FORMITEM_ROWID,
								_newInsertRowId);

						// insert got form item selector content info into form
						// item selector content info table and return new
						// insert row id
						long __newInsertRowId = _lswDB.insert(
								FormItems.FORMITEM_SELECTORCONTENTS_TABLE,
								FormItemSelectorContent._ID,
								formItemSelectorContentInfoContentValues);

						// check the insert process result
						if (0 > __newInsertRowId) {
							Log.e(LOG_TAG,
									"Insert form item selector content info to local storage database error, values = "
											+ formItemSelectorContentInfoContentValues);
						}
					}

					// set transaction successful
					_lswDB.setTransactionSuccessful();

					// update new insert employee uri
					_newInsertUri = ContentUris.withAppendedId(uri,
							_newInsertRowId);

					// notify data has been changed
					getContext().getContentResolver().notifyChange(
							_newInsertUri, null);
				} else {
					Log.d(LOG_TAG,
							"Insert form item to local storage database error, values = "
									+ _formItemContentValues);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Insert form item to local storage database error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			} finally {
				// end transaction
				_lswDB.endTransaction();
			}
		} else {
			// insert object into its local storage table and return new insert
			// row id
			long _newInsertRowId = _lswDB.insert(_insertTableName,
					BaseColumns._ID, values);

			// check the insert process result
			if (0 <= _newInsertRowId) {
				// update new insert uri
				_newInsertUri = ContentUris
						.withAppendedId(uri, _newInsertRowId);

				// notify data has been changed
				getContext().getContentResolver().notifyChange(_newInsertUri,
						null);
			} else {
				Log.d(LOG_TAG,
						"Insert object to local storage database error, values = "
								+ values);
			}
		}

		return _newInsertUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor _queryCursor = null;

		// get local storage database readable instance
		SQLiteDatabase _lsrDB = _mLocalStorageDBHelper.getReadableDatabase();

		// define where condition string, query table name and notification uri
		String _where = null;
		String _queryTableName = null;
		Uri _notificationUri = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		// form type
		case EnterpriseFormTypeTableAccessType.FORMTYPES:
		case EnterpriseFormTypeTableAccessType.FORMTYPE:
		case EnterpriseFormTypeTableAccessType.FORMTYPE_ID:
		case EnterpriseFormTypeTableAccessType.FORMTYPE_ENTERPRISEID:
			// define and get lookup key
			String _lookupKey = null;
			if (EnterpriseFormTypeTableAccessType.FORMTYPE_ID == URI_MATCHER
					.match(uri)) {
				_lookupKey = FormType._ID;
			} else if (EnterpriseFormTypeTableAccessType.FORMTYPE_ENTERPRISEID == URI_MATCHER
					.match(uri)) {
				_lookupKey = FormType.ENTERPRISE_ID;
			}

			// check lookup key
			if (null != _lookupKey) {
				// get query form type id or enterprise id and generate where
				// condition
				_where = _lookupKey + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += SimpleBaseColumns._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG, "Query enterprise form type with selection = "
					+ selection);

			// set enterprise form type table as query table and form type
			// notification uri
			_queryTableName = FormTypes.FORMTYPES_TABLE;
			_notificationUri = FormType.FORMTYPES_NOTIFICATION_CONTENT_URI;
			break;

		// form
		case EnterpriseFormTableAccessType.FORMS:
		case EnterpriseFormTableAccessType.FORM:
		case EnterpriseFormTableAccessType.FORM_ID:
			// get query form id and generate where condition
			if (EnterpriseFormTableAccessType.FORM_ID == URI_MATCHER.match(uri)) {
				_where = Form._ID + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += Form._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG, "Query enterprise form with selection = "
					+ selection);

			// set enterprise form table as query table and form notification
			// uri
			_queryTableName = Forms.FORMS_TABLE;
			_notificationUri = Form.FORMS_NOTIFICATION_CONTENT_URI;
			break;

		// form item
		case EnterpriseFormItemTableAccessType.FORMITEMS:
		case EnterpriseFormItemTableAccessType.FORMITEM:
		case EnterpriseFormItemTableAccessType.FORMITEM_ID:
			// get query form item id and generate where condition
			if (EnterpriseFormItemTableAccessType.FORMITEM_ID == URI_MATCHER
					.match(uri)) {
				_where = FormItem._ID + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += FormItem._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG, "Query enterprise form item with selection = "
					+ selection);

			// set enterprise form item selector content view table as query
			// table and form item notification uri
			_queryTableName = FormItems.ENTERPRISE_FORM_ITEM_SELECTORCONTENT_VIEW;
			_notificationUri = FormItem.FORMITEMS_NOTIFICATION_CONTENT_URI;
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		// query object from its local storage table with projection, selection
		// and order
		_queryCursor = _lsrDB.query(_queryTableName, projection, selection,
				selectionArgs, null, null, sortOrder);

		// set notification with uri
		_queryCursor.setNotificationUri(getContext().getContentResolver(),
				_notificationUri);

		return _queryCursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int _updateNumber = 0;

		// get local storage database writable instance
		SQLiteDatabase _lswDB = _mLocalStorageDBHelper.getWritableDatabase();

		// define where condition string and update table name
		String _where = null;
		String _updateTableName = null;

		// define enterprise form item and its selector content info selection
		String _formItemSelection = selection;
		String _formItemSelectorContentInfoSelection = selection;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		// form type
		case EnterpriseFormTypeTableAccessType.FORMTYPES:
		case EnterpriseFormTypeTableAccessType.FORMTYPE:
			// nothing to do
			break;

		case EnterpriseFormTypeTableAccessType.FORMTYPE_ID:
		case EnterpriseFormTypeTableAccessType.FORMTYPE_ENTERPRISEID:
			// define and get lookup key
			String _lookupKey = null;
			if (EnterpriseFormTypeTableAccessType.FORMTYPE_ID == URI_MATCHER
					.match(uri)) {
				_lookupKey = FormType._ID;
			} else if (EnterpriseFormTypeTableAccessType.FORMTYPE_ENTERPRISEID == URI_MATCHER
					.match(uri)) {
				_lookupKey = FormType.ENTERPRISE_ID;
			}

			// check lookup key
			if (null != _lookupKey) {
				// get update enterprise form type id or enterprise id and
				// generate where
				// condition
				_where = _lookupKey + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += SimpleBaseColumns._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG, "Update enterprise form type with selection = "
					+ _formItemSelection + " and update values = " + values);

			// set enterprise form type table as update table
			_updateTableName = FormTypes.FORMTYPES_TABLE;
			break;

		// form
		case EnterpriseFormTableAccessType.FORMS:
		case EnterpriseFormTableAccessType.FORM:
			// nothing to do
			break;

		case EnterpriseFormTableAccessType.FORM_ID:
			// get update enterprise form id and generate where condition
			if (EnterpriseFormTableAccessType.FORM_ID == URI_MATCHER.match(uri)) {
				_where = Form._ID + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += Form._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG,
					"Update user enterprise to-do list task form item with selection = "
							+ selection + " and update values = " + values);

			// set enterprise form table as update table
			_updateTableName = Forms.FORMS_TABLE;
			break;

		// form item
		case EnterpriseFormItemTableAccessType.FORMITEMS:
		case EnterpriseFormItemTableAccessType.FORMITEM:
			// nothing to do
			break;

		case EnterpriseFormItemTableAccessType.FORMITEM_ID:
			// get update enterprise form item id and generate where condition
			if (EnterpriseFormItemTableAccessType.FORMITEM_ID == URI_MATCHER
					.match(uri)) {
				_where = FormItem._ID + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					_formItemSelection += FormItem._AND_SELECTION + _where;
				} else {
					_formItemSelection = _where;
				}
			}

			Log.d(LOG_TAG, "Update enterprise form item with selection = "
					+ selection + " and update values = " + values);

			// set enterprise form item table as update table
			_updateTableName = FormItems.FORMITEMS_TABLE;
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		// check uri again, only for enterprise form item insert
		if (EnterpriseFormItemTableAccessType.FORMITEM_ID == URI_MATCHER
				.match(uri)) {
			// begin transaction
			_lswDB.beginTransaction();

			try {
				// get enterprise form item and its selector content info
				// content values list map
				Map<String, List<ContentValues>> _enterpriseFormItemAndSelectorContentInfoContentValuesMap = getFormItemAndSelectorContentInfoContentValuesMap(values);

				// get enterprise form item content values
				ContentValues _enterpriseFormItemContentValues = _enterpriseFormItemAndSelectorContentInfoContentValuesMap
						.get(FormItems.FORMITEMS_TABLE).get(0);

				// update enterprise form item into enterprise form items table
				// with selection
				_updateNumber = _lswDB.update(_updateTableName,
						_enterpriseFormItemContentValues, _formItemSelection,
						selectionArgs);

				// check uri again
				if (EnterpriseFormItemTableAccessType.FORMITEM_ID == URI_MATCHER
						.match(uri)) {
					// get enterprise form item selector content info id and
					// generate where condition
					_where = FormItemSelectorContent.FORMITEM_ROWID + "="
							+ ContentUris.parseId(uri);

					// check and update selection
					if (null != selection && !"".equalsIgnoreCase(selection)) {
						_formItemSelectorContentInfoSelection += FormItemSelectorContent._AND_SELECTION
								+ _where;
					} else {
						_formItemSelectorContentInfoSelection = _where;
					}
				}

				for (ContentValues formItemSelectorContentInfoContentValues : _enterpriseFormItemAndSelectorContentInfoContentValuesMap
						.get(FormItems.FORMITEM_SELECTORCONTENTS_TABLE)) {

					Log.d(LOG_TAG,
							"Update enterprise form item selector content info with selection = "
									+ formItemSelectorContentInfoContentValues);

					// update enterprise form item selector content info from
					// enterprise form item selector content info table with
					// selection
					_lswDB.update(FormItems.FORMITEM_SELECTORCONTENTS_TABLE,
							formItemSelectorContentInfoContentValues,
							_formItemSelectorContentInfoSelection,
							selectionArgs);
				}

				// set transaction successful
				_lswDB.setTransactionSuccessful();
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Update object to local storage database error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			} finally {
				// end transaction
				_lswDB.endTransaction();
			}
		} else {
			// update object from its local storage table with selection
			_updateNumber = _lswDB.update(_updateTableName, values, selection,
					selectionArgs);
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

		// define where condition string and delete table name
		String _where = null;
		String _deleteTableName = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		// form type
		case EnterpriseFormTypeTableAccessType.FORMTYPES:
		case EnterpriseFormTypeTableAccessType.FORMTYPE:
			// nothing to do
			break;

		case EnterpriseFormTypeTableAccessType.FORMTYPE_ID:
		case EnterpriseFormTypeTableAccessType.FORMTYPE_ENTERPRISEID:
			// define and get lookup key
			String _lookupKey = null;
			if (EnterpriseFormTypeTableAccessType.FORMTYPE_ID == URI_MATCHER
					.match(uri)) {
				_lookupKey = FormType._ID;
			} else if (EnterpriseFormTypeTableAccessType.FORMTYPE_ENTERPRISEID == URI_MATCHER
					.match(uri)) {
				_lookupKey = FormType.ENTERPRISE_ID;
			}

			// check lookup key
			if (null != _lookupKey) {
				// get delete enterprise form type id or enterprise id and
				// generate where condition
				_where = _lookupKey + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += SimpleBaseColumns._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG, "Delete enterprise form type with selection = "
					+ selection);

			// set enterprise form type table as delete table
			_deleteTableName = FormTypes.FORMTYPES_TABLE;
			break;

		// form
		case EnterpriseFormTableAccessType.FORMS:
		case EnterpriseFormTableAccessType.FORM:
			// nothing to do
			break;

		case EnterpriseFormTableAccessType.FORM_ID:
			// get delete enterprise form id and generate where condition
			if (EnterpriseFormTableAccessType.FORM_ID == URI_MATCHER.match(uri)) {
				_where = Form._ID + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += Form._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG, "Delete enterprise form with selection = "
					+ selection);

			// set enterprise form table as delete table
			_deleteTableName = Forms.FORMS_TABLE;
			break;

		// form item
		case EnterpriseFormItemTableAccessType.FORMITEMS:
		case EnterpriseFormItemTableAccessType.FORMITEM:
			// nothing to do
			break;

		case EnterpriseFormItemTableAccessType.FORMITEM_ID:
			// get delete to-do task attachment id and generate where condition
			if (EnterpriseFormItemTableAccessType.FORMITEM_ID == URI_MATCHER
					.match(uri)) {
				_where = FormItem._ID + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += FormItem._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG, "Delete enterprise form item with selection = "
					+ selection);

			// set enterprise form item table as query table
			_deleteTableName = FormItems.FORMITEMS_TABLE;
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		Log.d(LOG_TAG, "Delete enterprise form with selection = " + selection);

		// delete object from its local storage table with selection
		_deleteNumber = _lswDB.delete(_deleteTableName, selection,
				selectionArgs);

		// notify data has been changed
		getContext().getContentResolver().notifyChange(uri, null);

		return _deleteNumber;
	}

	// get form item and its selector content info content values list map with
	// given content values
	@SuppressWarnings("unchecked")
	private Map<String, List<ContentValues>> getFormItemAndSelectorContentInfoContentValuesMap(
			ContentValues values) {
		Map<String, List<ContentValues>> _formItemAndSelectorContentInfoContentValuesMap = new HashMap<String, List<ContentValues>>();

		// check given content values
		if (null != values) {
			// define form item and its selector content info content values
			// list
			ContentValues _formItemContentValues = new ContentValues(values);
			List<ContentValues> _formItemSelectorContentInfoContentValuesList = new ArrayList<ContentValues>();

			// get content values selector content info key prefix
			String _selectorContentInfoKeyPrefix = FormItemSelectorContent.CONTENTVALUES_INFO_KEY_FORMAT
					.replace("%d", "");

			for (String contentValuesKey : values.keySet()) {
				// check values
				if (contentValuesKey.startsWith(_selectorContentInfoKeyPrefix)) {
					// define the form item selector content info content values
					ContentValues _formItemSelectorContentInfoContentValues = new ContentValues();

					// generate form item selector content info content values
					// put form item selector content fake id, info into form
					// item selector content info content values
					_formItemSelectorContentInfoContentValues.put(
							FormItemSelectorContent.SELECTORCONTENT_FAKEID,
							Integer.parseInt(contentValuesKey
									.substring(_selectorContentInfoKeyPrefix
											.length())));
					_formItemSelectorContentInfoContentValues.put(
							FormItemSelectorContent.INFO,
							values.getAsString(contentValuesKey));

					// add form item selector content info content values to
					// list
					_formItemSelectorContentInfoContentValuesList
							.add(_formItemSelectorContentInfoContentValues);

					// remove the content value from form item content values
					_formItemContentValues.remove(contentValuesKey);
				}
			}

			// put form item content values and its selector content info
			// content values list into map using its table name as key
			_formItemAndSelectorContentInfoContentValuesMap
					.put(FormItems.FORMITEMS_TABLE,
							(List<ContentValues>) CommonUtils
									.array2List(new ContentValues[] { _formItemContentValues }));
			_formItemAndSelectorContentInfoContentValuesMap.put(
					FormItems.FORMITEM_SELECTORCONTENTS_TABLE,
					_formItemSelectorContentInfoContentValuesList);
		}

		return _formItemAndSelectorContentInfoContentValuesMap;
	}

	// inner class
	// enterprise form type table access type
	static class EnterpriseFormTypeTableAccessType {

		// enterprise form type table access type
		private static final int FORMTYPES = 30;
		private static final int FORMTYPE = 31;
		private static final int FORMTYPE_ID = 32;
		private static final int FORMTYPE_ENTERPRISEID = 33;

	}

	// enterprise form types
	public static final class FormTypes {

		// enterprise form type path
		private static final String PATH = FormTypes.class.getCanonicalName();

		// enterprise form type table name
		public static final String FORMTYPES_TABLE = "ia_enterprise_formtype";

		// inner class
		// enterprise form type
		public static final class FormType implements SimpleBaseColumns {

			// enterprise form content provider form type process data columns
			public static final String TYPE_ID = "typeId";
			public static final String ENTERPRISE_ID = "enterpriseId";
			public static final String NAME = "typeName";

			// content uri
			private static final Uri FORMTYPES_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH);
			public static final Uri FORMTYPES_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH + "/formTypes");
			public static final Uri FORMTYPE_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH + "/formType");
			public static final Uri ENTERPRISE_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/enterprise");

			// content type
			private static final String FORMTYPES_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ FormTypes.class.getCanonicalName();
			private static final String FORMTYPE_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ FormTypes.class.getCanonicalName();

		}

	}

	// enterprise form table access type
	static class EnterpriseFormTableAccessType {

		// enterprise form table access type
		private static final int FORMS = 34;
		private static final int FORM = 35;
		private static final int FORM_ID = 36;

	}

	// enterprise forms
	public static final class Forms {

		// enterprise form path
		private static final String PATH = Forms.class.getCanonicalName();

		// enterprise form table name
		public static final String FORMS_TABLE = "ia_enterprise_form";

		// inner class
		// enterprise form
		public static final class Form implements SimpleBaseColumns {

			// enterprise form content provider form process data columns
			public static final String FORM_ID = "formId";
			public static final String FORMTYPE_ID = "formTypeId";
			public static final String ENTERPRISE_ID = "enterpriseId";
			public static final String NAME = "formName";
			public static final String DEFAULT_SUBMITCONTACTS = "defaultSubmitContacts";

			// content uri
			private static final Uri FORMS_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH);
			public static final Uri FORMS_CONTENT_URI = Uri.parse("content://"
					+ AUTHORITY + '/' + PATH + "/forms");
			public static final Uri FORM_CONTENT_URI = Uri.parse("content://"
					+ AUTHORITY + '/' + PATH + "/form");

			// content type
			private static final String FORMS_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ Forms.class.getCanonicalName();
			private static final String FORM_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ Forms.class.getCanonicalName();

			// enterprise forms with type id condition
			public static final String ENTERPRISE_FORMS_WITHTYPEID_CONDITION = ENTERPRISE_ID
					+ "=?" + _AND_SELECTION + FORMTYPE_ID + "=?";

		}

	}

	// enterprise form item table access type
	static class EnterpriseFormItemTableAccessType {

		// enterprise form item table access type
		private static final int FORMITEMS = 37;
		private static final int FORMITEM = 38;
		private static final int FORMITEM_ID = 39;

	}

	// enterprise form items
	public static final class FormItems {

		// enterprise form item path
		private static final String PATH = FormItems.class.getCanonicalName();

		// enterprise form item and its selector content table name
		public static final String FORMITEMS_TABLE = "ia_form_item";
		public static final String FORMITEM_SELECTORCONTENTS_TABLE = "ia_form_item_selectorcontent";

		// enterprise form item selector content view
		public static final String ENTERPRISE_FORM_ITEM_SELECTORCONTENT_VIEW = "ia_enterprise_form_item_selectorcontent_view";

		// inner class
		// enterprise form item
		public static final class FormItem implements SimpleBaseColumns {

			// enterprise form content provider form item process data columns
			public static final String ITEM_ID = "itemId";
			public static final String FORM_ID = "formId";
			public static final String FORMTYPE_ID = "formTypeId";
			public static final String ENTERPRISE_ID = "enterpriseId";
			public static final String NAME = "itemName";
			public static final String PHYSICALNAME = "itemPhysicalName";
			public static final String TYPE = "itemType";
			public static final String MUSTWRITE_FLAG = "isMustWrite";
			public static final String CAPITAL_FLAG = "isCapital";
			public static final String FORMULA = "formula";

			public static final String SELECTORCONTENTS = "selectorContents";

			// form item selector content info format and separator
			public static final String SELECTORCONTENT_INFO_FORMAT = FormItemSelectorContent.CONTENTVALUES_INFO_KEY_FORMAT;
			public static final String SELECTORCONTENT_INFO_SEPARATOR = FormItemSelectorContent.INFO_SEPARATOR;

			// content uri
			private static final Uri FORMITEMS_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH);
			public static final Uri FORMITEMS_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH + "/formItems");
			public static final Uri FORMITEM_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH + "/formItem");

			// content type
			private static final String FORMITEMS_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ FormItems.class.getCanonicalName();
			private static final String FORMITEM_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ FormItems.class.getCanonicalName();

			// enterprise form items with form type id and form id condition
			public static final String ENTERPRISE_FORMITEMS_WITHFORMTYPEID7FORMID_CONDITION = ENTERPRISE_ID
					+ "=?"
					+ _AND_SELECTION
					+ FORMTYPE_ID
					+ "=?"
					+ _AND_SELECTION + FORM_ID + "=?";

		}

		// enterprise form item selector content
		static final class FormItemSelectorContent implements SimpleBaseColumns {

			// form item selector content data columns
			public static final String SELECTORCONTENT_FAKEID = "selectorcontentFakeId";
			public static final String FORMITEM_ROWID = "formItemRowId";
			public static final String INFO = "info";

			// form item selector content content values info key format
			public static final String CONTENTVALUES_INFO_KEY_FORMAT = "selectorContent_%d";

			// form item selector content info separator
			public static final String INFO_SEPARATOR = "~~";

		}

	}

}
