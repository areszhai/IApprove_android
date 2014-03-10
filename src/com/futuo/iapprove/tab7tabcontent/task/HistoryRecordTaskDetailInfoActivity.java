package com.futuo.iapprove.tab7tabcontent.task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.futuo.iapprove.R;
import com.futuo.iapprove.account.user.IAUserExtension;
import com.futuo.iapprove.addressbook.person.PersonBean;
import com.futuo.iapprove.customwidget.CommonFormSeparator;
import com.futuo.iapprove.customwidget.IApproveNavigationActivity;
import com.futuo.iapprove.customwidget.TaskFormAdviceFormItem;
import com.futuo.iapprove.customwidget.TaskFormAdviceFormItem.TaskFormAdviceType;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormApplicationAttachmentInfoDataKeys;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormAttachmentType;
import com.futuo.iapprove.customwidget.TaskFormAttachmentFormItem.TaskFormVoiceAttachmentInfoDataKeys;
import com.futuo.iapprove.customwidget.TaskFormItemFormItem;
import com.futuo.iapprove.tab7tabcontent.attachmentpresent.NAATDTTextImgAttachmentViewActivity;
import com.futuo.iapprove.tab7tabcontent.attachmentpresent.NAATDTTextImgAttachmentViewActivity.NAATDTTextImgAttachmentViewExtraData;
import com.futuo.iapprove.task.IApproveTaskAdviceBean;
import com.futuo.iapprove.task.IApproveTaskAttachmentBean;
import com.futuo.iapprove.task.IApproveTaskAttachmentType;
import com.futuo.iapprove.task.IApproveTaskFormItemBean;
import com.futuo.iapprove.task.TodoTaskStatus;
import com.futuo.iapprove.utils.AppDataSaveRestoreUtils;
import com.futuo.iapprove.utils.AudioUtils;
import com.futuo.iapprove.utils.HttpRequestParamUtils;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class HistoryRecordTaskDetailInfoActivity extends
		IApproveNavigationActivity {

	private static final String LOG_TAG = HistoryRecordTaskDetailInfoActivity.class
			.getCanonicalName();

	// history record list task form item form parent frameLayout and form
	// linearLayout
	private FrameLayout _mFormItemFormParentFrameLayout;
	private LinearLayout _mFormItemFormLinearLayout;

	// history record list task form item id(key) and form item form item
	// view(value) map
	private Map<Long, TaskFormItemFormItem> _mFormItemId7FormItemFormItemMap;

	// history record list task form attachment form parent frameLayout and form
	// linearLayout
	private FrameLayout _mAttachmentFormParentFrameLayout;
	private LinearLayout _mAttachmentFormLinearLayout;

	// history record list task attachment id(key) and form attachment form item
	// view(value) map
	private Map<Long, TaskFormAttachmentFormItem> _mFormAttachmentId7FormAttachmentFormItemMap;

	// history record list task form advice form parent frameLayout and form
	// linearLayout
	private FrameLayout _mAdviceFormParentFrameLayout;
	private LinearLayout _mAdviceFormLinearLayout;

	// get history record task detail info asynchronous post http request
	// progress dialog
	private ProgressDialog _mGetHistoryRecordTaskDetailInfoAsyncHttpReqProgDlg;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.history_record_task_detail_info_activity_layout);

		// get the extra data
		final Bundle _extraData = getIntent().getExtras();

		// define user enterprise history record list task id, title, sender
		// fake id, status and advice list
		Long _historyRecordTaskId = null;
		String _historyRecordTaskTitle = null;
		Long _historyRecordTaskSenderFakeId = null;
		TodoTaskStatus _historyRecordTaskStatus = null;
		List<IApproveTaskAdviceBean> _historyRecordTaskAdvices = new ArrayList<IApproveTaskAdviceBean>();

		// check the data
		if (null != _extraData) {
			// get history record list task id, title, sender fake id, status
			// and advice
			// list
			_historyRecordTaskId = _extraData
					.getLong(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKID);
			_historyRecordTaskTitle = _extraData
					.getString(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKTITLE);
			_historyRecordTaskSenderFakeId = _extraData
					.getLong(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKSENDERFAKEID);
			_historyRecordTaskStatus = (TodoTaskStatus) _extraData
					.getSerializable(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKSTATUS);
			_historyRecordTaskAdvices
					.addAll((Collection<? extends IApproveTaskAdviceBean>) _extraData
							.getSerializable(HistoryRecordTaskListTaskDetailInfoExtraData.HISTORYRECORDTASK_DETAILINFO_TASKADVICES));
		}

		// set subViews
		// set title
		setTitle(_historyRecordTaskTitle);

		// initialize history record list task form item id and form item form
		// item view
		// map
		_mFormItemId7FormItemFormItemMap = new HashMap<Long, TaskFormItemFormItem>();

		// get history record list task form item form parent frameLayout and
		// form linearLayout
		_mFormItemFormParentFrameLayout = (FrameLayout) findViewById(R.id.hrta_formItemForm_parent_frameLayout);
		_mFormItemFormLinearLayout = (LinearLayout) findViewById(R.id.hrta_formItemForm_linearLayout);

		// initialize history record list task form attachment id and form
		// attachment
		// form item view map
		_mFormAttachmentId7FormAttachmentFormItemMap = new HashMap<Long, TaskFormAttachmentFormItem>();

		// get history record list task form attachment form parent frameLayout
		// and form
		// linearLayout
		_mAttachmentFormParentFrameLayout = (FrameLayout) findViewById(R.id.hrta_attachmentForm_parent_frameLayout);
		_mAttachmentFormLinearLayout = (LinearLayout) findViewById(R.id.hrta_attachmentForm_linearLayout);

		// get history record list task form advice form parent frameLayout and
		// form linearLayout
		_mAdviceFormParentFrameLayout = (FrameLayout) findViewById(R.id.hrta_adviceForm_parent_frameLayout);
		_mAdviceFormLinearLayout = (LinearLayout) findViewById(R.id.hrta_adviceForm_linearLayout);

		// check history record list task advices, then generate to-do list task
		// each
		// others advice and add to form advice form linearLayout
		for (IApproveTaskAdviceBean todoTaskAdvice : _historyRecordTaskAdvices) {
			// test by ares
			PersonBean _othersAdvisor = new PersonBean();
			_othersAdvisor.setEmployeeName(todoTaskAdvice.getAdvisorName());

			addHistoryRecordTaskFormAdviceFormItem(
					TaskFormAdviceType.OTHERS_ADVICE, _othersAdvisor,
					todoTaskAdvice);
		}

		// get user enterprise history record list task form info
		// define get user enterprise history record list task post http request
		// parameter
		Map<String, String> _getUserEnterpriseHistoryRecordListTaskFormInfoPostHttpReqParam = HttpRequestParamUtils
				.genUserSigHttpReqParam();

		// put get user enterprise history record list task form info action in
		_getUserEnterpriseHistoryRecordListTaskFormInfoPostHttpReqParam
				.put(getResources().getString(
						R.string.rbgServer_commonReqParam_action),
						getResources()
								.getString(
										R.string.rbgServer_getIApproveListTaskFormInfoReqParam_action));

		// put get user enterprise history record list task form info user
		// enterprise id in
		_getUserEnterpriseHistoryRecordListTaskFormInfoPostHttpReqParam
				.put(getResources().getString(
						R.string.rbgServer_getIApproveReqParam_enterpriseId),
						StringUtils.base64Encode(IAUserExtension
								.getUserLoginEnterpriseId(
										UserManager.getInstance().getUser())
								.toString()));

		// put get user enterprise history record list task form info task id,
		// sender fake id and status in
		_getUserEnterpriseHistoryRecordListTaskFormInfoPostHttpReqParam
				.put(getResources()
						.getString(
								R.string.rbgServer_getIApproveListTaskFormInfoReqParam_taskId),
						StringUtils.base64Encode(_historyRecordTaskId
								.toString()));
		_getUserEnterpriseHistoryRecordListTaskFormInfoPostHttpReqParam
				.put(getResources()
						.getString(
								R.string.rbgServer_getIApproveListTaskFormInfoReqParam_taskSenderFakeId),
						StringUtils.base64Encode(_historyRecordTaskSenderFakeId
								.toString()));
		_getUserEnterpriseHistoryRecordListTaskFormInfoPostHttpReqParam
				.put(getResources()
						.getString(
								R.string.rbgServer_getIApproveListTaskFormInfoReqParam_taskStatus),
						StringUtils.base64Encode(_historyRecordTaskStatus
								.getValue().toString()));

		// show get history record task detail info asynchronous post http
		// request process dialog
		_mGetHistoryRecordTaskDetailInfoAsyncHttpReqProgDlg = ProgressDialog
				.show(HistoryRecordTaskDetailInfoActivity.this,
						null,
						getString(R.string.hrtdi_getDetailInfoAsyncHttpReq_progDlg_message),
						true);

		// send get user enterprise history record list task form info post http
		// request
		HttpUtils
				.postRequest(
						getResources().getString(R.string.server_url)
								+ getResources()
										.getString(
												R.string.get_iApproveListTaskFormInfo_url),
						PostRequestFormat.URLENCODED,
						_getUserEnterpriseHistoryRecordListTaskFormInfoPostHttpReqParam,
						null,
						HttpRequestType.ASYNCHRONOUS,
						new GetUserEnterpriseHistoryRecordTaskFormInfoPostHttpRequestListener());
	}

	@Override
	protected void onPause() {
		super.onPause();

		// stop play recorder audio if needed
		AudioUtils.stopPlayRecorderAudio();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		AppDataSaveRestoreUtils.onRestoreInstanceState(savedInstanceState);

		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		AppDataSaveRestoreUtils.onSaveInstanceState(outState);

		super.onSaveInstanceState(outState);
	}

	// add history record list task advice(my and others) as advice form item to
	// advice
	// form linearLayout
	private void addHistoryRecordTaskFormAdviceFormItem(
			TaskFormAdviceType adviceType, PersonBean advisorInfo,
			IApproveTaskAdviceBean adviceInfo) {
		// check to-do list task form advice form parent frameLayout
		// visibility
		if (View.VISIBLE != _mAdviceFormParentFrameLayout.getVisibility()) {
			// show to-do list task form advice form parent frameLayout
			_mAdviceFormParentFrameLayout.setVisibility(View.VISIBLE);
		}

		// generate new added my advice form item
		TaskFormAdviceFormItem _newAddedMyAdviceFormItem = TaskFormAdviceFormItem
				.generateTaskFormAdviceFormItem(adviceType, advisorInfo,
						adviceInfo);

		// set its on click listener
		_newAddedMyAdviceFormItem
				.setOnClickListener(new HistoryRecordTaskFormAdviceFormItemOnClickListener());

		// get the last my advice form item added in advice form linearLayout
		// get and check advice form linearLayout subviews count
		int _adviceFormLinearLayoutSubviewsCount = _mAdviceFormLinearLayout
				.getChildCount();
		if (1 < _adviceFormLinearLayoutSubviewsCount) {
			// miliSceonds of minute
			final Long MILLISECONDS_PER_MINUTE = 60 * 1000L;

			// compare to-do task advice the last form item advice given
			// timestamp with new added my advice given timestamp
			if (((TaskFormAdviceFormItem) _mAdviceFormLinearLayout
					.getChildAt(_adviceFormLinearLayoutSubviewsCount - 1))
					.getAdviceGivenTimestamp()
					+ MILLISECONDS_PER_MINUTE <= _newAddedMyAdviceFormItem
						.getAdviceGivenTimestamp()) {
				// show new add my advice given timestamp
				_newAddedMyAdviceFormItem.showAdviceGivenTimestampTextView();
			}
		} else {
			// show new add my advice given timestamp
			_newAddedMyAdviceFormItem.showAdviceGivenTimestampTextView();
		}

		// add new added my advice to advice form linearLayout
		_mAdviceFormLinearLayout.addView(_newAddedMyAdviceFormItem,
				new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1));
	}

	// inner class
	// history record task list task detail info extra data constant
	public static final class HistoryRecordTaskListTaskDetailInfoExtraData {

		// history record task list task id, title, sender fake id, status and
		// advice
		public static final String HISTORYRECORDTASK_DETAILINFO_TASKID = "historyrecord_task_detailinfo_taskid";
		public static final String HISTORYRECORDTASK_DETAILINFO_TASKTITLE = "historyrecord_task_detailinfo_tasktitle";
		public static final String HISTORYRECORDTASK_DETAILINFO_TASKSENDERFAKEID = "historyrecord_task_detailinfo_tasksenderfakeid";
		public static final String HISTORYRECORDTASK_DETAILINFO_TASKSTATUS = "historyrecord_task_detailinfo__taskstatus";
		public static final String HISTORYRECORDTASK_DETAILINFO_TASKADVICES = "historyrecord_task_detailinfo__taskadvices";

	}

	// get user enterprise history record list task form info post http request
	// listener
	class GetUserEnterpriseHistoryRecordTaskFormInfoPostHttpRequestListener
			extends OnHttpRequestListener {

		// done get history record task detail info
		private void doneGetHistoryRecordTaskDetailInfo() {
			// check and dismiss account login asynchronous post http request
			// process dialog
			if (null != _mGetHistoryRecordTaskDetailInfoAsyncHttpReqProgDlg) {
				_mGetHistoryRecordTaskDetailInfoAsyncHttpReqProgDlg.dismiss();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onFinished(HttpRequest request, HttpResponse response) {
			// done get history record task detail info
			doneGetHistoryRecordTaskDetailInfo();

			// get http response entity string
			String _respEntityString = HttpUtils
					.getHttpResponseEntityString(response);

			Log.d(LOG_TAG,
					"@@@, Send get user enterprise history record list task form info post http request successful, response entity string = "
							+ _respEntityString);

			// get and check http response entity string error json data
			final JSONObject _respJsonData = JSONUtils
					.toJSONObject(_respEntityString);

			if (null != _respJsonData) {
				// get and check error message
				String _errorMsg = JSONUtils.getStringFromJSONObject(
						_respJsonData,
						getResources().getString(
								R.string.rbgServer_commonReqResp_error));

				if (null != _errorMsg) {
					Log.e(LOG_TAG,
							"Get enterprise form info failed, response error message = "
									+ _errorMsg);

					Toast.makeText(HistoryRecordTaskDetailInfoActivity.this,
							_errorMsg, Toast.LENGTH_SHORT).show();
				} else {
					Log.d(LOG_TAG,
							"Get user enterprise to-do list task form info successful");

					// get and process history record list task form items
					for (IApproveTaskFormItemBean historyRecordTaskFormItem : IApproveTaskFormItemBean
							.getTaskFormItems(_respJsonData)) {
						// define history record list task form item form item
						TaskFormItemFormItem _historyRecordTaskFormItemFormItem = null;

						// check form item id existed in to-do list task form
						// item id
						// and form item form item view map key set
						if (!_mFormItemId7FormItemFormItemMap
								.keySet()
								.contains(historyRecordTaskFormItem.getItemId())) {
							// check history record task form item form parent
							// frameLayout visibility
							if (View.VISIBLE != _mFormItemFormParentFrameLayout
									.getVisibility()) {
								// show history record task form item form
								// parent frameLayout
								_mFormItemFormParentFrameLayout
										.setVisibility(View.VISIBLE);
							}

							// generate new to-do list task form item form item
							_historyRecordTaskFormItemFormItem = TaskFormItemFormItem
									.generateTaskFormItemFormItem(historyRecordTaskFormItem);

							// add separator line and to-do list task form item
							// form
							// item to form item form
							_mFormItemFormLinearLayout
									.addView(
											new CommonFormSeparator(
													HistoryRecordTaskDetailInfoActivity.this),
											new LayoutParams(
													LayoutParams.MATCH_PARENT,
													LayoutParams.WRAP_CONTENT));
							_mFormItemFormLinearLayout.addView(
									_historyRecordTaskFormItemFormItem,
									new LayoutParams(LayoutParams.MATCH_PARENT,
											LayoutParams.WRAP_CONTENT, 1));
						} else {
							// get the existed to-do list task form item form
							// item and
							// set its form item
							(_historyRecordTaskFormItemFormItem = _mFormItemId7FormItemFormItemMap
									.get(historyRecordTaskFormItem.getItemId()))
									.setFormItem(historyRecordTaskFormItem);
						}

						// add to-do list task form item form item to map
						_mFormItemId7FormItemFormItemMap.put(
								historyRecordTaskFormItem.getItemId(),
								_historyRecordTaskFormItemFormItem);
					}

					// get and process history list task attachments
					for (IApproveTaskAttachmentBean historyRecordTaskAttachment : IApproveTaskAttachmentBean
							.getTaskAttachments(_respJsonData)) {
						// define history record list task form attachment form
						// item
						TaskFormAttachmentFormItem _historyRecordTaskFormAttachmentFormItem = null;

						// check attachment id existed in to-do list task
						// attachment id
						// and form attachment form item view map key set
						if (!_mFormAttachmentId7FormAttachmentFormItemMap
								.keySet().contains(
										historyRecordTaskAttachment
												.getAttachmentId())) {
							// define task form attachment info and on click
							// listener
							Object _attachmentInfo = null;
							OnClickListener _attachmentOnClickListener = null;

							// get, check task form attachment type then
							// initialize task
							// form attachment info and on click listener
							TaskFormAttachmentType _taskFormAttachmentType = TaskFormAttachmentType
									.getType(historyRecordTaskAttachment
											.getAttachmentType());
							switch (_taskFormAttachmentType) {
							case TEXT_ATTACHMENT:
								// text
								_attachmentInfo = historyRecordTaskAttachment
										.getAttachmentUrl();
								_attachmentOnClickListener = new HistoryRecordTaskFormTextAttachmentFormItemOnClickListener();
								break;

							case IMAGE_ATTACHMENT:
								// image
								// check image attachment url
								if (null != historyRecordTaskAttachment
										.getAttachmentRemoteUrl()) {
									_attachmentInfo = historyRecordTaskAttachment
											.getAttachmentRemoteUrl();
									_attachmentOnClickListener = new HistoryRecordTaskFormImageAttachmentFormItemOnClickListener();
								}
								break;

							case VOICE_ATTACHMENT:
								// voice
								// check voice attachment url
								if (null != historyRecordTaskAttachment
										.getAttachmentRemoteUrl()) {
									_attachmentInfo = new HashMap<String, Object>();
									((Map<String, Object>) _attachmentInfo)
											.put(TaskFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH,
													historyRecordTaskAttachment
															.getAttachmentRemoteUrl());
									((Map<String, Object>) _attachmentInfo)
											.put(TaskFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_DURATION,
													AudioUtils
															.getRecorderAudioDuration(historyRecordTaskAttachment
																	.getAttachmentRemoteUrl()));
									_attachmentOnClickListener = new HistoryRecordTaskFormVoiceAttachmentFormItemOnClickListener();
								}
								break;

							case APPLICATION_ATTACHMENT:
								// check application attachment type
								if (IApproveTaskAttachmentType.COMMON_FILE == historyRecordTaskAttachment
										.getAttachmentType()) {
									_attachmentInfo = new HashMap<String, String>();
									((Map<String, String>) _attachmentInfo)
											.put(TaskFormApplicationAttachmentInfoDataKeys.APPLICATIONATTACHMENT_SHOWN_TIP,
													historyRecordTaskAttachment
															.getAttachmentOriginName()
															+ '.'
															+ historyRecordTaskAttachment
																	.getAttachmentSuffix());
									((Map<String, String>) _attachmentInfo)
											.put(TaskFormApplicationAttachmentInfoDataKeys.APPLICATIONATTACHMENT_OPEN_URL,
													historyRecordTaskAttachment
															.getAttachmentRemoteUrl());
									_attachmentOnClickListener = new HistoryRecordTaskFormApplicationAttachmentFormItemOnClickListener();
								}
								break;
							}

							// check attachment info
							if (null != _attachmentInfo) {
								// check history record list task form
								// attachment form parent
								// frameLayout visibility
								if (View.VISIBLE != _mAttachmentFormParentFrameLayout
										.getVisibility()) {
									// show history record list task form
									// attachment form parent
									// frameLayout
									_mAttachmentFormParentFrameLayout
											.setVisibility(View.VISIBLE);
								}

								// generate new history record list task form
								// attachment form
								// item
								_historyRecordTaskFormAttachmentFormItem = TaskFormAttachmentFormItem
										.generateTaskFormAttachmentFormItem(
												_taskFormAttachmentType,
												_attachmentInfo);

								// check attachment on click listener and set
								// new history record
								// list task form attachment form item on click
								// listener
								if (null != _attachmentOnClickListener) {
									_historyRecordTaskFormAttachmentFormItem
											.setOnClickListener(_attachmentOnClickListener);
								}

								// add history record list task form attachment
								// form item to form
								// attachment form
								_mAttachmentFormLinearLayout
										.addView(
												_historyRecordTaskFormAttachmentFormItem,
												new LayoutParams(
														LayoutParams.MATCH_PARENT,
														LayoutParams.WRAP_CONTENT,
														1));

								// add history record list task form attachment
								// form item to map
								_mFormAttachmentId7FormAttachmentFormItemMap
										.put(historyRecordTaskAttachment
												.getAttachmentId(),
												_historyRecordTaskFormAttachmentFormItem);
							}
						} else {
							// add history record list task form attachment form
							// item to map
							_mFormAttachmentId7FormAttachmentFormItemMap.put(
									historyRecordTaskAttachment
											.getAttachmentId(),
									_historyRecordTaskFormAttachmentFormItem);
						}
					}
				}
			} else {
				Log.e(LOG_TAG,
						"Get user enterprise history record list task form info failed, response entity unrecognized");
			}
		}

		@Override
		public void onFailed(HttpRequest request, HttpResponse response) {
			// done get history record task detail info
			doneGetHistoryRecordTaskDetailInfo();

			Log.e(LOG_TAG,
					"Send get user enterprise history record task detail info post http request failed");

			Toast.makeText(HistoryRecordTaskDetailInfoActivity.this,
					R.string.toast_request_exception, Toast.LENGTH_SHORT)
					.show();
		}

	}

	// history record list task form text attachment form item on click listener
	class HistoryRecordTaskFormTextAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// go to new approve application or history record task text or
			// image attachment view activity
			// define new approve application or history record task text or
			// image attachment view extra data map
			Map<String, String> _extraMap = new HashMap<String, String>();

			// put history record task text attachment text to extra data map as
			// param
			_extraMap
					.put(NAATDTTextImgAttachmentViewExtraData.NAA_TDT_TEXT_IMAGE_ATTCHMENT_OBJECT,
							((TextView) v).getText().toString());

			// go to new approve application or history record task text or
			// image attachment view activity with extra data map
			pushActivity(NAATDTTextImgAttachmentViewActivity.class, _extraMap);
		}

	}

	// history record list task form image attachment form item on click
	// listener
	class HistoryRecordTaskFormImageAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// get and check tag of the imageView
			Object _imageViewTag = ((ImageView) v).getTag();
			if (null != _imageViewTag) {
				// go to new approve application or history record task text or
				// image attachment view activity
				// define new approve application or history record task text or
				// image attachment view extra data map
				Map<String, Bitmap> _extraMap = new HashMap<String, Bitmap>();

				// put history record task image attachment image bitmap to
				// extra data map as param
				_extraMap
						.put(NAATDTTextImgAttachmentViewExtraData.NAA_TDT_TEXT_IMAGE_ATTCHMENT_OBJECT,
								(Bitmap) _imageViewTag);

				// go to new approve application or history record task text or
				// image attachment view activity with extra data map
				pushActivity(NAATDTTextImgAttachmentViewActivity.class,
						_extraMap);
			} else {
				Log.e(LOG_TAG,
						"Get history record task form image attachment image view error, view = "
								+ v);
			}
		}

	}

	// history record list task form voice attachment form item on click
	// listener
	class HistoryRecordTaskFormVoiceAttachmentFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// get voice playing flag
			if ((Boolean) v.getTag()) {
				Log.d(LOG_TAG, "Play the voice");

				// play the voice
				AudioUtils
						.playRemoteAudio((String) v
								.getTag(TaskFormVoiceAttachmentInfoDataKeys.VOICEATTACHMENT_VOICE_FILEPATH
										.hashCode()));
			} else {
				Log.d(LOG_TAG, "Stop play the voice");

				// stop play the voice
				AudioUtils.stopPlayRecorderAudio();
			}
		}

	}

	// history record list task form application attachment form item on click
	// listener
	class HistoryRecordTaskFormApplicationAttachmentFormItemOnClickListener
			implements OnClickListener {

		@Override
		public void onClick(View v) {
			// get and check the clicked response view tag
			Object _clickedResponseViewTag = v.getTag();
			if (null != _clickedResponseViewTag) {
				// get history record task application attachment open url
				String _historyRecordTaskApplicationAttachmentOpenUrl = (String) _clickedResponseViewTag;

				// go to open the application attachment activity
				Intent _viewIntent = new Intent("android.intent.action.VIEW");

				// set data, type, flags and category
				_viewIntent.setDataAndType(Uri.fromFile(new File(
						_historyRecordTaskApplicationAttachmentOpenUrl)),
						"application/pdf");
				_viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				_viewIntent.addCategory("android.intent.category.DEFAULT");

				startActivity(_viewIntent);

				// // go to task application attachment view activity
				// // define task application attachment view extra data map
				// Map<String, String> _extraMap = new HashMap<String,
				// String>();
				//
				// // put history record task application attachment name and
				// open url to extra data map as param
				// _extraMap
				// .put(TaskApplicationAttachmentViewExtraData.TASK_APPLICATIONATTCHMENT_NAME,
				// ((TextView) v).getText().toString());
				// _extraMap
				// .put(TaskApplicationAttachmentViewExtraData.TASK_APPLICATIONATTCHMENT_OPEN_URL,
				// _historyRecordTaskApplicationAttachmentOpenUrl);
				//
				// // go to task application attachment view activity with
				// // extra data map
				// pushActivity(TaskApplicationAttachmentViewActivity.class,
				// _extraMap);
			}
		}

	}

	// history record list task form advice form item on click listener
	class HistoryRecordTaskFormAdviceFormItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG,
					"History record list task Form advice form item on click listener, view = "
							+ v);

			//
		}

	}

}
