package com.futuo.iapprove.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTaskAttachments.TodoTaskAttachment;
import com.futuo.iapprove.utils.IApproveTaskAttachmentFileUrlUtils;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class IApproveTaskAttachmentBean implements
		Comparable<IApproveTaskAttachmentBean>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 613622753253713551L;

	private static final String LOG_TAG = IApproveTaskAttachmentBean.class
			.getCanonicalName();

	// row id, task attachment id, attachment name, origin name and type
	private Long rowId;
	private Long attachmentId;
	private String attachmentName;
	private String attachmentOriginName;
	private IApproveTaskAttachmentType attachmentType;

	// task attachment suffix
	private String attachmentSuffix;

	// task attachment url
	private String attachmentUrl;

	// task attachment download status
	private IApproveTaskAttachmentDownloadStatus attachmentDownloadStatus;

	// local storage data dirty type
	private LocalStorageDataDirtyType lsDataDirtyType;

	public IApproveTaskAttachmentBean() {
		super();

		// set default download status
		attachmentDownloadStatus = IApproveTaskAttachmentDownloadStatus.NORMAL;

		// set default local storage data dirty type
		lsDataDirtyType = LocalStorageDataDirtyType.NORMAL;
	}

	// constructor with JSON object
	public IApproveTaskAttachmentBean(JSONObject taskAttachmentJSONObject) {
		this();

		// check task attachment JSON object
		if (null != taskAttachmentJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// set task attachment attributes
			// task attachment id
			try {
				attachmentId = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										taskAttachmentJSONObject,
										_appContext
												.getResources()
												.getString(
														R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_id)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get task attachment id error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// task attachment name
			attachmentName = JSONUtils
					.getStringFromJSONObject(
							taskAttachmentJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_name));
			if (null == attachmentName) {
				attachmentName = "";
			}

			// task attachment origin name
			attachmentOriginName = JSONUtils
					.getStringFromJSONObject(
							taskAttachmentJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_originName));
			if (null == attachmentOriginName) {
				attachmentOriginName = "";
			}

			// get task attachment suffix
			attachmentSuffix = JSONUtils
					.getStringFromJSONObject(
							taskAttachmentJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_suffix));
			if (null == attachmentSuffix) {
				attachmentSuffix = _appContext
						.getResources()
						.getString(
								R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_textSuffix);
			}

			// task attachment type
			attachmentType = IApproveTaskAttachmentType
					.getType(attachmentSuffix);
			if (IApproveTaskAttachmentType.COMMON_TEXT == attachmentType) {
				attachmentUrl = JSONUtils
						.getStringFromJSONObject(
								taskAttachmentJSONObject,
								_appContext
										.getResources()
										.getString(
												R.string.rbgServer_getIApproveListTaskFormInfoReqResp_attachment_content));
			}
		} else {
			Log.e(LOG_TAG,
					"New iApprove task attachment with JSON object error, form JSON object = "
							+ taskAttachmentJSONObject);
		}
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public String getAttachmentOriginName() {
		return attachmentOriginName;
	}

	public void setAttachmentOriginName(String attachmentOriginName) {
		this.attachmentOriginName = attachmentOriginName;
	}

	public IApproveTaskAttachmentType getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(IApproveTaskAttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}

	public String getAttachmentSuffix() {
		return attachmentSuffix;
	}

	public void setAttachmentSuffix(String attachmentSuffix) {
		this.attachmentSuffix = attachmentSuffix;
	}

	public String getAttachmentRemoteUrl() {
		String _attachmentRemoteUrl = null;

		// task attachment url
		switch (attachmentType) {
		case IMAGE_JPG:
		case IMAGE_JPEG:
		case IMAGE_PNG:
		case IMAGE_BMP:
		case IMAGE_GIF:
			// image
			_attachmentRemoteUrl = IApproveTaskAttachmentFileUrlUtils
					.taskImageAttachmentFileUrl(attachmentName,
							attachmentOriginName, attachmentSuffix);
			break;

		case COMMON_TEXT:
			// text content
			_attachmentRemoteUrl = attachmentUrl;
			break;

		case AUDIO_AMR:
		case AUDIO_WAV:
		case AUDIO_3GPP:
		default:
			// file
			_attachmentRemoteUrl = IApproveTaskAttachmentFileUrlUtils
					.taskFileAttachmentFileUrl(attachmentName,
							attachmentOriginName, attachmentSuffix);
			break;
		}

		return _attachmentRemoteUrl;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public IApproveTaskAttachmentDownloadStatus getAttachmentDownloadStatus() {
		return attachmentDownloadStatus;
	}

	public void setAttachmentDownloadStatus(
			IApproveTaskAttachmentDownloadStatus attachmentDownloadStatus) {
		this.attachmentDownloadStatus = attachmentDownloadStatus;
	}

	public LocalStorageDataDirtyType getLocalStorageDataDirtyType() {
		return lsDataDirtyType;
	}

	public void setLocalStorageDataDirtyType(
			LocalStorageDataDirtyType lsDataDirtyType) {
		this.lsDataDirtyType = lsDataDirtyType;
	}

	@Override
	public int compareTo(IApproveTaskAttachmentBean another) {
		int _result = -1;

		// check iApprove task attachment name, origin name and type
		if ((null == attachmentName && null == another.attachmentName)
				|| (null != attachmentName && null != another.attachmentName && attachmentName
						.equalsIgnoreCase(another.attachmentName))) {
			if ((null == attachmentOriginName && null == another.attachmentOriginName)
					|| (null != attachmentOriginName
							&& null != another.attachmentOriginName && attachmentOriginName
								.equalsIgnoreCase(another.attachmentOriginName))) {
				if ((null == attachmentType && null == another.attachmentType)
						|| (null != attachmentType
								&& null != another.attachmentType && attachmentType == another.attachmentType)) {
					_result = 0;
				} else {
					Log.d(LOG_TAG,
							"IApprove task attachment type not equals, self attachment type = "
									+ attachmentType
									+ " and another attachment type = "
									+ another.attachmentType);
				}
			} else {
				Log.d(LOG_TAG,
						"IApprove task attachment origin name not equals, self attachment origin name = "
								+ attachmentOriginName
								+ " and another attachment origin name = "
								+ another.attachmentOriginName);
			}
		} else {
			Log.d(LOG_TAG,
					"IApprove task attachment name not equals, self attachment name = "
							+ attachmentName
							+ " and another attachment name = "
							+ another.attachmentName);
		}

		return _result;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	// get to-do list task attachment with cursor
	public static IApproveTaskAttachmentBean getTaskAttachment(Cursor cursor) {
		// new to-do list task attachment
		IApproveTaskAttachmentBean _todoTaskAttachment = new IApproveTaskAttachmentBean();

		// check the cursor
		if (null != cursor) {
			// set to-do list task attachment attributes
			// row id
			_todoTaskAttachment.rowId = cursor.getLong(cursor
					.getColumnIndex(TodoTaskAttachment._ID));

			// task attachment id
			_todoTaskAttachment.attachmentId = cursor.getLong(cursor
					.getColumnIndex(TodoTaskAttachment.ATTACHMENT_ID));

			// task attachment name
			_todoTaskAttachment.attachmentName = cursor.getString(cursor
					.getColumnIndex(TodoTaskAttachment.NAME));

			// task attachment origin name
			_todoTaskAttachment.attachmentOriginName = cursor.getString(cursor
					.getColumnIndex(TodoTaskAttachment.ORIGINNAME));

			// task attachment suffix
			_todoTaskAttachment.attachmentSuffix = cursor.getString(cursor
					.getColumnIndex(TodoTaskAttachment.SUFFIX));

			// task attachment type
			_todoTaskAttachment.attachmentType = IApproveTaskAttachmentType
					.getType(cursor.getInt(cursor
							.getColumnIndex(TodoTaskAttachment.TYPE)));

			// task attachment url
			_todoTaskAttachment.attachmentUrl = cursor.getString(cursor
					.getColumnIndex(TodoTaskAttachment.URL));

			// task attachment download status
			_todoTaskAttachment.attachmentDownloadStatus = IApproveTaskAttachmentDownloadStatus
					.getDownloadStatus(cursor.getInt(cursor
							.getColumnIndex(TodoTaskAttachment.DOWNLOADSTATUS)));
		} else {
			Log.e(LOG_TAG,
					"Get to-do list task attachment with cursor error, cursor = "
							+ cursor);
		}

		return _todoTaskAttachment;
	}

	// get iApprove task attachment list with JSON object
	public static List<IApproveTaskAttachmentBean> getTaskAttachments(
			JSONObject taskContentInfoJSONObject) {
		List<IApproveTaskAttachmentBean> _taskAttachments = null;

		// check iApprove task content info JSON object
		if (null != taskContentInfoJSONObject) {
			// get application context
			Context _appContext = CTApplication.getContext();

			// initialize return iApprove task attachment bean list
			_taskAttachments = new ArrayList<IApproveTaskAttachmentBean>();

			// get and check iApprove task content info json array
			JSONArray _taskContentInfosJSONArray = JSONUtils
					.getJSONArrayFromJSONObject(
							taskContentInfoJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListTaskFormInfoReqResp_contentList));
			if (null != _taskContentInfosJSONArray
					&& 0 < _taskContentInfosJSONArray.length()) {
				for (int i = 0; i < _taskContentInfosJSONArray.length(); i++) {
					// get each iApprove task content info and check its type
					JSONObject _taskContentInfoJSONObject = JSONUtils
							.getJSONObjectFromJSONArray(
									_taskContentInfosJSONArray, i);

					// get attachment type string
					String _attachmentTypeString = JSONUtils
							.getStringFromJSONObject(
									_taskContentInfoJSONObject,
									_appContext
											.getResources()
											.getString(
													R.string.rbgServer_getIApproveListTaskFormInfoReqResp_contentType));

					if (null != _taskContentInfoJSONObject
							&& (_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListTaskFormInfoReqResp_fileContentType)
									.equalsIgnoreCase(_attachmentTypeString) || _appContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListTaskFormInfoReqResp_textContentType)
									.equalsIgnoreCase(_attachmentTypeString))) {
						// add got user enterprise iApprove task attachment to
						// list
						_taskAttachments.add(new IApproveTaskAttachmentBean(
								_taskContentInfoJSONObject));
					}
				}
			}
		} else {
			Log.e(LOG_TAG,
					"Get iApprove task attachment list with JSON object error, content info JSON object = "
							+ taskContentInfoJSONObject);
		}

		return _taskAttachments;
	}

}
