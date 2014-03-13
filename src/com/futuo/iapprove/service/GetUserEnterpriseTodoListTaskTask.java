package com.futuo.iapprove.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTaskAttachments.TodoTaskAttachment;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTaskFormItems.TodoTaskFormItem;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTasks.TodoTask;
import com.futuo.iapprove.receiver.EnterpriseTodoTaskBroadcastReceiver;
import com.futuo.iapprove.tab7tabcontent.IApproveTabActivity;
import com.futuo.iapprove.task.IApproveTaskAdviceBean;
import com.futuo.iapprove.task.IApproveTaskAttachmentBean;
import com.futuo.iapprove.task.IApproveTaskAttachmentDownloadStatus;
import com.futuo.iapprove.task.IApproveTaskAttachmentType;
import com.futuo.iapprove.task.IApproveTaskFormItemBean;
import com.futuo.iapprove.task.TodoTaskBean;
import com.futuo.iapprove.task.TodoTaskStatus;
import com.futuo.iapprove.utils.HttpRequestParamUtils;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class GetUserEnterpriseTodoListTaskTask extends CoreServiceTask {

	private static final String LOG_TAG = GetUserEnterpriseTodoListTaskTask.class
			.getCanonicalName();

	// get user enterprise to-do list task and its form info post http request
	// parameter
	private Map<String, String> _mGetUserEnterpriseTodoListTaskPostHttpReqParam;
	private Map<String, String> _mGetUserEnterpriseTodoListTaskFormInfoPostHttpReqParam;

	@Override
	protected void enterpriseChanged() {
		Log.d(LOG_TAG, "Query user enterprise to-do list tasks count");

		// query user enterprise to-do list tasks count in work thread
		new Thread(new Runnable() {

			@Override
			public void run() {
				// get local storage to-do list tasks count
				Cursor _cursor = _mContentResolver.query(
						ContentUris
								.withAppendedId(
										TodoTask.ENTERPRISE_CONTENT_URI,
										_mEnterpriseId),
						new String[] { TodoTask._COUNT_PROJECTION },
						TodoTask.USER_ENTERPRISETODOLISTTASKS_WITHLOGINNAME_CONDITION,
						new String[] { UserManager.getInstance().getUser()
								.getName() }, null);

				// check cursor
				if (null != _cursor) {
					// get and check to-do list tasks count
					while (_cursor.moveToNext()) {
						if (0 == _cursor.getLong(_cursor
								.getColumnIndex(TodoTask._COUNT))) {
							// get user enterprise to-do list task immediately,
							// not update the next scheduled time
							execute();
						} else {
							Log.d(LOG_TAG,
									"Not need to get user enterprise to-do list task immediately, wait for next timer task period");
						}
					}

					// close cursor
					_cursor.close();
				}
			}

		}).start();
	}

	@Override
	protected void execute() {
		// send get user enterprise to-do list task post http request
		HttpUtils.postRequest(
				_mContext.getResources().getString(R.string.server_url)
						+ _mContext.getResources().getString(
								R.string.get_userEnterpriseTodoListTask_url),
				PostRequestFormat.URLENCODED,
				generateGetUserEnterpriseTodoListTaskPostHttpReqParam(), null,
				HttpRequestType.ASYNCHRONOUS,
				new GetUserEnterpriseTodoListTaskPostHttpRequestListener());
	}

	// generate get user enterprise to-do list task post http request param
	private Map<String, String> generateGetUserEnterpriseTodoListTaskPostHttpReqParam() {
		// check get user enterprise to-do list task post http request parameter
		if (null == _mGetUserEnterpriseTodoListTaskPostHttpReqParam) {
			_mGetUserEnterpriseTodoListTaskPostHttpReqParam = HttpRequestParamUtils
					.genUserSigHttpReqParam();

			// put get user enterprise to-do list task action and state in
			_mGetUserEnterpriseTodoListTaskPostHttpReqParam
					.put(_mContext.getResources().getString(
							R.string.rbgServer_commonReqParam_action),
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getUserEnterpriseTodoListTaskReqParam_action));
			_mGetUserEnterpriseTodoListTaskPostHttpReqParam
					.put(_mContext.getResources().getString(
							R.string.rbgServer_getIApproveReqParam_state),
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getUserEnterpriseTodoListTaskReqParam_state));
		}

		// put get user enterprise to-do list task user enterprise id in
		_mGetUserEnterpriseTodoListTaskPostHttpReqParam.put(
				_mContext.getResources().getString(
						R.string.rbgServer_getIApproveReqParam_enterpriseId),
				StringUtils.base64Encode(_mEnterpriseId.toString()));

		return _mGetUserEnterpriseTodoListTaskPostHttpReqParam;
	}

	// get local storage user enterprise to-do list task sender fake id as key
	// and bean as value map
	private Map<Long, TodoTaskBean> getLocalStorageUETodoTasksSenderFakeIdAndBeanMap(
			Cursor cursor) {
		Map<Long, TodoTaskBean> _localStorageUETodoTasksSenderFakeIdAndBeanMap = new HashMap<Long, TodoTaskBean>();

		// check the cursor
		if (null != cursor) {
			// set all local storage user enterprise to-do list task for
			// deleting
			while (cursor.moveToNext()) {
				// get for deleting to-do list task
				TodoTaskBean _4deletingTodoTask = new TodoTaskBean(cursor);

				// set it for deleting
				_4deletingTodoTask
						.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

				// put to-do task sender fake id and bean in
				_localStorageUETodoTasksSenderFakeIdAndBeanMap.put(
						_4deletingTodoTask.getSenderFakeId(),
						_4deletingTodoTask);
			}

			// close cursor
			cursor.close();
		}

		return _localStorageUETodoTasksSenderFakeIdAndBeanMap;
	}

	// get user enterprise to-do list task form info with task id, sender fake
	// id and status
	private void getUETodoTaskFormInfoWithTaskId7SenderFakeId7Status(
			Long taskId, Long senderFakeId, TodoTaskStatus taskStatus) {
		// check task id, sender fake id and status
		if (null != taskId && null != senderFakeId && null != taskStatus) {
			// send get user enterprise to-do list task form info post http
			// request
			HttpUtils
					.postRequest(
							_mContext.getResources().getString(
									R.string.server_url)
									+ _mContext
											.getResources()
											.getString(
													R.string.get_iApproveListTaskFormInfo_url),
							PostRequestFormat.URLENCODED,
							generateGetUETodoTaskFormInfoPostHttpReqParam(
									taskId, senderFakeId, taskStatus),
							null,
							HttpRequestType.ASYNCHRONOUS,
							new GetUserEnterpriseTodoTaskFormInfoPostHttpRequestListener(
									senderFakeId));
		} else {
			Log.e(LOG_TAG,
					"Get user enterprise to-do list task form info error, task id = "
							+ taskId + ", sender fake id = " + senderFakeId
							+ " and status = " + taskStatus);
		}
	}

	// generate get user enterprise to-do list task form info post http request
	// param
	private Map<String, String> generateGetUETodoTaskFormInfoPostHttpReqParam(
			Long taskId, Long senderFakeId, TodoTaskStatus taskStatus) {
		// check get user enterprise to-do list task post http request parameter
		if (null == _mGetUserEnterpriseTodoListTaskFormInfoPostHttpReqParam) {
			_mGetUserEnterpriseTodoListTaskFormInfoPostHttpReqParam = HttpRequestParamUtils
					.genUserSigHttpReqParam();

			// put get user enterprise to-do list task form info action in
			_mGetUserEnterpriseTodoListTaskFormInfoPostHttpReqParam
					.put(_mContext.getResources().getString(
							R.string.rbgServer_commonReqParam_action),
							_mContext
									.getResources()
									.getString(
											R.string.rbgServer_getUserEnterpriseTodoTaskFormInfoReqParam_action));
		}

		// put get user enterprise to-do list task form info user enterprise id
		// in
		_mGetUserEnterpriseTodoListTaskFormInfoPostHttpReqParam.put(
				_mContext.getResources().getString(
						R.string.rbgServer_getIApproveReqParam_enterpriseId),
				StringUtils.base64Encode(_mEnterpriseId.toString()));

		// put get user enterprise to-do list task form info task id, sender
		// fake id and status in
		_mGetUserEnterpriseTodoListTaskFormInfoPostHttpReqParam
				.put(_mContext
						.getResources()
						.getString(
								R.string.rbgServer_getIApproveListTaskFormInfoReqParam_taskId),
						StringUtils.base64Encode(taskId.toString()));
		_mGetUserEnterpriseTodoListTaskFormInfoPostHttpReqParam
				.put(_mContext
						.getResources()
						.getString(
								R.string.rbgServer_getIApproveListTaskFormInfoReqParam_taskSenderFakeId),
						StringUtils.base64Encode(senderFakeId.toString()));
		_mGetUserEnterpriseTodoListTaskFormInfoPostHttpReqParam
				.put(_mContext
						.getResources()
						.getString(
								R.string.rbgServer_getIApproveListTaskFormInfoReqParam_taskStatus),
						StringUtils.base64Encode(taskStatus.getValue()
								.toString()));

		return _mGetUserEnterpriseTodoListTaskFormInfoPostHttpReqParam;
	}

	// get local storage user enterprise to-do list task form items(task form
	// item id as key and bean as value map) or attachments(task attachment id
	// as key and bean as value map) with cursor and bean class
	private Map<Long, ?> getLocalStorageUETodoTaskFI6ASIdAndBeanMap(
			Cursor cursor, Class<?> beanCls) {
		Map<Long, Object> _localStorageUETodoTaskFI6ASIdAndBeanMap = new HashMap<Long, Object>();

		// check the cursor
		if (null != cursor) {
			// set all local storage user enterprise to-do list task form items
			// or attachments for deleting
			while (cursor.moveToNext()) {
				// check bean class
				if (IApproveTaskFormItemBean.class == beanCls) {
					// get for deleting to-do list task form item
					IApproveTaskFormItemBean _4deletingTodoTaskFormItem = IApproveTaskFormItemBean
							.getTaskFormItem(cursor);

					// set it for deleting
					_4deletingTodoTaskFormItem
							.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

					// put to-do task form item id and bean in
					_localStorageUETodoTaskFI6ASIdAndBeanMap.put(
							_4deletingTodoTaskFormItem.getItemId(),
							_4deletingTodoTaskFormItem);
				} else if (IApproveTaskAttachmentBean.class == beanCls) {
					// get for deleting to-do list task attachment
					IApproveTaskAttachmentBean _4deletingTodoTaskAttachment = IApproveTaskAttachmentBean
							.getTaskAttachment(cursor);

					// set it for deleting
					_4deletingTodoTaskAttachment
							.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

					// put to-do task attachment id and bean in
					_localStorageUETodoTaskFI6ASIdAndBeanMap.put(
							_4deletingTodoTaskAttachment.getAttachmentId(),
							_4deletingTodoTaskAttachment);
				} else {
					Log.e(LOG_TAG, "Unrecognized bean class = " + beanCls);

					// break immediately
					break;
				}
			}

			// close cursor
			cursor.close();
		}

		return _localStorageUETodoTaskFI6ASIdAndBeanMap;
	}

	// download to-do list task attachment to local file path with attachment
	// url
	@SuppressWarnings("resource")
	private String downloadTodoTaskAttachment2LocalFilePath(
			Long attachmentLSRowId, IApproveTaskAttachmentBean attachmentObject) {
		String _attachmentLocalFilePath = null;

		// check attachment object
		if (null != attachmentObject) {
			// define download attachemnt http request connection
			HttpURLConnection _attachmentDownloadHttpUrlConnection = null;

			// get attachment remote url
			String _attachmentRemoteUrl = attachmentObject
					.getAttachmentRemoteUrl();

			try {
				// define to-do list task attachment local storage file name
				StringBuilder _attachmentLocalStorageFileName = new StringBuilder();
				_attachmentLocalStorageFileName
						.append(System.currentTimeMillis()).append('.')
						.append(attachmentObject.getAttachmentSuffix());
				Log.d(LOG_TAG,
						"To-do list task attachment local storage file name = "
								+ _attachmentLocalStorageFileName
								+ " and remote url = " + _attachmentRemoteUrl);

				// initialize to-do list task attachment url
				URL _attachmentUrl = new URL(_attachmentRemoteUrl);

				// open to-do list task attachment download http url connection
				_attachmentDownloadHttpUrlConnection = (HttpURLConnection) _attachmentUrl
						.openConnection();

				// define the to-do list task attachment content values
				ContentValues _todoTaskAttachmentContentValues = new ContentValues();

				// generate the to-do list task attachment content values with
				// attachment downloading flag
				_todoTaskAttachmentContentValues.put(
						TodoTaskAttachment.DOWNLOADSTATUS,
						IApproveTaskAttachmentDownloadStatus.DOWNLOADING
								.getValue());

				_mContentResolver.update(ContentUris.withAppendedId(
						TodoTaskAttachment.TODOTASKATTACHMENT_CONTENT_URI,
						attachmentLSRowId), _todoTaskAttachmentContentValues,
						null, null);

				// get to-do list task attachment input stream
				InputStream _is = _attachmentDownloadHttpUrlConnection
						.getInputStream();

				// define external local storage file and file output stream
				File _file = null;
				FileOutputStream _fos = null;

				// check attachment type and suffix, then get to-do list task
				// attachment local storage file output stream
				if (IApproveTaskAttachmentType.COMMON_FILE == attachmentObject
						.getAttachmentType()
						&& !"txt".equalsIgnoreCase(attachmentObject
								.getAttachmentSuffix())) {
					// check external writeable environment
					if (!Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						Log.e(LOG_TAG,
								"SD card is not avaiable/writeable right now.");

						return _attachmentLocalFilePath;
					} else {
						String pathName = Environment
								.getExternalStorageDirectory().getPath()
								+ "/iApprove/";
						File path = new File(pathName);
						_file = new File(pathName
								+ _attachmentLocalStorageFileName);
						if (!path.exists()) {
							Log.d(LOG_TAG, "Create the path:" + pathName);

							path.mkdir();
						}
						if (!_file.exists()) {
							Log.d(LOG_TAG, "Create the file:"
									+ _attachmentLocalStorageFileName);

							_file.createNewFile();
						}

						_fos = new FileOutputStream(_file);
					}
				} else {
					_fos = _mContext.openFileOutput(
							_attachmentLocalStorageFileName.toString(),
							Context.MODE_PRIVATE);
				}

				// define stream read buffer byte array and has read flag
				byte[] _buffer = new byte[1024];
				int _hasRead = 0;

				// read to-do list task attachment input stream and write to
				// local storage file output stream
				while (0 < (_hasRead = _is.read(_buffer))) {
					_fos.write(_buffer, 0, _hasRead);
				}

				// check attachment type and suffix again, then get to-do list
				// task attachment local storage file path
				if (IApproveTaskAttachmentType.COMMON_FILE == attachmentObject
						.getAttachmentType()
						&& !"txt".equalsIgnoreCase(attachmentObject
								.getAttachmentSuffix())) {
					_attachmentLocalFilePath = _file.getAbsolutePath();
				} else {
					_attachmentLocalFilePath = _mContext.getFileStreamPath(
							_attachmentLocalStorageFileName.toString())
							.getAbsolutePath();
				}

				// close to-do list task attachment input stream and local
				// storage file output stream
				_is.close();
				_fos.close();

				//
				_attachmentDownloadHttpUrlConnection.disconnect();

				Log.d(LOG_TAG,
						"To-do list task attachment download finished, attachment remote url = "
								+ _attachmentUrl
								+ " and local storage file path = "
								+ _attachmentLocalFilePath);
			} catch (IOException e) {
				Log.e(LOG_TAG,
						"Download to-do list task attachment failed, exception message = "
								+ e.getMessage());

				//
				if (null != _attachmentDownloadHttpUrlConnection) {
					try {
						// get and check response code
						int _responseCode = _attachmentDownloadHttpUrlConnection
								.getResponseCode();

						// define the to-do list task attachment content values
						ContentValues _todoTaskAttachmentContentValues = new ContentValues();

						if (HttpStatus.SC_NOT_FOUND == _responseCode) {
							// generate the to-do list task attachment content
							// values
							// with attachment downloading flag
							_todoTaskAttachmentContentValues
									.put(TodoTaskAttachment.DOWNLOADSTATUS,
											IApproveTaskAttachmentDownloadStatus.DOWNLOAD_FAILED
													.getValue());
						} else if (HttpStatus.SC_INTERNAL_SERVER_ERROR == _responseCode) {
							// generate the to-do list task attachment content
							// values
							// with attachment downloading flag
							_todoTaskAttachmentContentValues.put(
									TodoTaskAttachment.DOWNLOADSTATUS,
									IApproveTaskAttachmentDownloadStatus.NORMAL
											.getValue());
						}

						_mContentResolver
								.update(ContentUris
										.withAppendedId(
												TodoTaskAttachment.TODOTASKATTACHMENT_CONTENT_URI,
												attachmentLSRowId),
										_todoTaskAttachmentContentValues, null,
										null);

						_attachmentDownloadHttpUrlConnection.disconnect();
					} catch (IOException e1) {
						Log.e(LOG_TAG,
								"Get download to-do list task attachment request response code error, exception message = "
										+ e.getMessage());

						e1.printStackTrace();
					}
				}

				e.printStackTrace();
			}
		} else {
			Log.e(LOG_TAG,
					"Get download to-do list task attachment error, attachment object = "
							+ attachmentObject);
		}

		return _attachmentLocalFilePath;
	}

	// inner class
	// get user enterprise to-do list task post http request listener
	class GetUserEnterpriseTodoListTaskPostHttpRequestListener extends
			OnHttpRequestListener {

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// send enterprise to-do task done refreshed broadcast
			// define enterprise to-do task done refreshed broadcast intent
			Intent _enterpriseTodoTaskDoneRefreshedBroadcastIntent = new Intent(
					EnterpriseTodoTaskBroadcastReceiver.A_FORMCHANGE);

			// set enterprise to-do task done refreshed message
			_enterpriseTodoTaskDoneRefreshedBroadcastIntent.putExtra(
					EnterpriseTodoTaskBroadcastReceiver.EK_FORMREFRESHED, true);

			// send normal broadcast
			_mContext
					.sendBroadcast(_enterpriseTodoTaskDoneRefreshedBroadcastIntent);

			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send get user enterprise to-do list task post http request successful, response entity string = "
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
							"Get user enterprise to-do list task failed, response error message = "
									+ _errorMsg);
				} else {
					// get and check to-do list tasks
					final JSONArray _todoListTaskJsonArray = JSONUtils
							.getJSONArrayFromJSONObject(
									_respJsonData,
									_mContext
											.getResources()
											.getString(
													R.string.rbgServer_getIApproveListReqResp_taskList));
					if (null != _todoListTaskJsonArray) {
						Log.d(LOG_TAG,
								"Get user enterprise to-do list task successful");

						// process user enterprise to-do list tasks in work
						// thread
						new Thread(new Runnable() {

							@SuppressWarnings("deprecation")
							@Override
							public void run() {
								// get login user name
								String _loginUserName = UserManager
										.getInstance().getUser().getName();

								// get local storage user enterprise to-do list
								// tasks sender fake id as key and bean as value
								// map
								Map<Long, TodoTaskBean> _localStorageUETodoTasksSenderFakeIdAndBeanMap = getLocalStorageUETodoTasksSenderFakeIdAndBeanMap(_mContentResolver.query(
										ContentUris
												.withAppendedId(
														TodoTask.ENTERPRISE_CONTENT_URI,
														_mEnterpriseId),
										null,
										TodoTask.USER_ENTERPRISETODOLISTTASKS_WITHLOGINNAME_CONDITION,
										new String[] { _loginUserName }, null));

								// define the new insert user enterprise to-do
								// list task
								List<TodoTaskBean> _newInsertTodoTaskList = new ArrayList<TodoTaskBean>();

								// define the to-do task content values
								ContentValues _todoTaskContentValues = new ContentValues();

								for (int i = 0; i < _todoListTaskJsonArray
										.length(); i++) {
									// clear the to-do task content values
									_todoTaskContentValues.clear();

									// get user enterprise each to-do list task
									TodoTaskBean _todoTask = new TodoTaskBean(
											JSONUtils
													.getJSONObjectFromJSONArray(
															_todoListTaskJsonArray,
															i));

									// generate the to-do list task content
									// values with to-do list task task id, task
									// title, applicant name, sender fake id,
									// task status and submit timestamp
									_todoTaskContentValues.put(
											TodoTask.TASK_ID,
											_todoTask.getTaskId());
									_todoTaskContentValues.put(
											TodoTask.TASK_TITLE,
											_todoTask.getTaskTitle());
									_todoTaskContentValues.put(
											TodoTask.APPLICANTNAME,
											_todoTask.getApplicantName());
									_todoTaskContentValues.put(
											TodoTask.SENDERFAKEID,
											_todoTask.getSenderFakeId());
									_todoTaskContentValues
											.put(TodoTask.TASK_STATUS,
													_todoTask.getTaskStatus()
															.getValue());
									_todoTaskContentValues.put(
											TodoTask.SUBMITTIMESTAMP,
											_todoTask.getSubmitTimestamp());

									// append to-do list task advices
									for (int j = 0; j < _todoTask.getAdvices()
											.size(); j++) {
										// get to-do list task each advice
										IApproveTaskAdviceBean _advice = _todoTask
												.getAdvices().get(j);

										_todoTaskContentValues.put(
												String.format(
														TodoTask.ADVICE_ADVISORID_FORMAT,
														j), _advice
														.getAdvisorId());
										_todoTaskContentValues.put(
												String.format(
														TodoTask.ADVICE_ADVISORNAME_FORMAT,
														j), _advice
														.getAdvisorName());
										_todoTaskContentValues.put(
												String.format(
														TodoTask.ADVICE_ADVICESTATE_FORMAT,
														j),
												_advice.modified() ? TodoTask.ADVICE_MODIFIED_STATE
														: (_advice.agreed() ? TodoTask.ADVICE_AGREED_STATE
																: TodoTask.ADVICE_DISAGREED_STATE));
										_todoTaskContentValues.put(
												String.format(
														TodoTask.ADVICE_ADVICECONTENT_FORMAT,
														j), _advice.getAdvice());
										_todoTaskContentValues.put(
												String.format(
														TodoTask.ADVICE_ADVICEGIVENTIMESTAMP_FORMAT,
														j),
												_advice.getAdviceGivenTimestamp());
									}

									// append to-do list task enterprise id and
									// approve number
									_todoTaskContentValues.put(
											TodoTask.ENTERPRISE_ID,
											_mEnterpriseId);
									_todoTaskContentValues.put(
											TodoTask.APPROVE_NUMBER,
											_loginUserName);

									// insert the to-do list task if not existed
									// in local storage database or update if
									// existed
									if (!_localStorageUETodoTasksSenderFakeIdAndBeanMap
											.keySet()
											.contains(
													_todoTask.getSenderFakeId())) {
										// add the to-do task to new insert list
										_newInsertTodoTaskList.add(_todoTask);

										// check the first
										if (0 == i) {
											// send enterprise to-do task begin
											// fetching broadcast
											// define enterprise to-do task
											// begin fetching broadcast intent
											Intent _enterpriseTodoTaskBeginFetchingBroadcastIntent = new Intent(
													EnterpriseTodoTaskBroadcastReceiver.A_FORMCHANGE);

											// set enterprise to-do task begin
											// fetching message
											_enterpriseTodoTaskBeginFetchingBroadcastIntent
													.putExtra(
															EnterpriseTodoTaskBroadcastReceiver.EK_FORMFETCHING,
															true);
											_enterpriseTodoTaskBeginFetchingBroadcastIntent
													.putExtra(
															EnterpriseTodoTaskBroadcastReceiver.EK_FORM_BEGINFETCHING,
															true);

											// send normal broadcast
											_mContext
													.sendBroadcast(_enterpriseTodoTaskBeginFetchingBroadcastIntent);
										}

										Log.d(LOG_TAG,
												"The to-do list task = "
														+ _todoTask
														+ " for inserting into local storage database, its content values = "
														+ _todoTaskContentValues);

										_mContentResolver.insert(
												TodoTask.TODOTASK_CONTENT_URI,
												_todoTaskContentValues);
									} else {
										// get for updating to-do list task
										TodoTaskBean _4updatingTodoTask = _localStorageUETodoTasksSenderFakeIdAndBeanMap
												.get(_todoTask
														.getSenderFakeId());

										// update local storage user enterprise
										// to-do list task normal
										_4updatingTodoTask
												.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.NORMAL);

										// compare the got to-do list task with
										// the for updating to-do list task
										if (0 != _todoTask
												.compareTo(_4updatingTodoTask)) {
											Log.d(LOG_TAG,
													"The to-do list task whose sender fake id = "
															+ _4updatingTodoTask
																	.getSenderFakeId()
															+ " for updating to local storage database, its content values = "
															+ _todoTaskContentValues);

											_mContentResolver.update(
													ContentUris
															.withAppendedId(
																	TodoTask.TODOTASK_CONTENT_URI,
																	_4updatingTodoTask
																			.getRowId()),
													_todoTaskContentValues,
													null, null);
										}
									}

									// check the last
									if (_todoListTaskJsonArray.length() - 1 == i) {
										// send enterprise to-do task end
										// fetching broadcast
										// define enterprise to-do task end
										// fetching broadcast intent
										Intent _enterpriseTodoTaskEndFetchingBroadcastIntent = new Intent(
												EnterpriseTodoTaskBroadcastReceiver.A_FORMCHANGE);

										// set enterprise to-do task end
										// fetching message
										_enterpriseTodoTaskEndFetchingBroadcastIntent
												.putExtra(
														EnterpriseTodoTaskBroadcastReceiver.EK_FORMFETCHING,
														true);
										_enterpriseTodoTaskEndFetchingBroadcastIntent
												.putExtra(
														EnterpriseTodoTaskBroadcastReceiver.EK_FORM_ENDFETCHING,
														true);

										// send normal broadcast
										_mContext
												.sendBroadcast(_enterpriseTodoTaskEndFetchingBroadcastIntent);

										// check new insert to-do task list
										if (!_newInsertTodoTaskList.isEmpty()) {
											// define there is new to-do list
											// task notification
											Notification _newTodoTaskNotification = new Notification();

											// set icon, ticker text, timestamp
											// and default
											_newTodoTaskNotification.icon = R.drawable.ic_launcher;
											_newTodoTaskNotification.tickerText = String
													.format(_mContext
															.getResources()
															.getString(
																	R.string.tdl_newIncomingTask_notification_format),
															_newInsertTodoTaskList
																	.size());
											_newTodoTaskNotification.when = System
													.currentTimeMillis();
											_newTodoTaskNotification.defaults = Notification.DEFAULT_ALL;

											// add pending intent
											PendingIntent _pendingIntent = PendingIntent
													.getActivity(
															_mContext,
															0,
															new Intent(
																	_mContext,
																	IApproveTabActivity.class),
															PendingIntent.FLAG_CANCEL_CURRENT);

											// get the last insert to-do task
											TodoTaskBean _lastInsertTodoTask = _newInsertTodoTaskList
													.get(_newInsertTodoTaskList
															.size() - 1);

											// set latest event info
											_newTodoTaskNotification
													.setLatestEventInfo(
															_mContext,
															_lastInsertTodoTask
																	.getTaskTitle(),
															_lastInsertTodoTask
																	.getApplicantName(),
															_pendingIntent);

											// send there is new to-do list task
											// notification
											((NotificationManager) _mContext
													.getSystemService(Context.NOTIFICATION_SERVICE))
													.notify(R.string.app_name,
															_newTodoTaskNotification);
										}
									}
								}

								// delete the local storage user enterprise
								// to-do list task for synchronizing
								for (Long _localStorageUETodoTaskSenderFakeId : _localStorageUETodoTasksSenderFakeIdAndBeanMap
										.keySet()) {
									// get the for deleting to-do list task
									TodoTaskBean _4deletingTodoTask = _localStorageUETodoTasksSenderFakeIdAndBeanMap
											.get(_localStorageUETodoTaskSenderFakeId);

									// check its data dirty type
									if (LocalStorageDataDirtyType.DELETE == _4deletingTodoTask
											.getLocalStorageDataDirtyType()) {
										Log.d(LOG_TAG,
												"The to-do list task whose sender fake id = "
														+ _4deletingTodoTask
																.getSenderFakeId()
														+ " will delete from local storage database");

										_mContentResolver.delete(
												ContentUris
														.withAppendedId(
																TodoTask.TODOTASK_CONTENT_URI,
																_4deletingTodoTask
																		.getRowId()),
												null, null);
									}
								}
							}

						}).start();

						// get user enterprise to-do list task form item info
						// with task id, sender fake id and status
						for (int i = 0; i < _todoListTaskJsonArray.length(); i++) {
							// get user enterprise each to-do list task again
							TodoTaskBean _todoTask = new TodoTaskBean(
									JSONUtils.getJSONObjectFromJSONArray(
											_todoListTaskJsonArray, i));

							// get enterprise form info with form id and its
							// type id
							getUETodoTaskFormInfoWithTaskId7SenderFakeId7Status(
									_todoTask.getTaskId(),
									_todoTask.getSenderFakeId(),
									_todoTask.getTaskStatus());
						}
					} else {
						Log.e(LOG_TAG,
								"Get user enterprise to-do list task failed, response entity unrecognized");
					}
				}
			} else {
				Log.e(LOG_TAG,
						"Get user enterprise to-do list task failed, response entity unrecognized");
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			// send enterprise to-do task done refreshed broadcast
			// define enterprise to-do task done refreshed broadcast intent
			Intent _enterpriseTodoTaskDoneRefreshedBroadcastIntent = new Intent(
					EnterpriseTodoTaskBroadcastReceiver.A_FORMCHANGE);

			// set enterprise to-do task done refreshed message
			_enterpriseTodoTaskDoneRefreshedBroadcastIntent.putExtra(
					EnterpriseTodoTaskBroadcastReceiver.EK_FORMREFRESHED, true);

			// send normal broadcast
			_mContext
					.sendBroadcast(_enterpriseTodoTaskDoneRefreshedBroadcastIntent);

			Log.e(LOG_TAG,
					"Send get user enterprise to-do list task post http request failed");
		}

	}

	// get user enterprise to-do list task form info post http request listener
	class GetUserEnterpriseTodoTaskFormInfoPostHttpRequestListener extends
			OnHttpRequestListener {

		// user enterprise to-do list task sender fake id
		private Long _mTaskSenderFakeId;

		public GetUserEnterpriseTodoTaskFormInfoPostHttpRequestListener(
				Long taskSenderFakeId) {
			super();

			// save user enterprise to-do list task sender fake id
			_mTaskSenderFakeId = taskSenderFakeId;
		}

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"Send get user enterprise to-do list task form info post http request successful, response entity string = "
							+ _respEntityString);

			// get and check http response entity string error json data
			final JSONObject _respJsonData = JSONUtils
					.toJSONObject(_respEntityString);

			if (null != _respJsonData) {
				// get and check error message
				String _errorMsg = JSONUtils.getStringFromJSONObject(
						_respJsonData,
						_mContext.getResources().getString(
								R.string.rbgServer_commonReqResp_error));

				if (null != _errorMsg) {
					Log.e(LOG_TAG,
							"Get enterprise form info failed, response error message = "
									+ _errorMsg);
				} else {
					Log.d(LOG_TAG,
							"Get user enterprise to-do list task form info successful");

					// process user enterprise to-do list task form info in work
					// thread
					new Thread(new Runnable() {

						@Override
						public void run() {
							// get login user name
							String _loginUserName = UserManager.getInstance()
									.getUser().getName();

							// get local storage user enterprise to-do list task
							// form items task form item id as key and bean as
							// value map
							@SuppressWarnings("unchecked")
							Map<Long, IApproveTaskFormItemBean> _localStorageUETodoTaskFormItemsItemIdAndBeanMap = (Map<Long, IApproveTaskFormItemBean>) getLocalStorageUETodoTaskFI6ASIdAndBeanMap(
									_mContentResolver
											.query(TodoTaskFormItem.TODOTASKFORMITEMS_CONTENT_URI,
													null,
													TodoTaskFormItem.USER_ENTERPRISETODOLISTTASK_FORMITEMS_WITHSENDERFAKEID7LOGINNAME_CONDITION,
													new String[] {
															_mEnterpriseId
																	.toString(),
															_mTaskSenderFakeId
																	.toString(),
															_loginUserName },
													null),
									IApproveTaskFormItemBean.class);

							// define the to-do list task form item content
							// values
							ContentValues _todoTaskFormItemContentValues = new ContentValues();

							// get and process to-do list task form items
							for (IApproveTaskFormItemBean todoTaskFormItem : IApproveTaskFormItemBean
									.getTaskFormItems(_respJsonData)) {
								// clear the to-do list task form item content
								// values
								_todoTaskFormItemContentValues.clear();

								// generate the to-do list task form item
								// content values with item id, name and info
								_todoTaskFormItemContentValues.put(
										TodoTaskFormItem.ITEM_ID,
										todoTaskFormItem.getItemId());
								_todoTaskFormItemContentValues.put(
										TodoTaskFormItem.NAME,
										todoTaskFormItem.getItemName());
								_todoTaskFormItemContentValues.put(
										TodoTaskFormItem.INFO,
										todoTaskFormItem.getItemInfo());
								_todoTaskFormItemContentValues.put(
										TodoTaskFormItem.CAPITAL_FLAG,
										todoTaskFormItem.itemNeedCapital());

								// append task form item form sender fake id,
								// enterprise id and approve number
								_todoTaskFormItemContentValues.put(
										TodoTaskFormItem.SENDER_FAKEID,
										_mTaskSenderFakeId.toString());
								_todoTaskFormItemContentValues.put(
										TodoTaskFormItem.ENTERPRISE_ID,
										_mEnterpriseId);
								_todoTaskFormItemContentValues.put(
										TodoTaskFormItem.APPROVE_NUMBER,
										_loginUserName);

								// insert the to-do list task form item if not
								// existed in local storage database or update
								// if existed
								if (!_localStorageUETodoTaskFormItemsItemIdAndBeanMap
										.keySet().contains(
												todoTaskFormItem.getItemId())) {
									Log.d(LOG_TAG,
											"The to-do list task form item = "
													+ todoTaskFormItem
													+ " for inserting into local storage database, its content values = "
													+ _todoTaskFormItemContentValues);

									_mContentResolver
											.insert(TodoTaskFormItem.TODOTASKFORMITEM_CONTENT_URI,
													_todoTaskFormItemContentValues);
								} else {
									// get for updating to-do list task form
									// item
									IApproveTaskFormItemBean _4updatingTodoTaskFormItem = _localStorageUETodoTaskFormItemsItemIdAndBeanMap
											.get(todoTaskFormItem.getItemId());

									// update local storage enterprise to-do
									// list task form item normal
									_4updatingTodoTaskFormItem
											.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.NORMAL);

									// compare the got to-do list task form item
									// with the for updating to-do list task
									// form item
									if (0 != todoTaskFormItem
											.compareTo(_4updatingTodoTaskFormItem)) {
										Log.d(LOG_TAG,
												"The to-do list task form item whose id = "
														+ _4updatingTodoTaskFormItem
																.getItemId()
														+ " for updating to local storage database, its content values = "
														+ _todoTaskFormItemContentValues);

										_mContentResolver.update(
												ContentUris
														.withAppendedId(
																TodoTaskFormItem.TODOTASKFORMITEM_CONTENT_URI,
																_4updatingTodoTaskFormItem
																		.getRowId()),
												_todoTaskFormItemContentValues,
												null, null);
									}
								}
							}

							// delete the local storage enterprise to-do list
							// task form item for synchronizing
							for (Long _localStorageUETDTFISFormItemId : _localStorageUETodoTaskFormItemsItemIdAndBeanMap
									.keySet()) {
								// get the for deleting to-do list task form
								// item
								IApproveTaskFormItemBean _4deletingTodoTaskFormItem = _localStorageUETodoTaskFormItemsItemIdAndBeanMap
										.get(_localStorageUETDTFISFormItemId);

								// check its data dirty type
								if (LocalStorageDataDirtyType.DELETE == _4deletingTodoTaskFormItem
										.getLocalStorageDataDirtyType()) {
									Log.d(LOG_TAG,
											"The to-do list task form item whose id = "
													+ _4deletingTodoTaskFormItem
															.getItemId()
													+ " will delete from local storage database");

									_mContentResolver.delete(
											ContentUris
													.withAppendedId(
															TodoTaskFormItem.TODOTASKFORMITEM_CONTENT_URI,
															_4deletingTodoTaskFormItem
																	.getRowId()),
											null, null);
								}
							}

							// send enterprise to-do task form item changed
							// broadcast
							// define enterprise to-do task form item changed
							// broadcast intent
							Intent _enterpriseTodoTaskFormItemChangedBroadcastIntent = new Intent(
									EnterpriseTodoTaskBroadcastReceiver.A_FORMITEMCHANGE);

							// set enterprise to-do task form item changed
							// message
							_enterpriseTodoTaskFormItemChangedBroadcastIntent
									.putExtra(
											EnterpriseTodoTaskBroadcastReceiver.EK_FORMITEM6ATTACHMENTCHANGED,
											true);
							_enterpriseTodoTaskFormItemChangedBroadcastIntent
									.putExtra(
											EnterpriseTodoTaskBroadcastReceiver.EK_CHANGEDEDFORMSENDERFAKEID,
											_mTaskSenderFakeId);

							// send normal broadcast
							_mContext
									.sendBroadcast(_enterpriseTodoTaskFormItemChangedBroadcastIntent);

							// get local storage user enterprise to-do list task
							// attachments task attachment id as key and bean as
							// value map
							@SuppressWarnings("unchecked")
							Map<Long, IApproveTaskAttachmentBean> _localStorageUETodoTaskAttachmentsAttachmentIdAndBeanMap = (Map<Long, IApproveTaskAttachmentBean>) getLocalStorageUETodoTaskFI6ASIdAndBeanMap(
									_mContentResolver
											.query(TodoTaskAttachment.TODOTASKATTACHMENTS_CONTENT_URI,
													null,
													TodoTaskAttachment.USER_ENTERPRISETODOLISTTASK_ATTACHMENTS_WITHSENDERFAKEID7LOGINNAME_CONDITION,
													new String[] {
															_mEnterpriseId
																	.toString(),
															_mTaskSenderFakeId
																	.toString(),
															_loginUserName },
													null),
									IApproveTaskAttachmentBean.class);

							// define the to-do list task attachment content
							// values
							ContentValues _todoTaskAttachmentContentValues = new ContentValues();

							// get and process to-do list task attachments
							for (final IApproveTaskAttachmentBean todoTaskAttachment : IApproveTaskAttachmentBean
									.getTaskAttachments(_respJsonData)) {
								// clear the to-do list task attachment content
								// values
								_todoTaskAttachmentContentValues.clear();

								// generate the to-do list task attachment
								// content values with attachment id, name,
								// origin name and type
								_todoTaskAttachmentContentValues.put(
										TodoTaskAttachment.ATTACHMENT_ID,
										todoTaskAttachment.getAttachmentId());
								_todoTaskAttachmentContentValues.put(
										TodoTaskAttachment.NAME,
										todoTaskAttachment.getAttachmentName());
								_todoTaskAttachmentContentValues.put(
										TodoTaskAttachment.ORIGINNAME,
										todoTaskAttachment
												.getAttachmentOriginName());
								_todoTaskAttachmentContentValues.put(
										TodoTaskAttachment.SUFFIX,
										todoTaskAttachment
												.getAttachmentSuffix());
								_todoTaskAttachmentContentValues.put(
										TodoTaskAttachment.TYPE,
										todoTaskAttachment.getAttachmentType()
												.getValue());

								// check task attachment type
								if (IApproveTaskAttachmentType.COMMON_TEXT == todoTaskAttachment
										.getAttachmentType()) {
									// add url
									_todoTaskAttachmentContentValues.put(
											TodoTaskAttachment.URL,
											todoTaskAttachment
													.getAttachmentRemoteUrl());
								}

								// append task attachment form sender fake id,
								// enterprise id and approve number
								_todoTaskAttachmentContentValues.put(
										TodoTaskAttachment.SENDER_FAKEID,
										_mTaskSenderFakeId.toString());
								_todoTaskAttachmentContentValues.put(
										TodoTaskAttachment.ENTERPRISE_ID,
										_mEnterpriseId);
								_todoTaskAttachmentContentValues.put(
										TodoTaskAttachment.APPROVE_NUMBER,
										_loginUserName);

								// insert the to-do list task attachment if not
								// existed in local storage database or update
								// if existed
								if (!_localStorageUETodoTaskAttachmentsAttachmentIdAndBeanMap
										.keySet().contains(
												todoTaskAttachment
														.getAttachmentId())) {
									Log.d(LOG_TAG,
											"The to-do list task attachment = "
													+ todoTaskAttachment
													+ " for inserting into local storage database, its content values = "
													+ _todoTaskAttachmentContentValues);

									final Long _newInsertTodoTaskAttachmentLSRowId = ContentUris.parseId(_mContentResolver
											.insert(TodoTaskAttachment.TODOTASKATTACHMENT_CONTENT_URI,
													_todoTaskAttachmentContentValues));

									// check task attachment type
									if (IApproveTaskAttachmentType.COMMON_TEXT != todoTaskAttachment
											.getAttachmentType()) {
										// download to-do list task attachment
										// in work thread
										new Thread(new Runnable() {

											@Override
											public void run() {
												// download to-do list task
												// attachment to local file path
												String _attachmentLocalStorageFilePath = downloadTodoTaskAttachment2LocalFilePath(
														_newInsertTodoTaskAttachmentLSRowId,
														todoTaskAttachment);

												// check to-do list task
												// attachment to local file path
												if (null != _attachmentLocalStorageFilePath) {
													// define the to-do list
													// task attachment content
													// values
													ContentValues _todoTaskAttachmentContentValues = new ContentValues();

													// generate the to-do list
													// task attachment content
													// values with attachment
													// url
													_todoTaskAttachmentContentValues
															.put(TodoTaskAttachment.DOWNLOADSTATUS,
																	IApproveTaskAttachmentDownloadStatus.NORMAL
																			.getValue());
													_todoTaskAttachmentContentValues
															.put(TodoTaskAttachment.URL,
																	_attachmentLocalStorageFilePath);

													Log.d(LOG_TAG,
															"The to-do list task attachment for updating to local storage database, its content values = "
																	+ _todoTaskAttachmentContentValues);

													_mContentResolver.update(
															ContentUris
																	.withAppendedId(
																			TodoTaskAttachment.TODOTASKATTACHMENT_CONTENT_URI,
																			_newInsertTodoTaskAttachmentLSRowId),
															_todoTaskAttachmentContentValues,
															null, null);

													// send enterprise to-do
													// task form attachment
													// changed broadcast
													// define enterprise to-do
													// task form attachment
													// changed broadcast intent
													Intent _enterpriseTodoTaskFormAttachmentChangedBroadcastIntent = new Intent(
															EnterpriseTodoTaskBroadcastReceiver.A_FORMATTACHMENTCHANGE);

													// set enterprise to-do task
													// form attachment changed
													// message
													_enterpriseTodoTaskFormAttachmentChangedBroadcastIntent
															.putExtra(
																	EnterpriseTodoTaskBroadcastReceiver.EK_FORMITEM6ATTACHMENTCHANGED,
																	true);
													_enterpriseTodoTaskFormAttachmentChangedBroadcastIntent
															.putExtra(
																	EnterpriseTodoTaskBroadcastReceiver.EK_CHANGEDEDFORMSENDERFAKEID,
																	_mTaskSenderFakeId);
													_enterpriseTodoTaskFormAttachmentChangedBroadcastIntent
															.putExtra(
																	EnterpriseTodoTaskBroadcastReceiver.EK_CHANGEDEDFORMATTACHMENTID,
																	todoTaskAttachment
																			.getAttachmentId());

													// send normal broadcast
													_mContext
															.sendBroadcast(_enterpriseTodoTaskFormAttachmentChangedBroadcastIntent);
												}
											}

										}).start();
									}
								} else {
									// get for updating to-do list task
									// attachment
									final IApproveTaskAttachmentBean _4updatingTodoTaskAttachment = _localStorageUETodoTaskAttachmentsAttachmentIdAndBeanMap
											.get(todoTaskAttachment
													.getAttachmentId());

									// update local storage enterprise to-do
									// list task attachment normal
									_4updatingTodoTaskAttachment
											.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.NORMAL);

									// compare the got to-do list task
									// attachment with the for updating to-do
									// list task attachment
									if (0 != todoTaskAttachment
											.compareTo(_4updatingTodoTaskAttachment)) {
										Log.d(LOG_TAG,
												"The to-do list task attachment whose id = "
														+ _4updatingTodoTaskAttachment
																.getAttachmentId()
														+ " for updating to local storage database, its content values = "
														+ _todoTaskAttachmentContentValues);

										_mContentResolver.update(
												ContentUris
														.withAppendedId(
																TodoTaskAttachment.TODOTASKATTACHMENT_CONTENT_URI,
																_4updatingTodoTaskAttachment
																		.getRowId()),
												_todoTaskAttachmentContentValues,
												null, null);
									}

									// check for updating to-do list task
									// attachment url and download to-do list
									// task attachment in work thread
									if (IApproveTaskAttachmentDownloadStatus.NORMAL == _4updatingTodoTaskAttachment
											.getAttachmentDownloadStatus()
											&& null == _4updatingTodoTaskAttachment
													.getAttachmentUrl()) {
										new Thread(new Runnable() {

											@Override
											public void run() {
												// download to-do list task
												// attachment to local file path
												String _attachmentLocalStorageFilePath = downloadTodoTaskAttachment2LocalFilePath(
														_4updatingTodoTaskAttachment
																.getRowId(),
														todoTaskAttachment);

												// check to-do list task
												// attachment to local file path
												if (null != _attachmentLocalStorageFilePath) {
													// define the to-do list
													// task attachment content
													// values
													ContentValues _todoTaskAttachmentContentValues = new ContentValues();

													// generate the to-do list
													// task attachment content
													// values with attachment
													// url
													_todoTaskAttachmentContentValues
															.put(TodoTaskAttachment.DOWNLOADSTATUS,
																	IApproveTaskAttachmentDownloadStatus.NORMAL
																			.getValue());
													_todoTaskAttachmentContentValues
															.put(TodoTaskAttachment.URL,
																	_attachmentLocalStorageFilePath);

													Log.d(LOG_TAG,
															"The to-do list task attachment whose id = "
																	+ _4updatingTodoTaskAttachment
																			.getAttachmentId()
																	+ " for updating to local storage database, its content values = "
																	+ _todoTaskAttachmentContentValues);

													_mContentResolver.update(
															ContentUris
																	.withAppendedId(
																			TodoTaskAttachment.TODOTASKATTACHMENT_CONTENT_URI,
																			_4updatingTodoTaskAttachment
																					.getRowId()),
															_todoTaskAttachmentContentValues,
															null, null);

													// send enterprise to-do
													// task form attachment
													// changed broadcast
													// define enterprise to-do
													// task form attachment
													// changed broadcast intent
													Intent _enterpriseTodoTaskFormAttachmentChangedBroadcastIntent = new Intent(
															EnterpriseTodoTaskBroadcastReceiver.A_FORMATTACHMENTCHANGE);

													// set enterprise to-do task
													// form attachment changed
													// message
													_enterpriseTodoTaskFormAttachmentChangedBroadcastIntent
															.putExtra(
																	EnterpriseTodoTaskBroadcastReceiver.EK_FORMITEM6ATTACHMENTCHANGED,
																	true);
													_enterpriseTodoTaskFormAttachmentChangedBroadcastIntent
															.putExtra(
																	EnterpriseTodoTaskBroadcastReceiver.EK_CHANGEDEDFORMSENDERFAKEID,
																	_mTaskSenderFakeId);
													_enterpriseTodoTaskFormAttachmentChangedBroadcastIntent
															.putExtra(
																	EnterpriseTodoTaskBroadcastReceiver.EK_CHANGEDEDFORMATTACHMENTID,
																	_4updatingTodoTaskAttachment
																			.getAttachmentId());

													// send normal broadcast
													_mContext
															.sendBroadcast(_enterpriseTodoTaskFormAttachmentChangedBroadcastIntent);
												}
											}

										}).start();
									}
								}
							}

							// delete the local storage enterprise to-do list
							// task attachment for synchronizing
							for (Long _localStorageUETDTASAttachmentId : _localStorageUETodoTaskAttachmentsAttachmentIdAndBeanMap
									.keySet()) {
								// get the for deleting to-do list task
								// attachment
								IApproveTaskAttachmentBean _4deletingTodoTaskAttachment = _localStorageUETodoTaskAttachmentsAttachmentIdAndBeanMap
										.get(_localStorageUETDTASAttachmentId);

								// check its data dirty type
								if (LocalStorageDataDirtyType.DELETE == _4deletingTodoTaskAttachment
										.getLocalStorageDataDirtyType()) {
									Log.d(LOG_TAG,
											"The to-do list task attachment whose id = "
													+ _4deletingTodoTaskAttachment
															.getAttachmentId()
													+ " will delete from local storage database");

									_mContentResolver.delete(
											ContentUris
													.withAppendedId(
															TodoTaskAttachment.TODOTASKATTACHMENT_CONTENT_URI,
															_4deletingTodoTaskAttachment
																	.getRowId()),
											null, null);
								}
							}
						}

					}).start();
				}
			} else {
				Log.e(LOG_TAG,
						"Get user enterprise to-do list task form info failed, response entity unrecognized");
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			Log.e(LOG_TAG,
					"Send get user enterprise to-do list task form info post http request failed");
		}

	}

}
