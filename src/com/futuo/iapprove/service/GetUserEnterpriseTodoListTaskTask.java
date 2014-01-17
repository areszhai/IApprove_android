package com.futuo.iapprove.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTaskFormItems.TodoTaskFormItem;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTasks.TodoTask;
import com.futuo.iapprove.task.IApproveTaskAdviceBean;
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
							R.string.rbgServer_getIApproveListReqParam_state),
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
											R.string.rbgServer_getIApproveListTaskFormInfoReqParam_action));
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

	// get local storage user enterprise to-do list task form item task form
	// item id as key and bean as value map
	private Map<Long, IApproveTaskFormItemBean> getLocalStorageUETodoTaskFormItemsItemIdAndBeanMap(
			Cursor cursor) {
		Map<Long, IApproveTaskFormItemBean> _localStorageUETodoTaskFormItemsItemIdAndBeanMap = new HashMap<Long, IApproveTaskFormItemBean>();

		// check the cursor
		if (null != cursor) {
			// set all local storage user enterprise to-do list task form item
			// for deleting
			while (cursor.moveToNext()) {
				// get for deleting to-do list task form item
				IApproveTaskFormItemBean _4deletingTodoTaskFormItem = IApproveTaskFormItemBean
						.getTaskFormItem(cursor);

				// set it for deleting
				_4deletingTodoTaskFormItem
						.setLocalStorageDataDirtyType(LocalStorageDataDirtyType.DELETE);

				// put to-do task form item id and bean in
				_localStorageUETodoTaskFormItemsItemIdAndBeanMap.put(
						_4deletingTodoTaskFormItem.getItemId(),
						_4deletingTodoTaskFormItem);
			}

			// close cursor
			cursor.close();
		}

		return _localStorageUETodoTaskFormItemsItemIdAndBeanMap;
	}

	// inner class
	// get user enterprise to-do list task post http request listener
	class GetUserEnterpriseTodoListTaskPostHttpRequestListener extends
			OnHttpRequestListener {

		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
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
														j), _advice.agreed());
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
							Map<Long, IApproveTaskFormItemBean> _localStorageUETodoTaskFormItemsItemIdAndBeanMap = (Map<Long, IApproveTaskFormItemBean>) getLocalStorageUETodoTaskFormItemsItemIdAndBeanMap(_mContentResolver
									.query(TodoTaskFormItem.TODOTASKFORMITEMS_CONTENT_URI,
											null,
											TodoTaskFormItem.USER_ENTERPRISETODOLISTTASK_FORMITEMS_WITHSENDERFAKEID7LOGINNAME_CONDITION,
											new String[] {
													_mEnterpriseId.toString(),
													_mTaskSenderFakeId
															.toString(),
													_loginUserName }, null));

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