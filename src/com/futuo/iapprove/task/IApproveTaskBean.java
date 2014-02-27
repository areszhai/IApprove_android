package com.futuo.iapprove.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.utils.DateStringUtils;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.CommonUtils;
import com.richitec.commontoolkit.utils.JSONUtils;

public class IApproveTaskBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8661391503415942906L;

	private static final String LOG_TAG = IApproveTaskBean.class
			.getCanonicalName();

	// application context
	protected transient Context _mAppContext;

	// task id, title, applicant name, create timestamp, sender fake id, is
	// ended flag and advice
	// list
	protected Long taskId;
	protected String taskTitle;
	protected String applicantName;
	protected Long createTimestamp;
	protected Long senderFakeId;
	protected Boolean ended;
	protected List<IApproveTaskAdviceBean> advices;

	// task status
	private TodoTaskStatus taskStatus;

	public IApproveTaskBean() {
		super();

		// get application context
		_mAppContext = CTApplication.getContext();

		// set default is ended flag
		ended = false;

		// initialize advices list
		advices = new ArrayList<IApproveTaskAdviceBean>();

		// set default task status
		taskStatus = TodoTaskStatus.UNREAD;
	}

	// constructor with JSON object
	public IApproveTaskBean(JSONObject taskJSONObject) {
		this();

		// check task JSON object
		if (null != taskJSONObject) {
			// set iApprove task attributes
			// task id
			try {
				taskId = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										taskJSONObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getIApproveListReqResp_task_id)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get iApprove list task id error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// task title
			taskTitle = JSONUtils
					.getStringFromJSONObject(
							taskJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListReqResp_task_title));

			// applicant name
			applicantName = JSONUtils
					.getStringFromJSONObject(
							taskJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListReqResp_task_applicantName));

			// create timestamp
			// get and check subcreatemit timestamp value
			Date _createTimestampValue = DateStringUtils
					.longDateString2Date(JSONUtils
							.getStringFromJSONObject(
									taskJSONObject,
									_mAppContext
											.getResources()
											.getString(
													R.string.rbgServer_getIApproveListReqResp_task_createTimestamp)));
			if (null != _createTimestampValue) {
				createTimestamp = _createTimestampValue.getTime();
			}

			// sender fake id
			try {
				senderFakeId = Long
						.parseLong(JSONUtils
								.getStringFromJSONObject(
										taskJSONObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getIApproveListReqResp_task_senderFakeId)));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get iApprove list task sender fake id error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// ended
			// get and check state value
			try {
				Integer _stateValue = Integer
						.parseInt(JSONUtils
								.getStringFromJSONObject(
										taskJSONObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getIApproveListReqResp_task_state)));
				if (Integer
						.parseInt(_mAppContext
								.getResources()
								.getString(
										R.string.rbgServer_getIApproveListReqResp_task_goingState)) == _stateValue
						.intValue()) {
					ended = false;
				} else if (Integer
						.parseInt(_mAppContext
								.getResources()
								.getString(
										R.string.rbgServer_getIApproveListReqResp_task_endedState)) == _stateValue
						.intValue()) {
					ended = true;
				}
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get iApprove list task state error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// advices
			// get and check advices value
			List<IApproveTaskAdviceBean> _advicesValue = IApproveTaskAdviceBean
					.getTaskAdvices(taskJSONObject);
			if (null != _advicesValue) {
				advices.addAll(_advicesValue);
			}

			// task status
			try {
				taskStatus = TodoTaskStatus
						.getStatus(Integer.parseInt(JSONUtils
								.getStringFromJSONObject(
										taskJSONObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getUserEnterpriseTodoListTaskReqResp_task_status))));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get IApprove task task status error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}
		} else {
			Log.e(LOG_TAG,
					"New IApprove task with JSON object error, task JSON object = "
							+ taskJSONObject);
		}
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public Long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Long getSenderFakeId() {
		return senderFakeId;
	}

	public void setSenderFakeId(Long senderFakeId) {
		this.senderFakeId = senderFakeId;
	}

	public Boolean ended() {
		return ended;
	}

	public void setEnded(Boolean ended) {
		this.ended = ended;
	}

	public List<IApproveTaskAdviceBean> getAdvices() {
		return advices;
	}

	public void setAdvices(List<IApproveTaskAdviceBean> advices) {
		this.advices = advices;
	}

	public TodoTaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TodoTaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	// comparable compare to
	public int compareTo(IApproveTaskBean another) {
		int _result = -1;

		// check task id, tile, applicant name, is ended flag and advices
		if ((null == taskId && null == another.taskId)
				|| (null != taskId && null != another.taskId && taskId
						.longValue() == another.taskId.longValue())) {
			if ((null == taskTitle && null == another.taskTitle)
					|| (null != taskTitle && null != another.taskTitle && taskTitle
							.equalsIgnoreCase(another.taskTitle))) {
				if ((null == applicantName && null == another.applicantName)
						|| (null != applicantName
								&& null != another.applicantName && applicantName
									.equalsIgnoreCase(another.applicantName))) {
					if ((null == ended && null == another.ended)
							|| (null != ended && null != another.ended && ended
									.booleanValue() == another.ended
									.booleanValue())) {
						if (CommonUtils.compareList(advices, another.advices,
								false)) {
							_result = 0;
						} else {
							Log.d(LOG_TAG,
									"IApprove task bean advice list not equals, self task advice list = "
											+ advices
											+ " and another task advice list = "
											+ another.advices);
						}
					} else {
						Log.d(LOG_TAG,
								"IApprove task bean is ended flag not equals, self task is ended flag = "
										+ ended
										+ " and another task is ended flag = "
										+ another.ended);
					}
				} else {
					Log.d(LOG_TAG,
							"IApprove task bean applicant name not equals, self task applicant name = "
									+ applicantName
									+ " and another task applicant name = "
									+ another.applicantName);
				}
			} else {
				Log.d(LOG_TAG,
						"IApprove task bean task title not equals, self task title = "
								+ taskTitle + " and another task title = "
								+ another.taskTitle);
			}
		} else {
			Log.d(LOG_TAG,
					"IApprove task bean task id not equals, self task id = "
							+ taskId + " and another task id = "
							+ another.taskId);
		}

		return _result;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append user enterprise iApprove task id, title, applicant name,
		// sender fake id, is ended flag and advices
		_description.append("User enterprise iApprove task id = ")
				.append(taskId).append(", ").append("title = ")
				.append(taskTitle).append(", ").append("applicant name = ")
				.append(applicantName).append(", ")
				.append("create timestamp = ").append(createTimestamp)
				.append(", ").append("sender fake id = ").append(senderFakeId)
				.append(", ").append("is ended = ").append(ended).append(", ")
				.append("advices = ").append(advices).append(" and ")
				.append("status = ").append(taskStatus);
		;

		return _description.toString();
	}

}
