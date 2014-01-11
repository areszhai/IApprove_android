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
import android.util.SparseArray;

import com.futuo.iapprove.provider.EnterpriseFormContentProvider.FormItems.FormItem;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTaskFormItems.TodoTaskFormItem;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTasks.TodoTask;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTasks.TodoTaskAdvice;
import com.richitec.commontoolkit.utils.CommonUtils;

public class UserEnterpriseTodoListTaskContentProvider extends
		LocalStorageContentProvider {

	private static final String LOG_TAG = UserEnterpriseTodoListTaskContentProvider.class
			.getCanonicalName();

	// user enterprise to-do list task content provider authority
	private static final String AUTHORITY = UserEnterpriseTodoListTaskContentProvider.class
			.getCanonicalName();

	// register custom uri for uri matcher
	static {
		// to-do task
		URI_MATCHER.addURI(AUTHORITY, TodoTasks.PATH + "/todoTasks",
				UserEnterpriseTodoTaskTableAccessType.TODOTASKS);
		URI_MATCHER.addURI(AUTHORITY, TodoTasks.PATH + "/todoTask",
				UserEnterpriseTodoTaskTableAccessType.TODOTASK);
		URI_MATCHER.addURI(AUTHORITY, TodoTasks.PATH + "/todoTask/#",
				UserEnterpriseTodoTaskTableAccessType.TODOTASK_ID);
		URI_MATCHER.addURI(AUTHORITY, TodoTasks.PATH + "/enterprise/#",
				UserEnterpriseTodoTaskTableAccessType.TODOTASK_ENTERPRISEID);

		// to-do task form item
		URI_MATCHER
				.addURI(AUTHORITY,
						TodoTaskFormItems.PATH + "/todoTaskFormItems",
						UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEMS);
		URI_MATCHER
				.addURI(AUTHORITY,
						TodoTaskFormItems.PATH + "/todoTaskFormItem",
						UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEM);
		URI_MATCHER
				.addURI(AUTHORITY,
						TodoTaskFormItems.PATH + "/todoTaskFormItem/#",
						UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEM_ID);
	}

	@Override
	public String getType(Uri uri) {
		String _contentType = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		// to-do task
		case UserEnterpriseTodoTaskTableAccessType.TODOTASKS:
			_contentType = TodoTask.TODOTASKS_CONTENT_TYPE;
			break;

		case UserEnterpriseTodoTaskTableAccessType.TODOTASK:
		case UserEnterpriseTodoTaskTableAccessType.TODOTASK_ID:
		case UserEnterpriseTodoTaskTableAccessType.TODOTASK_ENTERPRISEID:
			_contentType = TodoTask.TODOTASK_CONTENT_TYPE;
			break;

		// to-do task form item
		case UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEMS:
			_contentType = TodoTaskFormItem.TODOTASKFORMITEMS_CONTENT_TYPE;
			break;

		case UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEM:
		case UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEM_ID:
			_contentType = TodoTaskFormItem.TODOTASKFORMITEM_CONTENT_TYPE;
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
		// to-do task
		case UserEnterpriseTodoTaskTableAccessType.TODOTASKS:
		case UserEnterpriseTodoTaskTableAccessType.TODOTASK_ID:
		case UserEnterpriseTodoTaskTableAccessType.TODOTASK_ENTERPRISEID:
			// nothing to do
			break;

		case UserEnterpriseTodoTaskTableAccessType.TODOTASK:
			// set user enterprise to-do task table as insert table
			_insertTableName = TodoTasks.TODOTASKS_TABLE;

			Log.d(LOG_TAG,
					"Insert user enterprise to-do list task with values = "
							+ values);
			break;

		// to-do task form item
		case UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEMS:
		case UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEM_ID:
			// nothing to do
			break;

		case UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEM:
			// set user enterprise to-do task form item table as insert table
			_insertTableName = TodoTaskFormItems.TODOTASKFORMITEMINFOS_TABLE;

			Log.d(LOG_TAG,
					"Insert user enterprise to-do list task form item with values = "
							+ values);
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		// check uri again, only for to-do list task insert
		if (UserEnterpriseTodoTaskTableAccessType.TODOTASK == URI_MATCHER
				.match(uri)) {
			// begin transaction
			_lswDB.beginTransaction();

			try {
				// get to-do list task and its advice content values list map
				Map<String, List<ContentValues>> _todoListTaskAndAdviceContentValuesMap = getTodoListTaskAndAdviceContentValuesMap(values);

				// get to-do list task content values
				ContentValues _todoListTaskContentValues = _todoListTaskAndAdviceContentValuesMap
						.get(TodoTasks.TODOTASKS_TABLE).get(0);

				// insert got to-do list task into to-do list tasks table and
				// return new insert row id
				long _newInsertRowId = _lswDB.insert(_insertTableName,
						TodoTask._ID, _todoListTaskContentValues);

				// check the insert process result
				if (0 <= _newInsertRowId) {
					for (ContentValues todoListTaskAdviceContentValues : _todoListTaskAndAdviceContentValuesMap
							.get(TodoTasks.TODOTASK_ADVICES_TABLE)) {
						// put to-do list task row id into to-do list task
						// advice content values
						todoListTaskAdviceContentValues.put(
								TodoTaskAdvice.TODOTASK_ROWID, _newInsertRowId);

						// insert got to-do list task advice into to-do list
						// task advice table and return new insert row id
						long __newInsertRowId = _lswDB.insert(
								TodoTasks.TODOTASK_ADVICES_TABLE,
								TodoTaskAdvice._ID,
								todoListTaskAdviceContentValues);

						// check the insert process result
						if (0 > __newInsertRowId) {
							Log.e(LOG_TAG,
									"Insert to-do list task advice to local storage database error, values = "
											+ todoListTaskAdviceContentValues);
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
							"Insert to-do list task to local storage database error, values = "
									+ _todoListTaskContentValues);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG,
						"Insert to-do list task to local storage database error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			} finally {
				// end transaction
				_lswDB.endTransaction();
			}
		} else {
			// insert to-do list task form item into to-do list task form item
			// info table and return new insert row id
			long _newInsertRowId = _lswDB.insert(_insertTableName,
					TodoTaskFormItem._ID, values);

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
						"Insert to-do list task form item to local storage database error, values = "
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
		// to-do task
		case UserEnterpriseTodoTaskTableAccessType.TODOTASKS:
		case UserEnterpriseTodoTaskTableAccessType.TODOTASK:
		case UserEnterpriseTodoTaskTableAccessType.TODOTASK_ID:
		case UserEnterpriseTodoTaskTableAccessType.TODOTASK_ENTERPRISEID:
			// define and get lookup key
			String _lookupKey = null;
			if (UserEnterpriseTodoTaskTableAccessType.TODOTASK_ID == URI_MATCHER
					.match(uri)) {
				_lookupKey = TodoTask._ID;
			} else if (UserEnterpriseTodoTaskTableAccessType.TODOTASK_ENTERPRISEID == URI_MATCHER
					.match(uri)) {
				_lookupKey = TodoTask.ENTERPRISE_ID;
			}

			// check lookup key
			if (null != _lookupKey) {
				// get query to-do task id or enterprise id and generate where
				// condition
				_where = _lookupKey + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += SimpleBaseColumns._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG,
					"Query user enterprise to-do list task with selection = "
							+ selection);

			// set user enterprise to-do task table as query table and to-do
			// list task notification uri
			_queryTableName = TodoTasks.USERENTERPRISE_TODOTASK_ADVICE_VIEW;
			_notificationUri = TodoTask.TODOTASKS_NOTIFICATION_CONTENT_URI;
			break;

		// to-do task form item
		case UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEMS:
		case UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEM:
		case UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEM_ID:
			// get query to-do task form item id and generate where condition
			if (UserEnterpriseTodoTaskFormItemInfoTableAccessType.TODOTASKFORMITEM_ID == URI_MATCHER
					.match(uri)) {
				_where = TodoTaskFormItem._ID + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += FormItem._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG,
					"Query user enterprise to-do list task form item with selection = "
							+ selection);

			// set user enterprise to-do list task form item table as query
			// table and to-do list task form item notification uri
			_queryTableName = TodoTaskFormItems.TODOTASKFORMITEMINFOS_TABLE;
			_notificationUri = TodoTaskFormItem.TODOTASKFORMITEMS_NOTIFICATION_CONTENT_URI;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	// get to-do list task and its advice content values list map with given
	// content values
	@SuppressWarnings("unchecked")
	private Map<String, List<ContentValues>> getTodoListTaskAndAdviceContentValuesMap(
			ContentValues values) {
		Map<String, List<ContentValues>> _todoListTaskAndAdviceContentValuesMap = new HashMap<String, List<ContentValues>>();

		// check given content values
		if (null != values) {
			// define to-do list task and its advice content values list and map
			ContentValues _todoTaskContentValues = new ContentValues(values);
			List<ContentValues> _todoTaskAdviceContentValuesList = new ArrayList<ContentValues>();
			SparseArray<ContentValues> _todoTaskAdviceContentValuesMap = new SparseArray<ContentValues>();

			// get content values advice advisor id, name, advice state, content
			// and given timestamp key prefix
			String _adviceAdvisorIdKeyPrefix = TodoTask.ADVICE_ADVISORID_FORMAT
					.replace("%d", "");
			String _adviceAdvisorNameKeyPrefix = TodoTask.ADVICE_ADVISORNAME_FORMAT
					.replace("%d", "");
			String _adviceStateKeyPrefix = TodoTask.ADVICE_ADVICESTATE_FORMAT
					.replace("%d", "");
			String _adviceContentKeyPrefix = TodoTask.ADVICE_ADVICECONTENT_FORMAT
					.replace("%d", "");
			String _adviceGivenTimestampKeyPrefix = TodoTask.ADVICE_ADVICEGIVENTIMESTAMP_FORMAT
					.replace("%d", "");

			for (String contentValuesKey : values.keySet()) {
				// check values
				if (contentValuesKey.startsWith(_adviceAdvisorIdKeyPrefix)) {
					// get and check the to-do list task advice content values
					ContentValues _todoTaskAdviceContentValues = getTodoTaskAdviceContentValues(
							_todoTaskContentValues, contentValuesKey,
							_todoTaskAdviceContentValuesMap,
							_adviceAdvisorIdKeyPrefix);

					// generate to-do list task advice content values
					// put to-do list task advice advisor id into to-do list
					// task advice content values
					_todoTaskAdviceContentValues.put(TodoTaskAdvice.ADVISOR_ID,
							values.getAsLong(contentValuesKey));
				} else if (contentValuesKey
						.startsWith(_adviceAdvisorNameKeyPrefix)) {
					// get and check the to-do list task advice content values
					ContentValues _todoTaskAdviceContentValues = getTodoTaskAdviceContentValues(
							_todoTaskContentValues, contentValuesKey,
							_todoTaskAdviceContentValuesMap,
							_adviceAdvisorNameKeyPrefix);

					// generate to-do list task advice content values
					// put to-do list task advice advisor name into to-do list
					// task advice content values
					_todoTaskAdviceContentValues.put(
							TodoTaskAdvice.ADVISOR_NAME,
							values.getAsString(contentValuesKey));
				} else if (contentValuesKey.startsWith(_adviceStateKeyPrefix)) {
					// get and check the to-do list task advice content values
					ContentValues _todoTaskAdviceContentValues = getTodoTaskAdviceContentValues(
							_todoTaskContentValues, contentValuesKey,
							_todoTaskAdviceContentValuesMap,
							_adviceStateKeyPrefix);

					// generate to-do list task advice content values
					// put to-do list task advice advice state into to-do list
					// task advice content values
					_todoTaskAdviceContentValues.put(
							TodoTaskAdvice.ADVICE_STATE,
							values.getAsBoolean(contentValuesKey));
				} else if (contentValuesKey.startsWith(_adviceContentKeyPrefix)) {
					// get and check the to-do list task advice content values
					ContentValues _todoTaskAdviceContentValues = getTodoTaskAdviceContentValues(
							_todoTaskContentValues, contentValuesKey,
							_todoTaskAdviceContentValuesMap,
							_adviceContentKeyPrefix);

					// generate to-do list task advice content values
					// put to-do list task advice content into to-do list task
					// advice content values
					_todoTaskAdviceContentValues.put(
							TodoTaskAdvice.ADVICE_CONTENT,
							values.getAsString(contentValuesKey));
				} else if (contentValuesKey
						.startsWith(_adviceGivenTimestampKeyPrefix)) {
					// get and check the to-do list task advice content values
					ContentValues _todoTaskAdviceContentValues = getTodoTaskAdviceContentValues(
							_todoTaskContentValues, contentValuesKey,
							_todoTaskAdviceContentValuesMap,
							_adviceGivenTimestampKeyPrefix);

					// generate to-do list task advice content values
					// put to-do list task advice given timestamp into to-do
					// list task advice content values
					_todoTaskAdviceContentValues.put(
							TodoTaskAdvice.ADVICE_GIVENTIMESTAMP,
							values.getAsLong(contentValuesKey));
				}
			}

			// add to-do list task advice content values to list
			for (int i = 0; i < _todoTaskAdviceContentValuesMap.size(); i++) {
				_todoTaskAdviceContentValuesList
						.add(_todoTaskAdviceContentValuesMap.get(i));
			}

			// put to-do list task content values and its advice content values
			// list into map using its table name as key
			_todoListTaskAndAdviceContentValuesMap
					.put(TodoTasks.TODOTASKS_TABLE,
							(List<ContentValues>) CommonUtils
									.array2List(new ContentValues[] { _todoTaskContentValues }));
			_todoListTaskAndAdviceContentValuesMap.put(
					TodoTasks.TODOTASK_ADVICES_TABLE,
					_todoTaskAdviceContentValuesList);
		}

		return _todoListTaskAndAdviceContentValuesMap;
	}

	// get the to-do list task advice content values
	private ContentValues getTodoTaskAdviceContentValues(
			ContentValues todoTaskContentValues, String adviceContentValuesKey,
			SparseArray<ContentValues> adviceContentValuesMap,
			String adviceKeyProfix) {
		// get the to-do list task advice content values index
		int _index = Integer.parseInt(adviceContentValuesKey
				.substring(adviceKeyProfix.length()));

		// get and check the to-do list task advice content values
		ContentValues _todoTaskAdviceContentValues = adviceContentValuesMap
				.get(_index);
		if (null == _todoTaskAdviceContentValues) {
			// initialized to-do list task advice content values
			_todoTaskAdviceContentValues = new ContentValues();
		}

		// put to-do list task advice fake id into to-do list task advice
		// content values
		_todoTaskAdviceContentValues.put(TodoTaskAdvice.ADVICE_FAKEID, Integer
				.parseInt(adviceContentValuesKey.substring(adviceKeyProfix
						.length())));

		// add to-do list task advice content values to map
		adviceContentValuesMap.put(_index, _todoTaskAdviceContentValues);

		// remove the content value from to-do list task content values
		todoTaskContentValues.remove(adviceContentValuesKey);

		return _todoTaskAdviceContentValues;
	}

	// inner class
	// user enterprise to-do task table access type
	static class UserEnterpriseTodoTaskTableAccessType {

		// user enterprise to-do task table access type
		private static final int TODOTASKS = 20;
		private static final int TODOTASK = 21;
		private static final int TODOTASK_ID = 22;
		private static final int TODOTASK_ENTERPRISEID = 23;

	}

	// user enterprise to-do list tasks
	public static final class TodoTasks {

		// user enterprise to-do list task path
		private static final String PATH = TodoTasks.class.getCanonicalName();

		// user enterprise to-do list task and task advice table name
		public static final String TODOTASKS_TABLE = "ia_user_enterprise_todotask";
		public static final String TODOTASK_ADVICES_TABLE = "ia_todotask_advice";

		// user enterprise to-do list task advice view
		public static final String USERENTERPRISE_TODOTASK_ADVICE_VIEW = "ia_user_enterprise_todotask_advice_view";

		// inner class
		// user enterprise to-do list task
		public static final class TodoTask implements SimpleBaseColumns {

			// user enterprise to-do list task content provider to-do task type
			// process data columns
			public static final String TASK_ID = "taskId";
			public static final String ENTERPRISE_ID = "enterpriseId";
			public static final String APPROVE_NUMBER = "approveNumber";
			public static final String TASK_TITLE = "taskTitle";
			public static final String APPLICANTNAME = "applicantName";
			public static final String SENDERFAKEID = "taskSenderFakeId";
			public static final String TASK_STATUS = "taskStatus";
			public static final String SUBMITTIMESTAMP = "taskSubmitTimeStamp";

			public static final String ADVISOR_IDS = "advisorIds";
			public static final String ADVISOR_NAMES = "advisorNames";
			public static final String ADVICE_STATES = "adviceStates";
			public static final String ADVICE_CONTENTS = "adviceContents";
			public static final String ADVICE_GIVENTIMESTAMPS = "adviceGivenTimestamps";

			// to-do list task advice key format, separator and advice content
			// placeholder
			public static final String ADVICE_ADVISORID_FORMAT = TodoTaskAdvice.CONTENTVALUES_KEY_FORMAT
					.replace("%s", TodoTaskAdvice.ADVISOR_ID);
			public static final String ADVICE_ADVISORNAME_FORMAT = TodoTaskAdvice.CONTENTVALUES_KEY_FORMAT
					.replace("%s", TodoTaskAdvice.ADVISOR_NAME);
			public static final String ADVICE_ADVICESTATE_FORMAT = TodoTaskAdvice.CONTENTVALUES_KEY_FORMAT
					.replace("%s", TodoTaskAdvice.ADVICE_STATE);
			public static final String ADVICE_ADVICECONTENT_FORMAT = TodoTaskAdvice.CONTENTVALUES_KEY_FORMAT
					.replace("%s", TodoTaskAdvice.ADVICE_CONTENT);
			public static final String ADVICE_ADVICEGIVENTIMESTAMP_FORMAT = TodoTaskAdvice.CONTENTVALUES_KEY_FORMAT
					.replace("%s", TodoTaskAdvice.ADVICE_GIVENTIMESTAMP);
			public static final String ADVICE_SEPARATOR = TodoTaskAdvice.ADVICE_SEPARATOR;
			public static final String ADVICE_CONTENT_PLACEHOLDER = TodoTaskAdvice.ADVICE_CONTENT_PLACEHOLDER;

			// content uri
			private static final Uri TODOTASKS_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH);
			public static final Uri TODOTASKS_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH + "/todoTasks");
			public static final Uri TODOTASK_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH + "/todoTask");
			public static final Uri ENTERPRISE_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/enterprise");

			// content type
			private static final String TODOTASKS_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ TodoTasks.class.getCanonicalName();
			private static final String TODOTASK_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ TodoTasks.class.getCanonicalName();

			// user enterprise to-do list task condition
			public static final String USER_ENTERPRISETODOLISTTASKS_WITHLOGINNAME_CONDITION = APPROVE_NUMBER
					+ "=?";

		}

		// user enterprise to-do list task advice
		static final class TodoTaskAdvice implements SimpleBaseColumns {

			// to-do list task advice data columns
			public static final String ADVICE_FAKEID = "adviceFakeId";
			public static final String TODOTASK_ROWID = "todoTaskRowId";
			public static final String ADVISOR_ID = "advisorId";
			public static final String ADVISOR_NAME = "advisorName";
			public static final String ADVICE_STATE = "adviceState";
			public static final String ADVICE_CONTENT = "adviceContent";
			public static final String ADVICE_GIVENTIMESTAMP = "adviceGivenTimestamp";

			// to-do list task advice content values advisor id, name, advice
			// state, content and given timestamp key format
			public static final String CONTENTVALUES_KEY_FORMAT = "%s_%d";

			// to-do list task advice advisor id, name, advice state, content
			// and given timestamp separator
			public static final String ADVICE_SEPARATOR = "~~";

			// to-do list task advice content default placeholder
			public static final String ADVICE_CONTENT_PLACEHOLDER = "``";

		}

	}

	// user enterprise to-do task form item info table access type
	static class UserEnterpriseTodoTaskFormItemInfoTableAccessType {

		// enterprise form item table access type
		private static final int TODOTASKFORMITEMS = 30;
		private static final int TODOTASKFORMITEM = 31;
		private static final int TODOTASKFORMITEM_ID = 32;

	}

	// user enterprise to-do list task form items
	public static final class TodoTaskFormItems {

		// user enterprise to-do list task form item path
		private static final String PATH = TodoTaskFormItems.class
				.getCanonicalName();

		// user enterprise to-do list task form item info table name
		public static final String TODOTASKFORMITEMINFOS_TABLE = "ia_todotask_formiteminfo";

		// inner class
		// user enterprise to-do list task form item
		public static final class TodoTaskFormItem implements SimpleBaseColumns {

			//

			// content uri
			private static final Uri TODOTASKFORMITEMS_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH);
			public static final Uri TODOTASKFORMITEMS_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/todoTaskFormItems");
			public static final Uri TODOTASKFORMITEM_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/todoTaskFormItem");

			// content type
			private static final String TODOTASKFORMITEMS_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ TodoTaskFormItems.class.getCanonicalName();
			private static final String TODOTASKFORMITEM_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ TodoTaskFormItems.class.getCanonicalName();

		}

	}

}
