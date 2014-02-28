package com.futuo.iapprove.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.provider.LocalStorageDBHelper.LocalStorageDataDirtyType;
import com.futuo.iapprove.provider.UserEnterpriseTodoListTaskContentProvider.TodoTasks.TodoTask;
import com.futuo.iapprove.utils.DateStringUtils;
import com.richitec.commontoolkit.utils.CommonUtils;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.commontoolkit.utils.StringUtils;

public class TodoTaskBean extends IApproveTaskBean implements
		Comparable<TodoTaskBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4554934790251515447L;

	private static final String LOG_TAG = TodoTaskBean.class.getCanonicalName();

	// row id, task status and submit timestamp
	private Long rowId;
	private TodoTaskStatus taskStatus;
	private Long submitTimestamp;

	// local storage data dirty type
	private LocalStorageDataDirtyType lsDataDirtyType;

	// constructor with JSON object
	public TodoTaskBean(JSONObject todoTaskJSONObject) {
		super(todoTaskJSONObject);

		// initialize to-do list task attributes
		initTodoTaskAttrs();

		// check to-do list task JSON object
		if (null != todoTaskJSONObject) {
			// set to-do list task attributes
			// task status
			try {
				taskStatus = TodoTaskStatus
						.getStatus(Integer.parseInt(JSONUtils
								.getStringFromJSONObject(
										todoTaskJSONObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getUserEnterpriseTodoListTaskReqResp_task_status))));
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get user enterprise to-do list task status error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// submit timestamp
			// get and check submit timestamp value
			Date _submitTimestampValue = DateStringUtils
					.longDateString2Date(JSONUtils
							.getStringFromJSONObject(
									todoTaskJSONObject,
									_mAppContext
											.getResources()
											.getString(
													R.string.rbgServer_getUserEnterpriseTodoListTaskReqResp_task_submitTimestamp)));
			if (null != _submitTimestampValue) {
				submitTimestamp = _submitTimestampValue.getTime();
			}
		} else {
			Log.e(LOG_TAG,
					"New user enterprise to-do list task with JSON object error, contact JSON object = "
							+ todoTaskJSONObject);
		}
	}

	// constructor with cursor
	public TodoTaskBean(Cursor cursor) {
		super();

		// initialize to-do list task attributes
		initTodoTaskAttrs();

		// check the cursor
		if (null != cursor) {
			// set to-do list task attributes
			// row id
			rowId = cursor.getLong(cursor.getColumnIndex(TodoTask._ID));

			// task id
			taskId = cursor.getLong(cursor.getColumnIndex(TodoTask.TASK_ID));

			// task title
			taskTitle = cursor.getString(cursor
					.getColumnIndex(TodoTask.TASK_TITLE));

			// applicant name
			applicantName = cursor.getString(cursor
					.getColumnIndex(TodoTask.APPLICANTNAME));

			// sender fake id
			senderFakeId = cursor.getLong(cursor
					.getColumnIndex(TodoTask.SENDERFAKEID));

			// status
			taskStatus = TodoTaskStatus.getStatus(cursor.getInt(cursor
					.getColumnIndex(TodoTask.TASK_STATUS)));

			// submit timestamp
			submitTimestamp = cursor.getLong(cursor
					.getColumnIndex(TodoTask.SUBMITTIMESTAMP));

			// advices
			advices.addAll(getTodoTaskAdviceList(cursor.getString(cursor
					.getColumnIndex(TodoTask.ADVISOR_IDS)), cursor
					.getString(cursor.getColumnIndex(TodoTask.ADVISOR_NAMES)),
					cursor.getString(cursor
							.getColumnIndex(TodoTask.ADVICE_STATES)), cursor
							.getString(cursor
									.getColumnIndex(TodoTask.ADVICE_CONTENTS)),
					cursor.getString(cursor
							.getColumnIndex(TodoTask.ADVICE_GIVENTIMESTAMPS))));
		} else {
			Log.e(LOG_TAG,
					"New user enterprise to-do list task with cursor error, cursor = "
							+ cursor);
		}
	}

	// initialize to-do list task attributes
	private void initTodoTaskAttrs() {
		// set default task status and submit timestamp
		taskStatus = TodoTaskStatus.UNREAD;
		submitTimestamp = 0L;

		// set default local storage data dirty type
		lsDataDirtyType = LocalStorageDataDirtyType.NORMAL;
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public TodoTaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TodoTaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Long getSubmitTimestamp() {
		return submitTimestamp;
	}

	public void setSubmitTimestamp(Long submitTimestamp) {
		this.submitTimestamp = submitTimestamp;
	}

	public LocalStorageDataDirtyType getLocalStorageDataDirtyType() {
		return lsDataDirtyType;
	}

	public void setLocalStorageDataDirtyType(
			LocalStorageDataDirtyType lsDataDirtyType) {
		this.lsDataDirtyType = lsDataDirtyType;
	}

	@Override
	public int compareTo(TodoTaskBean another) {
		// get and check iApprove task compare result
		int _result = super.compareTo(another);
		if (0 == _result) {
			// to-do list task compare
			// check to-do list task submit timestamp
			if ((null == submitTimestamp && null == another.submitTimestamp)
					|| (null != submitTimestamp
							&& null != another.submitTimestamp && submitTimestamp
							.longValue() == another.submitTimestamp.longValue())) {
				_result = 0;
			} else {
				Log.d(LOG_TAG,
						"User enterprise to-do list task bean submit timestamp not equals, self submit timestamp = "
								+ submitTimestamp
								+ " and another submit timestamp = "
								+ another.submitTimestamp);
			}
		}

		return _result;
	}

	@Override
	public String toString() {
		// define description
		StringBuilder _description = new StringBuilder();

		// append user enterprise to-do list task row id, task id, title,
		// applicant name, sender fake id, is ended flag, advices, task status
		// and submit timestamp
		_description.append("User enterprise to-do list task row id = ")
				.append(rowId).append(", ").append("task id = ").append(taskId)
				.append(", ").append("title = ").append(taskTitle).append(", ")
				.append("applicant name = ").append(applicantName).append(", ")
				.append("sender fake id = ").append(senderFakeId).append(", ")
				.append("is ended = ").append(ended).append(", ")
				.append("advices = ").append(advices).append(", ")
				.append("task status = ").append(taskStatus).append(" and ")
				.append("submit timestamp = ").append(submitTimestamp);

		return _description.toString();
	}

	// get to-do list task advice list with advisor ids, names, advice states,
	// contents and given timestamps string
	@SuppressWarnings("unchecked")
	private List<IApproveTaskAdviceBean> getTodoTaskAdviceList(
			String advisorIds, String advisorNames, String adviceStates,
			String adviceContents, String adviceGivenTimestamps) {
		List<IApproveTaskAdviceBean> _todoTaskAdviceList = new ArrayList<IApproveTaskAdviceBean>();

		// check to-do list task advice advisor ids, names, advice states,
		// contents and given timestamps string
		if (null != advisorIds || null != advisorNames || null != adviceStates
				|| null != adviceContents || null != adviceGivenTimestamps) {
			// define to-do list task advice advisor id, name, advice state,
			// content and given timestamp list
			List<String> _advisorIdList = null;
			List<String> _advisorNameList = null;
			List<String> _adviceStateList = null;
			List<String> _adviceContentList = null;
			List<String> _adviceGivenTimestampList = null;

			// define advice count
			int _adviceCount = 0;

			// get advisor id, name, advice state, content, given timestamp
			// array and update advice count
			if (null != advisorIds) {
				_advisorIdList = (List<String>) CommonUtils
						.array2List(StringUtils.split(advisorIds,
								TodoTask.ADVICE_SEPARATOR));
				_adviceCount = Math.max(_adviceCount, _advisorIdList.size());
			}
			if (null != advisorNames) {
				_advisorNameList = (List<String>) CommonUtils
						.array2List(StringUtils.split(advisorNames,
								TodoTask.ADVICE_SEPARATOR));
				_adviceCount = Math.max(_adviceCount, _advisorNameList.size());
			}
			if (null != adviceStates) {
				_adviceStateList = (List<String>) CommonUtils
						.array2List(StringUtils.split(adviceStates,
								TodoTask.ADVICE_SEPARATOR));
				_adviceCount = Math.max(_adviceCount, _adviceStateList.size());
			}
			if (null != adviceContents) {
				_adviceContentList = (List<String>) CommonUtils
						.array2List(StringUtils.split(adviceContents,
								TodoTask.ADVICE_SEPARATOR));
				_adviceCount = Math
						.max(_adviceCount, _adviceContentList.size());
			}
			if (null != adviceGivenTimestamps) {
				_adviceGivenTimestampList = (List<String>) CommonUtils
						.array2List(StringUtils.split(adviceGivenTimestamps,
								TodoTask.ADVICE_SEPARATOR));
				_adviceCount = Math.max(_adviceCount,
						_adviceGivenTimestampList.size());
			}

			for (int i = 0; i < _adviceCount; i++) {
				// generate new to-do list task advice
				IApproveTaskAdviceBean _todoTaskAdvice = new IApproveTaskAdviceBean();
				if (null != _advisorIdList) {
					// set advisor id
					_todoTaskAdvice.setAdvisorId(Long.parseLong(_advisorIdList
							.get(i)));
				}
				if (null != _advisorNameList) {
					// set advisor name
					_todoTaskAdvice.setAdvisorName(_advisorNameList.get(i));
				}
				if (null != _adviceStateList) {
					Short _adviceState = Short.parseShort(_adviceStateList
							.get(i));

					// set advice agreed and modified
					_todoTaskAdvice.setAgreed(1 == _adviceState ? true : false);
					_todoTaskAdvice.setModified(2 == _adviceState ? true
							: false);
				}
				if (null != _adviceContentList && 0 < _adviceContentList.size()) {
					// get and check advice content value
					String _adviceContentValue = _adviceContentList.get(i);
					if (null != _adviceContentValue
							&& !TodoTask.ADVICE_CONTENT_PLACEHOLDER
									.equalsIgnoreCase(_adviceContentValue)) {
						// set advice content
						if ("~!@#$".equalsIgnoreCase(_adviceContentValue)) {
							_adviceContentValue = "";
						}
						_todoTaskAdvice.setAdvice(_adviceContentValue);
					}
				}
				if (null != _adviceGivenTimestampList) {
					// set advice given timestamp
					_todoTaskAdvice.setAdviceGivenTimestamp(Long
							.parseLong(_adviceGivenTimestampList.get(i)));
				}

				// add to-do list task advice to list
				_todoTaskAdviceList.add(_todoTaskAdvice);
			}
		} else {
			// set empty as default
			Log.e(LOG_TAG, "Get to-do list task = " + this
					+ " advices error, advices = " + _todoTaskAdviceList);
		}

		return _todoTaskAdviceList;
	}

}
