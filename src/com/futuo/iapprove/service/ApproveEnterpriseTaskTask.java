package com.futuo.iapprove.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.provider.UserEnterpriseTaskApprovingContentProvider.ApprovingTodoTasks.ApprovingTodoTask;
import com.futuo.iapprove.provider.UserEnterpriseTaskApprovingContentProvider.GeneratingNAATaskAttachments.GeneratingNAATaskAttachment;
import com.futuo.iapprove.provider.UserEnterpriseTaskApprovingContentProvider.GeneratingNAATasks.GeneratingNAATask;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTasks.TodoTask;
import com.futuo.iapprove.utils.HttpRequestParamUtils;
import com.futuo.iapprove.utils.UploadFileUtils;
import com.futuo.iapprove.utils.UploadFileUtils.UploadFileHttpRequestListener;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.CommonUtils;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class ApproveEnterpriseTaskTask extends CoreServiceTask {

	private static final String LOG_TAG = ApproveEnterpriseTaskTask.class
			.getCanonicalName();

	@Override
	protected void enterpriseChanged() {
		// execute immediately
		execute();
	}

	@Override
	protected void execute() {
		Log.d(LOG_TAG, "Query user enterprise tasks for approving");

		// query user enterprise tasks for approving in work thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				// get local storage user enterprise to-do tasks for approving
				Cursor _cursor = _mContentResolver.query(
						ApprovingTodoTask.APPROVINGTODOTASKS_CONTENT_URI, null,
						null, null, null);

				// check cursor
				if (null != _cursor) {
					// get and check user enterprise to-do tasks for approving
					while (_cursor.moveToNext()) {
						// get to-do task for approving local storage row id
						Long _todoTaskLSRowId = _cursor.getLong(_cursor
								.getColumnIndex(ApprovingTodoTask._ID));

						// get to-do task for approving task id, enterprise id,
						// submit contacts, judge, advice info, sender fake id
						// and status
						Long _todoTaskId = _cursor.getLong(_cursor
								.getColumnIndex(ApprovingTodoTask.TASK_ID));
						Long _todoTaskEnterpriseId = _cursor.getLong(_cursor
								.getColumnIndex(ApprovingTodoTask.ENTERPRISE_ID));
						String _todoTaskSubmitContacts = _cursor.getString(_cursor
								.getColumnIndex(ApprovingTodoTask.SUBMITCONTACTS));
						Integer _todoTaskJudge = _cursor.getInt(_cursor
								.getColumnIndex(ApprovingTodoTask.JUDGE));
						String _todoTaskAdviceInfo = _cursor.getString(_cursor
								.getColumnIndex(ApprovingTodoTask.ADVICE_INFO));
						Long _todoTaskSenderFakeId = _cursor.getLong(_cursor
								.getColumnIndex(ApprovingTodoTask.SENDER_FAKEID));
						Integer _todoTaskStatus = _cursor.getInt(_cursor
								.getColumnIndex(ApprovingTodoTask.TASK_STATUS));

						// get to-do task approving post http request param
						Map<String, String> _todoTaskApprovingPostHttpReqParam = HttpRequestParamUtils
								.genUserSigHttpReqParam();

						// put to-do task approving post http request param
						// enterprise id, task id, judge, advice info, sender
						// fake id and status
						_todoTaskApprovingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_enterpriseId),
										StringUtils
												.base64Encode(_todoTaskEnterpriseId
														.toString()));
						_todoTaskApprovingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_taskId),
										StringUtils.base64Encode(_todoTaskId
												.toString()));
						_todoTaskApprovingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_myJudge),
										StringUtils.base64Encode(_todoTaskJudge
												.toString()));
						_todoTaskApprovingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_myAdvices),
										StringUtils
												.base64Encode(_todoTaskAdviceInfo));
						_todoTaskApprovingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_taskSenderFakeId),
										StringUtils
												.base64Encode(_todoTaskSenderFakeId
														.toString()));
						_todoTaskApprovingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_userEnterpriseTodoListTaskApprove6FinApproveReqParam_taskStatus),
										StringUtils
												.base64Encode(_todoTaskStatus
														.toString()));

						// get and check to-do task for approving operate state
						Integer _todoTaskOperateState = _cursor.getInt(_cursor
								.getColumnIndex(ApprovingTodoTask.TASK_OPERATESTATE));
						// set to-do task approving post http request param
						// action
						if (ApprovingTodoTask.TASK_OPERATESTATE_NOTENDED == _todoTaskOperateState
								.intValue()) {
							_todoTaskApprovingPostHttpReqParam
									.put(_mContext
											.getResources()
											.getString(
													R.string.rbgServer_commonReqParam_action),
											_mContext
													.getResources()
													.getString(
															R.string.rbgServer_userEnterpriseTodoListTaskApproveReqParam_action));
							// set to-do task approving post http request param
							// submit contacts
							_todoTaskApprovingPostHttpReqParam
									.put(_mContext
											.getResources()
											.getString(
													R.string.rbgServer_userEnterpriseTodoListTaskApproveReqParam_submitContacts),
											StringUtils
													.base64Encode(_todoTaskSubmitContacts));
						} else if (ApprovingTodoTask.TASK_OPERATESTATE_ENDED == _todoTaskOperateState
								.intValue()) {
							_todoTaskApprovingPostHttpReqParam
									.put(_mContext
											.getResources()
											.getString(
													R.string.rbgServer_commonReqParam_action),
											_mContext
													.getResources()
													.getString(
															R.string.rbgServer_userEnterpriseTodoListTaskFinishApproveReqParam_action));
						}

						// send to-do task approving post http request
						HttpUtils
								.postRequest(
										_mContext.getResources().getString(
												R.string.server_url)
												+ _mContext
														.getResources()
														.getString(
																R.string.userEnterpriseTodoTask_approve_url),
										PostRequestFormat.URLENCODED,
										_todoTaskApprovingPostHttpReqParam,
										null,
										HttpRequestType.ASYNCHRONOUS,
										new ApproveUserEnterpriseTodoTaskPostHttpRequestListener(
												_todoTaskLSRowId,
												_todoTaskSenderFakeId));
					}

					// close cursor
					_cursor.close();
				}

				// get local storage user enterprise new approve applications
				// for generate
				_cursor = _mContentResolver.query(
						GeneratingNAATask.GENERATINGNAATASKS_CONTENT_URI, null,
						null, null, null);

				// check cursor
				if (null != _cursor) {
					// get and check user enterprise new approve applications
					// for generating
					while (_cursor.moveToNext()) {
						// get new approve application for generating local
						// storage row id
						Long _naaLSRowId = _cursor.getLong(_cursor
								.getColumnIndex(GeneratingNAATask._ID));

						// get new approve application for generating form id,
						// enterprise id, submit contacts, form name, form item
						// value and attachment path
						Long _naaFormId = _cursor.getLong(_cursor
								.getColumnIndex(GeneratingNAATask.FORM_ID));
						Long _naaEnterpriseId = _cursor.getLong(_cursor
								.getColumnIndex(GeneratingNAATask.ENTERPRISE_ID));
						String _naaSubmitContacts = _cursor.getString(_cursor
								.getColumnIndex(GeneratingNAATask.SUBMITCONTACTS));
						String _naaFormName = _cursor.getString(_cursor
								.getColumnIndex(GeneratingNAATask.FORM_NAME));
						String _naaFormItemValue = _cursor.getString(_cursor
								.getColumnIndex(GeneratingNAATask.FORMITEM_VALUE));
						String _naaAttachmentPath = _cursor.getString(_cursor
								.getColumnIndex(GeneratingNAATask.FORM_ATTACHMENTPATH));

						// get new approve application generating post http
						// request param
						Map<String, String> _naaGeneratingPostHttpReqParam = HttpRequestParamUtils
								.genUserSigHttpReqParam();

						// put new approve application generating post http
						// request param action, enterprise id, form id, submit
						// contacts, form name and form item value
						_naaGeneratingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_commonReqParam_action),
										_mContext
												.getResources()
												.getString(
														R.string.rbgServer_newUserEnterpriseApproveApplicationReqParam_action));
						_naaGeneratingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_newUserEnterpriseApproveApplicationReqParam_enterpriseId),
										StringUtils
												.base64Encode(_naaEnterpriseId
														.toString()));
						_naaGeneratingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_newUserEnterpriseApproveApplicationReqParam_formId),
										StringUtils.base64Encode(_naaFormId
												.toString()));
						_naaGeneratingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_newUserEnterpriseApproveApplicationReqParam_submitContacts),
										StringUtils
												.base64Encode(_naaSubmitContacts));
						_naaGeneratingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_newUserEnterpriseApproveApplicationReqParam_formName),
										StringUtils.base64Encode(_naaFormName));
						_naaGeneratingPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_newUserEnterpriseApproveApplicationReqParam_formItemValue),
										StringUtils
												.base64Encode(_naaFormItemValue));

						// send new approve application generating post http
						// request
						HttpUtils
								.postRequest(
										_mContext.getResources().getString(
												R.string.server_url)
												+ _mContext
														.getResources()
														.getString(
																R.string.new_userEnterpriseApproveApplication_url),
										PostRequestFormat.URLENCODED,
										_naaGeneratingPostHttpReqParam,
										null,
										HttpRequestType.ASYNCHRONOUS,
										new GenerateUserEnterpriseNAAPostHttpRequestListener(
												_naaLSRowId, _naaAttachmentPath));
					}

					// close cursor
					_cursor.close();
				}

				// get local storage user enterprise new approve application
				// attachments for generate
				_cursor = _mContentResolver
						.query(GeneratingNAATaskAttachment.GENERATINGNAATASKATTACHMENTS_CONTENT_URI,
								null, null, null, null);

				// check cursor
				if (null != _cursor) {
					// get and check user enterprise new approve application
					// attachments for generating
					while (_cursor.moveToNext()) {
						// get new approve application attachment for generating
						// local storage row id
						Long _naaAttachmentLSRowId = _cursor.getLong(_cursor
								.getColumnIndex(GeneratingNAATaskAttachment._ID));

						// get new approve application attachment for generating
						// task id, enterprise id, approve number and attachment
						// path
						Long _naaAttachmentTaskId = _cursor.getLong(_cursor
								.getColumnIndex(GeneratingNAATaskAttachment.TASK_ID));
						Long _naaAttachmentEnterpriseId = _cursor.getLong(_cursor
								.getColumnIndex(GeneratingNAATaskAttachment.ENTERPRISE_ID));
						Long _naaAttachmentApproveNumber = _cursor.getLong(_cursor
								.getColumnIndex(GeneratingNAATaskAttachment.APPROVE_NUMBER));
						String _naaAttachmentPath = _cursor.getString(_cursor
								.getColumnIndex(GeneratingNAATaskAttachment.ATTACHMENT_PATH));
						Boolean _naaAttachmentUploading = 0 != _cursor.getShort(_cursor
								.getColumnIndex(GeneratingNAATaskAttachment.ATTACHMENT_UPLOADING)) ? true
								: false;

						// check new approve application attachment uploading
						// flag
						if (!_naaAttachmentUploading) {
							// define new approve application attachment update
							// content values
							ContentValues _naaAttachmentUpdateContentValues = new ContentValues();
							_naaAttachmentUpdateContentValues
									.put(GeneratingNAATaskAttachment.ATTACHMENT_UPLOADING,
											Boolean.valueOf(true));

							// upload the new approve application attachment
							// uploading flag
							_mContentResolver.update(
									ContentUris
											.withAppendedId(
													GeneratingNAATaskAttachment.GENERATINGNAATASKATTACHMENT_CONTENT_URI,
													_naaAttachmentLSRowId),
									_naaAttachmentUpdateContentValues, null,
									null);

							// upload new approve application attachment
							// generate upload new approve application
							// attachment request param
							Map<String, Object> _uploadNAAAttachmentRequestParam = new HashMap<String, Object>();

							_uploadNAAAttachmentRequestParam
									.put(_mContext
											.getResources()
											.getString(
													R.string.rbgServer_uploadNewUserEnterpriseApproveApplicationReqParam_enterpriseId),
											StringUtils
													.base64Encode(_naaAttachmentEnterpriseId
															.toString()));
							_uploadNAAAttachmentRequestParam
									.put(_mContext
											.getResources()
											.getString(
													R.string.rbgServer_uploadNewUserEnterpriseApproveApplicationReqParam_approveNumber),
											StringUtils
													.base64Encode(_naaAttachmentApproveNumber
															.toString()));
							_uploadNAAAttachmentRequestParam
									.put(_mContext
											.getResources()
											.getString(
													R.string.rbgServer_uploadNewUserEnterpriseApproveApplicationReqParam_taskId),
											_naaAttachmentTaskId.toString());

							// define upload new approve application request url
							StringBuilder _uploadNAAAttachmentRequestUrlStringBuilder = new StringBuilder(
									_mContext.getResources().getString(
											R.string.server_url)
											+ _mContext
													.getResources()
													.getString(
															R.string.upload_userEnterpriseApproveApplicationAttachment_url));
							_uploadNAAAttachmentRequestUrlStringBuilder
									.append('?');
							for (String _uploadNAAAttachmentRequestParamKey : _uploadNAAAttachmentRequestParam
									.keySet()) {
								_uploadNAAAttachmentRequestUrlStringBuilder
										.append(_uploadNAAAttachmentRequestParamKey)
										.append('=')
										.append(_uploadNAAAttachmentRequestParam
												.get(_uploadNAAAttachmentRequestParamKey))
										.append('&');
							}
							String _uploadNAAAttachmentRequestUrl = _uploadNAAAttachmentRequestUrlStringBuilder
									.substring(0,
											_uploadNAAAttachmentRequestUrlStringBuilder
													.length() - 1);
							;

							Map<String, File> _uploadNAAAttachmentRequestFileParam = new HashMap<String, File>();
							_uploadNAAAttachmentRequestFileParam
									.put(_mContext
											.getResources()
											.getString(
													R.string.rbgServer_uploadNewUserEnterpriseApproveApplicationReqParam_fileName),
											new File(_naaAttachmentPath));

							// send upload new approve application attachment
							// post http request
							try {
								UploadFileUtils
										.post(_uploadNAAAttachmentRequestUrl,
												null,
												_uploadNAAAttachmentRequestFileParam,
												new UploadUserEnterpriseNAAAttachmentPostHttpRequestListener(
														_naaAttachmentLSRowId,
														_naaAttachmentTaskId));
							} catch (IOException e) {
								Log.e(LOG_TAG,
										"Upload new approve application attachment error, exception message = "
												+ e.getMessage());

								e.printStackTrace();
							}
						}
					}

					// close cursor
					_cursor.close();
				}
			}

		}).start();
	}

	// inner class
	// approve user enterprise to-do task post http request listener
	class ApproveUserEnterpriseTodoTaskPostHttpRequestListener extends
			OnHttpRequestListener {

		// to-do task approving local storage row id
		private Long _mTodoTaskLocalStorageRowId;

		// to-do task sender fake id
		private Long _mTodoTaskSenderFakeId;

		public ApproveUserEnterpriseTodoTaskPostHttpRequestListener(
				Long todoTaskLocalStorageRowId, Long todoTaskSenderFakeId) {
			super();

			// save to-do task approving local storage row id and sender fake id
			_mTodoTaskLocalStorageRowId = todoTaskLocalStorageRowId;
			_mTodoTaskSenderFakeId = todoTaskSenderFakeId;
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// delete the to-do task for approving local storage data
			_mContentResolver
					.delete(ContentUris.withAppendedId(
							TodoTask.ENTERPRISE_CONTENT_URI, IAUserExtension
									.getUserLoginEnterpriseId(UserManager
											.getInstance().getUser())),
							TodoTask.APPROVE_USER_ENTERPRISETODOLISTTASK_WITHSENDERFAKEID_CONDITION,
							new String[] { _mTodoTaskSenderFakeId.toString() });
			_mContentResolver.delete(ContentUris.withAppendedId(
					ApprovingTodoTask.APPROVINGTODOTASK_CONTENT_URI,
					_mTodoTaskLocalStorageRowId), null, null);

			// synchronized enterprise server data
			// define and initialize synchronized enterprise server data
			// post http request param
			Map<String, String> _synchronizedEnterpriseServerDataPostHttpReqParam = new HashMap<String, String>();
			_synchronizedEnterpriseServerDataPostHttpReqParam
					.put(_mContext
							.getResources()
							.getString(
									R.string.rbgServer_synchronizedEnterpriseServerDataReqParam_enterpriseId),
							StringUtils.base64Encode(_mEnterpriseId.toString()));

			// send synchronized enterprise server data post http request
			HttpUtils.postRequest(
					_mContext.getResources().getString(R.string.server_url)
							+ _mContext.getResources().getString(
									R.string.syncEnterpriseData_url),
					PostRequestFormat.URLENCODED,
					_synchronizedEnterpriseServerDataPostHttpReqParam, null,
					HttpRequestType.ASYNCHRONOUS, null);
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			// nothing to do
		}

	}

	// generate user enterprise new approve application post http request
	// listener
	class GenerateUserEnterpriseNAAPostHttpRequestListener extends
			OnHttpRequestListener {

		// new approve application generating local storage row id and
		// attachment path
		private Long _mNAALocalStorageRowId;
		private String _mNAALocalStorageAttachmentPath;

		// new approve application task id
		private Long _mNAATaskId;

		public GenerateUserEnterpriseNAAPostHttpRequestListener(
				Long naaLocalStorageRowId, String naaLocalStorageAttachmentPath) {
			super();

			// save new approve application generating local storage row id and
			// attachment path
			_mNAALocalStorageRowId = naaLocalStorageRowId;
			_mNAALocalStorageAttachmentPath = naaLocalStorageAttachmentPath;
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send new approve application post http request successful, response entity string = "
							+ _respEntityString);

			// get new approve application task id
			_mNAATaskId = Long.parseLong(_respEntityString);

			// delete the new approve application for generating local storage
			// data
			_mContentResolver.delete(ContentUris.withAppendedId(
					GeneratingNAATask.GENERATINGNAATASK_CONTENT_URI,
					_mNAALocalStorageRowId), null, null);

			// get new approve application generating local storage attachment
			// path list
			@SuppressWarnings("unchecked")
			List<String> _naaLocalStorageAttachmentPathList = (List<String>) CommonUtils
					.array2List(StringUtils.split(
							_mNAALocalStorageAttachmentPath, ","));

			if (null == _naaLocalStorageAttachmentPathList
					|| 0 == _naaLocalStorageAttachmentPathList.size()) {
				// synchronized enterprise new approve application attachment
				// define and initialize synchronized enterprise new approve
				// application attachment post http request param
				Map<String, String> _synchronizedEnterpriseNAAAttachmentPostHttpReqParam = new HashMap<String, String>();
				_synchronizedEnterpriseNAAAttachmentPostHttpReqParam
						.put(_mContext.getResources().getString(
								R.string.rbgServer_commonReqParam_action),
								_mContext
										.getResources()
										.getString(
												R.string.rbgServer_synchronizedNewEnterpriseApproveApplicationAttachmentReqParam_action));
				_synchronizedEnterpriseNAAAttachmentPostHttpReqParam
						.put(_mContext
								.getResources()
								.getString(
										R.string.rbgServer_synchronizedNewEnterpriseApproveApplicationAttachmentReqParam_taskId),
								StringUtils.base64Encode(_mNAATaskId.toString()));

				// send synchronized enterprise new approve application
				// attachment post http request
				HttpUtils
						.postRequest(
								_mContext.getResources().getString(
										R.string.server_url)
										+ _mContext
												.getResources()
												.getString(
														R.string.syncNewEnterpriseApproveApplicationAttachment_url),
								PostRequestFormat.URLENCODED,
								_synchronizedEnterpriseNAAAttachmentPostHttpReqParam,
								null,
								HttpRequestType.ASYNCHRONOUS,
								new SynchronizedEnterpriseNAAAttachmentPostHttpRequestListener());
			} else {
				// insert new approve application attachment to local storage
				ContentValues _insertContentValues = new ContentValues();
				_insertContentValues.put(GeneratingNAATaskAttachment.TASK_ID,
						_mNAATaskId.toString());
				_insertContentValues.put(
						GeneratingNAATaskAttachment.ENTERPRISE_ID,
						_mEnterpriseId.toString());
				_insertContentValues.put(
						GeneratingNAATaskAttachment.APPROVE_NUMBER, UserManager
								.getInstance().getUser().getName());

				for (String naaTaskAttachmentPath : _naaLocalStorageAttachmentPathList) {
					_insertContentValues.put(
							GeneratingNAATaskAttachment.ATTACHMENT_PATH,
							naaTaskAttachmentPath);

					_mContentResolver
							.insert(GeneratingNAATaskAttachment.GENERATINGNAATASKATTACHMENTS_CONTENT_URI,
									_insertContentValues);
				}
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			// nothing to do
		}

	}

	// upload user enterprise new approve application attachment post http
	// request listener
	class UploadUserEnterpriseNAAAttachmentPostHttpRequestListener extends
			UploadFileHttpRequestListener {

		// new approve application attachment generating local storage row id
		// and task id
		private Long _mNAAAttachmentLocalStorageRowId;
		private Long _mNAAAttachmentLocalStorageTaskId;

		public UploadUserEnterpriseNAAAttachmentPostHttpRequestListener(
				Long naaAttachmentLocalStorageRowId,
				Long naaAttachmentLocalStorageTaskId) {
			super();

			// save new approve application attachment generating local storage
			// row id and task id
			_mNAAAttachmentLocalStorageRowId = naaAttachmentLocalStorageRowId;
			_mNAAAttachmentLocalStorageTaskId = naaAttachmentLocalStorageTaskId;
		}

		@Override
		public void onFinished(String responseString) {
			// delete the new approve application attachment for generating
			// local storage data
			_mContentResolver
					.delete(ContentUris
							.withAppendedId(
									GeneratingNAATaskAttachment.GENERATINGNAATASKATTACHMENT_CONTENT_URI,
									_mNAAAttachmentLocalStorageRowId), null,
							null);

			// query the new approve application attachment left
			Cursor _cursor = _mContentResolver
					.query(GeneratingNAATaskAttachment.GENERATINGNAATASKATTACHMENTS_CONTENT_URI,
							new String[] { GeneratingNAATaskAttachment._COUNT_PROJECTION },
							GeneratingNAATaskAttachment.USER_ENTERPRISENAATASK_ATTACHMENTS_WITHTASKID_CONDITION,
							new String[] { _mNAAAttachmentLocalStorageTaskId
									.toString() }, null);

			// check cursor
			if (null != _cursor) {
				// get and check the new approve application attachments count
				while (_cursor.moveToNext()) {
					if (0 == _cursor
							.getLong(_cursor
									.getColumnIndex(GeneratingNAATaskAttachment._COUNT))) {
						// synchronized enterprise new approve application
						// attachment
						// define and initialize synchronized enterprise new
						// approve application attachment post http request
						// param
						Map<String, String> _synchronizedEnterpriseNAAAttachmentPostHttpReqParam = new HashMap<String, String>();
						_synchronizedEnterpriseNAAAttachmentPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_commonReqParam_action),
										_mContext
												.getResources()
												.getString(
														R.string.rbgServer_synchronizedNewEnterpriseApproveApplicationAttachmentReqParam_action));
						_synchronizedEnterpriseNAAAttachmentPostHttpReqParam
								.put(_mContext
										.getResources()
										.getString(
												R.string.rbgServer_synchronizedNewEnterpriseApproveApplicationAttachmentReqParam_taskId),
										StringUtils
												.base64Encode(_mNAAAttachmentLocalStorageTaskId
														.toString()));

						// send synchronized enterprise new approve application
						// attachment post http request
						HttpUtils
								.postRequest(
										_mContext.getResources().getString(
												R.string.server_url)
												+ _mContext
														.getResources()
														.getString(
																R.string.syncNewEnterpriseApproveApplicationAttachment_url),
										PostRequestFormat.URLENCODED,
										_synchronizedEnterpriseNAAAttachmentPostHttpReqParam,
										null,
										HttpRequestType.ASYNCHRONOUS,
										new SynchronizedEnterpriseNAAAttachmentPostHttpRequestListener());
					}
				}

				// close cursor
				_cursor.close();
			}
		}

		@Override
		public void onFailed(Integer responseCode) {
			// define new approve application attachment update
			// content values
			ContentValues _naaAttachmentUpdateContentValues = new ContentValues();
			_naaAttachmentUpdateContentValues.put(
					GeneratingNAATaskAttachment.ATTACHMENT_UPLOADING,
					Boolean.valueOf(false));

			// upload the new approve application attachment
			// uploading flag
			_mContentResolver
					.update(ContentUris
							.withAppendedId(
									GeneratingNAATaskAttachment.GENERATINGNAATASKATTACHMENT_CONTENT_URI,
									_mNAAAttachmentLocalStorageRowId),
							_naaAttachmentUpdateContentValues, null, null);
		}

	}

	// synchronized enterprise new approve application attachment post http
	// request listener
	class SynchronizedEnterpriseNAAAttachmentPostHttpRequestListener extends
			OnHttpRequestListener {

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send synchronized enterprise new approve application attachment post http request successful, response entity string = "
							+ _respEntityString);

			// get and check http response entity string error json data
			JSONObject _respJsonData = JSONUtils
					.toJSONObject(_respEntityString);

			if (null != _respJsonData) {
				// get and check error message
				String _errorMsg = JSONUtils.getStringFromJSONObject(
						_respJsonData,
						_mContext.getResources().getString(
								R.string.rbgServer_commonReqResp_error));

				if (null != _errorMsg) {
					Log.e(LOG_TAG,
							"Synchronized enterprise new approve application attachment failed, response error message = "
									+ _errorMsg);

					// return immediately
					return;
				}
			}

			// synchronized enterprise server data
			// define and initialize synchronized enterprise server data
			// post http request param
			Map<String, String> _synchronizedEnterpriseServerDataPostHttpReqParam = new HashMap<String, String>();
			_synchronizedEnterpriseServerDataPostHttpReqParam
					.put(_mContext
							.getResources()
							.getString(
									R.string.rbgServer_synchronizedEnterpriseServerDataReqParam_enterpriseId),
							StringUtils.base64Encode(_mEnterpriseId.toString()));

			// send synchronized enterprise server data post http request
			HttpUtils.postRequest(
					_mContext.getResources().getString(R.string.server_url)
							+ _mContext.getResources().getString(
									R.string.syncEnterpriseData_url),
					PostRequestFormat.URLENCODED,
					_synchronizedEnterpriseServerDataPostHttpReqParam, null,
					HttpRequestType.ASYNCHRONOUS, null);
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			// nothing to do
		}

	}

}
