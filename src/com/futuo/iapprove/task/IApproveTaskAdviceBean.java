package com.futuo.iapprove.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.futuo.iapprove.R;
import com.futuo.iapprove.utils.DateStringUtils;
import com.richitec.commontoolkit.CTApplication;
import com.richitec.commontoolkit.utils.JSONUtils;

public class IApproveTaskAdviceBean implements
		Comparable<IApproveTaskAdviceBean>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -125663361725095968L;

	private static final String LOG_TAG = IApproveTaskAdviceBean.class
			.getCanonicalName();

	// application context
	protected transient Context _mAppContext;

	// advisor id, advisor name, is agreed flag, advice and advice given
	// timestamp
	private Long advisorId;
	private String advisorName;
	private Boolean agreed;
	private String advice;
	private Long adviceGivenTimestamp;

	public IApproveTaskAdviceBean() {
		super();

		// get application context
		_mAppContext = CTApplication.getContext();

		// set default is agreed flag and advice given timestamp
		agreed = false;
		adviceGivenTimestamp = 0L;
	}

	// constructor with JSON object
	public IApproveTaskAdviceBean(JSONObject taskAdviceJSONObject) {
		this();

		// check task advice JSON object
		if (null != taskAdviceJSONObject) {
			// set iApprove task advice attributes
			// advisor id
			try {
				advisorId = 0L;
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get iApprove list task advice advisor id error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// advisor name
			advisorName = JSONUtils
					.getStringFromJSONObject(
							taskAdviceJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListReqResp_task_advice_advisorName));

			// agreed
			// get and check state value
			try {
				Integer _stateValue = Integer
						.parseInt(JSONUtils
								.getStringFromJSONObject(
										taskAdviceJSONObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getIApproveListReqResp_task_advice_state)));
				if (Integer
						.parseInt(_mAppContext
								.getResources()
								.getString(
										R.string.rbgServer_getIApproveListReqResp_task_advice_disAgreedstate)) == _stateValue
						.intValue()) {
					agreed = false;
				} else if (Integer
						.parseInt(_mAppContext
								.getResources()
								.getString(
										R.string.rbgServer_getIApproveListReqResp_task_advice_agreedState)) == _stateValue
						.intValue()) {
					agreed = true;
				}
			} catch (NumberFormatException e) {
				Log.e(LOG_TAG,
						"Get iApprove list task advice state error, exception message = "
								+ e.getMessage());

				e.printStackTrace();
			}

			// advice
			// get and check advice list value
			JSONArray _adviceList = JSONUtils
					.getJSONArrayFromJSONObject(
							taskAdviceJSONObject,
							_mAppContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListReqResp_task_advice_adviceList));
			if (null != _adviceList && !_adviceList.isNull(0)) {
				// get advice object
				JSONObject _adviceObject = JSONUtils
						.getJSONObjectFromJSONArray(_adviceList, 0);

				// get and check advice content object
				JSONObject _adviceContentObject = JSONUtils
						.getJSONObjectFromJSONObject(
								_adviceObject,
								_mAppContext
										.getResources()
										.getString(
												R.string.rbgServer_getIApproveListReqResp_task_advice_adviceContent));
				if (null != _adviceContentObject) {
					// advice
					advice = JSONUtils
							.getStringFromJSONObject(
									_adviceContentObject,
									_mAppContext
											.getResources()
											.getString(
													R.string.rbgServer_getIApproveListReqResp_task_advice_adviceContentInfo));
				}

				// advice given timestamp
				// get and check advice given timestamp value
				Date _adviceGivenTimestampValue = DateStringUtils
						.longDateString2Date(JSONUtils
								.getStringFromJSONObject(
										_adviceObject,
										_mAppContext
												.getResources()
												.getString(
														R.string.rbgServer_getIApproveListReqResp_task_advice_adviceGivenTimestamp)));
				if (null != _adviceGivenTimestampValue) {
					adviceGivenTimestamp = _adviceGivenTimestampValue.getTime();
				}
			}
		} else {
			Log.e(LOG_TAG,
					"New IApprove task advice with JSON object error, task JSON object = "
							+ taskAdviceJSONObject);
		}
	}

	public Long getAdvisorId() {
		return advisorId;
	}

	public void setAdvisorId(Long advisorId) {
		this.advisorId = advisorId;
	}

	public String getAdvisorName() {
		return advisorName;
	}

	public void setAdvisorName(String advisorName) {
		this.advisorName = advisorName;
	}

	public Boolean agreed() {
		return agreed;
	}

	public void setAgreed(Boolean agreed) {
		this.agreed = agreed;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public Long getAdviceGivenTimestamp() {
		return adviceGivenTimestamp;
	}

	public void setAdviceGivenTimestamp(Long adviceGivenTimestamp) {
		this.adviceGivenTimestamp = adviceGivenTimestamp;
	}

	@Override
	public int compareTo(IApproveTaskAdviceBean another) {
		int _result = -1;

		// check advisor name, is agreed flag, advice and advice given timestamp
		if ((null == advisorName && null == another.advisorName)
				|| (null != advisorName && null != another.advisorName && advisorName
						.equalsIgnoreCase(another.advisorName))) {
			if ((null == agreed && null == another.agreed)
					|| (null != agreed && null != another.agreed && agreed
							.booleanValue() == another.agreed.booleanValue())) {
				if ((null == advice && null == another.advice)
						|| (null != advice && null != another.advice && advice
								.equalsIgnoreCase(another.advice))) {
					if ((null == adviceGivenTimestamp && null == another.adviceGivenTimestamp)
							|| (null != adviceGivenTimestamp
									&& null != another.adviceGivenTimestamp && adviceGivenTimestamp
									.longValue() == another.adviceGivenTimestamp
									.longValue())) {
						_result = 0;
					} else {
						Log.d(LOG_TAG,
								"IApprove task advice bean advice given timestamp not equals, self task advice given timestamp = "
										+ adviceGivenTimestamp
										+ " and another task advice given timestamp = "
										+ another.adviceGivenTimestamp);
					}
				} else {
					Log.d(LOG_TAG,
							"IApprove task advice bean advice not equals, self task advice = "
									+ advice + " and another task advice = "
									+ another.advice);
				}
			} else {
				Log.d(LOG_TAG,
						"IApprove task advice bean is agreed flag not equals, self task advice is agreed flag = "
								+ agreed
								+ " and another task advice is agreed flag = "
								+ another.agreed);
			}
		} else {
			Log.d(LOG_TAG,
					"IApprove task advice bean advisor name not equals, self task advice advisor name = "
							+ advisorName
							+ " and another task advice advisor name = "
							+ another.advisorName);
		}

		return _result;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	// get iApprove task advice list with JSON object
	public static List<IApproveTaskAdviceBean> getTaskAdvices(
			JSONObject taskJSONObject) {
		List<IApproveTaskAdviceBean> _taskAdvices = null;

		// check iApprove task JSON object
		if (null != taskJSONObject) {
			// initialize task advice list
			_taskAdvices = new ArrayList<IApproveTaskAdviceBean>();

			// get application context
			Context _appContext = CTApplication.getContext();

			// get and check task advice list
			JSONArray _taskAdviceList = JSONUtils
					.getJSONArrayFromJSONObject(
							taskJSONObject,
							_appContext
									.getResources()
									.getString(
											R.string.rbgServer_getIApproveListReqResp_task_adviceList));
			if (null != _taskAdvices) {
				for (int i = 0; i < _taskAdvices.size(); i++) {
					// get, check task each advice and then add the advice for
					// the task
					IApproveTaskAdviceBean _taskAdvice = new IApproveTaskAdviceBean(
							JSONUtils.getJSONObjectFromJSONArray(
									_taskAdviceList, i));
					if (null != _taskAdvice
							&& 0L != _taskAdvice.getAdviceGivenTimestamp()
									.longValue()) {
						_taskAdvices.add(_taskAdvice);
					}
				}
			}
		}

		return _taskAdvices;
	}

}
