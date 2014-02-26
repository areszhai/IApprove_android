package com.futuo.iapprove.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.futuo.iapprove.provider.UserEnterpriseTaskApprovingContentProvider.ApprovingTodoTasks.ApprovingTodoTask;
import com.futuo.iapprove.provider.UserEnterpriseTaskApprovingContentProvider.GeneratingNAATaskAttachments.GeneratingNAATaskAttachment;
import com.futuo.iapprove.provider.UserEnterpriseTaskApprovingContentProvider.GeneratingNAATasks.GeneratingNAATask;

public class UserEnterpriseTaskApprovingContentProvider extends
		LocalStorageContentProvider {

	private static final String LOG_TAG = UserEnterpriseTaskApprovingContentProvider.class
			.getCanonicalName();

	// user enterprise task approve content provider authority
	private static final String AUTHORITY = UserEnterpriseTaskApprovingContentProvider.class
			.getCanonicalName();

	// register custom uri for uri matcher
	static {
		// approving to-do task
		URI_MATCHER.addURI(AUTHORITY, ApprovingTodoTasks.PATH
				+ "/approvingTodoTasks",
				TodoTaskApprovingTableAccessType.APPROVINGTODOTASKS);
		URI_MATCHER.addURI(AUTHORITY, ApprovingTodoTasks.PATH
				+ "/approvingTodoTask",
				TodoTaskApprovingTableAccessType.APPROVINGTODOTASK);
		URI_MATCHER.addURI(AUTHORITY, ApprovingTodoTasks.PATH
				+ "/approvingTodoTask/#",
				TodoTaskApprovingTableAccessType.APPROVINGTODOTASK_ID);

		// generating new approve application
		URI_MATCHER.addURI(AUTHORITY, GeneratingNAATasks.PATH
				+ "/generatingNAATasks",
				NAATaskGeneratingTableAccessType.GENERATINGNAATASKS);
		URI_MATCHER.addURI(AUTHORITY, GeneratingNAATasks.PATH
				+ "/generatingNAATask",
				NAATaskGeneratingTableAccessType.GENERATINGNAATASK);
		URI_MATCHER.addURI(AUTHORITY, GeneratingNAATasks.PATH
				+ "/generatingNAATask/#",
				NAATaskGeneratingTableAccessType.GENERATINGNAATASK_ID);

		// generating new approve application attachment
		URI_MATCHER
				.addURI(AUTHORITY,
						GeneratingNAATaskAttachments.PATH
								+ "/generatingNAATaskAttachments",
						NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENTS);
		URI_MATCHER
				.addURI(AUTHORITY,
						GeneratingNAATaskAttachments.PATH
								+ "/generatingNAATaskAttachment",
						NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT);
		URI_MATCHER
				.addURI(AUTHORITY,
						GeneratingNAATaskAttachments.PATH
								+ "/generatingNAATaskAttachment/#",
						NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT_ID);
	}

	@Override
	public String getType(Uri uri) {
		String _contentType = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		// approving to-do task
		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASKS:
			_contentType = ApprovingTodoTask.APPROVINGTODOTASKS_CONTENT_TYPE;
			break;

		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASK:
		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASK_ID:
			_contentType = ApprovingTodoTask.APPROVINGTODOTASK_CONTENT_TYPE;
			break;

		// generating new approve application
		case NAATaskGeneratingTableAccessType.GENERATINGNAATASKS:
			_contentType = GeneratingNAATask.GENERATINGNAATASKS_CONTENT_TYPE;
			break;

		case NAATaskGeneratingTableAccessType.GENERATINGNAATASK:
		case NAATaskGeneratingTableAccessType.GENERATINGNAATASK_ID:
			_contentType = GeneratingNAATask.GENERATINGNAATASK_CONTENT_TYPE;
			break;

		// generating new approve application attachment
		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENTS:
			_contentType = GeneratingNAATaskAttachment.GENERATINGNAATASKATTACHMENTS_CONTENT_TYPE;
			break;

		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT:
		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT_ID:
			_contentType = GeneratingNAATaskAttachment.GENERATINGNAATASKATTACHMENT_CONTENT_TYPE;
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
		// approving to-do task
		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASK:
		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASK_ID:
			// nothing to do
			break;

		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASKS:
			// set user enterprise to-do task approving table as insert table
			_insertTableName = ApprovingTodoTasks.TODOTASKAPPROVING_TABLE;

			Log.d(LOG_TAG,
					"Insert user enterprise to-do task for approving with values = "
							+ values);
			break;

		// generating new approve application
		case NAATaskGeneratingTableAccessType.GENERATINGNAATASK:
		case NAATaskGeneratingTableAccessType.GENERATINGNAATASK_ID:
			// nothing to do
			break;

		case NAATaskGeneratingTableAccessType.GENERATINGNAATASKS:
			// set user enterprise new approve application generating table as
			// insert table
			_insertTableName = GeneratingNAATasks.NAATASKGENERATING_TABLE;

			Log.d(LOG_TAG,
					"Insert user enterprise new approve application for generating with values = "
							+ values);
			break;

		// generating new approve application attachment
		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT:
		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT_ID:
			// nothing to do
			break;

		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENTS:
			// set user enterprise new approve application generating attachment
			// table as insert table
			_insertTableName = GeneratingNAATaskAttachments.NAATASKGENERATINGATTACHMENT_TABLE;

			Log.d(LOG_TAG,
					"Insert user enterprise new approve application attachment for generating with values = "
							+ values);
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		// insert object into its local storage table and return new insert row
		// id
		long _newInsertRowId = _lswDB.insert(_insertTableName,
				SimpleBaseColumns._ID, values);

		// check the insert process result
		if (0 <= _newInsertRowId) {
			// update new insert uri
			_newInsertUri = ContentUris.withAppendedId(uri, _newInsertRowId);

			// notify data has been changed
			getContext().getContentResolver().notifyChange(_newInsertUri, null);
		} else {
			Log.d(LOG_TAG,
					"Insert object to local storage database error, values = "
							+ values);
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
		// approving to-do task
		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASKS:
		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASK:
		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASK_ID:
			// define and get lookup key
			String _lookupKey = null;
			if (TodoTaskApprovingTableAccessType.APPROVINGTODOTASK_ID == URI_MATCHER
					.match(uri)) {
				_lookupKey = ApprovingTodoTask._ID;
			}

			// check lookup key
			if (null != _lookupKey) {
				// get query to-do task id for approving and generate where
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
					"Query user enterprise to-do task for approving with selection = "
							+ selection);

			// set user enterprise to-do for approving task table as query table
			// and to-do task approving notification uri
			_queryTableName = ApprovingTodoTasks.TODOTASKAPPROVING_TABLE;
			_notificationUri = ApprovingTodoTask.APPROVINGTODOTASKS_NOTIFICATION_CONTENT_URI;
			break;

		// generating new approve application
		case NAATaskGeneratingTableAccessType.GENERATINGNAATASKS:
		case NAATaskGeneratingTableAccessType.GENERATINGNAATASK:
		case NAATaskGeneratingTableAccessType.GENERATINGNAATASK_ID:
			// get query new approve application for generating and generate
			// where condition
			if (NAATaskGeneratingTableAccessType.GENERATINGNAATASK_ID == URI_MATCHER
					.match(uri)) {
				_where = GeneratingNAATask._ID + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += GeneratingNAATask._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG,
					"Query user enterprise new approve application for generating with selection = "
							+ selection);

			// set user enterprise new approve application for generating table
			// as query table and new approve application generating
			// notification uri
			_queryTableName = GeneratingNAATasks.NAATASKGENERATING_TABLE;
			_notificationUri = GeneratingNAATask.GENERATINGNAATASKS_NOTIFICATION_CONTENT_URI;
			break;

		// generating new approve application attachment
		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENTS:
		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT:
		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT_ID:
			// get query new approve application attachment for generating and
			// generate where condition
			if (NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT_ID == URI_MATCHER
					.match(uri)) {
				_where = GeneratingNAATaskAttachment._ID + "="
						+ ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += GeneratingNAATaskAttachment._AND_SELECTION
							+ _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG,
					"Query user enterprise new approve application attachment for generating with selection = "
							+ selection);

			// set user enterprise new approve application attachment for
			// generating table as query table and new approve application
			// generating notification uri
			_queryTableName = GeneratingNAATaskAttachments.NAATASKGENERATINGATTACHMENT_TABLE;
			_notificationUri = GeneratingNAATaskAttachment.GENERATINGNAATASKATTACHMENTS_NOTIFICATION_CONTENT_URI;
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
		int _deleteNumber = 0;

		// get local storage database writable instance
		SQLiteDatabase _lswDB = _mLocalStorageDBHelper.getWritableDatabase();

		// define where condition string and delete table name
		String _where = null;
		String _deleteTableName = null;

		// check uri
		switch (URI_MATCHER.match(uri)) {
		// approving to-do task
		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASKS:
		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASK:
			// nothing to do
			break;

		case TodoTaskApprovingTableAccessType.APPROVINGTODOTASK_ID:
			// define and get lookup key
			String _lookupKey = null;
			if (TodoTaskApprovingTableAccessType.APPROVINGTODOTASK_ID == URI_MATCHER
					.match(uri)) {
				_lookupKey = ApprovingTodoTask._ID;
			}

			// check lookup key
			if (null != _lookupKey) {
				// get delete to-do task for approving id and generate where
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
					"Delete user enterprise to-do task for approving with selection = "
							+ selection);

			// set user enterprise to-do task for approving table as delete
			// table
			_deleteTableName = ApprovingTodoTasks.TODOTASKAPPROVING_TABLE;
			break;

		// generating new approve application
		case NAATaskGeneratingTableAccessType.GENERATINGNAATASKS:
		case NAATaskGeneratingTableAccessType.GENERATINGNAATASK:
			// nothing to do
			break;

		case NAATaskGeneratingTableAccessType.GENERATINGNAATASK_ID:
			// get delete new approving application for generating id and
			// generate where condition
			if (NAATaskGeneratingTableAccessType.GENERATINGNAATASK_ID == URI_MATCHER
					.match(uri)) {
				_where = GeneratingNAATask._ID + "=" + ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += GeneratingNAATask._AND_SELECTION + _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG,
					"Delete user enterprise new approve application for generating with selection = "
							+ selection);

			// set user enterprise new approve application for generating table
			// as delete table
			_deleteTableName = GeneratingNAATasks.NAATASKGENERATING_TABLE;
			break;

		// generating new approve application attachment
		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENTS:
		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT:
			// nothing to do
			break;

		case NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT_ID:
			// get delete new approving application attachment for generating id
			// and generate where condition
			if (NAATaskGeneratingAttachmentTableAccessType.GENERATINGNAATASKATTACHMENT_ID == URI_MATCHER
					.match(uri)) {
				_where = GeneratingNAATaskAttachment._ID + "="
						+ ContentUris.parseId(uri);

				// check and update selection
				if (null != selection && !"".equalsIgnoreCase(selection)) {
					selection += GeneratingNAATaskAttachment._AND_SELECTION
							+ _where;
				} else {
					selection = _where;
				}
			}

			Log.d(LOG_TAG,
					"Delete user enterprise new approve application attachment for generating with selection = "
							+ selection);

			// set user enterprise new approve application attachment for
			// generating table as delete table
			_deleteTableName = GeneratingNAATaskAttachments.NAATASKGENERATINGATTACHMENT_TABLE;
			break;

		default:
			throw new UnknownCPContentUriException(uri);
		}

		Log.d(LOG_TAG, "Delete enterprise task for approving with selection = "
				+ selection);

		// delete object from its local storage table with selection
		_deleteNumber = _lswDB.delete(_deleteTableName, selection,
				selectionArgs);

		// notify data has been changed
		getContext().getContentResolver().notifyChange(uri, null);

		return _deleteNumber;
	}

	// inner class
	// user enterprise to-do task approving table access type
	static class TodoTaskApprovingTableAccessType {

		// user enterprise to-do task approving table access type
		private static final int APPROVINGTODOTASKS = 90;
		private static final int APPROVINGTODOTASK = 91;
		private static final int APPROVINGTODOTASK_ID = 92;

	}

	// user enterprise approving to-do tasks
	public static final class ApprovingTodoTasks {

		// user enterprise approving to-do task path
		private static final String PATH = ApprovingTodoTasks.class
				.getCanonicalName();

		// user enterprise to-do task approving table name
		public static final String TODOTASKAPPROVING_TABLE = "ia_todotask_approving";

		// inner class
		// user enterprise approving to-do task
		public static final class ApprovingTodoTask implements
				SimpleBaseColumns {

			// user enterprise task approving content provider to-do task
			// approving process data columns
			public static final String TASK_ID = "taskId";
			public static final String ENTERPRISE_ID = "enterpriseId";
			public static final String APPROVE_NUMBER = "approveNumber";
			public static final String SUBMITCONTACTS = "submitContacts";
			public static final String JUDGE = "judge";
			public static final String ADVICE_INFO = "adviceInfo";
			public static final String SENDER_FAKEID = "taskSenderFakeId";
			public static final String TASK_STATUS = "taskStatus";
			public static final String TASK_OPERATESTATE = "taskOperateState";

			// content uri
			private static final Uri APPROVINGTODOTASKS_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH);
			public static final Uri APPROVINGTODOTASKS_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/approvingTodoTasks");
			public static final Uri APPROVINGTODOTASK_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/approvingTodoTask");

			// content type
			private static final String APPROVINGTODOTASKS_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ ApprovingTodoTasks.class.getCanonicalName();
			private static final String APPROVINGTODOTASK_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ ApprovingTodoTasks.class.getCanonicalName();

			// to-do task for approving operate state ended or not ended
			public static final Integer TASK_OPERATESTATE_NOTENDED = 1;
			public static final Integer TASK_OPERATESTATE_ENDED = 0;

		}

	}

	// user enterprise new approve application generating table access type
	static class NAATaskGeneratingTableAccessType {

		// user enterprise new approve application generating table access type
		private static final int GENERATINGNAATASKS = 94;
		private static final int GENERATINGNAATASK = 95;
		private static final int GENERATINGNAATASK_ID = 96;

	}

	// user enterprise generating new approve application
	public static final class GeneratingNAATasks {

		// user enterprise generating new approve application path
		private static final String PATH = GeneratingNAATasks.class
				.getCanonicalName();

		// user enterprise new approve application generating table name
		public static final String NAATASKGENERATING_TABLE = "ia_approveapplication_generating";

		// inner class
		// user enterprise generating new approve application
		public static final class GeneratingNAATask implements
				SimpleBaseColumns {

			// user enterprise task approving content provider new approve
			// application generating process data columns
			public static final String FORM_ID = "formId";
			public static final String ENTERPRISE_ID = "enterpriseId";
			public static final String APPROVE_NUMBER = "approveNumber";
			public static final String SUBMITCONTACTS = "submitContacts";
			public static final String FORM_NAME = "formName";
			public static final String FORMITEM_VALUE = "formItemValue";
			public static final String FORM_ATTACHMENTPATH = "formAttachmentPath";

			// content uri
			private static final Uri GENERATINGNAATASKS_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH);
			public static final Uri GENERATINGNAATASKS_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/generatingNAATasks");
			public static final Uri GENERATINGNAATASK_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/generatingNAATask");

			// content type
			private static final String GENERATINGNAATASKS_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ GeneratingNAATasks.class.getCanonicalName();
			private static final String GENERATINGNAATASK_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ GeneratingNAATasks.class.getCanonicalName();

		}

	}

	// user enterprise new approve application generating attachment table
	// access type
	static class NAATaskGeneratingAttachmentTableAccessType {

		// user enterprise new approve application generating attachment table
		// access type
		private static final int GENERATINGNAATASKATTACHMENTS = 97;
		private static final int GENERATINGNAATASKATTACHMENT = 98;
		private static final int GENERATINGNAATASKATTACHMENT_ID = 99;

	}

	// user enterprise generating new approve application attachment
	public static final class GeneratingNAATaskAttachments {

		// user enterprise generating new approve application attachment path
		private static final String PATH = GeneratingNAATaskAttachments.class
				.getCanonicalName();

		// user enterprise new approve application generating attachment table
		// name
		public static final String NAATASKGENERATINGATTACHMENT_TABLE = "ia_approveapplication_generating_attachment";

		// inner class
		// user enterprise generating new approve application attachment
		public static final class GeneratingNAATaskAttachment implements
				SimpleBaseColumns {

			// user enterprise task approving content provider new approve
			// application generating attachment process data columns
			public static final String TASK_ID = "taskId";
			public static final String ENTERPRISE_ID = "enterpriseId";
			public static final String APPROVE_NUMBER = "approveNumber";
			public static final String ATTACHMENTPATH = "attachmentPath";

			// content uri
			private static final Uri GENERATINGNAATASKATTACHMENTS_NOTIFICATION_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH);
			public static final Uri GENERATINGNAATASKATTACHMENTS_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/generatingNAATaskAttachments");
			public static final Uri GENERATINGNAATASKATTACHMENT_CONTENT_URI = Uri
					.parse("content://" + AUTHORITY + '/' + PATH
							+ "/generatingNAATaskAttachment");

			// content type
			private static final String GENERATINGNAATASKATTACHMENTS_CONTENT_TYPE = "vnd.android.cursor.dir/"
					+ GeneratingNAATaskAttachments.class.getCanonicalName();
			private static final String GENERATINGNAATASKATTACHMENT_CONTENT_TYPE = "vnd.android.cursor.item/"
					+ GeneratingNAATaskAttachments.class.getCanonicalName();

			// user enterprise new approve application generating attachment
			// condition
			public static final String USER_ENTERPRISENAATASK_ATTACHMENTS_WITHTASKID_CONDITION = TASK_ID
					+ "=?";

		}

	}

}
